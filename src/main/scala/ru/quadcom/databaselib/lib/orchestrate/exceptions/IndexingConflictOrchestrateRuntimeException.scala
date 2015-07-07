package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class IndexingConflictOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "indexing_conflict"
  override val description: String = "The item has been stored but conflicts were detected when indexing. Conflicting fields have not been indexed."
  override val statusCode: Int = 409
}
