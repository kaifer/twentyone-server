package securities.rp

import scala.collection.BitSet

/**
 * Created by jaeung on 2015-11-16.
 */
object RolePermissions {
  lazy val permissionMap = getPermissionMap()
  lazy val roleMap = getRoleMap()
  
  private def getPermissionMap() = {
    import Permissions._

    val master = RPTree(Master)
    val root = RPTree(Root, List(master))

    val permissions = root.toBitSetTree[String]()

    permissions.toMap[String](permissions)
  }

  private def getRoleMap() = {

    val master = permissionMap(Permissions.Master)
    val root = master

    Map(Roles.Root -> root)
  }
}
