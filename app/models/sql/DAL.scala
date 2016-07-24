package models.sql

import java.sql.SQLException
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.db.Database
import play.api.inject.ApplicationLifecycle
import scalikejdbc._
import scalikejdbc.config.TypesafeConfigReader

import SqlExceptionExtension._
import scala.concurrent.{Future, ExecutionContext, blocking}

/**
 * Created by kaifer on 2016. 6. 12..
 */
@Singleton
class DAL @Inject()(actorSystem: ActorSystem,
                    database: Database,
                    lifecycle: ApplicationLifecycle,
                    configuration: Configuration)(implicit val ec: ExecutionContext) {

  lazy val loggingSQLErrors = configuration.getBoolean("scalikejdbc.global.loggingSQLErrors").getOrElse(true)

  /*
    execution contexts
   */
  val simpleDbLookups: ExecutionContext = actorSystem.dispatchers.lookup("contexts.simple-db-lookups")
  val expensiveDbLookups: ExecutionContext = actorSystem.dispatchers.lookup("contexts.expensive-db-lookups")
  val dbWriteOperations: ExecutionContext = actorSystem.dispatchers.lookup("contexts.db-write-operations")

  /*
    models
   */
  val Device = devices.Device

  val LinkedAccount = users.LinkedAccount
  val Token = users.Token
  val User = users.User
  val Friend = users.Friend

  val Game = games.Game
  val UserScore = games.UserScore

  /*
    run block
   */
  def lookup[T](body: (DBSession) => T): Future[T] = Future(blocking(DB.readOnly(body)))(simpleDbLookups)

  def lookupExp[T](body: (DBSession) => T): Future[T] = Future(blocking(DB.readOnly(body)))(expensiveDbLookups)

  def write[T](body: (DBSession) => T): Future[T] = Future(blocking(DB.autoCommit(body)))(dbWriteOperations).recoverWith(handleWriteSQLException)

  def tx[T](body: (DBSession) => T): Future[T] = Future(blocking(DB.localTx(body)))(dbWriteOperations).recoverWith(handleWriteSQLException)


  def handleWriteSQLException[A]: PartialFunction[Throwable, Future[A]] = {
    case e: SQLException =>
      if (e.getErrorCode == INTEGRITY_CONSTRAINT_VIOLATION) {
        Future.failed(IntegrityConstraintSqlException(e.getMessage))
      } else if (e.getErrorCode == FOREIGN_KEY_CONSTRAINT) {
        Future.failed(ForeignKeyConstraintSqlException(e.getMessage))
      } else if (e.getErrorCode == OUT_OF_RANGE_VALUE) {
        Future.failed(OutOfRangeSqlException(e.getMessage))
      } else {
        throw e
      }
  }

  /**
   * Start
   */
  def onStart(): Unit = {
    GlobalSettings.loggingSQLErrors = loggingSQLErrors

    TypesafeConfigReader.loadGlobalSettings()

    ConnectionPool.singleton(new DataSourceConnectionPool(database.dataSource))
  }

  def onStop(): Unit = {
    ConnectionPool.closeAll()
  }

  lifecycle.addStopHook(() => Future.successful(onStop))
  onStart()
}
