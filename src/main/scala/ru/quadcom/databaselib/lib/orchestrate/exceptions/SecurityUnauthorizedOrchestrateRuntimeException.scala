package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class SecurityUnauthorizedOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "security_unauthorized"
  override val description: String = "Valid credentials are required."
  override val statusCode: Int = 401
}
