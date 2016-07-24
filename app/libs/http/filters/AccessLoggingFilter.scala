package libs.http.filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by jaeung on 2015-10-21.
 */
class AccessLoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  val assetsUri = "/assets"
  val webjarsUri = "/webjars"
  val jsRoutesUri = "/jsroutes.js"

  val logger = Logger("access")

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime


      if (!(requestHeader.uri.startsWith(assetsUri) || requestHeader.uri.startsWith(webjarsUri) || requestHeader.uri.startsWith(jsRoutesUri))) {
        logger.info(s"${requestHeader.method} ${requestHeader.uri} ${requestHeader.headers} " + s"took ${requestTime}ms and returned ${result.header.status}")
      }
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
