package libs.rediscala

import akka.util.ByteString
import redis.ByteStringDeserializerDefault

/**
 * Created by jaeung on 2015-06-03.
 */

abstract class AbstractRedisClient()(implicit _system: akka.actor.ActorRefFactory)
  extends redis.RedisClientActorLike(_system) with redis.RedisCommands with redis.commands.Transactions
  with scala.Serializable with ByteStringDeserializerDefault {

  override val password: Option[String] = None
  override val db: Option[Int] = None

  val name: String = "RedisClient"

  def port: Int = 6379

  def port_=(x: Int) = Unit

  def host: String = "localhost"

  def host_=(x: String) = Unit
  
  def bsToLong(bs: ByteString): Long = String.deserialize(bs).toLong

  def mapDeserialize(f: Map[String, ByteString]) =
    f map (t => (t._1, String.deserialize(t._2)))

}
