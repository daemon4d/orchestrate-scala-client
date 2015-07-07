package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class SearchQueryMalformedOrchestrateRuntimeException(_requestId: String) extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "search_query_malformed"
  override val description: String = "The provided search query is not a valid lucene query."
  override val statusCode: Int = 400
}
