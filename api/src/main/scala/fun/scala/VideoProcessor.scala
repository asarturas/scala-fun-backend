package fun.scala

object VideoProcessor {
  def embedYoutube(url: String): String =
    url.replaceFirst("youtube\\.com\\/watch\\?v=", "youtube.com/embed/").split('&')(0) +
      "?autoplay=1&modestbranding=1&rel=0&showinfo=0&autohide=1"
  def embedVimeo(url: String): String =
    url.replaceFirst("vimeo\\.com\\/", "player.vimeo.com/video/") +
      "?autoplay=1&title=0&byline=0&portrait=0"
}
