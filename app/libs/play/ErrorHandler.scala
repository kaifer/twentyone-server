package libs.play

import libs.http.Error
import libs.http.HttpErrorCode._
import org.slf4j.LoggerFactory
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._
/**
 * Created by kaifer on 2016. 6. 3..
 */
class ErrorHandler extends HttpErrorHandler {
  val logger = LoggerFactory.getLogger(getClass)

  def onClientError(requestHeader: RequestHeader, statusCode: Int, message: String) = {
    logger.info(s"${requestHeader.method} ${requestHeader.uri} ${requestHeader.headers}\n\n" + s"\t\treturned ${statusCode} and message ${message} \n")
    Future.successful(
      Status(statusCode)(Json.toJson(Error(CLIENT_ERROR, Some(message))))
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    exception.printStackTrace
    Future.successful(
      InternalServerError(Json.toJson(Error(UNHANDLED_SERVER_ERROR, Some(exception.getMessage))))
    )
  }

}
