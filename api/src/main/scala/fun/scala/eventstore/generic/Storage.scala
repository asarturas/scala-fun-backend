package fun.scala.eventstore.generic

trait Storage[A] {
  def allStreamIds: Iterable[StreamId]
  def eventsOf(id: StreamId): Option[(List[Event[A]], Version)]
  def createStream(id: StreamId, version: Version, events: List[Event[A]]): Boolean
  def appendEventsTo(id: StreamId, expectedVersion: Version, events: List[Event[A]]): Boolean
  def appendEventTo(id: StreamId, event: Event[A]): Option[Version]
  def getVersion(id: StreamId): Option[Version]
  def matchesVersion(id: StreamId, version: Version): Boolean = getVersion(id).contains(version)
  def streamExists(id: StreamId): Boolean = this.getVersion(id).nonEmpty
}
