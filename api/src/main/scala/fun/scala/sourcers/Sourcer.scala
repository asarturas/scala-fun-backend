package fun.scala.sourcers

import fun.scala.data.SourcedVideo
import monix.eval.Task

trait Sourcer {
  def collect(): Task[List[SourcedVideo]]
}

trait SourcerConfig