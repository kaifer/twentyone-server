package models.sql.games

import ds.res.ScoreRes
import models.sql.abtracts.{PaginationModel, SqlModel}
import models.sql.devices.Device
import models.sql.users.User
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class UserScore(id: Option[Long],
                     userId: Long,
                     score: Long,
                     step: Long,
                     updatedAt: DateTime)

private[sql] object UserScore extends SqlModel[UserScore] with PaginationModel {

  override val tableName = "USER_SCORE"

  override val columnNames = Seq("ID", "USER_ID", "SCORE", "STEP", "UPDATED_AT")

  def apply(s: SyntaxProvider[UserScore])(rs: WrappedResultSet): UserScore = apply(s.resultName)(rs)

  def apply(s: ResultName[UserScore])(rs: WrappedResultSet): UserScore = UserScore(
    id = rs.longOpt(s.id),
    userId = rs.long(s.userId),
    score = rs.long(s.score),
    step = rs.long(s.step),
    updatedAt = rs.jodaDateTime(s.updatedAt)
  )

  def opt(m: SyntaxProvider[UserScore])(rs: WrappedResultSet): Option[UserScore] =
    rs.longOpt(m.resultName.id).map(_ => UserScore(m)(rs))

  lazy val us = UserScore.syntax("us")
  private val (u, d) = (User.u, Device.d)

  /*
    Queries
   */
  def create(userId: Long, score: Long, step: Long)(implicit session: DBSession): Long = withSQL {
    insert.into(UserScore).namedValues(column.userId -> userId, column.score -> score, column.step -> step)
  }.updateAndReturnGeneratedKey.apply()

  def updateScore(userId: Long, score: Long, step: Long)(implicit session: DBSession) = withSQL {
    update(UserScore as us)
      .set(us.score -> score, us.step -> step)
      .where.eq(us.userId, userId)
  }.update.apply()

  def findRankFall(userId: Long, min: Long, max: Long)(implicit session: DBSession): Seq[String] = withSQL {
    select(d.regToken)
      .from(UserScore as us)
      .innerJoin(User as u).on(u.id, us.userId)
      .innerJoin(Device as d).on(d.id, u.deviceId)
      .where.ne(us.userId, userId).and.ge(us.score, min).and.lt(us.score, max)
  }.map { rs =>
    rs.string(1)
  }.list.apply()

  def findBy(userId: Long)(implicit session: DBSession): Option[ScoreRes] = withSQL {
      select.from(UserScore as us)
        .innerJoin(User as u).on(us.userId, u.id)
        .where.eq(us.userId, userId)
    }.one(UserScore(us)).toOne(User(u)).map {
      (userScore, user) => ScoreRes(userScore.userId, user.name, userScore.score, userScore.step)
    }.single.apply()

  def ranking()(implicit session: DBSession): Seq[ScoreRes] = withSQL {
    select.from(UserScore as us)
      .innerJoin(User as u).on(us.userId, u.id)
      .where.ge(us.score, 0)
      .pagination(us.score, 1, 100)
  }.one(UserScore(us)).toOne(User(u)).map {
    (userScore, user) => ScoreRes(userScore.userId, user.name, userScore.score, userScore.step)
  }.list.apply()

}
