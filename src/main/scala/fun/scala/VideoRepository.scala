package fun.scala

import akka.actor.{Actor, ActorLogging}

class VideoRepository extends Actor with ActorLogging {

  var videoData: Option[String] = None

  def receive: Receive = {
    case GetRandomVideo() =>
      log.info("Got video request")
      sender() ! VideoData(videoData.getOrElse("none"))
    case StoreNewVideos(newVideoData) =>
      log.info("Got video data and stored it")
      videoData = Some(newVideoData)
  }
}
