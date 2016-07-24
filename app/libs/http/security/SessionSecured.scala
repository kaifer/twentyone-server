package libs.http.security

import libs.http.ResultsExtension
import play.api.mvc.{Action, Request, Results}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait SessionSecured { this: Results with ResultsExtension =>
  implicit val ec: ExecutionContext

  def getAuthorizedUserBy[A](request: Request[A]): Future[Option[AuthorizedUser]]

  val sessionAuthUserAction = (Action andThen SessionUserAction)
  val sessionAuthUserFormAction = (Action andThen ValidatedFormAction andThen SessionUserAction)

  object SessionUserAction extends UserAction[Request] {
    override protected def refine[A](request: Request[A]) =
      getAuthorizedUserBy(request) map {
        case Some(u) => Right(UserRequest(u.userId, request))
        case None => Left(Unauthorized.error(INVALID_SESSION))
      } recover {
        case e: Exception => Left(Unauthorized.error(e.getMessage))
      }
  }
}
