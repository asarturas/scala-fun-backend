package fun.scala.processors

import fun.scala.data.{SourcedVideoMetadata, VideoMetadata}

class Combinator {
  def combine(videoProcessing: List[SourcedVideoMetadata]): Iterable[VideoMetadata] = {
    videoProcessing.groupBy(_.video).map { case (sourcedVideo, listOfMetadata) =>
      listOfMetadata.fold(SourcedVideoMetadata(sourcedVideo)) { (v1, v2) =>
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
