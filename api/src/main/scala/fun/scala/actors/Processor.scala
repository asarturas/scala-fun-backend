package fun.scala.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import fun.scala.actors.Messages.{ProcessSourcedVideos, StoreVideoMetadata}
import fun.scala.processors.Combinator

import scala.util.{Failure, Success}

object Processor {
  def create(processors: List[fun.scala.processors.Processor], storage: ActorRef): Props =
    Props(new Processor(processors, storage))
}

class Processor(processors: List[fun.scala.processors.Processor], storage: ActorRef) extends Actor with ActorLogging {

  val combinator = new Combinator()

  override def preStart(): Unit = {
    log.info(self.toString)
    super.preStart()
  }

  def receive: Receive = {
    case ProcessSourcedVideos(videosTask) =>
      log.info("processor received a task")

      import monix.execution.Scheduler.Implicits.global
      val processTask = for {
        videos <- videosTask
      } yield combinator.combine(processors.flatMap(_.process(videos)))
      processTask.memoizeOnSuccess.runAsync.onComplete {
        case Success(processedVideos) =>
          storage ! StoreVideoMetadata(processedVideos)
        case Failure(e) =>
          log.error(e.getMessage)
          log.debug(e.getStackTrace.toString)
      }
  }
}
