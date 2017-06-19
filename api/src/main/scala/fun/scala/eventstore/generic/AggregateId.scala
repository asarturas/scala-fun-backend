package fun.scala.eventstore.generic

import java.util.UUID

class AggregateId[A](aggregatePrefix: String, idStr: String = "") {
  val id: UUID = {
    val uuidMatch = "^([0-9a-f]{8})-?([0-9a-f]{4})-?([0-9a-f]{4})-?([0-9a-f]{4})-?([0-9a-f]{12})$".r
    idStr match {
      case uuidMatch(one, two, three, four, five) => UUID.fromString(f"$one-$two-$three-$four-$five")
      case _ => UUID.randomUUID()
    }
  }
  def toStreamId: StreamId = StreamId("video", id.toString)
}

object AggregateIdString {
  def apply(seed: String): String = UUID.nameUUIDFromBytes(seed.getBytes).toString
}