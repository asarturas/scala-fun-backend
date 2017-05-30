package fun.scala.store.video

import fun.scala.VideoMetadata
import fun.scala.store.generic.Event

object Events {
  case class UpdatedMetadata(metadata: VideoMetadata) extends Event[Video]
  case class Played() extends Event[Video]
  case class Liked() extends Event[Video]
}
