package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class ItemRefMalformedOrchestrateRuntimeException(_requestId: String) extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "item_ref_malformed"
  override val description: String = "The provided Item Ref is malformed."
  override val statusCode: Int = 400
}
