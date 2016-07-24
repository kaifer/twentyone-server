package models.sql.abtracts

import scalikejdbc._

/**
 * Created by kaifer on 2016. 6. 6..
 */
trait PaginationModel {
  def getOffset(page: Int, limit: Int) = (page - 1) * limit

  implicit class condPager[A](sqlBuilder: ConditionSQLBuilder[A]) {
    def pagination(target: SQLSyntax, page: Int, limit: Int): PagingSQLBuilder[A] =
      sqlBuilder.orderBy(target).desc.limit(limit).offset(getOffset(page, limit))
  }

  implicit class groupPager[A](sqlBuilder: GroupBySQLBuilder[A]) {
    def pagination(target: SQLSyntax, page: Int, limit: Int): PagingSQLBuilder[A] =
      sqlBuilder.orderBy(target).desc.limit(limit).offset(getOffset(page, limit))
  }
}
