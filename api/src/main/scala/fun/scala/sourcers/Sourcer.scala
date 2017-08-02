package fun.scala.sourcers

import fun.scala.data.SourcedVideo
import monix.execution.CancelableFuture

trait Sourcer {
  def collect(): CancelableFuture[List[SourcedVideo]]
}

trait SourcerConfig