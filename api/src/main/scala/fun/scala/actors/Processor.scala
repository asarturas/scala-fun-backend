package fun.scala.actors

import akka.actor.{Actor, ActorLogging, Props}
import fun.scala.actors.Messages.{ProcessSourcedVideos, StoreVideoMetadata}
import fun.scala.processors.Combinator

object Processor {
  def create(processors: List[fun.scala.processors.Processor]): Props = Props(new Processor(processors))
}

class Processor(processors: List[fun.scala.processors.Processor]) extends Actor with ActorLogging {

  val combinator = new Combinator()

  override def preStart(): Unit = {
    log.info(self.toString)
    super.preStart()
  }

  def receive: Receive = {
    case ProcessSourcedVideos(videos) =>
      import monix.execution.Scheduler.Implicits.global
      log.info("processor received")
      for {
        futureProcessedVideos <- processors.map(_.process(videos))
      } yield for {
        processedVideos <- futureProcessedVideos
        combined <- combinator.combine(processedVideos)
      } yield context.system.actorSelection("user/storage") ! StoreVideoMetadata(combined)
  }
}
