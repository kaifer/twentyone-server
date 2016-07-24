package models.sql.users

import ds.res.{UserRes, ScoreRes}
import models.sql.abtracts.SqlModel
import models.sql.games.UserScore
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class Friend(id: Option[Long],
                  userId: Long,
                  friendId: Long,
                  createdAt: DateTime)

private[sql] object Friend extends SqlModel[Friend] {

  override val tableName = "FRIEND"

  override val columnNames = Seq("ID", "USER_ID", "FRIEND_ID", "CREATED_AT")

  def apply(s: SyntaxProvider[Friend])(rs: WrappedResultSet): Friend = apply(s.resultName)(rs)

  def apply(s: ResultName[Friend])(rs: WrappedResultSet): Friend = Friend(
    id = rs.longOpt(s.id),
    userId = rs.long(s.userId),
    friendId = rs.long(s.friendId),
    createdAt = rs.jodaDateTime(s.createdAt)
  )

  lazy val f = Friend.syntax("f")

  private val (u, us) = (User.u, UserScore.us)

  /*
    Queries
   */
  def create(userId: Long, friendId: Long)(implicit session: DBSession): Long = withSQL {
    insert.into(Friend).namedValues(column.userId -> userId, column.friendId -> friendId)
  }.updateAndReturnGeneratedKey.apply()

  def remove(userId: Long, friendId: Long)(implicit session: DBSession): Int = withSQL {
    delete.from(Friend)
      .where.eq(column.userId, userId).and.eq(column.friendId, friendId)
  }.update.apply()

  def list(userId: Long)(implicit session: DBSession): Seq[UserRes] = withSQL {
    select(u.id, u.name, us.score, us.step).from(Friend as f)
      .innerJoin(User as u).on(f.friendId, u.id)
      .leftJoin(UserScore as us).on(f.friendId, us.userId)
      .where.eq(f.userId, userId)
  }.map { implicit row =>
    UserRes(1, 2, 3, 4)
  }.list.apply()

  def ranking(userId: Long)(implicit session: DBSession): Seq[ScoreRes] = withSQL {
    select.from(UserScore as us)
      .innerJoin(User as u).on(us.userId, u.id)
      .where.in(us.userId, select(f.id).from(Friend as f).where.eq(f.userId, userId))
  }.one(UserScore(us)).toOne(User(u)).map {
    (userScore, user) => ScoreRes(userScore.userId, user.name, userScore.score, userScore.step)
  }.list.apply()
}

