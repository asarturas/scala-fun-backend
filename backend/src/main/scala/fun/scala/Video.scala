package fun.scala

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto._

import scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSExportTopLevel }

@JSExportTopLevel("fun.scala.Video") @JSExportAll
case class Video(id: String, title: String, url: String, likes: Int, plays: Int) {
  def isOnYoutube: Boolean = url.contains("youtube.com")
  def isOnVimeo: Boolean = url.contains("vimeo.com")
}

object Video {
  implicit val decodeVideo: Decoder[Video] = deriveDecoder[Video]
  implicit val encodeVideo: Encoder[Video] = deriveEncoder[Video]
}
