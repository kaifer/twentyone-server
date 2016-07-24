package securities

import constants.RedisKeys
import libs.http.security.AuthorizedUser
import akka.util.ByteString
import redis.ByteStringDeserializerDefault

/**
 * Created by kaifer on 2016. 6. 3..
 */
package object secured {
  case class MyAuthUser(userId: Long) extends AuthorizedUser
  object MyAuthUser extends ByteStringDeserializerDefault {
    import String.deserialize

    implicit def toMap(u: MyAuthUser): Map[String, String] = Map(RedisKeys.ID -> u.userId.toString)

    def unapply(m: Map[String, ByteString]): Option[MyAuthUser] = {
      for {
        id <- m.get(RedisKeys.ID) map (deserialize(_))
      } yield MyAuthUser(id.toLong)
    }
  }
}
