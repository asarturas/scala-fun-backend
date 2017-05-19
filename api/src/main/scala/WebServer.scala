import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import fun.scala.pocket.PocketAdapter
import fun.scala.pocket.actors.PocketClient
import fun.scala.{ReturnRandomVideo, Video, VideoRepository}

import akka.pattern.ask

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

/**
  * Created by arturas on 19/05/2017.
  */
object WebServer {

  val config = ConfigFactory.load().getConfig("scala-fun")

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
//    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val videoRepository = system.actorOf(Props[VideoRepository], "videoRepository")
    val pocketAdapter = system.actorOf(PocketAdapter.withProps(videoRepository), "pocketAdapter")

    val (consumerKey, accessToken) = (config.getString("pocketConsumerKey"), config.getString("pocketAccessToken"))
    system.actorOf(PocketClient.withProps(consumerKey, accessToken, pocketAdapter), "pocketClient")

    //
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import io.circe.generic.auto._

    val route =
      path("next") {
        get {
          implicit val timeout: Timeout = 5.seconds
          val video: Future[Video] = (videoRepository ? ReturnRandomVideo).mapTo[Video]
          complete(video)
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
