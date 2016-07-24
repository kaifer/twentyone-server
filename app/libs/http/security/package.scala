package libs.http

/**
 * Created by kaifer on 2016. 6. 3..
 */
package object security {
  val EXPIRED_TOKEN = "ExpiredToken"
  val INVALID_TOKEN = "InvalidToken"
  val INVALID_SESSION = "InvalidSession"
  val PERMISSION_DENY = "PermissionDeny"
  val EXPIRED_REFRESH_TOKEN = "ExpiredRefreshToken"
  val NOT_FOUND_REFRESH_TOKEN = "NotFoundRefreshToken"
  val INVALID_REFRESH_TOKEN = "InvalidRefreshToken"
  val EMAIL_OR_PASSWORD_WRONG = "EmailOrPasswordWrong"

  trait AuthorizedUser {
    val userId: Long
  }
}
