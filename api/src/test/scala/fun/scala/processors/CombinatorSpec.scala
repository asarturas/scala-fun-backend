package fun.scala.processors

import com.netaporter.uri.Uri
import fun.scala.data.{Service, Source, SourcedVideo, SourcedVideoMetadata, VideoMetadata}
import monix.eval.Task
import org.scalatest.{FlatSpec, Matchers}

class CombinatorSpec extends FlatSpec with Matchers {
  it should "combine processed videos" in {
    val video1 = SourcedVideo(Uri.parse("http://scala.fun"), "title1", Service.Unknown, Source.Unknown)
    val video2 = SourcedVideo(Uri.parse("http://asm.lt"), "title2", Service.Unknown, Source.Unknown)

    val processTasks = List(
      SourcedVideoMetadata(
        video1, embedUrl = Some(Uri.parse("http://scala.fun/embed")), likes = Some(2), plays = Some(7)
      ),
      SourcedVideoMetadata(
        video2, embedUrl = Some(Uri.parse("http://asm.lt/another-embed")), likes = Some(2)
      ),
      SourcedVideoMetadata(
        video1, embedUrl = None, likes = Some(3)
      )
    )
    new Combinator().combine(processTasks).toSet should be(
      Set(
        VideoMetadata(video1, embedUrl = Uri.parse("http://scala.fun/embed"), None, likes = 5, plays = 7),
        VideoMetadata(video2, embedUrl = Uri.parse("http://asm.lt/another-embed"), None, likes = 2, plays = 0)
      )
    )
  }
}
