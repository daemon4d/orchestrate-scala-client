package ru.quadcom.databaselib.orchestrate.api

import java.util.UUID
import java.util.concurrent.TimeUnit

import model.Player
import ru.quadcom.databaselib.lib.orchestrate.exceptions._
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateKeyValueService

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
 * Created by Dmitry on 7/6/2015.
 */
class KeyValueServiceTests extends OrchestrateAPISpec {

  "Put " should " put object and return response " in {
    val keyValService = inject[OrchestrateKeyValueService]
    val putF = keyValService.put("players", "me", new Player("Player1", 32))
    val result = Await.result(putF, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putF.isCompleted)
    assert(!result.eTag.isEmpty)
    assert(!result.requestId.isEmpty)
    assert(!result.contentLocation.isEmpty)
  }

  "Put " should " put object if version the same" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.put("players", "me", new Player("Player1", 32))
    val resultFirst = Await.result(putFirst, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(!resultFirst.eTag.isEmpty)
    val putSecond = keyValueService.putIfVersionTheSame("players", "me", new Player("Player2", 35), resultFirst.eTag)
    val resultSecond = Await.result(putSecond, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putSecond.isCompleted)
    assert(!resultSecond.eTag.isEmpty)
  }

  "Put " should " generate exception if version is not same" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.put("players", "me", new Player("Player1", 32))
    val resultFirst = Await.result(putFirst, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putFirst.isCompleted)
    assert(!resultFirst.eTag.isEmpty)
    val putSecond = keyValueService.putIfVersionTheSame("players", "me", new Player("Player2", 35), resultFirst.eTag)
    val resultSecond = Await.result(putSecond, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putSecond.isCompleted)
    assert(!resultSecond.eTag.isEmpty)
    intercept[ItemVersionMismatchOrchestrateRuntimeException] {
      val putThird = keyValueService.putIfVersionTheSame("players", "me", new Player("Player3", 38), resultFirst.eTag)
      val resultThird = Await.result(putThird, new FiniteDuration(5, TimeUnit.SECONDS))
    }
  }

  "Put " should " generate exception if record exist with same key" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.put("players", "me", new Player("Player1", 32))
    val resultFirst = Await.result(putFirst, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putFirst.isCompleted)
    assert(!resultFirst.eTag.isEmpty)
    intercept[ItemAlreadyPresentOrchestrateRuntimeException] {
      val putSecond = keyValueService.putIfNotExist("players", "me", new Player("Player2", 35))
      val resultSecond = Await.result(putSecond, new FiniteDuration(5, TimeUnit.SECONDS))
    }
  }

  "Put " should " put record if not exist " in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.putIfNotExist("players", UUID.randomUUID().toString, new Player("Player1", 32))
    val result = Await.result(putFirst, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putFirst.isCompleted)
    assert(!result.eTag.isEmpty)
  }

  "Get " should " return previous put data " in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val origPlayer = new Player("Player1", 32)
    val putFirst = keyValueService.put("players", "player1", origPlayer)
    val result = Await.result(putFirst, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(putFirst.isCompleted)
    assert(!result.eTag.isEmpty)
    val getFuture = keyValueService.get("players", "player1", classOf[Player])
    val getResult = Await.result(getFuture, new FiniteDuration(5, TimeUnit.SECONDS))
    assert(getFuture.isCompleted)
    assertResult(origPlayer)(getResult.value)
  }

  "Get with invalid key " should " generate ItemsNotFoundOrchestrateRuntimeException" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    intercept[ItemsNotFoundOrchestrateRuntimeException] {
      val getFuture = keyValueService.get("players", UUID.randomUUID().toString, classOf[Player])
      Await.result(getFuture, new FiniteDuration(5, TimeUnit.SECONDS))
    }
  }
}
