package fun.scala.sourcers

import com.netaporter.uri.Uri
import io.taig.communicator.OkHttpRequest
import io.taig.communicator.request.Request
import okhttp3.{MediaType, OkHttpClient, RequestBody}
import io.circe.parser.decode
import io.circe.Decoder
import io.circe.generic.semiauto._
import fun.scala.data.{Service, Source, SourcedVideo}
import fun.scala.sourcers.PocketSourcer.{Collection, Item, Pocket}

import scala.concurrent.Await
import scala.concurrent.duration._

object PocketSourcer {
  case object Pocket extends Source

  case class Item(item_id: String, resolved_url: String, resolved_title: String)
  case class Collection(list: Map[String, Item])

  object Decoders {
    implicit val itemDecoder: Decoder[Item] = deriveDecoder[Item]
    implicit val collectionDecoder: Decoder[Collection] = deriveDecoder[Collection]
  }
}

class PocketSourcer(consumerKey: String, accessToken: String) extends Sourcer {

  def collect(): List[SourcedVideo] = {
    import fun.scala.sourcers.PocketSourcer.Decoders._
    val sourceTask = requestToCollect()
      .map { response =>
        decode[Collection](response.body) match {
          case Right(Collection(list)) =>
            list.map {
              case (_, Item(_, url, title)) => SourcedVideo(Uri.parse(url), title, Service(url), Pocket)
            }.toList
          case Left(message) =>
            println(response.body)
            println(message)
            Nil
        }
      }
    import monix.execution.Scheduler.Implicits.global
    Await.result(sourceTask.runAsync, 30.seconds)
  }

  implicit val client = new OkHttpClient

  private def requestToCollect() = {
    val body = RequestBody.create(
      MediaType.parse("application/json; charset=utf-8"),
      s"""
        |{
        |  "consumer_key": "$consumerKey",
        |  "access_token": "$accessToken",
        |  "tag": "scala",
        |  "contentType": "video",
        |  "detailType": "simple",
        |  "state": "all"
        |}
      """.stripMargin
    )
    println(new OkHttpRequest.Builder().url(url).post(body).build().toString)
    Request(new OkHttpRequest.Builder().url(url).post(body).build()).parse[String]
  }

  val url = "https://getpocket.com/v3/get"
}

case class PocketConfig(consumerKey: String, accessToken: String) extends SourcerConfig