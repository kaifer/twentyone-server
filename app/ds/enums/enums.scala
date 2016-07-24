package ds.enums

import play.api.libs.json.{JsString, JsValue, Writes, Reads}
import scalikejdbc.WrappedResultSet
import utils.EnumUtils

/**
 * Created by kaifer on 2016. 6. 2..
 */
object EnumHelpers {
  implicit class ChosehWrappedResultSet(rs: WrappedResultSet) {
    def userStatus(columnLabel: String): UserStatus.Value = UserStatus.withName(rs.get[String](columnLabel))
    def userStatus(columnIndex: Int): UserStatus.Value = UserStatus.withName(rs.get[String](columnIndex))

    def tokenType(columnLabel: String): TokenType.Value = TokenType.withName(rs.get[String](columnLabel))
    def tokenType(columnIndex: Int): TokenType.Value = TokenType.withName(rs.get[String](columnIndex))
  }
}

object UserStatus extends Enumeration {
  val Active = Value("A")
  val Banned = Value("B")

  implicit val jsonReads: Reads[Value] = EnumUtils.enumReads(UserStatus)
  implicit def jsonWrites: Writes[Value] = EnumUtils.enumWrites
}

object TokenType extends Enumeration {
  val Normal = Value("N")
  val Inactive = Value("I")

  implicit val jsonReads: Reads[Value] = EnumUtils.enumReads(TokenType)
  implicit def jsonWrites: Writes[Value] = EnumUtils.enumWrites
}
