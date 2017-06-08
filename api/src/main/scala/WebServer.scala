import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Allow
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.{MethodRejection, RejectionHandler}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import fun.scala.actors.Messages.{RandomVideo, ReturnRandomVideo}
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import fun.scala.actors.Messages.CollectVideos
import fun.scala.actors.{Processor, Sourcer, Storage}
import fun.scala.processors.UrlProcessor
import fun.scala.sourcers.{PocketConfig, PocketSourcer}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

object WebServer {

  val config = ConfigFactory.load().getConfig("scala-fun")

  def main(args: Array[String]) {

    implicit val system = ActorSystem("scala-fun")
    implicit val materializer = ActorMaterializer()

    implicit def rejectionHandler =
    RejectionHandler.newBuilder().handleAll[MethodRejection] { rejections =>
      val methods = rejections map (_.supported)
      lazy val names = methods map (_.name) mkString ", "
      respondWithHeader(Allow(methods)) {
        options {
          complete(s"Supported methods : $names.")
        } ~
        complete(MethodNotAllowed, s"HTTP method not allowed, supported methods: $names!")
      }
    }
    .result()

//    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    // app actors
    val (consumerKey, accessToken) = (config.getString("pocketConsumerKey"), config.getString("pocketAccessToken"))
    val sourcer = system.actorOf(
      Sourcer.create(PocketSourcer.Pocket, PocketConfig(consumerKey, accessToken)),
      "pocket-sourcer"
    )
    system.actorOf(
      Processor.create(new UrlProcessor()),
      "url-processor"
    )
    val storage = system.actorOf(
      Props[Storage],
      "storage"
    )
    sourcer ! CollectVideos()

    // circe for decoding responses in json
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import io.circe.generic.auto._

    val route = cors() {
      path("next") {
        get {
          implicit val timeout: Timeout = 5.seconds
          val video: Future[fun.scala.Video] = (storage ? ReturnRandomVideo()).map {
            case RandomVideo(v) =>
              fun.scala.Video(v.title, v.embedUrl.toString, v.likesOverall, v.playsOverall)
          }
          complete(video)
        }
      }
    }


    val apiHost = config.getString("apiServerHost")
    val apiPort = config.getInt("apiServerPort")

    val bindingFuture = Http().bindAndHandle(route, apiHost, apiPort)

    println(s"Server online at http://${apiHost}:${apiPort}/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
