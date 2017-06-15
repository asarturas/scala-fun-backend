package fun.scala.data

import com.netaporter.uri.Uri
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

case class VideoMetadata(video: SourcedVideo, embedUrl: Uri, id: Option[String], plays: Int, likes: Int) {
  def idSeed: String = id.getOrElse(embedUrl.toString)
}

object VideoMetadata {
  import SourcedVideo._
  import io.circe.syntax._
  implicit val videoMetadataEncoder: Encoder[VideoMetadata] = new Encoder[VideoMetadata] {
    def apply(a: VideoMetadata): Json = Json.obj(
      ("video", a.video.asJson),
      ("embed-url", Json.fromString(a.embedUrl.toString)),
      ("id", Json.fromString(a.id.getOrElse(""))),
      ("plays", Json.fromInt(a.plays)),
      ("likes", Json.fromInt(a.likes))
    )
  }

  implicit val videoMetadataDecoder: Decoder[VideoMetadata] = new Decoder[VideoMetadata] {
    def apply(c: HCursor): Result[VideoMetadata] = {
      for {
        video <- c.downField("video").as[SourcedVideo]
        embedUrl <- c.downField("embed-url").as[String]
        id <- c.downField("id").as[String]
        plays <- c.downField("plays").as[Int]
        likes <- c.downField("likes").as[Int]
      } yield {
        VideoMetadata(video, Uri.parse(embedUrl), Option(id), plays, likes)
      }
    }
  }
}