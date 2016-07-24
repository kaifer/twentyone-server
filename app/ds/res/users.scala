package ds.res

import java.sql.Timestamp

import libs.http.Response
import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet
import utils.TimeUtils._

/**
 * Created by kaifer on 2016. 6. 1..
 */
case class TokenRes(userId: Long, token: String, expiredAt: Timestamp) extends Response
object TokenRes {
  implicit val fmt = Json.format[TokenRes]
}

case class UserRes(id: Long, name: String, score: Option[Long], step: Option[Long]) extends Response
object UserRes {
  implicit val fmt = Json.format[UserRes]

  def apply(id: Int, name: Int, score: Int, step: Int)(implicit rs: WrappedResultSet): UserRes =
    apply(rs.long(id), rs.string(name), rs.longOpt(score), rs.longOpt(step))
}

case class UserDetailsRes(id: Long, name: String,
                          score: Option[Long], step: Option[Long],
                          installedAt: Timestamp,
                          connectedAt: Option[Timestamp],
                          exitedAt: Option[Timestamp],
                          lastPlayedAt: Option[Timestamp]) extends Response
object UserDetailsRes {
  implicit val fmt = Json.format[UserDetailsRes]

  def apply(id: Int, name: Int, score: Int, step: Int,
            installedAt: Int, connectedAt: Int, exitedAt: Int, lastPlayedAt: Int)(implicit rs: WrappedResultSet): UserDetailsRes =
    apply(rs.long(id), rs.string(name), rs.longOpt(score), rs.longOpt(step),
         rs.timestamp(installedAt), rs.timestampOpt(connectedAt),
         rs.timestampOpt(exitedAt), rs.timestampOpt(lastPlayedAt))
}