package fun.scala.store.es
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import eventstore.EventNumber.Last
import eventstore.TransactionActor.{Commit, GetTransactionId, Start, Write}
import eventstore.tcp.ConnectionActor
import eventstore.{EventData, EventStoreExtension, EventStream, ReadStreamEvents, ReadStreamEventsCompleted, TransactionActor, TransactionStart, WriteEvents, WriteEventsCompleted}
import io.circe.parser.decode
import fun.scala.store.generic.{Event, NumericVersion, StreamId, Version}
import fun.scala.store.video.Events.UpdatedMetadata
import fun.scala.store.video.Video

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class EventStore extends fun.scala.store.generic.EventStore[Video] {

  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val materializer = ActorMaterializer()
  private val connection = EventStoreExtension(system).connection

  def allStreamIds: Iterable[StreamId] = {
    val publisher = connection.streamPublisher(EventStream.System.`$streams`, infinite = false, resolveLinkTos = true)
    val x: Future[List[String]] = Source.fromPublisher(publisher).runFold(List.empty[String])((a, b) => a :+ b.streamId.streamId)
    var r = Await.result(x, 100.second).flatMap(StreamId(_))
    println("fawefawefwaef")
    println(r)
    r
  }

  def eventsOf(id: StreamId): Option[(List[Event[Video]], Version)] = {
    val stream = EventStream.Id(id.toString)
    try {
      val readEvent: Future[ReadStreamEventsCompleted] = connection.apply(ReadStreamEvents(stream))
      val esEvents = Await.result(readEvent, 1.second)
      val events = esEvents.events.map { e =>
        e.data.eventType match {
          case "update-metadata" =>
            decode[UpdatedMetadata](e.data.data.value.utf8String)
          case _ =>
            Left("error matching event " + e.data.data.value.utf8String)
        }
      }.flatMap {
        case Right(result) =>
          Some(result)
        case Left(e) =>
          println("error parsing" + e)
          None
      }
      Some((events, NumericVersion(esEvents.events.map(_.number.value).max)))
    } catch {
      case e =>
        println("error happened ev of" + e)
        None
    }
  }

  def createStream(id: StreamId, version: Version, events: List[Event[Video]]): Boolean = {
//    val stream = EventStream.Id(id.toString)
//    try {
//      val wri: Future[ReadStreamEventsCompleted] = connection.apply(ReadStreamEvents(stream, fromNumber = Last))
//      val esEvents = Await.result(readEvent, 1.second)
//      Some(NumericVersion(esEvents.events.map(_.number.value).head))
//    } catch {
//      case e =>
//        println("error happened " + e)
//        None
//    }
    appendEventsTo(id, version, events)
  }

  def appendEventsTo(id: StreamId, expectedVersion: Version, events: List[Event[Video]]): Boolean = {
    val kickoff = Start(TransactionStart(EventStream.Id(id.toString)))
    val connection = system.actorOf(ConnectionActor.props())
    val transaction = system.actorOf(TransactionActor.props(connection, kickoff))
    transaction ! GetTransactionId
    events.foreach(e => transaction ! Write(EventData.Json(e.typeName, data = e.asJson.noSpaces)))
    transaction ! Commit
    true
  }

  def appendEventTo(id: StreamId, event: Event[Video]): Option[Version] = {
    val stream = EventStream.Id(id.toString)
    val writeEvents: Future[WriteEventsCompleted] = connection.apply(WriteEvents(stream, List(EventData.Json(event.typeName, data = event.asJson.noSpaces))))
    Await.ready(writeEvents, 1.second)
//    writeEvents foreach { x =>
//      log.info(x.numbersRange.toString)
//    }
//
//    val kickoff = Start(TransactionStart(EventStream.Id(id.toString)))
//    val connection = system.actorOf(ConnectionActor.props())
//    val transaction = system.actorOf(TransactionActor.props(connection, kickoff))
//    transaction ! GetTransactionId
//    transaction ! Write(EventData.Json(event.typeName, data = event.asJson.noSpaces))
//    transaction ! Commit
    getVersion(id)
  }

  def matchesVersion(id: StreamId, version: Version): Boolean = {
    val stream = EventStream.Id(id.toString)
    val storedVersion = getVersion(id)
    storedVersion.isEmpty || storedVersion.get == version
  }

  def getVersion(id: StreamId): Option[Version] = {
    val stream = EventStream.Id(id.toString)
    try {
      val readEvent: Future[ReadStreamEventsCompleted] = connection.apply(ReadStreamEvents(stream, fromNumber = Last))
      val esEvents = Await.result(readEvent, 1.second)
      Some(NumericVersion(esEvents.events.map(_.number.value).head))
    } catch {
      case e =>
        println("error happened xx " + e)
        None
    }
  }
}
