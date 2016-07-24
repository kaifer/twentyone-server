package models.sql.users

import models.sql.abtracts.SqlModel
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class LinkedAccount(id: Option[Long],
                         userId: Long,
                         providerKey: String,
                         providerUserId: String,
                         createdAt: DateTime)

private[sql] object LinkedAccount extends SqlModel[LinkedAccount] {

  override val tableName = "LINKED_ACCOUNT"

  override val columnNames = Seq("ID", "USER_ID", "PROVIDER_KEY", "PROVIDER_USER_ID", "CREATED_AT")

  def apply(s: SyntaxProvider[LinkedAccount])(rs: WrappedResultSet): LinkedAccount = apply(s.resultName)(rs)

  def apply(s: ResultName[LinkedAccount])(rs: WrappedResultSet): LinkedAccount = LinkedAccount(
    id = rs.longOpt(s.id),
    userId = rs.long(s.userId),
    providerKey = rs.string(s.providerKey),
    providerUserId = rs.string(s.providerUserId),
    createdAt = rs.jodaDateTime(s.createdAt)
  )

  lazy val la = LinkedAccount.syntax("la")

  /*
    Queries
   */
  def create(userId: Long, providerKey: String, providerUserId: String)(implicit session: DBSession): Long = withSQL {
    insert.into(LinkedAccount).namedValues(column.userId -> userId, column.providerKey -> providerKey, column.providerUserId -> providerUserId)
  }.updateAndReturnGeneratedKey.apply()

  def findBy(providerKey: String, providerUserId: String)(implicit session: DBSession): Option[Long] = withSQL {
    select(la.userId).from(LinkedAccount as la)
      .where.eq(la.providerKey, providerKey)
      .and.eq(la.providerUserId, providerUserId)
  }.map {
    rs => rs.long(1)
  }.single.apply()
}
