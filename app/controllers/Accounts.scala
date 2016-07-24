package controllers

import javax.inject.{Inject, Singleton}

import constants.ErrorCode._
import ds.forms.{AuthenticateForm, InstallForm, RegisterForm}
import ds.res.CreatedIdModelRes
import libs.autheticate.Authenticator
import libs.generator.TokenGenerator
import libs.http.ResultsExtension
import models.sql.{DAL, SqlExceptionExtension}
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import play.api.Configuration
import play.api.mvc._
import securities.secured.{MyAuthUser, MyTokenSecured}
import utils.TimeUtils._
import SqlExceptionExtension._

import scala.concurrent.{ExecutionContext, Future}
import libs.http.HttpErrorCode._
import libs.authenticate._

/**
 * Created by kaifer on 2016. 5. 31..
 */
@Singleton
class Accounts @Inject()(val dal: DAL, authenticator: Authenticator,
                         tokenGenerator: TokenGenerator, configuration: Configuration)(implicit val ec: ExecutionContext)
  extends Controller with ResultsExtension with MyTokenSecured {

  val logger = LoggerFactory.getLogger(getClass)

  val tokenDuration: Int = configuration.getInt("auth.token.maxAge").getOrElse(8640000)

  def install() = ValidatedFormAction.async(BodyParsers.parse.json[InstallForm]) { request =>
    val form = request.body

    dal.write { implicit session =>
      dal.Device.create(form.deviceUid)
    } map { id =>
      Ok.toJson(CreatedIdModelRes(id))
    } recoverWith {
      case IntegrityConstraintSqlException(message) =>
        dal.lookup { implicit session =>
          dal.Device.findBy(form.deviceUid)
        } map {
          case Some(device) => Ok.toJson(CreatedIdModelRes(device.id.get))
        }
    }
  }

  def register() = authUserFormAction.async(BodyParsers.parse.json[RegisterForm]) { request =>
    val form = request.body

    dal.write { implicit session =>
      dal.Device.setRegisterToken(request.userId, form.registerToken)
    } map { _ =>
      NoContent
    }
  }

  def authenticate(provider: String) = ValidatedFormAction.async(BodyParsers.parse.json[AuthenticateForm]) { request =>
    val form = request.body

    authenticator.authenticate(provider, form.accessToken) flatMap {
      case Some(au) =>
        val token = tokenGenerator.generateSHAToken(au.authUuid, 64)
        val now = DateTime.now
        val expiredAt = now.plusSeconds(tokenDuration)

        dal.tx { implicit session =>
          dal.LinkedAccount.findBy(provider, au.authUuid) match {
            case Some(userId) =>
              dal.Token.create(userId, token, expiredAt)
              userId
            case None =>
              dal.Device.findBy(form.deviceUid) match {
                case Some(device) =>
                  val userId = dal.User.create(device.id.get, au.genericName)
                  dal.LinkedAccount.create(userId, provider, au.authUuid)
                  dal.Token.create(userId, token, expiredAt)
                  userId
                case None => throw new Exception(INVALID_DEVICE_UID)
              }
          }
        } map { userId =>
          Ok.toJson(ds.res.TokenRes(userId, token, expiredAt))
        }
      case None => Future(BadRequest.error(INVALID_ACCESS_TOKEN))
    } recover {
      case ae: AuthException => UnHandledError.error(ae.msg)
      case e: Exception => UnHandledError.error(e.getMessage)
    }
  }
}
