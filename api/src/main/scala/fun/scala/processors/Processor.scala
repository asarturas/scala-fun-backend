package fun.scala.processors

import fun.scala.data.{SourcedVideo, SourcedVideoMetadata}
import monix.eval.Task

import scala.concurrent.Future

trait Processor {
  def process(collectedVideos: Future[List[SourcedVideo]]): Future[List[Task[SourcedVideoMetadata]]]
}