package fun.scala.eventstore.generic

import io.circe.Json

trait Event[A] {
  def typeName: String
  def asJson: Json = Json.obj()
}

case class VersionedEvent[A](event: Event[A], version: Version)