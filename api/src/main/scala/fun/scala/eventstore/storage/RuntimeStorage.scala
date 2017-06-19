package fun.scala.eventstore.storage

import fun.scala.eventstore.generic.{Event, Storage, StreamId, Version}

import scala.collection.mutable

class RuntimeStorage[A] extends Storage[A] {
  var streams: mutable.Map[StreamId, (List[Event[A]], Version)] = mutable.Map.empty
  def allStreamIds: Iterable[StreamId] = streams.keys
  def eventsOf(id: StreamId): Option[(List[Event[A]], Version)] = {
    if (streams.contains(id)) Some(streams(id))
    else None
  }
  def createStream(newId: StreamId, version: Version, events: List[Event[A]]): Boolean = {
    if (streams.contains(newId)) false
    else {
      streams(newId) = (events, version)
      true
    }
  }
  def appendEventsTo(id: StreamId, expectedVersion: Version, events: List[Event[A]]): Boolean = {
    if (streams.contains(id)) {
      if (streams(id)._2 == expectedVersion) {
        streams(id) = (streams(id)._1 ::: events, expectedVersion)
        true
      } else {
        false
      }
    } else {
      false
    }
  }
  def appendEventTo(id: StreamId, event: Event[A]): Option[Version] = {
    if (streams.contains(id)) {
      streams(id) = (streams(id)._1 :+ event, streams(id)._2.next)
      Some(streams(id)._2)
    } else {
      None
    }
  }

  def getVersion(id: StreamId): Option[Version] = {
    if (streams.contains(id)) Some(streams(id)._2)
    else None
  }
}