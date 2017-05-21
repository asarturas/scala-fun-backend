package fun.scala

import io.circe.Decoder
import io.circe.generic.semiauto._

import scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSExportTopLevel }

@JSExportTopLevel("fun.scala.Video") @JSExportAll
case class Video(title: String, url: String)

object Video {
  implicit val decodeFileData: Decoder[Video] = deriveDecoder[Video]
}
