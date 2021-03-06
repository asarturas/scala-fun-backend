package fun.scala.actors

import akka.actor.{Actor, ActorLogging}
import fun.scala.actors.Messages.{RandomVideo, ReturnRandomVideo, StoreVideoMetadata}
import fun.scala.eventstore.storage.GetEventStore
import fun.scala.eventstore.generic.{AggregateIdString, Repository}
import fun.scala.eventstore.video.Commands.UpdateMetadata
import fun.scala.eventstore.video.{Video, VideoAggregate, VideoAggregateId}

object Storage {

}

class Storage extends Actor with ActorLogging {

  val runtimeRepository = new Repository[Video](new GetEventStore(), VideoAggregate.factory)

  def receive: Receive = {
    case StoreVideoMetadata(metadata) =>
      log.info("got video metadata to store")
      metadata.foreach { m =>
        val id = VideoAggregateId(AggregateIdString(m.idSeed))
        val video = runtimeRepository.getById(id).getOrElse(runtimeRepository.createAs(m.idSeed))
        if (video.state.metadata != m) video & UpdateMetadata(m)
      }
    case ReturnRandomVideo() =>
      val respondTo = sender()
      respondTo ! RandomVideo(runtimeRepository.getRandom.map(v => (v.id.id.toString, v.state)).get)

  }
}