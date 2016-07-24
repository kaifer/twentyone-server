package libs.http

/**
 * Created by kaifer on 2016. 6. 20..
 */
object HttpErrorCode {
  /*
    Error Code
   */
  val CLIENT_ERROR = "ClientError"
  val NOT_YOURS = "NotYours"
  val UNHANDLED_SERVER_ERROR = "UnHandledServerError"
  val FAIL = "Fail"
  val NOT_EXISTS = "NotExists"
  val DB_EXCEPTION = "DBException"
  val VIOLATE_DATA_INTEGRITY = "ViolateDataIntegrity"
  val NOT_READY = "NotReady"
  val NOT_IMPLEMENTED_API = "NotImplementedAPI"

  /*
    Image Error
   */
  val FILE_SIZE_EXCEEDED = "FileSizeExceeded"
  val BODY_TYPE_ERROR = "BodyTypeError"
  val IMAGE_TYPE_ERROR = "ImageTypeError"
  val CONTENT_TYPE_ERROR = "ContentTypeError"

  /*
    Input Error
  */
  val INVALID_AUTHORIZATION_HEADER = "InvalidAuthorizationHeader"
  val INVALID_FORM = "InvalidForm"
}
