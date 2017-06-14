package fun.scala.actors

import akka.actor.{Actor, ActorLogging}
import fun.scala.actors.Messages.{RandomVideo, ReturnRandomVideo, StoreVideoMetadata}
import fun.scala.store.generic.{AggregateIdString, Repository, RuntimeEventStore}
import fun.scala.store.video.Commands.UpdateMetadata
import fun.scala.store.video.{Video, VideoAggregateId, VideoFactory}

object Storage {

}

class Storage extends Actor with ActorLogging {

  val runtimeRepository = new Repository[Video](new RuntimeEventStore(), VideoFactory)

  def receive: Receive = {
    case StoreVideoMetadata(metadata) =>
      log.info("got video metadata")
      log.info(metadata.toString)
      metadata.foreach { m =>
        val id = VideoAggregateId(AggregateIdString(m.idSeed))
        val video = runtimeRepository.getById(id).getOrElse(runtimeRepository.createAs(m.idSeed))
        runtimeRepository.save(video & UpdateMetadata(m))
      }
    case ReturnRandomVideo() =>
      val respondTo = sender()
      respondTo ! RandomVideo(runtimeRepository.getRandom.map(v => (v.id.id.toString, v.state)).get)

  }
}