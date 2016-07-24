package libs.http.security

import play.api.mvc.Results

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait SessionPermissionSecured { this: Results with PermissionSecured with SessionSecured =>
  def sessionAuthPermissionUserAction(permission: String) = sessionAuthUserAction andThen permissionFilter(permission)
  def sessionAuthPermissionUserFormAction(permission: String) = sessionAuthUserFormAction andThen permissionFilter(permission)
}
