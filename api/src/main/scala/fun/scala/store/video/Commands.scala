package fun.scala.store.video

import fun.scala.VideoMetadata
import fun.scala.store.generic.Command

object Commands {
  case class UpdateMetadata(metadata: VideoMetadata) extends Command[Video]
  case class Play() extends Command[Video]
  case class Like() extends Command[Video]
}
