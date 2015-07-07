package ru.quadcom.databaselib.lib.orchestrate.traits

import ru.quadcom.databaselib.lib.orchestrate.responses.{GetResponse, PutResponse}

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/7/2015.
 */
trait OrchestrateKeyValueService {

  def put(collectionName: String, key: String, obj: AnyRef): Future[PutResponse]

  def putIfVersionTheSame(collectionName: String, key: String, obj: AnyRef, eTag: String): Future[PutResponse]

  def putIfNotExist(collectionName: String, key: String, obj: AnyRef): Future[PutResponse]

  def get[T <: AnyRef](collectionName: String, key: String, tClass: Class[T]): Future[GetResponse[T]]
}
