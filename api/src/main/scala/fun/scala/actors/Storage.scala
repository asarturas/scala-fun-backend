package fun.scala.actors

import akka.actor.{Actor, ActorLogging}
import fun.scala.actors.Messages.{RandomVideo, ReturnRandomVideo, StoreVideoMetadata}
import fun.scala.store.generic.{Repository, RuntimeEventStore}
import fun.scala.store.video.Commands.UpdateMetadata
import fun.scala.store.video.{Video, VideoFactory}

object Storage {

}

class Storage extends Actor with ActorLogging {

  val runtimeRepository = new Repository[Video](new RuntimeEventStore(), VideoFactory)

  def receive: Receive = {
    case StoreVideoMetadata(metadata) =>
      log.info("got video metadata")
      log.info(metadata.toString)
      metadata.foreach(m => runtimeRepository.save(runtimeRepository.create & UpdateMetadata(m)))
    case ReturnRandomVideo() =>
      val respondTo = sender()
      respondTo ! RandomVideo(runtimeRepository.getRandom.map(_.state).get)

  }
}