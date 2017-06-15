package fun.scala.store.video

import fun.scala.store.generic._
import fun.scala.store.video.Events._
import fun.scala.store.video.Commands._

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
