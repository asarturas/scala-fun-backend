package fun.scala.store.generic

trait EventStore[A] {
  def eventsOf(id: StreamId): Option[(List[Event[A]], VersionNumber)]
  def createStream(id: StreamId, events: List[Event[A]]): Boolean
  def appendEventsTo(id: StreamId, expectedVersion: VersionNumber, events: List[Event[A]]): Boolean
  def matchesVersion(id: StreamId, version: VersionNumber): Boolean
}
