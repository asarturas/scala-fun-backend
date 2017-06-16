package fun.scala.store.generic

trait EventStore[A] {
  def allStreamIds: Iterable[StreamId]
  def eventsOf(id: StreamId): Option[(List[Event[A]], Version)]
  def createStream(id: StreamId, version: Version, events: List[Event[A]]): Boolean
  def appendEventsTo(id: StreamId, expectedVersion: Version, events: List[Event[A]]): Boolean
  def appendEventTo(id: StreamId, event: Event[A]): Option[Version]
  def matchesVersion(id: StreamId, version: Version): Boolean
}
