package fun.scala.pocket

import io.circe.Decoder
import io.circe.generic.semiauto._

object Pocket {
  implicit val itemDecoder: Decoder[Item] = deriveDecoder[Item]
  implicit val collectionDecoder: Decoder[Collection] = deriveDecoder[Collection]
}

case class Item(item_id: String, resolved_title: String, resolved_url: String)
case class Collection(list: Map[String, Item])