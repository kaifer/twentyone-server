package ds.res

import libs.http.Response
import play.api.libs.json.{JsValue, Writes, Json}

/**
 * Created by kaifer on 2016. 6. 1..
 */
case class CreatedIdModelRes(id: Long) extends Response
object CreatedIdModelRes {
  implicit val fmt = Json.format[CreatedIdModelRes]
}

case class RankFallBody(body: String) extends Response
object RankFallBody {
  implicit val fmt = Json.format[RankFallBody]
}

case class RankFallPush(registration_ids: Seq[String], data: RankFallBody) extends Response
object RankFallPush {
  implicit val fmt = Json.format[RankFallPush]
}