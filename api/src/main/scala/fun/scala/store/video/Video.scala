package fun.scala.store.video

import com.netaporter.uri.Uri
import fun.scala.data.VideoMetadata

case class Video(likes: Int, plays: Int, metadata: VideoMetadata) {
  def embedUrl: Uri = metadata.embedUrl
  def title: String = metadata.video.title
  def likesOverall: Int = likes + metadata.likes
  def playsOverall: Int = plays + metadata.plays
}
