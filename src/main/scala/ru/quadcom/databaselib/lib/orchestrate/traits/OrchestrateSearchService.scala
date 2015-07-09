package ru.quadcom.databaselib.lib.orchestrate.traits

import play.api.libs.json.Reads
import ru.quadcom.databaselib.lib.orchestrate.responses.SearchResponse

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/8/2015.
 */
trait OrchestrateSearchService {
  def search[T <: AnyRef](collectionName: String, query: String, tClass: Class[T], limit: Int = -1, offset: Int = -1): Future[SearchResponse[T]]
}
