package ru.quadcom.databaselib.lib.orchestrate.impl

import play.api.libs.ws.WSResponse

/**
 * Created by Dmitry on 7/7/2015.
 */
object WSHelper {
  def getHeader(response: WSResponse, header: String): String = {
    response.header(header) match {
      case Some(str) => str
      case None => throw new RuntimeException("Header " + header + " not found in response")
    }
  }
}
