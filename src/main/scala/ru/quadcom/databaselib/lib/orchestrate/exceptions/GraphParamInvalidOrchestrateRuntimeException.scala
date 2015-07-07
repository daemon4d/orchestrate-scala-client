package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class GraphParamInvalidOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "graph_param_invalid"
  override val description: String = "A provided graph query param is invalid."
  override val statusCode: Int = 400
}
