package fun.scala.processors

import fun.scala.data.{SourcedVideo, SourcedVideoMetadata}

trait Processor {
  def process(collectedVideos: List[SourcedVideo]): List[SourcedVideoMetadata]
}