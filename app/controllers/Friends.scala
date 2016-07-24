package controllers

import javax.inject.{Inject, Singleton}

import libs.http.HttpErrorCode._
import libs.http.ResultsExtension
import ds.PageResponse
import models.sql.DAL
import org.slf4j.LoggerFactory
import play.api.mvc.Controller
import securities.secured.MyTokenSecured

import scala.concurrent.{Future, ExecutionContext}

/**
  * Created by kaifer on 2016. 5. 31..
  */
@Singleton
class Friends @Inject()(val dal: DAL)(implicit val ec: ExecutionContext)
    extends Controller with ResultsExtension with MyTokenSecured {

  val logger = LoggerFactory.getLogger(getClass)

  def list(id: Long) = authUserAction.async { request =>
    dal.lookup { implicit session => 
      dal.Friend.list(id)
    } map { list =>
      Ok.toJson(PageResponse(list))
    }
  }

  def add(id: Long) = authUserAction.async { request =>
    dal.write {
      implicit session => dal.Friend.create(request.userId, id)
    } map { _ =>
      NoContent
    }
  }

  def remove(id: Long) = authUserAction.async { request =>
    dal.write {
      implicit session => dal.Friend.remove(request.userId, id)
    } map { _ =>
      NoContent
    }
  }

  def getRankings(id: Long) = authUserAction.async { request =>
    dal.lookup { implicit session =>
      dal.Friend.ranking(id)
    } map { list =>
      Ok.toJson(PageResponse(list))
    }
  }
 }
