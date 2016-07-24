package ds.res

import java.sql.Timestamp

import libs.http.Response
import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class ScoreRes(userId: Long, userName: String, score: Long, step: Long) extends Response
object ScoreRes {
  implicit val fmt = Json.format[ScoreRes]

  def apply(userId: Int, userName: Int, score: Int, step: Int)(implicit rs: WrappedResultSet): ScoreRes =
    apply(rs.long(userId), rs.string(userName), rs.long(score), rs.long(step))
}
