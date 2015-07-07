package ru.quadcom.databaselib.lib.orchestrate.exceptions

/**
 * Created by Dmitry on 7/7/2015.
 */
abstract class BaseOrchestrateRuntimeException extends RuntimeException {
  val requestId: String
  val statusCode: Int
  val errorCode: String
  val description:String
}
