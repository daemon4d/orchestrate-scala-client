package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class SearchIndexNotFoundOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "search_index_not_found"
  override val description: String = "Index could not be queried for this application."
  override val statusCode: Int = 500
}
