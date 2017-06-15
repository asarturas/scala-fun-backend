package fun.scala.store.video

import fun.scala.store.generic.AggregateId

case class VideoAggregateId(idStr: String = "") extends AggregateId[Video]("video", idStr)