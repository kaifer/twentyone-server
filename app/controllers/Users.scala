package controllers

import javax.inject.{Inject, Singleton}

import ds.PageResponse
import libs.http.ResultsExtension
import libs.rediscala.Redis
import models.sql.DAL
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import play.api.mvc.{BodyParsers, Action, Controller}
import securities.secured.MyTokenSecured

import scala.concurrent.{Future, ExecutionContext}
import libs.http.HttpErrorCode._

/**
 * Created by kaifer on 2016. 5. 31..
 */
@Singleton
class Users @Inject()(val dal: DAL)(implicit val ec: ExecutionContext)
  extends Controller with ResultsExtension with MyTokenSecured {

  val logger = LoggerFactory.getLogger(getClass)

  def connect(id: Long) = authUserAction.async { request =>
    if (id == request.userId) {
      dal.write { implicit session =>
        dal.User.updateConnectedAt(request.userId, Some(DateTime.now))
        dal.User.updateExitedAt(request.userId, None)
      } map { id =>
        NoContent
      }
    } else {
      Future(Forbidden.error(NOT_YOURS))
    }
  }

  def exit(id: Long) = authUserAction.async { request =>
    if (id == request.userId) {
      dal.write { implicit session =>
        dal.User.updateExitedAt(request.userId, Some(DateTime.now))
      } map { id =>
        NoContent
      }
    } else {
      Future(Forbidden.error(NOT_YOURS))
    }
  }

  def getScore(id: Long) = authUserAction.async { request =>
    dal.lookup { implicit session =>
      dal.UserScore.findBy(id)
    } map {
      case Some(score) => Ok.toJson(score)
      case None => NotFound.error(NOT_EXISTS)
    }
  }

  def search(keyword: String) = authUserAction.async { request =>
    dal.lookup { implicit session =>
      dal.User.search(keyword)
    } map { list =>
      Ok.toJson(PageResponse(list))
    }
  }

  def getUserDetails(id: Long) = authUserAction.async { request =>
    dal.lookup { implicit session =>
      dal.User.findDetails(id)
    } map {
      case Some(score) => Ok.toJson(score)
      case None => NotFound.error(NOT_EXISTS)
    }
  }
}
