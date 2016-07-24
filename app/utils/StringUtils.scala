package utils

/**
 * Created by kaifer on 2016. 6. 3..
 */
object StringUtils {
  implicit def string2ListMapper(s: String) = s.split(',').toList

  implicit def list2StringMapper(l: List[String]) = l.mkString(",")
}
