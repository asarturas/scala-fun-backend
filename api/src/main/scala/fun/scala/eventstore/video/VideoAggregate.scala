package fun.scala.eventstore.video

import com.netaporter.uri.Uri
import fun.scala.data.{Service, Source, SourcedVideo, VideoMetadata}
import fun.scala.eventstore.generic._
import fun.scala.eventstore.video.Events._
import fun.scala.eventstore.video.Commands._

object VideoAggregate {
  val commandHandler: Aggregate.CommandHandler[Video] = (snapshot, command) => command match {
    case UpdateMetadata(metadata) => (Aggregate.noEffect, List(UpdatedMetadata(metadata)))
    case Play() => (Aggregate.noEffect, List(Played()))
    case Like() => (Aggregate.noEffect, List(Liked()))
  }
  val eventHandler: Aggregate.EventHandler[Video] = (snapshot, event) => event match {
    case UpdatedMetadata(metadata) => snapshot.copy(state = snapshot.state.copy(metadata = metadata))
    case Played() => snapshot.copy(state = snapshot.state.copy(plays = snapshot.state.plays + 1))
    case Liked() => snapshot.copy(state = snapshot.state.copy(likes = snapshot.state.likes + 1))
  }
  val factory: Factory[Video] = Factory[Video](
    Video(0, 0,
      VideoMetadata(SourcedVideo(Uri.parse(""), "", Service.Unknown, Source.Unknown), Uri.parse(""), None, 0, 0)
    ),
    VideoAggregateId("00000000-0000-0000-0000-000000000000"),
    VideoAggregateId,
    commandHandler,
    eventHandler
  )
}
