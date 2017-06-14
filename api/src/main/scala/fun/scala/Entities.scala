package fun.scala

import com.netaporter.uri.Uri

trait Source
object Source {
  case object Unknown extends Source
}

sealed trait Service
object Service {
  case object Youtube extends Service
  case object Vimeo extends Service
  case object Unknown extends Service
  def apply(url: String): Service =
    if (url.contains("youtube.com")) Youtube
    else if (url.contains("vimeo.com")) Vimeo
    else Unknown
}

case class SourcedVideo(url: Uri, title: String, service: Service, source: Source)

case class SourcedVideoMetadata(
  video: SourcedVideo,
  embedUrl: Option[Uri] = None,
  id: Option[String] = None,
  plays: Option[Int] = None,
  likes: Option[Int] = None
)

case class VideoMetadata(video: SourcedVideo, embedUrl: Uri, id: String, plays: Int, likes: Int)
