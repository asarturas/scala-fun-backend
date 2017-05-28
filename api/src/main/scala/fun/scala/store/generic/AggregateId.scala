package fun.scala.store.generic

import java.util.UUID

trait AggregateId[A] {
  def id: UUID
  def toStreamId: StreamId
}
