package fun.scala.eventstore.video

import fun.scala.data.VideoMetadata
import fun.scala.eventstore.generic.Event
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

object Events {
  case class UpdatedMetadata(metadata: VideoMetadata) extends Event[Video] {
    val typeName = "update-metadata"
    import io.circe.syntax._
    import VideoMetadata._
    override def asJson: Json = Json.obj(("metadata", this.metadata.asJson)).deepMerge(super.asJson)
  }
  case class Played() extends Event[Video] {
    val typeName = "played"
  }
  case class Liked() extends Event[Video] {
    val typeName = "played"
  }

  import io.circe.syntax._
  def asJson(event: Event[Video]): Json = event match {
    case e: UpdatedMetadata  => e.asJson
  }

  import VideoMetadata._
  implicit val updatedMetadataEncoder: Encoder[UpdatedMetadata] = new Encoder[UpdatedMetadata] {
    import io.circe.syntax._
    def apply(a: UpdatedMetadata): Json = Json.obj(
      ("metadata", a.metadata.asJson)
    )
  }

  implicit val updatedMetadataDecoder: Decoder[UpdatedMetadata] = new Decoder[UpdatedMetadata] {
    def apply(c: HCursor): Result[UpdatedMetadata] = {
      for {
        metadata <- c.downField("metadata").as[VideoMetadata]
      } yield {
        UpdatedMetadata(metadata)
      }
    }
  }
}
