package ru.quadcom.databaselib.lib.orchestrate.impl

import akka.actor.ActorSystem
import com.google.common.reflect.{TypeParameter, TypeToken}
import com.google.gson.Gson
import play.api.libs.json.Reads
import play.api.libs.ws.WSResponse
import ru.quadcom.databaselib.lib.orchestrate.responses.{InnerSearchResponse, SearchResponse}
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateSearchService
import scaldi.{Injector, Injectable}

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/8/2015.
 */
class OrchestrateSearchServiceImpl(implicit inj: Injector) extends OrchestrateSearchService with Injectable {

  private implicit val ec = inject[ActorSystem](identified by 'orchestrateClientAS).dispatcher

  private val client = inject[OrchestrateClientImpl]

  private val gson = new Gson()

  override def search[T <: AnyRef](collectionName: String, query: String, tClass: Class[T], limit: Int, offset: Int): Future[SearchResponse[T]] = {
    client.baseSearchRequestHolder(collectionName, query, limit, offset).get().flatMap((response: WSResponse) => {
      if (response.status != 200)
        client.throwExceptionDependsOnStatusCode(response, throwMismatchAndAlreadyPresent = true)

      val reqId = WSHelper.getHeader(response, Constants.OrchestrateReqIdHeader)
      val searchResponse =jsonDeserialize(response.body, tClass)
      Future {
        searchResponse.copy(requestId = reqId)
      }
    })
  }

  private def jsonDeserialize[T <: AnyRef](str: String, tClass: Class[T]): SearchResponse[T] = {
    val tToken = TypeToken.of(tClass)
    val innerSearchType = new TypeToken[SearchResponse[T]]() {}.where(new TypeParameter[T]() {}, tToken).getType
    gson.fromJson(str, innerSearchType)
  }
}
