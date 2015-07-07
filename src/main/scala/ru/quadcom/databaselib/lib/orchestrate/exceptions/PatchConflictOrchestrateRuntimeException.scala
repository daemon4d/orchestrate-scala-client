package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class PatchConflictOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "patch_conflict"
  override val description: String = "The patch could not be applied due to a potentially inconsistent state."
  override val statusCode: Int = 409
}
