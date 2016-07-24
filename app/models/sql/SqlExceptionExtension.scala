package models.sql

/**
 * Created by kaifer on 2016. 6. 20..
 */
object SqlExceptionExtension {

  /*
    SQL ErrorCode
   */
  val INTEGRITY_CONSTRAINT_VIOLATION = 1062
  val FOREIGN_KEY_CONSTRAINT = 1452
  val OUT_OF_RANGE_VALUE = 1264


  class SqlException(message: String) extends Exception(message)

  case object NoAffectedRow extends SqlException("NoAffectedRow")

  case class IntegrityConstraintSqlException(message: String) extends Exception(message)
  case class ForeignKeyConstraintSqlException(message: String) extends Exception(message)
  case class OutOfRangeSqlException(message: String) extends Exception(message)
}
