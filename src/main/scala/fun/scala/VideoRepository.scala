package fun.scala

import akka.actor.{Actor, ActorLogging}

import scala.collection.mutable
import scala.util.Random

class VideoRepository extends Actor with ActorLogging {

  val repository: mutable.Map[String, Video] = mutable.Map.empty[String, Video]

  def receive: Receive = {
    case StoreVideos(videos) =>
      log.info("Got videos data and going to store it")
      repository ++= videos.filterKeys(repository.contains(_) == false)
    case ReturnRandomVideo =>
      log.info("Got video request")
      val keys = repository.keys.toList
      sender() ! repository(keys(Random.nextInt(keys.size)))
  }
}
