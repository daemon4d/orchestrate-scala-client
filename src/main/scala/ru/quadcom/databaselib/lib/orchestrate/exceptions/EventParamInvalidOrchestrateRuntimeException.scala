package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class EventParamInvalidOrchestrateRuntimeException(_requestId: String) extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "event_param_invalid"
  override val description: String = "A provided event param is invalid."
  override val statusCode: Int = 400
}
