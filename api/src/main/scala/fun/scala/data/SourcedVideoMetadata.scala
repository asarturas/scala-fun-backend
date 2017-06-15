package fun.scala.data

import com.netaporter.uri.Uri
import fun.scala.data.Service.{Vimeo, Youtube}
import fun.scala.sourcers.PocketSourcer.Pocket
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

case class SourcedVideoMetadata(
  video: SourcedVideo,
  embedUrl: Option[Uri] = None,
  id: Option[String] = None,
  plays: Option[Int] = None,
  likes: Option[Int] = None
)

object SourcedVideoMetadata {
  import SourcedVideo._
  import io.circe.syntax._
  implicit val sourcedVideoEncoder: Encoder[SourcedVideoMetadata] = new Encoder[SourcedVideoMetadata] {
    def apply(a: SourcedVideoMetadata): Json = Json.obj(
      ("video", a.video.asJson),
      ("embed-url", Json.fromString(a.embedUrl.map(_.toString).getOrElse(""))),
      ("id", Json.fromString(a.id.getOrElse(""))),
      ("plays", Json.fromInt(a.plays.getOrElse(0))),
      ("likes", Json.fromInt(a.likes.getOrElse(0)))
    )
  }

  implicit val sourcedVideoDecoder: Decoder[SourcedVideoMetadata] = new Decoder[SourcedVideoMetadata] {
    def apply(c: HCursor): Result[SourcedVideoMetadata] = {
      for {
        video <- c.downField("video").as[SourcedVideo]
        embedUrl <- c.downField("embed-url").as[String]
        id <- c.downField("id").as[String]
        plays <- c.downField("plays").as[Int]
        likes <- c.downField("likes").as[Int]
      } yield {
        SourcedVideoMetadata(video, Option(Uri.parse(embedUrl)), Option(id), Option(plays), Option(likes))
      }
    }
  }
}