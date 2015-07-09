package model

import play.api.libs.json._

/**
 * Created by Dmitry on 7/8/2015.
 */
case class InventoryItem(id: String, count: Int, acquisitionDate: String, ownerId: String)

object InventoryItem {
  val CollectionName = "inventory"
}
