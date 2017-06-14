package fun.scala.processors

import com.netaporter.uri.Uri
import fun.scala.Service.{Vimeo, Youtube}
import fun.scala.{Service, SourcedVideo, SourcedVideoMetadata}
import monix.eval.Task

class VideoIdProcessor extends Processor {
  def process(collectedVideos: List[SourcedVideo]): List[Task[Option[SourcedVideoMetadata]]] = {
    collectedVideos.map {
      case video @ SourcedVideo(url, title, service, source) =>
        Task.eval(
          videoId(service, url).map { videoId =>
            SourcedVideoMetadata(video, None, id = Some(videoId), plays = None, likes = None)
          }
        )
    }
  }

  def videoId(service: Service, url: Uri): Option[String] = {
    if (service == Youtube) youtubeVideoId(url)
    else if (service == Vimeo) vimeoVideoId(url)
    else None
  }

  private def youtubeVideoId(url: Uri): Option[String] = url.query.param("v")

  private def vimeoVideoId(url: Uri): Option[String] = {
    val videoId = url.path
    "^/[0-9]+$".r.findFirstMatchIn(videoId).map(_.toString.filterNot(_ == '/'))
  }
}
