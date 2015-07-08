package ru.quadcom.databaselib.orchestrate.api

import java.util.UUID
import java.util.concurrent.TimeUnit

import model.Player
import ru.quadcom.databaselib.lib.orchestrate.exceptions._
import ru.quadcom.databaselib.lib.orchestrate.responses.SearchResponse
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateKeyValueService

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by Dmitry on 7/6/2015.
 */
class KeyValueServiceTests extends OrchestrateAPISpec {

  "Put " should " put object and return response " in {
    val keyValService = inject[OrchestrateKeyValueService]
    val putF = keyValService.put(Player.CollectionName, "me", new Player("Player1", 32))
    val result = Await.result(putF,5.seconds)
    assert(putF.isCompleted)
    assert(!result.eTag.isEmpty)
    assert(!result.requestId.isEmpty)
    assert(!result.contentLocation.isEmpty)
  }

  "Put " should " put object if version the same" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.put(Player.CollectionName, "me", new Player("Player1", 32))
    val resultFirst = Await.result(putFirst, 5.seconds)
    assert(!resultFirst.eTag.isEmpty)
    val putSecond = keyValueService.putIfVersionTheSame(Player.CollectionName, "me", new Player("Player2", 35), resultFirst.eTag)
    val resultSecond = Await.result(putSecond, 5.seconds)
    assert(putSecond.isCompleted)
    assert(!resultSecond.eTag.isEmpty)
  }

  "Put " should " generate exception if version is not same" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.put(Player.CollectionName, "me", new Player("Player1", 32))
    val resultFirst = Await.result(putFirst, 5.seconds)
    assert(putFirst.isCompleted)
    assert(!resultFirst.eTag.isEmpty)
    val putSecond = keyValueService.putIfVersionTheSame(Player.CollectionName, "me", new Player("Player2", 35), resultFirst.eTag)
    val resultSecond = Await.result(putSecond, 5.seconds)
    assert(putSecond.isCompleted)
    assert(!resultSecond.eTag.isEmpty)
    intercept[ItemVersionMismatchOrchestrateRuntimeException] {
      val putThird = keyValueService.putIfVersionTheSame(Player.CollectionName, "me", new Player("Player3", 38), resultFirst.eTag)
      val resultThird = Await.result(putThird, 5.seconds)
    }
  }

  "Put " should " generate exception if record exist with same key" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.put(Player.CollectionName, "me", new Player("Player1", 32))
    val resultFirst = Await.result(putFirst, 5.seconds)
    assert(putFirst.isCompleted)
    assert(!resultFirst.eTag.isEmpty)
    intercept[ItemAlreadyPresentOrchestrateRuntimeException] {
      val putSecond = keyValueService.putIfNotExist(Player.CollectionName, "me", new Player("Player2", 35))
      val resultSecond = Await.result(putSecond, 5.seconds)
    }
  }

  "Put " should " put record if not exist " in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFirst = keyValueService.putIfNotExist(Player.CollectionName, UUID.randomUUID().toString, new Player("Player1", 32))
    val result = Await.result(putFirst, 5.seconds)
    assert(putFirst.isCompleted)
    assert(!result.eTag.isEmpty)
  }

  "Get " should " return previous put data " in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val origPlayer = new Player("Player1", 32)
    val putFirst = keyValueService.put(Player.CollectionName, "player1", origPlayer)
    val result = Await.result(putFirst, 5.seconds)
    assert(putFirst.isCompleted)
    assert(!result.eTag.isEmpty)
    val getFuture = keyValueService.get(Player.CollectionName, "player1", classOf[Player])
    val getResult = Await.result(getFuture, 5.seconds)
    assert(getFuture.isCompleted)
    assertResult(origPlayer)(getResult.value)
  }

  "Get with invalid key " should " generate ItemsNotFoundOrchestrateRuntimeException" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    intercept[ItemsNotFoundOrchestrateRuntimeException] {
      val getFuture = keyValueService.get(Player.CollectionName, UUID.randomUUID().toString, classOf[Player])
      Await.result(getFuture, 5.seconds)
    }
  }

  "Item " should " be deleted after delete" in {
    val keyValueService = inject[OrchestrateKeyValueService]
    val uniqueKey = UUID.randomUUID().toString
    val putFuture = keyValueService.put(Player.CollectionName, uniqueKey, new Player("Player3", 36))
    val putResult = Await.result(putFuture, 5.seconds)
    assertResult(true)(putFuture.isCompleted)
    val deleteFuture = keyValueService.delete(Player.CollectionName, uniqueKey)
    val delResult = Await.result(deleteFuture, 5.seconds)
    assertResult(true)(deleteFuture.isCompleted)
    intercept[ItemsNotFoundOrchestrateRuntimeException] {
      val getFuture = keyValueService.get(Player.CollectionName, uniqueKey, classOf[Player])
      Await.result(getFuture, 5.seconds)
    }
  }
}
