package libs

import _root_.play.api.libs.json.Json

/**
 * Created by kaifer on 2016. 7. 17..
 */
package object authenticate {
  /*
    providers
   */
  val PROVIDER_KEY_PASSWORD = "password"
  val PROVIDER_KEY_FACEBOOK = "facebook"
  val PROVIDER_KEY_KAKAO = "kakao"

  val AUTH_URL_FACEBOOK = "https://graph.facebook.com/me"
  val AUTH_URL_KAKAO = "https://kapi.kakao.com/v1/user/me"

  /*
    authUsers
   */
  trait AuthUser {
    def authUuid: String
//    def genderValue: Gender.Value
    def genericName: String
  }

  trait AuthError {
    def getErrorMessage: String
  }

  case class FacebookAuthUser(id: String, name: String) extends AuthUser {
//    override def genderValue = gender match {
//      case "male" => Gender.Male
//      case _ => Gender.Female
//    }
    override val authUuid = id
    override val genericName = name
  }
  object FacebookAuthUser {
    implicit val fmt = Json.reads[FacebookAuthUser]
  }


  case class FacebookAuthSubError(message: String, `type`: String, code: Int, error_subcode: Option[Int])
  object FacebookAuthSubError { implicit val fmt = Json.reads[FacebookAuthSubError] }

  case class FacebookAuthError(error: FacebookAuthSubError) extends AuthError {
    override def getErrorMessage = error.message
  }
  object FacebookAuthError {
    import FacebookAuthSubError._
    implicit val fmt = Json.reads[FacebookAuthError]
  }


  case class KakaoAuthUserSub(nickname: String, profile_image: String, thumbnail_image: String)
  object KakaoAuthUserSub { implicit val fmt = Json.reads[KakaoAuthUserSub] }

  case class KakaoAuthUser(id: Long, properties: KakaoAuthUserSub) extends AuthUser {
//    override def genderValue = Gender.None
    override val authUuid = id.toString
    override val genericName = properties.nickname
  }
  object KakaoAuthUser {
    implicit val fmt = Json.reads[KakaoAuthUser]
  }

  case class KakaoAuthError(code: Long, msg: String) extends AuthError {
    override def getErrorMessage = msg
  }
  object KakaoAuthError {
    implicit val fmt = Json.reads[KakaoAuthError]
  }

  /*
    exceptions
   */
  case class AuthException(msg: String) extends Exception(msg)
}