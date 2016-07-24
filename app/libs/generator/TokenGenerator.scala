package libs.generator

import java.security.{MessageDigest, SecureRandom}
import javax.inject.{Singleton, Inject}

import play.api.Configuration

/**
 * Created by kaifer on 2016. 6. 4..
 */
@Singleton
class TokenGenerator @Inject()(configuration: Configuration) {

  private[this] lazy val secretKey = configuration.getString("play.crypto.secret").getOrElse("d;ItyvyKrt_oGL?MkpUb`t;:tQ6pq8N8Bamp06ucF]v>H8h2Mv55lHjR`18wL=HB")

  val TOKEN_LENGTH = 64

  val TOKEN_CHARS = secretKey

  val secureRandom = new SecureRandom()

  def generateMD5Token(source: String, length: Int = TOKEN_LENGTH): String =
    md5(source + System.nanoTime() + generateToken(length - 19))

  def generateSHAToken(source: String, length: Int = TOKEN_LENGTH): String =
    sha(source + System.nanoTime() + generateToken(length - 19))

  private def toHex(bytes: Array[Byte]): String = bytes.map( "%02x".format(_) ).mkString("")

  private def sha(s: String): String = {
    toHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")))
  }
  private def md5(s: String): String = {
    toHex(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8")))
  }

  private def generateToken(length: Int) : String = {
    val charLen = TOKEN_CHARS.length()
    def generateTokenAccumulator(accumulator: String, number: Int) : String = {
      if (number == 0) return accumulator
      else
        generateTokenAccumulator(accumulator + TOKEN_CHARS(secureRandom.nextInt(charLen)).toString, number - 1)
    }
    generateTokenAccumulator("", length)
  }
}
