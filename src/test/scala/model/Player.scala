package model

import play.api.libs.json._
import ru.quadcom.databaselib.lib.orchestrate.responses.SearchPath

/**
 * Created by Dmitry on 7/7/2015.
 */
case class Player(name: String, age: Int)

object Player {
  val CollectionName = "players"
}
