package models.sql.devices

import models.sql.abtracts.SqlModel
import models.sql.users.User
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class Device(id: Option[Long],
                  deviceUid: String,
                  regToken: Option[String],
                  createdAt: DateTime)

private[sql] object Device extends SqlModel[Device] {

  override val tableName = "DEVICE"

  override val columnNames = Seq("ID", "DEVICE_UID", "REG_TOKEN", "CREATED_AT")

  def apply(s: SyntaxProvider[Device])(rs: WrappedResultSet): Device = apply(s.resultName)(rs)

  def apply(s: ResultName[Device])(rs: WrappedResultSet): Device = Device(
    id = rs.longOpt(s.id),
    deviceUid = rs.string(s.deviceUid),
    regToken = rs.stringOpt(s.regToken),
    createdAt = rs.jodaDateTime(s.createdAt)
  )

  lazy val d = Device.syntax("d")

  /*
    Queries
   */
  def create(deviceUid: String)(implicit session: DBSession): Long = withSQL {
    insert.into(Device).namedValues(column.deviceUid -> deviceUid)
  }.updateAndReturnGeneratedKey.apply()

  def findBy(deviceUid: String)(implicit session: DBSession): Option[Device] = withSQL {
    select.from(Device as d).where.eq(d.deviceUid, deviceUid)
  }.map(Device(d)).single.apply()

  def findBy(userId: Long)(implicit session: DBSession): Option[Device] = withSQL {
    select.from(Device as d).where.in(d.id, User.findDeviceIdQuery(userId))
  }.map(Device(d)).single.apply()

  def setRegisterToken(userId: Long, registerToken: String)(implicit session: DBSession) = withSQL {
    update(Device as d)
      .set(d.regToken -> registerToken)
      .where.in(d.id, User.findDeviceIdQuery(userId))
  }.update.apply().isUpdatedSingle
}
