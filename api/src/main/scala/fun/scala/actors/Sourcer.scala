package fun.scala.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import fun.scala.actors.Messages.{CollectVideos, ProcessSourcedVideos}
import fun.scala.data.Source
import fun.scala.sourcers.{PocketConfig, PocketSourcer, SourcerConfig}
import fun.scala.sourcers.PocketSourcer.Pocket

object Sourcer {
  def create(source: Source, config: SourcerConfig, processor: ActorRef): Props =
    Props(new Sourcer(source, config, processor))
}

class Sourcer(source: Source, config: SourcerConfig, processor: ActorRef) extends Actor with ActorLogging {
  val sourcer: fun.scala.sourcers.Sourcer = (source, config) match {
    case (Pocket, PocketConfig(consumerKey, accessToken)) => new PocketSourcer(consumerKey, accessToken)
  }
  def receive: Receive = {
    case CollectVideos() =>
      log.info("sourcer received a request to collect stuff")
      processor ! ProcessSourcedVideos(sourcer.collect())
  }
}
