package fun.scala.processors

import com.netaporter.uri.Uri
import fun.scala.data.{Service, SourcedVideo, SourcedVideoMetadata}
import fun.scala.data.Service.{Vimeo, Youtube}
import monix.eval.Task

import scala.concurrent.Future

class UrlProcessor extends Processor {
  def process(collectedVideos: Future[List[SourcedVideo]]): Future[List[Task[SourcedVideoMetadata]]] = {
    import monix.execution.Scheduler.Implicits.global
    for {
      videos <- collectedVideos
    } yield videos.map(
      video => Task.eval(
        SourcedVideoMetadata(video, embedUrl(video.service, video.url), id = None, plays = None, likes = None)
      )
    )
  }

  def embedUrl(service: Service, url: Uri): Option[Uri] = {
    if (service == Youtube) embedYoutubeUrl(url)
    else if (service == Vimeo) embedVimeoUrl(url)
    else Some(url)
  }

  private def embedYoutubeUrl(url: Uri): Option[Uri] = {
    import com.netaporter.uri.dsl._
    for {
      videoId <- url.query.param("v")
    } yield ("https://www.youtube.com/embed/" + videoId) ?
      ("autoplay" -> 1) & ("modestbranding" -> 1) & ("rel" -> 0) & ("showinfo" -> 0) & ("autohide" -> 1)
  }

  private def embedVimeoUrl(url: Uri): Option[Uri] = {
    import com.netaporter.uri.dsl._
    val videoId = url.path
    "^/[0-9]+$".r.findFirstMatchIn(videoId).map { _ =>
      ("https://player.vimeo.com/video" + videoId) ?
        ("autoplay" -> 1) & ("title" -> 0) & ("byline" -> 0) & ("portrait" -> 0)
    }
  }
}
