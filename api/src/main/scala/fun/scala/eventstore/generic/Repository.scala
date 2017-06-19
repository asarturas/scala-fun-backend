package fun.scala.eventstore.generic

import scala.util.Random

case class Repository[A](private val store: Storage[A], private val factory: Factory[A]) {
  def createAs(idSeed: String): Aggregate[A] = factory.getAggregate(id = factory.getAggregateId(AggregateIdString(idSeed)))(this)

  def create: Aggregate[A] = factory.getAggregate(id = factory.newAggregateId)(this)

  def getById(id: AggregateId[A]): Option[Aggregate[A]] = {
    store.eventsOf(id.toStreamId) match {
      case Some((events, version)) =>
        Some(factory.getAggregate(id, version, events)(this))
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
  def save(id: AggregateId[A], event: Event[A], expected: Version): Option[Version] = {
    if (!store.streamExists(id.toStreamId)) {
      store.createStream(id.toStreamId, expected, List(event))
      store.getVersion(id.toStreamId)
    } else {
      if (!store.matchesVersion(id.toStreamId, expected)) {
        None
      } else {
        store.appendEventTo(id.toStreamId, event)
      }
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
