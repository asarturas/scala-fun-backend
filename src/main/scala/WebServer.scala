import akka.Done
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.scaladsl.Source
import akka.util.ByteString
import fun.scala.{GetRandomVideo, VideoData, VideoRepository}
import fun.scala.pocket.PocketClient
import spray.json.DefaultJsonProtocol._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.util.Random

object WebServer {

  val config = ConfigFactory.load().getConfig("scala-fun")

  // domain model
  final case class Item(name: String, id: Long)
  final case class Order(items: List[Item])

  // formats for unmarshalling and marshalling
  implicit private val itemFormat = jsonFormat2(Item)
  implicit private val orderFormat = jsonFormat1(Order)

  import scala.concurrent.ExecutionContext.Implicits.global
  // (fake) async database query api
  def fetchItem(itemId: Long): Future[Option[Item]] = Future(Some(Item(s"Random $itemId", itemId)))
  def saveOrder(order: Order): Future[Done] = Future(Done)

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    // streams are re-usable so we can define it here
    // and use it for every request
    val numbers = Source.fromIterator(() => Iterator.continually(Random.nextInt()))

    val videoRepository = system.actorOf(Props[VideoRepository], "videoRepository")
    val pocketClient = system.actorOf(PocketClient.withProps(config.getString("pocketConsumerKey"), config.getString("pocketAccessToken")), "pocketClient")

    //
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import io.circe.generic.auto._

    val route =
      path("getVideo") {
        get {
          implicit val timeout: Timeout = 5.seconds
          val videos: Future[VideoData] = (videoRepository ? GetRandomVideo()).mapTo[VideoData]
          complete(videos)
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}