package ru.quadcom.databaselib.lib.orchestrate.impl

import java.util.Base64

import akka.actor.ActorSystem
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}
import play.api.libs.ws.{DefaultWSClientConfig, WSRequestHolder, WSResponse}
import ru.quadcom.databaselib.lib.orchestrate.exceptions._
import ru.quadcom.databaselib.lib.orchestrate.impl.WSHelper._
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateClient
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.parsing.json.JSONObject


/**
 * Created by Dmitry on 7/6/2015.
 */
class OrchestrateClientImpl(actorSystem: ActorSystem, url: String, key: String) extends OrchestrateClient {

  private val executionContext: ExecutionContext = actorSystem.dispatcher
  private val baseURL: String = url
  private val apiKey: String = key
  private val auth = "Basic " + Base64.getEncoder.encodeToString((key + ":").getBytes)
  private val jsonMapper = new ObjectMapper()

  private val wsClient = new NingWSClient(new AsyncHttpClientConfig.Builder(new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build()).build())

  jsonMapper.registerModule(DefaultScalaModule)

  private def baseRequestUrl(collection: String, key: String): String = {
    val sb = new mutable.StringBuilder()
    sb ++= baseURL + "/" + collection
    if (key != null) {
      sb ++= "/" + key
    }
    sb.toString()
  }

  def throwExceptionDependsOnStatusCode(response: WSResponse, throwMismatchAndAlreadyPresent: Boolean) = {
    val reqId = getHeader(response, Constants.OrchestrateReqIdHeader)
    val errorMessage = jsonMapper.readValue(response.body, classOf[ErrorMessage])
    val statusAndErrCode = (response.status, errorMessage.code)
    statusAndErrCode match {
      case (400, "api_bad_request") => throw new ApiBadRequestOrchestrateRuntimeException(reqId)
      case (400, "search_param_invalid") => throw new SearchParamInvalidOrchestrateRuntimeException(reqId)
      case (400, "search_query_malformed") => throw new SearchQueryMalformedOrchestrateRuntimeException(reqId)
      case (400, "item_ref_malformed") => throw new ItemRefMalformedOrchestrateRuntimeException(reqId)
      case (400, "event_param_invalid") => throw new EventParamInvalidOrchestrateRuntimeException(reqId)
      case (400, "graph_param_invalid") => throw new GraphParamInvalidOrchestrateRuntimeException(reqId)
      case (401, "security_unauthorized") => throw new SecurityUnauthorizedOrchestrateRuntimeException(reqId)
      case (404, "items_not_found") => throw new ItemsNotFoundOrchestrateRuntimeException(reqId)
      case (409, "indexing_conflict") => throw new IndexingConflictOrchestrateRuntimeException(reqId)
      case (409, "patch_conflict") => throw new PatchConflictOrchestrateRuntimeException(reqId)
      case (412, "item_version_mismatch") => if (throwMismatchAndAlreadyPresent) throw new ItemVersionMismatchOrchestrateRuntimeException(reqId)
      case (412, "item_already_present") => if (throwMismatchAndAlreadyPresent) throw new ItemAlreadyPresentOrchestrateRuntimeException(reqId)
      case (500, "security_authentication") => throw new SecurityAuthenticationOrchestrateRuntimeException(reqId)
      case (500, "search_index_not_found") => throw new SearchIndexNotFoundOrchestrateRuntimeException(reqId)
      case (500, "indexing_timeout") => throw new IndexingTimeoutOrchestrateRuntimeException(reqId)
      case (500, "operation_timed_out") => throw new OperationTimeOutOrchestrateRuntimeException(reqId)
      case (500, "internal_error") => throw new InternalErrorOrchestrateRuntimeException(reqId)
      case _ => throw new RuntimeException(response.body)
    }
  }

  def baseRequestHolder(collection: String, key: String): WSRequestHolder = {
    val url = baseRequestUrl(collection, key)
    wsClient.url(url).withHeaders(("Authorization", auth), ("Content-Type", Constants.ContentType))
  }
}

case class ErrorMessage(message: String, code: String, details: Map[String, Any])
