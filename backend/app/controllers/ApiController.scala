package controllers

import javax.inject._
import models._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import todone.data._

@Singleton
class ApiController @Inject() (
    val controllerComponents: ControllerComponents,
    val model: TasksModel
) extends BaseController {
  import JsonFormats._

  def tasks(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(model.tasks))
  }

  def create(): Action[JsValue] = Action(parse.json) { implicit request =>
    val taskResult = request.body.validate[Task]
    taskResult.fold(
      errors => BadRequest(JsError.toJson(errors)),
      task => {
        val id = model.create(task)
        Ok(Json.toJson(id))
      }
    )
  }

  def closeTask(id: Int): Action[AnyContent] = Action { implicit request =>
    val completedTask = model.closeTask(Id(id))
    completedTask match {
      case Some(_) => Ok(Json.toJson("Success"))
      case None => BadRequest(Json.toJson("Error"))
    }
  }

  def tags(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(model.tags))
  }

  def projects(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(model.projects))
  }
}
