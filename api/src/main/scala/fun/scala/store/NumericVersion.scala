package fun.scala.store

import fun.scala.store.generic.{Factory, Version}

case class NumericVersion(version: Int = 0) extends Version {
  def next: Version = NumericVersion(version + 1)
}

trait NumericFactory[A] extends Factory[A] {
  val versionZero: Version = NumericVersion()
}

object NumericVersion {
  implicit def Zero = NumericVersion()
}