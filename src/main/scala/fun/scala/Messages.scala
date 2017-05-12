package fun.scala

import fun.scala.pocket.{Collection, Item}

case class StoreNewVideos(videos: Collection)
case class GetRandomVideo()
case class VideoData(video: Item)