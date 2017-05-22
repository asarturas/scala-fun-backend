package fun.scala.video

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import fun.scala.{PostProcessVideos, StoreVideos, Video, VideoProcessor}

object VideoPostProcessor {
  def withProps(repository: ActorRef): Props = {
    Props(new VideoPostProcessor(repository: ActorRef))
  }
}


class VideoPostProcessor(repository: ActorRef) extends Actor with ActorLogging {
  def receive: Receive = {
    case PostProcessVideos(videos: Map[String, Video]) =>
      log.info("Got videos to post process")
      val embedVideos = videos.mapValues {
        case video @ Video(title, url) =>
          if (video.isOnYoutube) Video(title, VideoProcessor.embedYoutube(url))
          else if (video.isOnVimeo) Video(title, VideoProcessor.embedVimeo(url))
          else video
      }
      log.info("Done, sending to store")
      repository ! StoreVideos(embedVideos)
  }
}
