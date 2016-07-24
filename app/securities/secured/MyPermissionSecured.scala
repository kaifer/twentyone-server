package securities.secured

import libs.http.ResultsExtension
import libs.http.security.PermissionSecured
import play.api.mvc.Results
import securities.rp.RolePermissions

import scala.collection.BitSet
import scala.concurrent.Future

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait MyPermissionSecured extends PermissionSecured { this: Results with ResultsExtension =>

  def getUserRoles(userId: Long): Future[Seq[String]] = Future(Seq())

  def authorize(permission: String, roles: Seq[String]): Boolean = {
    val roleBit = roles.foldLeft(BitSet())((b, a) => b | RolePermissions.roleMap(a))
    val permissionBit = RolePermissions.permissionMap(permission)

    !(roleBit & permissionBit).isEmpty
  }
}
