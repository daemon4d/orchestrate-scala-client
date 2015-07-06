package ru.quadcom.databaselib.lib.orchestrate.traits

import ru.quadcom.databaselib.lib.orchestrate.responses.PutResponse

import scala.concurrent.Future

/**
 * Created by Dmitry on 7/7/2015.
 */
trait OrchestrateKeyValueService {

  def put(collectionName: String, key: String, obj: Any): Future[PutResponse]
}
