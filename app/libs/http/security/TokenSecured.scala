package libs.http.security

import libs.http.ResultsExtension
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait TokenSecured { this: Results with ResultsExtension =>
  implicit val ec: ExecutionContext

  def getAuthorizedUserBy[A](request: AuthorizationHeaderRequest[A]): Future[Option[AuthorizedUser]]

  val authUserAction = (Action andThen AuthorizationHeaderAction andThen TokenUserAction)
  val authUserFormAction = (Action andThen ValidatedFormAction andThen AuthorizationHeaderAction andThen TokenUserAction)

  object TokenUserAction extends UserAction[AuthorizationHeaderRequest] {
    override protected def refine[A](request: AuthorizationHeaderRequest[A]) = {
      getAuthorizedUserBy(request) map {
        case Some(au) => Right(UserRequest(au.userId, request))
        case None => Left(Unauthorized.error(INVALID_TOKEN))
      } recover {
        case e: Exception => Left(Unauthorized.error(e.getMessage))
      }
    }
  }
}
