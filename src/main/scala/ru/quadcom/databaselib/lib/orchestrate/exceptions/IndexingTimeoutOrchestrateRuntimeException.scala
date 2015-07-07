package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class IndexingTimeoutOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "indexing_timeout"
  override val description: String = "The item has been stored but indexing timed out."
  override val statusCode: Int = 500
}
