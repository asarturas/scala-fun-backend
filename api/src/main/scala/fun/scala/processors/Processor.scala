package fun.scala.processors

import fun.scala.{SourcedVideo, SourcedVideoMetadata}
import monix.eval.Task

trait Processor {
  def process(collectedVideos: List[SourcedVideo]): List[Task[Option[SourcedVideoMetadata]]]
}