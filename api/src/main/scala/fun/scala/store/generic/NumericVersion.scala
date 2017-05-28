package fun.scala.store.generic

case class NumericVersion(version: Int = 0) extends Version {
  def next: Version = NumericVersion(version + 1)
}
