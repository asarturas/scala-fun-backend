package fun.scala.store.generic

import io.circe.Json

trait Event[A] {
  def typeName: String
  def asJson: Json = Json.obj()
}