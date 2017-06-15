package fun.scala.data

import com.netaporter.uri.Uri
import fun.scala.data.Service.{Vimeo, Youtube}
import fun.scala.sourcers.PocketSourcer.Pocket
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

case class SourcedVideo(url: Uri, title: String, service: Service, source: Source)

object SourcedVideo {
  implicit val sourcedVideoEncoder: Encoder[SourcedVideo] = new Encoder[SourcedVideo] {
    def apply(a: SourcedVideo): Json = Json.obj(
      ("url", Json.fromString(a.url.toString)),
      ("title", Json.fromString(a.title)),
      ("service", Json.fromString(a.service match {
        case Youtube => "youtube"
        case Vimeo => "vimeo"
        case Service.Unknown => "unknown"
      })),
      ("source", Json.fromString(a.source match {
        case Pocket => "pocket"
        case Source.Unknown => "unknown"
      }))
    )
  }

  implicit val sourcedVideoDecoder: Decoder[SourcedVideo] = new Decoder[SourcedVideo] {
    def apply(c: HCursor): Result[SourcedVideo] = {
      for {
        url <- c.downField("url").as[String]
        title <- c.downField("title").as[String]
        serviceStr <- c.downField("service").as[String]
        sourceStr <- c.downField("source").as[String]
      } yield {
        val service = serviceStr match {
          case "youtube" => Youtube
          case "vimeo" => Vimeo
          case _ => Service.Unknown
        }
        val source = sourceStr match {
          case "pocket" => Pocket
          case _ => Source.Unknown
        }
        SourcedVideo(Uri.parse(url), title, service, source)
      }
    }
  }
}
