package fun.scala

import akka.actor.{Actor, ActorLogging}
import fun.scala.pocket.Collection

import scala.util.Random

class VideoRepository extends Actor with ActorLogging {

  var videoData: Option[Collection] = None

  def receive: Receive = {
    case GetRandomVideo() =>
      log.info("Got video request")
      val videos = videoData.getOrElse(Collection(Map())).list
      val keys = videos.toList.map(_._2)
      sender() ! VideoData(keys(Random.nextInt(keys.size)))
    case StoreNewVideos(newVideoData) =>
      log.info("Got video data and stored it")
      log.info(newVideoData.toString)
      videoData = Some(newVideoData)
  }
}
