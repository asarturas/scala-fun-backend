package fun.scala.store.video

import fun.scala.data.VideoMetadata
import fun.scala.store.generic.Command
import io.circe._
import io.circe.generic.semiauto._


object Commands {
  case class UpdateMetadata(metadata: VideoMetadata) extends Command[Video]
  case class Play() extends Command[Video]
  case class Like() extends Command[Video]
}