package libs.http.security

import libs.http.ResultsExtension
import play.api.mvc.{ActionFilter, Result, Results}
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait PermissionSecured { this: Results with ResultsExtension  =>
  implicit val ec: ExecutionContext

  def getUserRoles(userId: Long): Future[Seq[String]]

  def authorize(permission: String, roles: Seq[String]): Boolean

  def permissionFilter(permission: String) = new ActionFilter[UserRequest] {
    override protected def filter[A](request: UserRequest[A]): Future[Option[Result]] = {
      getUserRoles(request.userId) map { roles =>
        if (authorize(permission, roles)) None else Some(Forbidden.error(PERMISSION_DENY))
      }
    }
  }
}
