package fun.scala.pocket.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import fun.scala.pocket.{Collection, ParseCollectionResponse, ProcessParsedCollection}
import io.circe.parser.decode
import fun.scala.pocket.Pocket._

private object Parser {
  def withProps(target: ActorRef): Props = {
    Props(new Parser(target: ActorRef))
  }
}

private class Parser(target: ActorRef) extends Actor with ActorLogging {
  def receive: Receive = {
    case ParseCollectionResponse(response) =>
      log.info("About to parse collection response...")
      decode[Collection](response) match {
        case Left(message) => log.info("Issue parsing: " + message)
        case Right(collection) =>
          log.info("sending parsed collection to " + target.toString)
          target ! ProcessParsedCollection(collection)
      }
  }
}
