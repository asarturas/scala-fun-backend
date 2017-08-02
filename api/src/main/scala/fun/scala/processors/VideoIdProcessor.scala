package fun.scala.processors

import com.netaporter.uri.Uri
import fun.scala.data.{Service, SourcedVideo, SourcedVideoMetadata}
import fun.scala.data.Service.{Vimeo, Youtube}

class VideoIdProcessor extends Processor {
  def process(collectedVideos: List[SourcedVideo]): List[SourcedVideoMetadata] = {
    for {
      video <- collectedVideos
      videoId <- videoId(video.service, video.url)
    } yield SourcedVideoMetadata(video, None, id = Some(videoId), plays = None, likes = None)
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
