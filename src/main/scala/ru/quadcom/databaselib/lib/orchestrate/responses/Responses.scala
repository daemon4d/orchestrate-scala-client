package ru.quadcom.databaselib.lib.orchestrate.responses

/**
 * Created by Dmitry on 7/7/2015.
 */

abstract class BaseResponse(requestId: String)

case class PutResponse(requestId: String, eTag: String, contentLocation: String) extends BaseResponse(requestId)
