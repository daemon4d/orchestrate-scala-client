package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
class SecurityAuthenticationOrchestrateRuntimeException(_requestId: String)
  extends BaseOrchestrateRuntimeException {
  override val requestId: String = _requestId
  override val errorCode: String = "security_authentication"
  override val description: String = "An error occurred while trying to authenticate."
  override val statusCode: Int = 500
}
