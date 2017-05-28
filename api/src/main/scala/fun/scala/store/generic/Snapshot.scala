package fun.scala.store.generic

case class Snapshot[A](id: AggregateId[A], state: A)