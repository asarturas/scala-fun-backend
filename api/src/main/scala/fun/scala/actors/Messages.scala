package fun.scala.actors

import fun.scala.data.{SourcedVideo, SourcedVideoMetadata, VideoMetadata}
import fun.scala.eventstore.video.{Video}
import monix.eval.Task

object Messages {
  type AggregateId = String
  case class CollectVideos()
  case class ProcessSourcedVideos(videos: List[SourcedVideo])
  case class CombineSourcedVideoMetadata(videos: List[Task[Option[SourcedVideoMetadata]]])
  case class StoreVideoMetadata(videos: Iterable[VideoMetadata])
  case class ReturnRandomVideo()
  case class RandomVideo(video: (AggregateId, Video))
}
