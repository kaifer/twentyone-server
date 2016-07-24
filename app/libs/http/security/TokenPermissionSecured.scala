package libs.http.security

import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait TokenPermissionSecured { this: Results with PermissionSecured with TokenSecured =>
  def authPermissionUserAction(permission: String) = authUserAction andThen permissionFilter(permission)
  def authPermissionUserFormAction(permission: String) = authUserFormAction andThen permissionFilter(permission)
}
