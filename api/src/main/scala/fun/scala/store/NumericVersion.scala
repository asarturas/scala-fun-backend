package fun.scala.store

import fun.scala.store.generic.VersionNumber

case class NumericVersion(version: Int = 0) extends VersionNumber {
  def next: VersionNumber = NumericVersion(version + 1)
}

object NumericVersion {
  implicit def Zero = NumericVersion()
}