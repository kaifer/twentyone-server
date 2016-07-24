package controllers

import javax.inject.Inject

import play.api.routing._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRoute

class Application @Inject()() extends Controller{

  def index = Action {
    Ok(views.html.main("hello"))
  }

  def jsRoutes = /*Cached(_ => "jsRoutes", duration = 0) */ {
    Action { implicit requestHeader: RequestHeader =>
      Ok(JavaScriptReverseRouter("jsRoutes")(routeCache: _*)).as(JAVASCRIPT)
    }
  }

  private val routeCache: Array[JavaScriptReverseRoute] = {
    val jsRoutesClass = classOf[routes.javascript]
    val controllers = jsRoutesClass.getFields.map(_.get(null))

    for {
      controller <- controllers
      method <- controller.getClass.getDeclaredMethods if method.getReturnType() == classOf[play.api.routing.JavaScriptReverseRoute]
    } yield method.invoke(controller).asInstanceOf[JavaScriptReverseRoute]
  }
}
