package models.sql.abtracts

import models.sql.SqlExceptionExtension._
import org.slf4j.LoggerFactory
import scalikejdbc._

/**
 * Created by kaifer on 2016. 6. 6..
 */
trait SqlModel[A] extends SQLSyntaxSupport[A] {

  val logger = LoggerFactory.getLogger(getClass)

  implicit class updatedChecker(affectedRowCnt: Int) {
    def isUpdatedSingle: Int = {
      if (affectedRowCnt == 1) affectedRowCnt
      else {
//        logger.error("No AffectedRow!!")
        throw NoAffectedRow
      }
    }

    def isUpdatedList: Int = {
      if (affectedRowCnt >= 1) affectedRowCnt
      else {
//        logger.error("No AffectedRow!!")
        throw NoAffectedRow
      }
    }
  }


}
