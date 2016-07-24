import play.api.libs.functional.syntax._
import play.api.libs.json._

import libs.http.Response

/**
 * Created by kaifer on 2016. 6. 1..
 */
package object ds {

  object Email {
    def apply(user: String, domain: String) = user + "@" + domain

    def unapply(str: String): Option[(String, String)] = {
      val parts = str split "@"
      if (parts.length == 2) Some(parts(0), parts(1)) else None
    }
  }

  case class PageResponse[E](data: Seq[E], draw: Int = 0, totalLength: Option[Int] = None) extends Response

  object PageResponse {
    implicit def pageFormat[T: Format]: Format[PageResponse[T]] =
      ((__ \ "data").format[Seq[T]] ~
        (__ \ "draw").format[Int] ~
        (__ \ "totalLength").formatNullable[Int])(PageResponse.apply, unlift(PageResponse.unapply))
  }

}
