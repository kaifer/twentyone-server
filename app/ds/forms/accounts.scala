package ds.forms

import libs.http.Form
import play.api.libs.json.Json
import utils.RegexUtils

/**
 * Created by kaifer on 2016. 6. 1..
 */
//trait EmailForm extends Form {
//  val email: String
//  val password: String
//
//  override def validate: Either[String, Form] = {
//    if(!RegexUtils.isValidEmail(email)) Left("InvalidEmail")
//    else if (password.length < 6) Left("PasswordTooShort")
//    else super.validate
//  }
//}
//
//case class JoinForm(email: String, password: String, name: String) extends EmailForm
//object JoinForm {
//  implicit val fmt = Json.reads[JoinForm]
//}
//
//case class LoginForm(email: String, password: String) extends EmailForm
//object LoginForm {
//  implicit val fmt = Json.reads[LoginForm]
//}
case class InstallForm(deviceUid: String) extends Form
object InstallForm {
  implicit val fmt = Json.reads[InstallForm]
}

case class AuthenticateForm(accessToken: String, deviceUid: String) extends Form
object AuthenticateForm {
  implicit val fmt = Json.reads[AuthenticateForm]
}

case class RegisterForm(registerToken: String) extends Form
object RegisterForm {
  implicit val fmt = Json.reads[RegisterForm]
}