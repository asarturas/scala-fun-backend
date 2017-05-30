package fun.scala.store.generic

case class StreamId(prefix: String, id: String) {
  override def toString: String = prefix + "-" + id
}