package ru.quadcom.databaselib.orchestrate.api

import java.io.StringWriter
import com.google.common.reflect.{TypeParameter, TypeToken}
import model.Player
import org.scalatest.FlatSpec
import ru.quadcom.databaselib.lib.orchestrate.responses.{SearchPath, InnerSearchResponse}
import com.google.gson.Gson

/**
 * Created by Dmitry on 7/8/2015.
 */
class GenericJsonSerialization extends FlatSpec {
  val gson = new Gson()

  private def jsonDeserialize[T <: AnyRef](str: String, tClass: Class[T]): InnerSearchResponse[T] = {
    val tToken = TypeToken.of(tClass)
    val innerSearchType = new TypeToken[InnerSearchResponse[T]]() {}.where(new TypeParameter[T]() {}, tToken).getType
    gson.fromJson(str, innerSearchType)
  }

  private def jsonSerialize[T <: AnyRef](obj: InnerSearchResponse[T], tClass: Class[T]): String = {
    val tToken = TypeToken.of(tClass)
    val innerSearchType = new TypeToken[InnerSearchResponse[T]]() {}.where(new TypeParameter[T]() {}, tToken).getType
    gson.toJson(obj, innerSearchType)
  }

  "Generic serialization " should " pass " in {
    val path = new SearchPath(Player.CollectionName, "123", "123", "item", 0L)
    val player = new Player("player1", 35)
    val origSearchResponse = new InnerSearchResponse[Player](path, 0.0, player, 0L)
    val jsonVal = jsonSerialize(origSearchResponse, classOf[Player])
    assertResult(false)(jsonVal.isEmpty)
    val searchResponse = jsonDeserialize(jsonVal, classOf[Player])
    assertResult(origSearchResponse)(searchResponse)
  }
}
