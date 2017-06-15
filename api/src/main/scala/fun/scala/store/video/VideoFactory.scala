package fun.scala.store.video

import java.util.UUID

import com.netaporter.uri.Uri
import fun.scala.data.{Service, Source, SourcedVideo, VideoMetadata}
import fun.scala.store.generic._

object VideoFactory extends NumericFactory[Video] {
  val initialState: Video = Video(0, 0,
    VideoMetadata(SourcedVideo(Uri.parse(""), "", Service.Unknown, Source.Unknown), Uri.parse(""), None, 0, 0)
  )
  val zeroAggregateId: AggregateId[Video] = VideoAggregateId("00000000-0000-0000-0000-000000000000")
  def newAggregateId: AggregateId[Video] = VideoAggregateId()
  def getAggregateId(idStr: String): AggregateId[Video] = VideoAggregateId(idStr)
  def getAggregateId(streamId: StreamId): AggregateId[Video] = VideoAggregateId(streamId.id)
  def getAggregate(id: AggregateId[Video], version: Version, events: List[Event[Video]]): Aggregate[Video] =
    VideoAggregate(id, version, events)
}