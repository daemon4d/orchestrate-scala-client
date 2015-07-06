package ru.quadcom.databaselib.orchestrate.api

import java.util.concurrent.TimeUnit

import model.Person
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateKeyValueService

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
 * Created by Dmitry on 7/6/2015.
 */
class KeyValueServiceTests extends OrchestrateAPISpec {

  "Put " should " put object and return response " in {
    val keyValService = inject[OrchestrateKeyValueService]
    val putF = keyValService.put("persons", "me", new Person("Dmitry Sidorenko", 32))
    val result = Await.result(putF, new FiniteDuration(5, TimeUnit.SECONDS))
    val eTag = result.eTag
  }
}
