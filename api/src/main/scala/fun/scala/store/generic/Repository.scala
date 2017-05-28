package fun.scala.store.generic

case class Repository[A](private val store: EventStore[A])
                                 (implicit private val initAggregate: (AggregateId[A], VersionNumber, List[Event[A]]) => Aggregate[A],
                                  implicit private val aggregateIdGenerator: () => AggregateId[A],
                                  implicit private val zeroVersion: VersionNumber) {
  def create: Aggregate[A] = initAggregate(aggregateIdGenerator(), zeroVersion, Nil)

  def getById(id: AggregateId[A]): Option[Aggregate[A]] = {
    store.eventsOf(id.toStreamId) match {
      case Some((events, version)) => Some(initAggregate(id, version, events))
      case None => None
    }
  }
  def save(aggregate: Aggregate[A]): Aggregate[A] = {
    val persistedVersion = persist(aggregate.id.toStreamId, aggregate.version, aggregate.events)
    if (persistedVersion == aggregate.version) {
      aggregate
    } else {
      initAggregate(aggregate.id, persistedVersion, aggregate.events)
    }
  }

  private def persist(id: StreamId, version: VersionNumber, events: List[Event[A]]): VersionNumber = {
    if (version == zeroVersion) {
      store.createStream(id, events)
      version.next
    } else if (store.appendEventsTo(id, version, events)) {
      version.next
    } else {
      version
    }
  }
}
