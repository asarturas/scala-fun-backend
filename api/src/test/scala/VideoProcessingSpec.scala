import fun.scala.VideoProcessor
import org.scalatest._

class VideoProcessingSpec extends FlatSpec with Matchers {
  it should "transform ordinary youtube url to embeddable url" in {
    val url = "https://www.youtube.com/watch?v=HXIJcIivMHg&list=2435675432"
    VideoProcessor.embedYoutube(url) should
      be("https://www.youtube.com/embed/HXIJcIivMHg?autoplay=1&modestbranding=1&rel=0&showinfo=0&autohide=1")
  }
  it should "transform ordinary vimeo url to embeddable url" in {
    val url = "https://vimeo.com/217847572"
    VideoProcessor.embedVimeo(url) should
      be("https://player.vimeo.com/video/217847572?autoplay=1&title=0&byline=0&portrait=0")
  }
}
