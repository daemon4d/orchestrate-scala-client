package ru.quadcom.databaselib.lib.orchestrate.responses

import play.api.libs.json._

/**
 * Created by Dmitry on 7/7/2015.
 */

abstract class BaseResponse(requestId: String)

case class PutResponse(requestId: String, eTag: String, contentLocation: String) extends BaseResponse(requestId)

case class GetResponse[T](requestId: String, eTag: String, value: T, contentLocation: String) extends BaseResponse(requestId)

case class DeleteResponse(requestId: String) extends BaseResponse(requestId)

case class SearchResponse[T <: AnyRef](requestId: String, count: Int, total_count: Int, results: Array[InnerSearchResponse[T]], next: String) extends BaseResponse(requestId)

case class SearchPath(collection: String, key: String, ref: String, kind: String, reftime: Long)

case class InnerSearchResponse[T <: AnyRef](path: SearchPath, score: Double, value: T, reftime: Long)