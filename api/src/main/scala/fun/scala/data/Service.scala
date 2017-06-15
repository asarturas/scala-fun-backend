package fun.scala.data

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
