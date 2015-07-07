package ru.quadcom.databaselib.lib.orchestrate.traits

import play.api.libs.ws.{WSResponse, WSRequestHolder}
import ru.quadcom.databaselib.lib.orchestrate.impl.Constants
import ru.quadcom.databaselib.lib.orchestrate.impl.WSHelper.getHeader

/**
 * Created by Dmitry on 7/6/2015.
 */
trait OrchestrateClient {

  def throwExceptionDependsOnStatusCode(response: WSResponse, throwMismatchAndAlreadyPresent:Boolean)

  def baseRequestHolder(collection: String, key: String): WSRequestHolder
}
