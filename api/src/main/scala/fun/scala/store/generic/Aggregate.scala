package fun.scala.store.generic

abstract class Aggregate[A](val id: AggregateId[A], val version: VersionNumber, private val init: List[Event[A]],
                           implicit private val initSnapshot: Snapshot[A]) {
  val events: List[Event[A]] = init
  val snapshot: Snapshot[A] = events.foldLeft(initSnapshot)(replay)
  val state: A = snapshot.state
  def replay(snapshot: Snapshot[A], event: Event[A]): Snapshot[A]
  def apply(command: Command[A]): Aggregate[A]
  def &(command: Command[A]): Aggregate[A] = apply(command)
}
