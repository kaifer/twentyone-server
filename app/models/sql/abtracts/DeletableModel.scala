package models.sql.abtracts

import scalikejdbc._

/**
 * Created by kaifer on 2016. 6. 6..
 */
trait DeletableModel[A] extends SqlModel[A] {
  val isNotDeleted: SQLSyntax
}
