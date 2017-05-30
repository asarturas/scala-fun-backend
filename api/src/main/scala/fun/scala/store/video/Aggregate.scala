package fun.scala.store.video

import java.util.UUID

import com.netaporter.uri.Uri
import io.circe.Decoder
import io.circe.generic.semiauto._
import fun.scala.{SourcedVideo, VideoMetadata}
import fun.scala.store.generic._
import fun.scala.store.video.Events._
import fun.scala.store.video.Commands._

case class Video(likes: Int, plays: Int, metadata: VideoMetadata) {
  def embedUrl: Uri = metadata.embedUrl
  def title: String = metadata.video.title
  def likesOverall: Int = likes + metadata.likes
  def playsOverall: Int = plays + metadata.plays
}

//object Video {
//  implicit val decodeUri: Decoder[Uri] = deriveDecoder[Uri]
//  implicit val decodeSourcedVideo: Decoder[SourcedVideo] = deriveDecoder[SourcedVideo]
//  implicit val decodeVideoMetadata: Decoder[VideoMetadata] = deriveDecoder[VideoMetadata]
//  implicit val decodeVideo: Decoder[Video] = deriveDecoder[Video]
//}

case class VideoAggregateId(id: UUID) extends AggregateId[Video] {
  def toStreamId: StreamId = StreamId("video", id.toString)
}

case class VideoAggregate(override val id: AggregateId[Video], override val version: Version, init: List[Event[Video]])
  extends Aggregate[Video](id, version, init, VideoFactory.initialSnapshot) {
  def replay(snapshot: Snapshot[Video], event: Event[Video]): Snapshot[Video] = event match {
    case UpdatedMetadata(metadata) => snapshot.copy(state = snapshot.state.copy(metadata = metadata))
    case Played() => snapshot.copy(state = snapshot.state.copy(plays = snapshot.state.plays + 1))
    case Liked() => snapshot.copy(state = snapshot.state.copy(likes = snapshot.state.likes + 1))
  }
  def run(command: Command[Video]): Aggregate[Video] = command match {
    case UpdateMetadata(metadata) => this.copy(init = this.init :+ UpdatedMetadata(metadata))
    case Play() => this.copy(init = this.init :+ Played())
    case Like() => this.copy(init = this.init :+ Liked())
  }
}