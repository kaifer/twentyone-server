package libs.http

import play.api.libs.json.{Json, Writes}
import play.api.mvc._
import HttpErrorCode._
import scala.concurrent.Future

/**
 * Created by kaifer on 2015. 10. 4..
 */
trait ResultsExtension { this: Results =>

  /*
    HttpCode
   */
  val UNHANDLED_ERROR = 553
  val UnHandledError = new Status(UNHANDLED_ERROR)

  /*
    Response
   */
  implicit class StatusExtension(val s: Status) {
    def toJson[T](res: T)(implicit tjs: Writes[T]): Result = s.apply(Json.toJson(res))

    def error(error: String, message: Option[String] = None): Result = s.apply(Json.toJson(Error(error, message)))

    def error(): Result = s.apply("")

    def success(code: String, message: Option[String] = None): Result = s.apply(Json.toJson(Success(code, message)))

    def success(): Result = s.apply("")
  }

  /*
    Action
   */
  case class UserRequest[+A](userId: Long, request: Request[A]) extends WrappedRequest[A](request)
  trait UserAction[-R[_]] extends ActionRefiner[R, UserRequest]

  case class AuthorizationHeaderRequest[A](name: String, token: String, request: Request[A]) extends WrappedRequest[A](request)
  object AuthorizationHeaderAction extends ActionRefiner[Request, AuthorizationHeaderRequest] {
    override protected def refine[A](request: Request[A]) = Future.successful {
      request.headers.get("Authorization") match {
        case Some(AuthorizationHeader(n, t)) => Right(AuthorizationHeaderRequest(n, t, request))
        case _ => Left(Unauthorized.error(INVALID_AUTHORIZATION_HEADER))
      }
    }
  }

  object ValidatedFormAction extends ActionBuilder[Request] with ActionFilter[Request] {
    def filter[A](request: Request[A]): Future[Option[Result]] = Future.successful {
      request.body match {
        case f: Form => f.validate match {
          case Left(reason) => Some(BadRequest.error(INVALID_FORM, Some(reason)))
          case Right(form) => None
        }
        case _ => None
      }
    }
  }
}
