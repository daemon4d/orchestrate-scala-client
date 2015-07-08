package ru.quadcom.databaselib.orchestrate.api

import java.io.StringWriter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import model.Player
import org.scalatest.FlatSpec
import ru.quadcom.databaselib.lib.orchestrate.responses.{SearchPath, InnerSearchResponse}

/**
 * Created by Dmitry on 7/8/2015.
 */
class GenericJsonSerialization extends FlatSpec {
  "Generic serialization " should " pass " in {
    val jsonMapper = new ObjectMapper()
    jsonMapper.registerModule(DefaultScalaModule)
    val out = new StringWriter
    jsonMapper.writeValue(out, new InnerSearchResponse[Player](new SearchPath(Player.CollectionName, "123", "123", "item", 0L), 0.0,
      new Player("player1", 35), 0L))
    val jsonVal = out.toString
    assertResult(false)(jsonVal.isEmpty)
    val searchResponse = play.api.libs.json.Json.parse(jsonVal).as[InnerSearchResponse[Player]]
    assertResult("player1")(searchResponse.value.name)
  }
}
