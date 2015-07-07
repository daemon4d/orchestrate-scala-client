package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class OperationTimeOutOrchestrateRuntimeException(_requestId: String) extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "operation_timed_out"
  override val description: String = "Operation timed out."
  override val statusCode: Int = 500
}
