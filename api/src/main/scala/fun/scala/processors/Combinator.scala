package fun.scala.processors

import fun.scala.data.{SourcedVideoMetadata, VideoMetadata}
import monix.eval.Task

import scala.concurrent.Future

class Combinator {
  def combine(videoProcessing: List[Task[SourcedVideoMetadata]]): Future[Iterable[VideoMetadata]] = {
    import monix.execution.Scheduler.Implicits.global
    for {
      processedVideos <- Task.gatherUnordered(videoProcessing).runAsync
    } yield processedVideos.groupBy(_.video).map { case (sourcedVideo, listOfMetadata) =>
      listOfMetadata.fold(SourcedVideoMetadata(sourcedVideo)) { (v1: SourcedVideoMetadata, v2: SourcedVideoMetadata) =>
        SourcedVideoMetadata(
          sourcedVideo,
          v1.embedUrl.orElse(v2.embedUrl),
          v1.id.orElse(v2.id),
          sumOptions(v1.plays, v2.plays),
          sumOptions(v1.likes, v2.likes)
        )
      }
    }.map(sourced =>
      VideoMetadata(
        sourced.video,
        sourced.embedUrl.getOrElse(sourced.video.url),
        sourced.id,
        sourced.plays.getOrElse(0),
        sourced.likes.getOrElse(0)
      )
    )
  }

  private def sumOptions(options: Option[Int]*): Option[Int] =
    options.fold(None) { (a, b) =>
      if (a.isEmpty && b.isEmpty) None
      else Some(a.getOrElse(0) + b.getOrElse(0))
    }
}
