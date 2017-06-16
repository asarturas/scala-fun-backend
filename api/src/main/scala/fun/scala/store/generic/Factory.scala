package fun.scala.store.generic

case class Factory[A](initialState: A, zeroAggregateId: AggregateId[A], aggregateId: (String) => AggregateId[A], commandHandler: Aggregate.CommandHandler[A], eventHandler: Aggregate.EventHandler[A]) {
  val versionZero: Version = NumericVersion()
  def newAggregateId: AggregateId[A] = aggregateId("")
  def getAggregateId(idStr: String): AggregateId[A] = aggregateId(idStr)
  def getAggregateId(streamId: StreamId): AggregateId[A] = aggregateId(streamId.id)
  lazy val initialSnapshot: Snapshot[A] = Snapshot[A](zeroAggregateId, initialState)
  def getAggregate(id: AggregateId[A] = zeroAggregateId, version: Version = versionZero, events: List[Event[A]] = Nil): (Repository[A]) => Aggregate[A] =
    Aggregate[A](id, version, events.foldLeft(initialSnapshot)(eventHandler), commandHandler, eventHandler)
}
