package libs.http

import java.io.File

import akka.stream.Materializer
import libs.generator.TokenGenerator
import libs.http.security.TokenSecured
import org.slf4j.{LoggerFactory, Logger}
import play.api.Configuration
import play.api.libs.Files.TemporaryFile
import play.api.mvc.BodyParsers.parse
import play.api.mvc._

import scala.concurrent.{Future, ExecutionContext}
import HttpErrorCode._

/**
 * Created by kaifer on 2016. 6. 16..
 */
trait ImageController extends Controller { this: Results with ResultsExtension with TokenSecured =>
  implicit val ec: ExecutionContext
  implicit val mat: Materializer

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val tokenGenerator: TokenGenerator

  val configuration: Configuration

  val storageConfiguration = configuration.getConfig("storage").getOrElse(Configuration.empty)

  val RESOURCE_PATH: String = storageConfiguration.getString("path").getOrElse("/tmp/") + "resource/"

  val maxSizeMB: Long = storageConfiguration.getConfig("image").getOrElse(Configuration.empty).getLong("size").getOrElse(10L)

  lazy val maxSizeTemporaryFileParse = parse.maxLength(maxSizeMB * 1024 * 1024, parse.temporaryFile)

  def deleteFile(fileName: String): Boolean = {
    val file = new File(RESOURCE_PATH + fileName)
    if(file.exists) file.delete else false
  }

  val imageAction = (Action andThen ImageAction)
  val userImageAction = (authUserAction andThen UserImageAction)

  /*
    Image Handler
   */
  case class ImageRequest[+A](image: String, request: Request[A]) extends WrappedRequest[A](request)
  case class UserImageRequest[+A](image: String, userId: Long, request: UserRequest[A]) extends WrappedRequest[A](request)

  object ImageAction extends AbstractImageAction[Request, ImageRequest] {
    override def makeRequest[A](fileName: String, request: Request[A]): Future[Either[Result, ImageRequest[A]]] =
      Future(Right(ImageRequest(fileName, request)))
  }

  object UserImageAction extends AbstractImageAction[UserRequest, UserImageRequest] {
    override def makeRequest[A](fileName: String, request: UserRequest[A]): Future[Either[Result, UserImageRequest[A]]] =
      Future(Right(UserImageRequest(fileName, request.userId, request)))
  }

  abstract class AbstractImageAction[-R[_] <: Request[_], +IR[_]] extends ActionRefiner[R, IR] {
    def makeRequest[A](fileName: String, request: R[A]): Future[Either[Result, IR[A]]]

    override protected def refine[A](request: R[A]): Future[Either[Result, IR[A]]] =
      request.contentType match {
        case Some(s) =>
          s match {
            case ImageContentType(ext) =>
              request.body match {
                case Right(body: TemporaryFile) =>
                  val encrypted = tokenGenerator.generateMD5Token(body.file.getName, 32)
                  val fileName = encrypted + "." + ext
                  body.moveTo(new File(RESOURCE_PATH + fileName))

                  makeRequest(fileName, request)
                case Left(maxSizeExceeded) => Future(Left(EntityTooLarge.error(FILE_SIZE_EXCEEDED)))
                case e =>
                  logger.debug(s"$e")
                  Future(Left(UnHandledError.error(BODY_TYPE_ERROR)))
              }

            case _ => Future(Left(UnsupportedMediaType.error(IMAGE_TYPE_ERROR)))
          }
        case None => Future(Left(UnsupportedMediaType.error(CONTENT_TYPE_ERROR)))
      }
  }
}
