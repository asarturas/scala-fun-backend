package fun.scala.eventstore.video

import fun.scala.eventstore.generic.AggregateId

case class VideoAggregateId(idStr: String = "") extends AggregateId[Video]("video", idStr)