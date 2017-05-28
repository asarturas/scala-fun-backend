package fun.scala.store.generic

trait EventStore[A] {
  def eventsOf(id: StreamId): Option[(List[Event[A]], Version)]
  def createStream(id: StreamId, version: Version, events: List[Event[A]]): Boolean
  def appendEventsTo(id: StreamId, expectedVersion: Version, events: List[Event[A]]): Boolean
  def matchesVersion(id: StreamId, version: Version): Boolean
}
