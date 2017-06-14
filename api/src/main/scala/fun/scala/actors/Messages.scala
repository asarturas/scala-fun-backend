package fun.scala.actors

import fun.scala.store.generic.AggregateId
import fun.scala.store.video.{Video, VideoAggregateId}
import fun.scala.{SourcedVideo, SourcedVideoMetadata, VideoMetadata}
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
