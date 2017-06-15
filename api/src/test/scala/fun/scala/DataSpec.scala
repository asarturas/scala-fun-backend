package fun.scala

import com.netaporter.uri.Uri
import fun.scala.data.Service.{Vimeo, Youtube}
import fun.scala.data.{Source, SourcedVideo, SourcedVideoMetadata, VideoMetadata}
import fun.scala.sourcers.PocketSourcer.Pocket
import io.circe.parser.decode
import org.scalatest.{FlatSpec, Matchers}
import io.circe.syntax._

class DataSpec extends FlatSpec with Matchers {
  "sourced video" should "serialise into json" in {
    val vid = SourcedVideo(Uri.parse("http://scala.fun"), "Sample", Youtube, Pocket)

    import SourcedVideo._

    vid.asJson.noSpaces should be (
      """{"url":"http://scala.fun","title":"Sample","service":"youtube","source":"pocket"}"""
    )
  }

  it should "deserialise from json string" in {
    val json =
      """
        |{
        |  "url": "http://scala.fun",
        |  "title": "Sample",
        |  "service": "vimeo",
        |  "source": "unknown"
        |}
      """.stripMargin

    import SourcedVideo._

    decode[SourcedVideo](json) should be(
      Right(SourcedVideo(Uri.parse("http://scala.fun"), "Sample", Vimeo, Source.Unknown))
    )
  }

  "sourced video metadata" should "serialise into json" in {
    val vid = SourcedVideoMetadata(
      SourcedVideo(Uri.parse("http://scala.fun"), "Sample", Youtube, Pocket),
      Some(Uri.parse("http://scala.fun/embed")),
      Some("embedId"),
      plays = Some(10),
      likes = Some(5)
    )

    import SourcedVideoMetadata._

    vid.asJson.noSpaces should be (
      """
        |{
        |"video":{"url":"http://scala.fun","title":"Sample","service":"youtube","source":"pocket"},
        |"embed-url":"http://scala.fun/embed",
        |"id":"embedId",
        |"plays":10,
        |"likes":5
        |}""".stripMargin.filterNot(_ == '\n')
    )
  }

  it should "deserialise from json string" in {
    val json =
      """
        |{
        |  "video":{
        |    "url":"http://scala.fun",
        |    "title":"Sample",
        |    "service":"youtube",
        |    "source":"pocket"
        |  },
        |  "embed-url":"http://scala.fun/embed",
        |  "id":"embedId",
        |  "plays":10,
        |  "likes":5
        |}
        |""".stripMargin

    import SourcedVideoMetadata._

    decode[SourcedVideoMetadata](json) should be(
      Right(
        SourcedVideoMetadata(
          SourcedVideo(Uri.parse("http://scala.fun"), "Sample", Youtube, Pocket),
          Some(Uri.parse("http://scala.fun/embed")),
          Some("embedId"),
          plays = Some(10),
          likes = Some(5)
        )
      )
    )
  }

  "video metadata" should "serialise into json" in {
    val vid = VideoMetadata(
      SourcedVideo(Uri.parse("http://scala.fun"), "Sample", Youtube, Pocket),
      Uri.parse("http://scala.fun/embed"),
      Some("embedId"),
      plays = 10,
      likes = 5
    )

    import VideoMetadata._

    vid.asJson.noSpaces should be (
      """
        |{
        |"video":{"url":"http://scala.fun","title":"Sample","service":"youtube","source":"pocket"},
        |"embed-url":"http://scala.fun/embed",
        |"id":"embedId",
        |"plays":10,
        |"likes":5
        |}""".stripMargin.filterNot(_ == '\n')
    )
  }

  it should "deserialise from json string" in {
    val json =
      """
        |{
        |  "video":{
        |    "url":"http://scala.fun",
        |    "title":"Sample",
        |    "service":"youtube",
        |    "source":"pocket"
        |  },
        |  "embed-url":"http://scala.fun/embed",
        |  "id":"embedId",
        |  "plays":10,
        |  "likes":5
        |}
        |""".stripMargin

    import VideoMetadata._

    decode[VideoMetadata](json) should be(
      Right(
        VideoMetadata(
          SourcedVideo(Uri.parse("http://scala.fun"), "Sample", Youtube, Pocket),
          Uri.parse("http://scala.fun/embed"),
          Some("embedId"),
          plays = 10,
          likes = 5
        )
      )
    )
  }
}
