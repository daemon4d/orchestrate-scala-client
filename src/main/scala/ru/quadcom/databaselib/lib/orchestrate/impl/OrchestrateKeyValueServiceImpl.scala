package ru.quadcom.databaselib.lib.orchestrate.impl

import java.io.StringWriter

import akka.actor.ActorSystem
import akka.dispatch.Futures
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.libs.ws.WSResponse
import play.libs.Json
import ru.quadcom.databaselib.lib.orchestrate.responses.PutResponse
import ru.quadcom.databaselib.lib.orchestrate.traits.{OrchestrateClient, OrchestrateKeyValueService}

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/7/2015.
 */
class OrchestrateKeyValueServiceImpl(actorSystem: ActorSystem, client: OrchestrateClient) extends OrchestrateKeyValueService {
  private implicit val ec = actorSystem.dispatcher

  private val gsonMapper = new ObjectMapper()

  gsonMapper.registerModule(DefaultScalaModule)

  private def putWithHeaders(collectionName: String, key: String, obj: Any, eTag: String, notExist: Boolean, throwMismatchOrAlreadyPresentedException: Boolean): Future[PutResponse] = {
    var holder = client.baseRequestHolder(collectionName, key)
    if (eTag != null) {
      holder = holder.withHeaders((Constants.IfMatchHeader, eTag))
    }
    val out = new StringWriter
    gsonMapper.writeValue(out, obj)
    holder.put(out.toString).flatMap((response: WSResponse) => {
      val reqId = unpackResponseHeader(response, Constants.OrchestrateReqIdHeader)
      val eTagResp = unpackResponseHeader(response, Constants.ETagHeader)
      val location = unpackResponseHeader(response, Constants.LocationHeader)
      Future {
        PutResponse(reqId, eTagResp, location)
      }
    })
  }

  def put(collectionName: String, key: String, obj: Any): Future[PutResponse] = {
    putWithHeaders(collectionName, key, obj, null, notExist = false, throwMismatchOrAlreadyPresentedException = true)
  }

  private def unpackResponseHeader(response: WSResponse, header: String): String = {
    response.header(header) match {
      case Some(str) => str
      case None => throw new RuntimeException("Header " + header + " not found in response")
    }
  }
}
