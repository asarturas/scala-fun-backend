package fun.scala.actors

import fun.scala.data.{SourcedVideo, VideoMetadata}
import fun.scala.eventstore.video.Video
import monix.eval.Task

object Messages {
  type AggregateId = String
  case class CollectVideos()
  case class ProcessSourcedVideos(videosTask: Task[List[SourcedVideo]])
  case class StoreVideoMetadata(videos: Iterable[VideoMetadata])
  case class ReturnRandomVideo()
  case class RandomVideo(video: (AggregateId, Video))
}
