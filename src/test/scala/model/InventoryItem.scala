package model

import play.api.libs.json._

/**
 * Created by Dmitry on 7/8/2015.
 */
case class InventoryItem(id: String, count: Int, acquisitionDate: String, ownerId: String)

object InventoryItem {
  val CollectionName = "inventory"

  implicit object InventoryItemReads extends Format[InventoryItem] {

    def reads(json: JsValue) = JsSuccess(InventoryItem(
      (json \ "id").as[String],
      (json \ "count").as[Int],
      (json \ "acquisitionDate").as[String],
      (json \ "ownerId").as[String]
    ))

    def writes(ts: InventoryItem) = JsObject(Seq(
      "id" -> JsString(ts.id),
      "count" -> JsNumber(ts.count),
      "acquisitionDate" -> JsString(ts.acquisitionDate),
      "ownerId" -> JsString(ts.ownerId)
    ))
  }

}
