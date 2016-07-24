package libs.autheticate

import javax.inject.Inject

import libs.authenticate._
import org.slf4j.{Logger, LoggerFactory}
import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by kaifer on 2016. 7. 17..
 */
class Authenticator @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  def authenticate(providerKey: String, accessToken: String): Future[Option[AuthUser]] = providerKey match {
    case PROVIDER_KEY_FACEBOOK =>
      ws.url(AUTH_URL_FACEBOOK).withQueryString("access_token" -> accessToken).get() flatMap { r =>
        import FacebookAuthUser._
        import FacebookAuthError._
        handleAuthInfo[FacebookAuthUser, FacebookAuthError](r.json)
      }

    case PROVIDER_KEY_KAKAO =>
      ws.url(AUTH_URL_KAKAO).withHeaders(HeaderNames.AUTHORIZATION -> "Bearer %s".format(accessToken)).get() flatMap { r =>
        import KakaoAuthUser._
        import KakaoAuthError._
        handleAuthInfo[KakaoAuthUser, KakaoAuthError](r.json)
      }
  }

  private def handleAuthInfo[U, E](json: JsValue)(implicit ur : Reads[U], er : Reads[E]) = {
    logger.debug(s"${json}")

    json.asOpt[U] match {
      case None =>
        json.asOpt[E] match {
          case None => Future.failed(AuthException("UnexpectedResponse"))
          case s => Future(None)
        }
      case s => Future(s)
    }
  }

}