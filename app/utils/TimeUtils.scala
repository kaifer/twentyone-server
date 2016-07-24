package utils

import java.sql.{Date, Timestamp}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, JsResult, JsValue, Format}

/**
 * Created by kaifer on 2016. 6. 1..
 */
object TimeUtils {
  val ZERO = new Date(0)

  val ONLY_TIME_FORMAT = DateTimeFormat.forPattern("HH:mm:ss")

  implicit def utilDate2SqlDate(ud: java.util.Date): Date = new Date(ud.getTime)

  implicit def dateTime2Timestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)

  implicit def timestamp2DateTime(t: Timestamp): DateTime = new DateTime(t.getTime)

  implicit def dateTime2SqlDate(dt: DateTime): Date = new Date(dt.getMillis)

  implicit def str2DateTimeOnlyTime(str: String): DateTime = DateTime.parse(str, ONLY_TIME_FORMAT)

  implicit def dateTime2StrOnlyTime(dt: DateTime): String = ONLY_TIME_FORMAT.print(dt)

  implicit def long2DateTime(l: Long): DateTime = new DateTime(l)

  implicit class DateTimeExtensions(val dt: DateTime) extends AnyVal {
    def toTimestamp() = new Timestamp(dt.getMillis)

    def getOnlyDate() = new DateTime(dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth, 0, 0, 0, 0);
  }

  implicit val timestampFormat = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = Json.toJson(timestamp2DateTime(t))

    def reads(json: JsValue): JsResult[Timestamp] = Json.fromJson[DateTime](json).map(dateTime2Timestamp)
  }
}

