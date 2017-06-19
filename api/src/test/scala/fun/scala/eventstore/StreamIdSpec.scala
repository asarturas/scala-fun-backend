package fun.scala.eventstore

import fun.scala.eventstore.generic.StreamId
import org.scalatest.{FlatSpec, Matchers}

class StreamIdSpec extends FlatSpec with Matchers {

  it should "return combined prefix and id as string representation" in {
    val id = StreamId("sample", "123")
    id.toString should be ("sample-123")
  }

  it should "be constructed from a string representation" in {
    StreamId("sample-321") should be (Some(StreamId("sample", "321")))
  }

}
