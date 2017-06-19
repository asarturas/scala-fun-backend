package fun.scala.actors

import akka.actor.{Actor, ActorLogging}
import fun.scala.actors.Messages.{RandomVideo, ReturnRandomVideo, StoreVideoMetadata}
import fun.scala.store.es.EventStore
import fun.scala.store.generic.{AggregateIdString, Repository}
import fun.scala.store.video.Commands.UpdateMetadata
import fun.scala.store.video.{Video, VideoAggregate, VideoAggregateId}

object Storage {

}

class Storage extends Actor with ActorLogging {

//  val runtimeRepository = new Repository[Video](new RuntimeEventStore(), VideoFactory)
  val runtimeRepository = new Repository[Video](new EventStore(), VideoAggregate.factory)

  def receive: Receive = {
    case StoreVideoMetadata(metadata) =>
      log.info("got video metadata")
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