package ru.quadcom.databaselib.lib.orchestrate.impl

import akka.actor.ActorSystem
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.libs.json.Reads
import play.api.libs.ws.WSResponse
import ru.quadcom.databaselib.lib.orchestrate.responses.SearchResponse
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateSearchService
import scaldi.{Injector, Injectable}

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/8/2015.
 */
class OrchestrateSearchServiceImpl(implicit inj: Injector) extends OrchestrateSearchService with Injectable {

  private implicit val ec = inject[ActorSystem](identified by 'orchestrateClientAS).dispatcher

  private val client = inject[OrchestrateClientImpl]

  private val jsonMapper = new ObjectMapper()

  jsonMapper.registerModule(DefaultScalaModule)

  override def search[T<: AnyRef](collectionName: String, query: String)(implicit fjs: Reads[T]): Future[SearchResponse[T]] = {
    search(collectionName, query, -1, -1)
  }

  override def search[T <: AnyRef](collectionName: String, query: String, limit: Int, offset: Int)(implicit fjs: Reads[T]): Future[SearchResponse[T]] = {
    client.baseSearchRequestHolder(collectionName, query, limit, offset).get().flatMap((response: WSResponse) => {
      if (response.status != 200)
        client.throwExceptionDependsOnStatusCode(response, throwMismatchAndAlreadyPresent = true)

      val reqId = WSHelper.getHeader(response, Constants.OrchestrateReqIdHeader)
      val searchResponse = play.api.libs.json.Json.parse(response.body).as[SearchResponse[T]]
      Future {
        searchResponse.copy(requestId = reqId)
      }
    })
  }
}
