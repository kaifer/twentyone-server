package controllers

import javax.inject.{Inject, Singleton}

import ds.PageResponse
import ds.forms.EndGameForm
import ds.res.{CreatedIdModelRes, RankFallBody, RankFallPush}
import libs.http.HttpErrorCode._
import libs.http.ResultsExtension
import libs.rediscala.Redis
import models.sql.DAL
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{BodyParsers, Controller}
import securities.secured.MyTokenSecured

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by kaifer on 2016. 5. 31..
  */
@Singleton
class Games @Inject()(val dal: DAL, ws: WSClient, configuration: Configuration)(implicit val ec: ExecutionContext)
    extends Controller with ResultsExtension with MyTokenSecured {

  val FCM_URL = "https://fcm.googleapis.com/fcm/send"
  val FCM_SERVER_KEY = configuration.getString("fcmServerKey").getOrElse("")

  val logger = LoggerFactory.getLogger(getClass)

  def start() = authUserAction.async { request =>
    dal.tx { implicit session =>
      dal.User.updateLastPlayedAt(request.userId, Some(DateTime.now))
      dal.Game.create(request.userId)
    } map { id =>
      Ok.toJson(CreatedIdModelRes(id))
    }
  }

  def end(id: Long) = authUserAction.async(BodyParsers.parse.json[EndGameForm]) { request =>
    val form = request.body

    dal.write { implicit session =>
      dal.Game.updateScore(id, request.userId, form.score, form.step)
      dal.UserScore.findBy(request.userId) match {
        case Some(us) =>
          if(us.score < form.score) {
            sendRankFallPush(dal.UserScore.findRankFall(request.userId, us.score, form.score))
            dal.UserScore.updateScore(request.userId, form.score, form.step)
          }
        case None =>
          sendRankFallPush(dal.UserScore.findRankFall(request.userId, 0, form.score))
          dal.UserScore.create(request.userId, form.score, form.step)
      }
    } map { id =>
      NoContent
    }
  }

  def getRankings() = authUserAction.async { request =>
   dal.lookup { implicit session =>
      dal.UserScore.ranking()
    } map { list =>
      Ok.toJson(PageResponse(list))
    }
  }

  private def sendRankFallPush(regTokens: Seq[String]) =
    if(!regTokens.isEmpty) {
      Future {
        ws.url(FCM_URL)
          .withHeaders(AUTHORIZATION -> "key=%s".format(FCM_SERVER_KEY), CONTENT_TYPE -> JSON)
          .post(Json.toJson(RankFallPush(regTokens, RankFallBody("good"))))
          .map { r =>
            logger.debug(s"Result for sendRankFallPush. ${r.status} ${r.body}")
          }
      }
    }
 }
