package fun.scala.store.generic

import scala.util.Random

case class Repository[A](private val store: EventStore[A], private val factory: Factory[A]) {
  def create: Aggregate[A] = factory.getAggregate(id = factory.newAggregateId)

  def getById(id: AggregateId[A]): Option[Aggregate[A]] = {
    store.eventsOf(id.toStreamId) match {
      case Some((events, version)) => Some(factory.getAggregate(id, version, events))
      case None => None
    }
  }
  def getByStreamId(id: StreamId): Option[Aggregate[A]] = {
    getById(factory.getAggregateId(id))
  }
  def getRandom: Option[Aggregate[A]] = {
    val streamIds = store.allStreamIds.toList
    getByStreamId(streamIds(Random.nextInt(streamIds.size)))
  }
  def save(aggregate: Aggregate[A]): Aggregate[A] = {
    val persistedVersion = persist(aggregate.id.toStreamId, aggregate.version, aggregate.events)
    if (persistedVersion == aggregate.version) {
      aggregate
    } else {
      factory.getAggregate(aggregate.id, persistedVersion, aggregate.events)
    }
  }

  private def persist(id: StreamId, version: Version, events: List[Event[A]]): Version = {
    if (version == factory.versionZero) {
      store.createStream(id, factory.versionZero.next, events)
      version.next
    } else if (store.appendEventsTo(id, version, events)) {
      version.next
    } else {
      version
    }
  }
}
