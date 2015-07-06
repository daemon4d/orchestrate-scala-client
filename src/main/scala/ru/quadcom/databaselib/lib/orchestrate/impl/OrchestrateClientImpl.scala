package ru.quadcom.databaselib.lib.orchestrate.impl

import java.util.Base64

import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.{WSRequestHolder, DefaultWSClientConfig}
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateClient
import scala.collection.mutable
import scala.collection.mutable.StringBuilder
import scala.concurrent.ExecutionContext


/**
 * Created by Dmitry on 7/6/2015.
 */
class OrchestrateClientImpl(actorSystem: ActorSystem, url: String, key: String) extends OrchestrateClient {

  private val executionContext: ExecutionContext = actorSystem.dispatcher
  private val baseURL: String = url
  private val apiKey: String = key
  private val auth = "Basic " + Base64.getEncoder.encodeToString((key + ":").getBytes)

  private val wsClient = new NingWSClient(new AsyncHttpClientConfig.Builder(new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build()).build())


  private def baseRequestUrl(collection: String, key: String): String = {
    val sb = new mutable.StringBuilder()
    sb ++= baseURL + "/" + collection
    if (key != null) {
      sb ++= "/" + key
    }
    sb.toString()
  }

  def baseRequestHolder(collection: String, key: String): WSRequestHolder = {
    val url = baseRequestUrl(collection, key)
    wsClient.url(url).withHeaders(("Authorization",auth), ("Content-Type", Constants.ContentType))
  }
}
