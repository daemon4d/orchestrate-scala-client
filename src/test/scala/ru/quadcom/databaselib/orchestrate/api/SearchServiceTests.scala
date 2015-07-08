package ru.quadcom.databaselib.orchestrate.api

import akka.actor.ActorSystem
import model.InventoryItem
import org.joda.time.{DateTimeZone, DateTime}
import org.scalatest.BeforeAndAfterAll
import ru.quadcom.databaselib.lib.orchestrate.traits.{OrchestrateKeyValueService, OrchestrateSearchService}
import utils.DateFormatHelper

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import ru.quadcom.databaselib.lib.orchestrate.responses.SearchResponse

/**
 * Created by Dmitry on 7/8/2015.
 */
class SearchServiceTests extends OrchestrateAPISpec with BeforeAndAfterAll {
  private val itemTemplates = ("id1", 1, "player1", 1) ::("id2", 2, "player1", 2) ::("id3", 3, "player2", 3) :: Nil

  private implicit val ec = inject[ActorSystem](identified by 'orchestrateClientAS).dispatcher

  def createTestItems(): List[InventoryItem] = itemTemplates.map((item: (String, Int, String, Int)) => {
    new InventoryItem(item._1, item._2, DateFormatHelper.dateToString(DateTime.now(DateTimeZone.UTC).plusDays(item._4)), item._3)
  })

  override def beforeAll() {
    val keyValueService = inject[OrchestrateKeyValueService]
    val putFutures = createTestItems().map((item: InventoryItem) => {
      keyValueService.put(InventoryItem.CollectionName, item.id, item)
    })
    val insertResult = Await.result(Future.sequence(putFutures), 10.seconds)
    for (result <- insertResult) {
      assertResult(true)(!result.eTag.isEmpty)
    }
    Thread sleep 5000
  }

  override def afterAll() {
    val keyValueService = inject[OrchestrateKeyValueService]
    val batchDelete = Future.sequence(itemTemplates.map((item: (String, Int, String, Int)) => {
      keyValueService.delete(InventoryItem.CollectionName, item._1)
    }))
    val delResult = Await.result(batchDelete, 10.seconds)
    assertResult(true)(batchDelete.isCompleted)
  }

  "SearchService " should " find two player1 inventory items" in {
    val searchService = inject[OrchestrateSearchService]
    val searchFuture = searchService.search[InventoryItem](InventoryItem.CollectionName, "ownerId:player1")
    val searchResult = Await.result(searchFuture, 5.seconds)
    assertResult(2)(searchResult.results.size)
    for (result <- searchResult.results)
      assertResult("player1")(result.value.ownerId)
  }

  "SearchService " should " find two items both bought before tomorrow" in {
    val searchService = inject[OrchestrateSearchService]
    val nowTime = DateTime.now(DateTimeZone.UTC)
    val query = "acquisitionDate:[" + DateFormatHelper.dateToString(nowTime.minusMinutes(1)) + " TO " + DateFormatHelper.dateToString(nowTime.plusDays(2)) + "]"
    val searchFuture = searchService.search[InventoryItem](InventoryItem.CollectionName, query)
    val searchResult = Await.result(searchFuture, 5.seconds)
    assertResult(2)(searchResult.results.size)
  }
}
