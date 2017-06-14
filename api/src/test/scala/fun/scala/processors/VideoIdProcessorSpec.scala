package fun.scala.processors

import com.netaporter.uri.Uri
import fun.scala.Service.{Vimeo, Youtube}
import fun.scala.sourcers.PocketSourcer.Pocket
import fun.scala.{SourcedVideo, SourcedVideoMetadata}
import monix.eval.Task
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class VideoIdProcessorSpec extends FlatSpec with Matchers {
  it should "extract video id from youtube url" in {
    val processor = new VideoIdProcessor()
    processor.videoId(Youtube, Uri.parse("https://www.youtube.com/watch?v=HXIJcIivMH&list=2435675432")) should
      be(Some("HXIJcIivMH"))
  }
  it should "extract video id from vimeo url" in {
    val processor = new VideoIdProcessor()
    processor.videoId(Vimeo, Uri.parse("https://vimeo.com/217847572")) should be(Some("217847572"))
  }
  it should "return none for misformatted youtube or vimeo url or url from unknown service" in {
    new UrlProcessor().embedUrl(Youtube, Uri.parse("https://www.youtube.com/watch")) should be(None)
  }
  it should "return none for vimeo url without a video id parameter" in {
    new UrlProcessor().embedUrl(Vimeo, Uri.parse("https://vimeo.com/some/random/path")) should be(None)
  }
  it should "return a list of video processing tasks when given list of collected videos" in {
    val sourcedVideo1 = SourcedVideo(Uri.parse("https://www.youtube.com/watch?v=HXIJcIivMH"), "title1", Youtube, Pocket)
    val sourcedVideo2 = SourcedVideo(Uri.parse("https://vimeo.com/217847572"), "title2", Vimeo, Pocket)
    val sourcedVideo3 = SourcedVideo(Uri.parse("random"), "title2", Youtube, Pocket)

    val collectedVideos = List(
      sourcedVideo1,
      sourcedVideo2,
      sourcedVideo3
    )
    val processingTasks = new UrlProcessor().process(collectedVideos)
    import monix.execution.Scheduler.Implicits.global
    val result = Await.result(Task.sequence(processingTasks).runAsync, 5.seconds)
    result.flatten should be(List(
      SourcedVideoMetadata(
        sourcedVideo1,
        Some(Uri.parse("https://www.youtube.com/embed/HXIJcIivMH?autoplay=1&modestbranding=1&rel=0&showinfo=0&autohide=1")),
        plays = None,
        likes = None
      ),
      SourcedVideoMetadata(
        sourcedVideo2,
        Some(Uri.parse("https://player.vimeo.com/video/217847572?autoplay=1&title=0&byline=0&portrait=0")),
        plays = None,
        likes = None
      )
    ))
  }
}
