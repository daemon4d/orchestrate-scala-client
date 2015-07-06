package ru.quadcom.databaselib.lib.orchestrate.traits

import play.api.libs.ws.WSRequestHolder

/**
 * Created by Dmitry on 7/6/2015.
 */
trait OrchestrateClient {

  def baseRequestHolder(collection: String, key: String): WSRequestHolder
}
