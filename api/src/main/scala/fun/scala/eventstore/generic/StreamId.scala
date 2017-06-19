package fun.scala.eventstore.generic

case class StreamId(prefix: String, id: String) {
  override def toString: String = prefix + "-" + id
}

object StreamId {
  def apply(representation: String): Option[StreamId] = {
    if (representation.contains("-")) {
      representation.split("-", 2).toList match {
        case (prefix: String) :: (id: String) :: Nil => Some(StreamId(prefix, id))
        case _ => None
      }
    } else None
  }
}