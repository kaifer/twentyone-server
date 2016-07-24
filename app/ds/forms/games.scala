package ds.forms

import libs.http.Form
import play.api.libs.json.Json

/**
 * Created by kaifer on 2016. 7. 17..
 */
case class EndGameForm(score: Long, step: Long) extends Form
object EndGameForm {
  implicit val fmt = Json.reads[EndGameForm]
}
