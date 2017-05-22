package fun.scala

case class PostProcessVideos(videos: Map[String, Video])
case class StoreVideos(videos: Map[String, Video])
case object ReturnRandomVideo