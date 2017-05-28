package fun.scala.store

import fun.scala.store.generic.{AggregateId, Event, EventStore, StreamId, VersionNumber}

import scala.collection.mutable

class RuntimeEventStore[A](idGenerator: () => AggregateId[A], zeroVersion: VersionNumber) extends EventStore[A] {
  var streams: mutable.Map[StreamId, (List[Event[A]], VersionNumber)] = mutable.Map.empty
  def eventsOf(id: StreamId): Option[(List[Event[A]], VersionNumber)] = {
    if (streams.contains(id)) Some(streams(id))
    else None
  }
  def createStream(newId: StreamId, events: List[Event[A]]): Boolean = {
    if (streams.contains(newId)) matchesVersion(newId, zeroVersion.next)
    else {
      streams(newId) = (events, zeroVersion.next)
      true
    }
  }
  def appendEventsTo(id: StreamId, expectedVersion: VersionNumber, events: List[Event[A]]): Boolean = {
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
  def matchesVersion(id: StreamId, version: VersionNumber): Boolean = {
    streams.contains(id) && streams(id)._2 == version
  }
}