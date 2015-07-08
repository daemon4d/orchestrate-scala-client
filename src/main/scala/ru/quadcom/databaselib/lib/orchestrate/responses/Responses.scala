package ru.quadcom.databaselib.lib.orchestrate.responses

import play.api.libs.json._

/**
 * Created by Dmitry on 7/7/2015.
 */

abstract class BaseResponse(requestId: String)

case class PutResponse(requestId: String, eTag: String, contentLocation: String) extends BaseResponse(requestId)

case class GetResponse[T](requestId: String, eTag: String, value: T, contentLocation: String) extends BaseResponse(requestId)

case class DeleteResponse(requestId: String) extends BaseResponse(requestId)

case class SearchResponse[T <: AnyRef](requestId: String, count: Int, total_count: Int, results: List[InnerSearchResponse[T]]) extends BaseResponse(requestId)

case class SearchPath(collection: String, key: String, ref: String, kind: String, reftime: Long)

case class InnerSearchResponse[T <: AnyRef](path: SearchPath, score: Double, value: T, reftime: Long)

object SearchPath {

  implicit object SearchPathReads extends Format[SearchPath] {
    def reads(json: JsValue) = JsSuccess(SearchPath(
      (json \ "collection").as[String],
      (json \ "key").as[String],
      (json \ "ref").as[String],
      (json \ "kind").as[String],
      (json \ "reftime").as[Long]
    ))

    def writes(ts: SearchPath) = JsObject(Seq(
      "collection" -> JsString(ts.collection),
      "key" -> JsString(ts.key),
      "ref" -> JsString(ts.ref),
      "kind" -> JsString(ts.kind),
      "reftime" -> JsNumber(ts.reftime)
    ))
  }

}

object SearchResponse {
  implicit def searchResponseReads[T <: AnyRef](implicit fmt: Reads[T]): Reads[SearchResponse[T]] = new Reads[SearchResponse[T]] {
    def reads(json: JsValue): JsResult[SearchResponse[T]] = JsSuccess(new SearchResponse[T](
      (json \ "requestId") match {
        case JsString(str) => str
        case _ => ""
      },
      (json \ "count").as[Int],
      (json \ "total_count").as[Int],
      (json \ "results") match {
        case JsArray(ts) => ts.map(t => t.as[InnerSearchResponse[T]]).toList
        case _ => throw new RuntimeException("path must be object")
      }
    )
    )
  }
}

object InnerSearchResponse {
  implicit def innerSearchResponseReads[T <: AnyRef](implicit fmt: Reads[T]): Reads[InnerSearchResponse[T]] = new Reads[InnerSearchResponse[T]] {
    def reads(json: JsValue): JsResult[InnerSearchResponse[T]] = JsSuccess(new InnerSearchResponse[T](

      (json \ "path") match {
        case JsObject(ts) => new JsObject(ts).as[SearchPath]
        case _ => throw new RuntimeException("path must be object")
      },
      (json \ "score").as[Double],
      (json \ "value") match {
        case JsObject(ts) => new JsObject(ts).as[T]
        case _ => throw new RuntimeException()
      },
      (json \ "reftime").as[Long]
    )
    )
  }
}

