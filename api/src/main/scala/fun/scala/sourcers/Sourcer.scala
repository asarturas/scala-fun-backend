package fun.scala.sourcers

import fun.scala.SourcedVideo
import monix.eval.Task

trait Sourcer {
  def collect(): List[SourcedVideo]
}

trait SourcerConfig