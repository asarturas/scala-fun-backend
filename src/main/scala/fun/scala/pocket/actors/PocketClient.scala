package fun.scala.pocket.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import fun.scala.pocket.ParseCollectionResponse

object PocketClient {
  def withProps(consumerKey: String, accessToken: String, target: ActorRef): Props = {
    Props(new PocketClient(consumerKey, accessToken, target))
  }
}

class PocketClient(consumerKey: String, accessToken: String, target: ActorRef) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)
  val parser: ActorRef = context.system.actorOf(Parser.withProps(target), "parser")

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
//      implicit val materializer = ActorMaterializer()
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        log.info("Got response from pocket")
        parser ! ParseCollectionResponse(body.utf8String)
      }
    case resp @ HttpResponse(code, _, _, _) =>
      log.info("Request to Pocket failed")
      resp.discardEntityBytes()
  }
}
