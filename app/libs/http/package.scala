package libs

import _root_.play.api.libs.json.Json

/**
 * Created by jaeung on 2015-09-14.
 */
package object http {

  trait Response

  trait Form {
    def validate: Either[String, Form] = Right(this)
  }

  case class Success(code: String, message: Option[String] = None) extends Response
  object Success {
    implicit val fmt = Json.writes[Success]
  }

  case class Error(error: String, message: Option[String] = None) extends Response
  object Error {
    implicit val fmt = Json.writes[Error]
  }

  object ImageContentType {
    def apply(typ: String) = "image/" + typ
    def unapply(str: String): Option[String] = {
      val parts = str split "image/"
      if (parts.length == 2) Some(parts(1)) else None
    }
  }

  object AuthorizationHeader {
    def unapply(str: String): Option[(String, String)] = {
      val parts = str split " "
      if (parts.length == 2) Some(parts(0), parts(1)) else None
    }
  }
}
