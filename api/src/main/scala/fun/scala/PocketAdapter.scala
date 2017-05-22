package fun.scala

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import fun.scala.pocket.{Item, ProcessParsedCollection}

object PocketAdapter {
  def withProps(postProcessor: ActorRef): Props = {
    Props(new PocketAdapter(postProcessor: ActorRef))
  }
}

class PocketAdapter(postProcessor: ActorRef) extends Actor with ActorLogging {
  def receive: Receive = {
    case ProcessParsedCollection(collection) =>
      log.info("about to process videos")
      val videos = collection.list.map {
        case (key, Item(_, title, url)) => (key, Video(title, url))
      }
      log.info("processed videos, sending for post processing")
      postProcessor ! PostProcessVideos(videos)
  }
}