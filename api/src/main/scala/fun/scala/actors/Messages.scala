package fun.scala.actors

import fun.scala.store.generic.AggregateId
import fun.scala.store.video.{Video, VideoAggregateId}
import fun.scala.{SourcedVideo, VideoMetadata}

object Messages {
  type AggregateId = String
  case class CollectVideos()
  case class ProcessSourcedVideos(videos: List[SourcedVideo])
  case class StoreVideoMetadata(videos: Iterable[VideoMetadata])
  case class ReturnRandomVideo()
  case class RandomVideo(video: (AggregateId, Video))
}
