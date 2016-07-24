package ds.sql

import scalikejdbc.WrappedResultSet

case class UserPasswordSR(userId: Long, password: String)
object UserPasswordSR {
  def apply(userId: Int, password: Int)(implicit rs: WrappedResultSet): UserPasswordSR =
    apply(rs.long(userId), rs.string(password))
}