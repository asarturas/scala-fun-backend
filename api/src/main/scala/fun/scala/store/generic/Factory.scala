package fun.scala.store.generic

trait Factory[A] {
  val versionZero: Version
  val initialState: A
  val zeroAggregateId: AggregateId[A]
  def newAggregateId: AggregateId[A]
  lazy val initialSnapshot: Snapshot[A] = Snapshot[A](zeroAggregateId, initialState)
  def getAggregate(id: AggregateId[A] = zeroAggregateId, version: Version = versionZero, events: List[Event[A]] = Nil): Aggregate[A]
}
