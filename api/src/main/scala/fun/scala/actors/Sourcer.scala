package fun.scala.actors

import akka.actor.{Actor, ActorLogging, Props}
import fun.scala.Source
import fun.scala.actors.Messages.{CollectVideos, ProcessSourcedVideos}
import fun.scala.sourcers.{PocketConfig, PocketSourcer, SourcerConfig}
import fun.scala.sourcers.PocketSourcer.Pocket

object Sourcer {
  def create(source: Source, config: SourcerConfig): Props = Props(new Sourcer(source, config))
}

class Sourcer(source: Source, config: SourcerConfig) extends Actor with ActorLogging {
  val sourcer: fun.scala.sourcers.Sourcer = (source, config) match {
    case (Pocket, PocketConfig(consumerKey, accessToken)) => new PocketSourcer(consumerKey, accessToken)
  }
  def receive: Receive = {
    case CollectVideos() =>
      log.info("sourcer received")
      context.system.actorSelection("user/*") ! ProcessSourcedVideos(sourcer.collect())
  }
}