package fun.scala.pocket

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import fun.scala.{GetRandomVideo, StoreNewVideos}
import io.circe.parser.decode

import scala.concurrent.Await
import scala.concurrent.duration._

object PocketClient {
  def withProps(consumerKey: String, accessToken: String): Props = {
    Props(new PocketClient(consumerKey, accessToken))
  }
}

//trait TradingApiSerialization extends SprayJsonSupport {
  // One of the built-in spray-json auto-formatters
//  implicit val itemFormat = jsonFormat2(ItemOfVideos)
//  implicit val listFormat = jsonFormat2(ListOfVideos)
//}


class PocketClient(consumerKey: String, accessToken: String) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)

  override def preStart() = {
    http.singleRequest(
      HttpRequest(
        uri = s"https://getpocket.com/v3/get?consumer_key=$consumerKey&access_token=$accessToken&tag=scala&contentType=video",
        method = HttpMethods.POST
      )
    ).pipeTo(self)
  }

  def receive: Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      implicit val materializer = ActorMaterializer()

      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        log.info("Got response from Pocket")
        val str = body.utf8String
        import Pocket._
        log.info(str)
        val data = decode[Collection](str)
        //val result = Await.result(data, 5.second)
        val coll: Collection = data match {
          case Right(collection) => collection
          case Left(_) => Collection(Map())
        }
        log.info(data.toString)
        context.actorSelection("../videoRepository") ! StoreNewVideos(coll)
      }
    case resp @ HttpResponse(code, _, _, _) =>
      log.info("Request to Pocket failed")
      resp.discardEntityBytes()
  }
}
