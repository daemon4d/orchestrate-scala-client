package model

import play.api.libs.json._
import ru.quadcom.databaselib.lib.orchestrate.responses.SearchPath

/**
 * Created by Dmitry on 7/7/2015.
 */
case class Player(name: String, age: Int)

object Player {
  val CollectionName = "players"

  implicit object PlayerReads extends Format[Player] {
    def reads(json: JsValue) = JsSuccess(Player(
      (json \ "name").as[String],
      (json \ "age").as[Int]
    ))

    def writes(ts: Player) = JsObject(Seq(
      "name" -> JsString(ts.name),
      "age" -> JsNumber(ts.age)
    ))
  }
}
