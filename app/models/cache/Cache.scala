package models.cache

import libs.rediscala.Redis
import models.sql.DAL
import org.slf4j.LoggerFactory
import redis.ByteStringDeserializerDefault

import scala.concurrent.ExecutionContext

/**
 * Created by kaifer on 2016. 7. 7..
 */
private[cache] trait Cache extends ByteStringDeserializerDefault {
  trait CacheModel

  val redis: Redis
  val dal: DAL

  implicit val ec: ExecutionContext

  val logger = LoggerFactory.getLogger(getClass)
}
