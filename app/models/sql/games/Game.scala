package models.sql.games

import models.sql.abtracts.SqlModel
import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class Game(id: Option[Long],
                userId: Long,
                score: Option[Long],
                step: Option[Long],
                starteddAt: DateTime,
                endedAt: DateTime)

private[sql] object Game extends SqlModel[Game] {

  override val tableName = "GAME"

  override val columnNames = Seq("ID", "USER_ID", "SCORE", "STEP", "STARTED_AT", "ENDED_AT")

  def apply(s: SyntaxProvider[Game])(rs: WrappedResultSet): Game = apply(s.resultName)(rs)

  def apply(s: ResultName[Game])(rs: WrappedResultSet): Game = Game(
    id = rs.longOpt(s.id),
    userId = rs.long(s.userId),
    score = rs.longOpt(s.score),
    step = rs.longOpt(s.step),
    starteddAt = rs.jodaDateTime(s.starteddAt),
    endedAt = rs.jodaDateTime(s.endedAt)
  )

  lazy val g = Game.syntax("g")

  /*
    Queries
   */
  def create(userId: Long)(implicit session: DBSession): Long = withSQL {
    insert.into(Game).namedValues(column.userId -> userId)
  }.updateAndReturnGeneratedKey.apply()

  def updateScore(id: Long, userId: Long, score: Long, step: Long)(implicit session: DBSession): Int = withSQL {
    update(Game as g)
      .set(g.score -> score, g.step -> step, g.endedAt -> DateTime.now)
      .where.eq(g.id, id).and.eq(g.userId, userId).and.isNull(g.score)
  }.update.apply().isUpdatedSingle
}

