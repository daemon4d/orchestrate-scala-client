package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class InternalErrorOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "internal_error"
  override val description: String = "Internal Error."
  override val statusCode: Int = 500
}
