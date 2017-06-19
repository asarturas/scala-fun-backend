package fun.scala.eventstore.generic

case class Snapshot[A](id: AggregateId[A], state: A)