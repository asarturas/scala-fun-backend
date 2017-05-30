package fun.scala.actors

import fun.scala.store.video.Video
import fun.scala.{SourcedVideo, VideoMetadata}
import monix.eval.Task

object Messages {
  case class CollectVideos()
  case class ProcessSourcedVideos(videos: List[SourcedVideo])
  case class StoreVideoMetadata(videos: Iterable[VideoMetadata])
  case class ReturnRandomVideo()
  case class RandomVideo(video: Video)
}
