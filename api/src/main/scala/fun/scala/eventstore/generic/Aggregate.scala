package fun.scala.eventstore.generic

case class Aggregate[A](id: AggregateId[A], version: Version, snapshot: Snapshot[A],
                        private val commandHandler: Aggregate.CommandHandler[A],
                        private val eventHandler: Aggregate.EventHandler[A])
                       (private val repository: Repository[A]) {
  val state: A = snapshot.state
  def run(command: Command[A]): Aggregate[A] = {
    val (effects, events) = commandHandler(snapshot, command)
    val versionAfterUpdate =
      events.foldLeft(Option(version))(
        (versionSoFar, event) => versionSoFar.flatMap(
          v => repository.save(id, event, v)
        )
      )
    if (versionAfterUpdate.nonEmpty) {
      effects()
      this.copy(version = versionAfterUpdate.get, snapshot = events.foldLeft(this.snapshot)(eventHandler))(this.repository)
    } else {
      this
    }
  }
  def &(command: Command[A]): Aggregate[A] = run(command)
}

object Aggregate {
  type Effect = () => Unit
  val noEffect: Effect = () => ()
  type CommandHandler[A] = (Snapshot[A], Command[A]) => (Effect, List[Event[A]])
  type EventHandler[A] = (Snapshot[A], Event[A]) => Snapshot[A]
}
