package securities.secured

import libs.http.ResultsExtension
import libs.http.security.{AuthorizedUser, SessionSecured}
import play.api.mvc.{Request, Results}

import scala.concurrent.Future

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait MySessionSecured extends SessionSecured { this: Results with ResultsExtension =>
  val USER_ID_KEY = "u.id"

  def getAuthorizedUserBy[A](request: Request[A]): Future[Option[AuthorizedUser]] = {
    request.session.get(USER_ID_KEY) match {
      case Some(id) => Future(Some(MyAuthUser(id.toLong)))
      case None => Future(None)
    }
  }
}
