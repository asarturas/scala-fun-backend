package fun.scala.eventstore.generic

trait Version {
  def next: Version
}
