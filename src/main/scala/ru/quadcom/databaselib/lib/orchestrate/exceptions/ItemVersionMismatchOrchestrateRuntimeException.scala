package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class ItemVersionMismatchOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "item_version_mismatch"
  override val description: String = "The version of the item does not match."
  override val statusCode: Int = 412
}
