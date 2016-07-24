package securities.secured

import libs.http.ResultsExtension
import libs.http.security.{AuthorizedUser, TokenSecured}
import models.sql.DAL
import models.sql.users.Token
import org.joda.time.DateTime
import play.api.mvc.Results

import scala.concurrent.Future

/**
 * Created by kaifer on 2016. 6. 3..
 */
trait MyTokenSecured extends TokenSecured { this: Results with ResultsExtension =>

  val dal: DAL

  def getByToken(token: String): Future[Option[Token]] = dal.lookup { implicit session =>
    dal.Token.findBy(token)
  }

  def getAuthorizedUserBy[A](request: AuthorizationHeaderRequest[A]): Future[Option[AuthorizedUser]] = {
    getByToken(request.token) map {
      case Some(t) =>
        val expires = t.expiredAt.getMillis - DateTime.now.getMillis
        if (expires > 0) {
          Some(MyAuthUser(t.userId))
        } else {
          None
        }
      case None => None
    }
  }
}
