package fun.scala.store.video

import java.util.UUID

import com.netaporter.uri.Uri
import fun.scala.{Service, Source, SourcedVideo, VideoMetadata}
import fun.scala.store.generic._

object VideoFactory extends NumericFactory[Video] {
  val initialState: Video = Video(0, 0,
    VideoMetadata(SourcedVideo(Uri.parse(""), "", Service.Unknown, Source.Unknown), Uri.parse(""), 0, 0)
  )
  val zeroAggregateId: AggregateId[Video] = VideoAggregateId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
  def newAggregateId: AggregateId[Video] = VideoAggregateId(UUID.randomUUID)
  def getAggregateId(idStr: String): AggregateId[Video] = VideoAggregateId(UUID.fromString(idStr))
  def getAggregateId(streamId: StreamId): AggregateId[Video] = VideoAggregateId(UUID.fromString(streamId.id))
  def getAggregate(id: AggregateId[Video], version: Version, events: List[Event[Video]]): Aggregate[Video] =
    VideoAggregate(id, version, events)
}