package controllers


import com.google.inject.Inject
import models.University
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.i18n._
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import play.api.mvc._
import repository.UniversityRepo
import utils.{Constants, SecureAction}
import utils.JsonFormat._

import scala.concurrent.{ExecutionContext, Future}

class UniversityController @Inject()(
                                      cc: ControllerComponents,
                                      universityRepo: UniversityRepo,
                                      secureAction :SecureAction
                                    )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  import Constants._


  def list: Action[AnyContent] =
    secureAction.async { request =>
      universityRepo.getAll(request.user.userID).map { res =>
        Ok(Json.toJson(res)).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }

  def create: Action[JsValue] =
    secureAction.async(parse.json) { request =>
      print(request.body)
      request.body.validate[University].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { university =>
        universityRepo.create(University(university.id, university.name, university.location, request.user.userID)).map { createdId =>
          Ok(Json.toJson(Map("id" -> createdId))).withHeaders("Access-Control-Allow-Origin" -> "*")
        }
      })
    }

  def delete(universityId: Int): Action[AnyContent] =
    secureAction.async { request =>
      universityRepo.delete(universityId,request.user.userID).map {id =>
        Ok(Json.toJson(Map("id" -> id))).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }
  def getById(universityId: Int): Action[AnyContent] =
    secureAction.async { request =>
      universityRepo.getById(universityId,request.user.userID).map { universityOpt =>
        universityOpt.fold(Ok(Json.toJson("{}")))(university => Ok(
          Json.toJson(university))).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }

  def update: Action[JsValue] =
    secureAction.async(parse.json) { request =>
      request.body.validate[University].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { university =>
        universityRepo.update(University(university.id, university.name, university.location, request.user.userID),request.user.userID).map {id => Ok(Json.toJson(Map("id" -> id))).withHeaders("Access-Control-Allow-Origin" -> "*") }
      })
    }

}

