package fun.scala.store.generic

abstract class Aggregate[A](val id: AggregateId[A], val version: Version, private val init: List[Event[A]],
                            private val initialSnapshot: Snapshot[A]) {
  val events: List[Event[A]] = init
  val snapshot: Snapshot[A] = events.foldLeft(initialSnapshot)(replay)
  val state: A = snapshot.state
  def replay(snapshot: Snapshot[A], event: Event[A]): Snapshot[A]
  def run(command: Command[A]): Aggregate[A]
  def &(command: Command[A]): Aggregate[A] = run(command)
}
