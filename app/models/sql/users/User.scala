package models.sql.users

import ds.enums.EnumHelpers._
import ds.enums.UserStatus
import ds.res.{UserDetailsRes, UserRes, ScoreRes}
import models.sql.abtracts.DeletableModel
import models.sql.devices.Device
import models.sql.games.{Game, UserScore}
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 6. 2..
 */
case class User(id: Option[Long],
                deviceId: Option[Long],
                name: String,
                status: UserStatus.Value,
                connectedAt: DateTime,
                exitedAt: DateTime,
                lastPlayedAt: DateTime,
                deletedAt: DateTime,
                updatedAt: DateTime,
                createdAt: DateTime)

private[sql] object User extends DeletableModel[User] {

  override val tableName = "USER"

  override val columnNames = Seq("ID", "DEVICE_ID", "NAME", "STATUS", "CONNECTED_AT", "EXITED_AT", "LAST_PLAYED_AT", "DELETED_AT", "UPDATED_AT", "CREATED_AT")

  def apply(s: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(s.resultName)(rs)

  def apply(s: ResultName[User])(rs: WrappedResultSet): User = User(
    id = rs.longOpt(s.id),
    deviceId = rs.longOpt(s.deviceId),
    name = rs.string(s.name),
    status = rs.userStatus(s.status),
    connectedAt = rs.jodaDateTime(s.connectedAt),
    exitedAt = rs.jodaDateTime(s.exitedAt),
    lastPlayedAt = rs.jodaDateTime(s.lastPlayedAt),
    deletedAt = rs.jodaDateTime(s.deletedAt),
    updatedAt = rs.jodaDateTime(s.updatedAt),
    createdAt = rs.jodaDateTime(s.createdAt)
  )

  def opt(m: SyntaxProvider[User])(rs: WrappedResultSet): Option[User] =
    rs.longOpt(m.resultName.id).map(_ => User(m)(rs))

  lazy val u = User.syntax("u")
  private val (us, d) = (UserScore.us, Device.d)

  val isNotDeleted = sqls.isNull(u.deletedAt)

  /*
    Queries
   */
  def create(deviceId: Long, name: String)(implicit session: DBSession): Long = withSQL {
    insert.into(User).namedValues(column.deviceId -> deviceId, column.name -> name)
  }.updateAndReturnGeneratedKey.apply()

  def search(keyword: String)(implicit session: DBSession): Seq[UserRes] = withSQL {
    select(u.id, u.name, us.score, us.step)
      .from(User as u)
      .leftJoin(UserScore as us).on(u.id, us.userId)
      .where.like(u.name, keyword)
  }.map { implicit row =>
    UserRes(1, 2, 3, 4)
  }.list.apply()

  def findDetails(id: Long)(implicit session: DBSession): Option[UserDetailsRes] = withSQL {
    select(u.id, u.name, us.score, us.step, d.createdAt, u.connectedAt, u.exitedAt, u.lastPlayedAt)
      .from(User as u)
      .innerJoin(Device as d).on(d.id, u.deviceId)
      .leftJoin(UserScore as us).on(us.userId, u.id)
      .where.eq(u.id, id)
  }.map { implicit rs =>
    UserDetailsRes(1, 2, 3, 4, 5, 6, 7, 8)
  }.single.apply()

  def findDeviceIdQuery(id: Long) = select(u.deviceId).from(User as u).where.eq(u.id, id)

  def updateLastPlayedAt(id: Long, time: Option[DateTime])(implicit session: DBSession): Int = updatedTime(id, u.lastPlayedAt, time)

  def updateConnectedAt(id: Long, time: Option[DateTime])(implicit session: DBSession): Int = updatedTime(id, u.connectedAt, time)

  def updateExitedAt(id: Long, time: Option[DateTime])(implicit session: DBSession): Int = updatedTime(id, u.exitedAt, time)

  private def updatedTime(id: Long, column: SQLSyntax, time: Option[DateTime])(implicit session: DBSession) = withSQL {
    update(User as u)
      .set(column -> time)
      .where.eq(u.id, id)
  }.update.apply().isUpdatedSingle
}