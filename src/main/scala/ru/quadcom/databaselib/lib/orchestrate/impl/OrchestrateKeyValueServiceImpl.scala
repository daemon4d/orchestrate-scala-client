package ru.quadcom.databaselib.lib.orchestrate.impl

import java.io.StringWriter

import akka.actor.ActorSystem
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.libs.ws.WSResponse
import ru.quadcom.databaselib.lib.orchestrate.impl.WSHelper.getHeader
import ru.quadcom.databaselib.lib.orchestrate.responses.{DeleteResponse, GetResponse, PutResponse}
import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateKeyValueService
import scaldi.{Injectable, Injector}

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/7/2015.
 */
class OrchestrateKeyValueServiceImpl(implicit inj: Injector) extends OrchestrateKeyValueService with Injectable {

  private implicit val ec = inject[ActorSystem](identified by 'orchestrateClientAS).dispatcher

  private val client = inject[OrchestrateClientImpl]

  private val jsonMapper = new ObjectMapper()

  jsonMapper.registerModule(DefaultScalaModule)

  private def putWithHeaders(collectionName: String, key: String, obj: AnyRef, eTag: String, notExist: Boolean, throwMismatchOrAlreadyPresentedException: Boolean): Future[PutResponse] = {
    var holder = client.baseRequestHolder(collectionName, key)
    if (eTag != null) {
      holder = holder.withHeaders((Constants.IfMatchHeader, eTag))
    } else {
      if (notExist) {
        holder = holder.withHeaders((Constants.IfNoneMatchHeader, "\"*\""))
      }
    }
    val out = new StringWriter
    jsonMapper.writeValue(out, obj)
    holder.put(out.toString).flatMap((response: WSResponse) => {
      if (response.status != 201) {
        client.throwExceptionDependsOnStatusCode(response, throwMismatchOrAlreadyPresentedException)
      }
      val reqId = getHeader(response, Constants.OrchestrateReqIdHeader)
      val eTagResp = getHeader(response, Constants.ETagHeader)
      val location = getHeader(response, Constants.LocationHeader)
      Future {
        PutResponse(reqId, eTagResp, location)
      }
    })
  }

  override def put(collectionName: String, key: String, obj: AnyRef): Future[PutResponse] = {
    putWithHeaders(collectionName, key, obj, null, notExist = false, throwMismatchOrAlreadyPresentedException = true)
  }

  override def putIfVersionTheSame(collectionName: String, key: String, obj: AnyRef, eTag: String): Future[PutResponse] = {
    putWithHeaders(collectionName, key, obj, eTag, notExist = false, throwMismatchOrAlreadyPresentedException = true)
  }

  override def putIfNotExist(collectionName: String, key: String, obj: AnyRef): Future[PutResponse] = {
    putWithHeaders(collectionName, key, obj, null, notExist = true, throwMismatchOrAlreadyPresentedException = true)
  }

  override def get[T <: AnyRef](collectionName: String, key: String, tClass: Class[T]): Future[GetResponse[T]] = {
    client.baseRequestHolder(collectionName, key).get().flatMap((response: WSResponse) => {
      Future {
        if (response.status != 200)
          client.throwExceptionDependsOnStatusCode(response, throwMismatchAndAlreadyPresent = true)
        val reqId = getHeader(response, Constants.OrchestrateReqIdHeader)
        val eTag = getHeader(response, Constants.ETagHeader)
        val location = getHeader(response, Constants.ContentLocationHeader)
        val objVal = jsonMapper.readValue(response.body, tClass)
        GetResponse[T](reqId, eTag, objVal, location)
      }
    })
  }

  override def delete(collectionName: String, key: String): Future[DeleteResponse] = {
    client.baseRequestHolder(collectionName, key).delete().flatMap((response: WSResponse) => {
      Future {
        if (response.status != 204)
          client.throwExceptionDependsOnStatusCode(response, throwMismatchAndAlreadyPresent = true)
        val reqId = getHeader(response, Constants.OrchestrateReqIdHeader)
        DeleteResponse(reqId)
      }
    })
  }
}
