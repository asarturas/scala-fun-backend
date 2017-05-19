package fun.scala.pocket

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import fun.scala.{StoreVideos, Video}

object PocketAdapter {
  def withProps(repository: ActorRef): Props = {
    Props(new PocketAdapter(repository: ActorRef))
  }
}

class PocketAdapter(repository: ActorRef) extends Actor with ActorLogging {
  def receive: Receive = {
    case ProcessParsedCollection(collection) =>
      log.info("about to process videos")
      val videos = collection.list.map {
        case (key, Item(_, title, url)) => (key, Video(title, url))
      }
      log.info("processed videos")
      repository ! StoreVideos(videos)
  }
}