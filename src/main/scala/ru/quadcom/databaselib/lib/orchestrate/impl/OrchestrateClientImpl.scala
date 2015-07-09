package ru.quadcom.databaselib.lib.orchestrate.impl

import java.lang.reflect.Type
import java.util.Base64

import akka.actor.ActorSystem
import com.google.gson.{InstanceCreator, GsonBuilder, Gson}
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}
import play.api.libs.ws.{DefaultWSClientConfig, WSRequestHolder, WSResponse}
import ru.quadcom.databaselib.lib.orchestrate.exceptions._
import ru.quadcom.databaselib.lib.orchestrate.impl.WSHelper._
import scaldi.{Injectable, Injector}

import scala.collection.mutable
import scala.concurrent.ExecutionContext


/**
 * Created by Dmitry on 7/6/2015.
 */
class OrchestrateClientImpl(implicit inj: Injector) extends Injectable {

  private val executionContext: ExecutionContext = inject[ActorSystem](identified by 'orchestrateClientAS).dispatcher
  private val baseURL = inject[String](identified by 'url)
  private val apiKey = inject[String](identified by 'key)
  private val auth = "Basic " + Base64.getEncoder.encodeToString((apiKey + ":").getBytes)


  private val wsClient = new NingWSClient(new AsyncHttpClientConfig.Builder(new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build()).build())

  private val gson = new GsonBuilder().registerTypeAdapter(classOf[mutable.Map[String,AnyRef]], new MapCreator()).create()

  new Gson()

  private def baseRequestUrl(collection: String, key: String): String = {
    val sb = new mutable.StringBuilder()
    sb ++= baseURL + "/" + collection
    if (key != null) {
      sb ++= "/" + key
    }
    sb.toString()
  }

  private def baseSearchRequestUrl(collection: String): String = {
    val urlBuilder = new mutable.StringBuilder(baseURL)
    urlBuilder ++= "/" + collection
    urlBuilder.toString()
  }

  def throwExceptionDependsOnStatusCode(response: WSResponse, throwMismatchAndAlreadyPresent: Boolean) = {
    val reqId = getHeader(response, Constants.OrchestrateReqIdHeader)
    val errorMessage = gson.fromJson(response.body, classOf[ErrorMessage])
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

  def baseSearchRequestHolder(collection: String, query: String, limit: Int, offset: Int): WSRequestHolder = {
    var holder = wsClient.url(baseSearchRequestUrl(collection)).withQueryString(("query", query))
    if (limit > 0)
      holder = holder.withQueryString(("limit", if (limit < 100) limit.toString else "100"))
    if (offset > 0)
      holder = holder.withQueryString(("offset", offset.toString))
    holder.withHeaders(("Authorization", auth), ("Content-Type", Constants.ContentType))
  }

}

private class MapCreator extends InstanceCreator[mutable.Map[String, Any]] {
  override def createInstance(`type`: Type): mutable.Map[String, Any] = mutable.Map.empty
}

case class ErrorMessage(message: String, code: String, details: mutable.Map[String, Any])
