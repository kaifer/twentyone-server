package libs.rediscala

import javax.inject.{Inject, Singleton}

import _root_.play.api.Configuration
import akka.actor.ActorSystem

/**
 * Created by kaifer on 2016. 6. 3..
 */
@Singleton
class Redis @Inject()(configuration: Configuration)(
                                 implicit val system: ActorSystem) extends AbstractRedisClient {

  private[this] lazy val redisConfiguration = configuration.getConfig("redis").getOrElse(Configuration.empty)

  override def port: Int = redisConfiguration.getInt("port").getOrElse(6379)
  override def host: String = redisConfiguration.getString("host").getOrElse("localhost")

  override val password: Option[String] = redisConfiguration.getString("password")
  override val db: Option[Int] = redisConfiguration.getInt("db")

  override val name: String = redisConfiguration.getString("name").getOrElse("RedisClient")
}
