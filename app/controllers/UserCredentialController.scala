package controllers


import com.google.inject.Inject
import models.{LoginDetail, UserCredential, UserInfo}
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.i18n._
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import play.api.mvc._
import repository.UserCredentialRepo
import utils.{AuthService, Constants, SecureAction, UserRequest}
import utils.JsonFormat._
import utils.SecureAction
import scala.concurrent.{ExecutionContext, Future}

class UserCredentialController @Inject()(
                                          cc: ControllerComponents,
                                          auth : AuthService,
                                          userRepo: UserCredentialRepo
                                    )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  import Constants._


  def list: Action[AnyContent] =
    Action.async {
      userRepo.getAll().map { res =>
        Ok(Json.toJson(res)).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }

  def create: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[UserCredential].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { user =>
        userRepo.create(user).map { createdId =>
          Ok(Json.toJson(Map("id" -> createdId))).withHeaders("Access-Control-Allow-Origin" -> "*")
        }
      })
    }

  def delete(userID: String): Action[AnyContent] =
    Action.async { _ =>
      userRepo.delete(userID).map {id =>
        Ok(Json.toJson(Map("id" -> id))).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }



  def getById(userID: String): Action[AnyContent] =
    Action.async { _ =>
      userRepo.getById(userID).map { userOpt =>
        userOpt.fold(Ok(Json.toJson("{}")))(user => Ok(
          Json.toJson(user))).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }


  def validate: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[LoginDetail].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { usr =>
        userRepo.validate(usr).map {
          case Some(user) =>
            val json = Json.toJson(Map("token"-> auth.encodeToken(UserInfo(user.userID, user.firstName, user.lastName, user.createdDate))))
            Ok(json).withHeaders("Access-Control-Allow-Origin" -> "*")
          case None =>
            BadRequest("Invalid credential.........")
        }

      })
    }


  def update: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body.validate[UserCredential].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { usr =>
        userRepo.update(usr).map {id => Ok(Json.toJson(Map("id" -> id))).withHeaders("Access-Control-Allow-Origin" -> "*") }
      })
    }

}

