package models.cache

import javax.inject.{Inject, Singleton}

import akka.util.ByteString
import constants.RedisKeys._
import libs.rediscala.Redis
import models.sql.DAL
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import redis.ByteStringDeserializerDefault
import redis.commands.TransactionBuilder

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by kaifer on 2016. 6. 29..
 */
@Singleton
class CacheDAL @Inject()(val redis: Redis, val dal: DAL)(implicit val ec: ExecutionContext)
  extends ByteStringDeserializerDefault {

}
