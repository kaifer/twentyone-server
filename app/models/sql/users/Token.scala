package models.sql.users

import ds.enums.EnumHelpers._
import ds.enums.TokenType
import models.sql.abtracts.SqlModel
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 6. 2..
 */
case class Token(id: Option[Long],
                 userId: Long,
                 token: String,
                 typ: TokenType.Value,
                 expiredAt: DateTime,
                 createdAt: DateTime)

private[sql] object Token extends SqlModel[Token] {

  override val tableName = "TOKEN"

  override val columnNames = Seq("ID", "USER_ID", "TOKEN", "TYP", "EXPIRED_AT", "CREATED_AT")

  def apply(s: SyntaxProvider[Token])(rs: WrappedResultSet): Token = apply(s.resultName)(rs)

  def apply(s: ResultName[Token])(rs: WrappedResultSet): Token = Token(
    id = rs.longOpt(s.id),
    userId = rs.long(s.userId),
    token = rs.string(s.token),
    typ = rs.tokenType(s.typ),
    expiredAt = rs.jodaDateTime(s.expiredAt),
    createdAt = rs.jodaDateTime(s.createdAt)
  )

  lazy val tk = Token.syntax("tk")

  /*
    Queries
   */
  def create(userId: Long, token: String, expiredAt: DateTime)(implicit session: DBSession): Long = withSQL {
    insert.into(Token).namedValues(column.userId -> userId, column.token -> token,
      column.expiredAt -> expiredAt, column.typ -> TokenType.Normal.toString)
  }.updateAndReturnGeneratedKey.apply()

  def findBy(token: String)(implicit session: DBSession): Option[Token] = withSQL {
    select.from(Token as tk)
      .where.eq(tk.token, token)
  }.map(Token(tk)).single.apply()
}