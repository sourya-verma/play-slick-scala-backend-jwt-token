package controllers


import com.google.inject.Inject
import models.{Student}
import play.filters.cors.CORSFilter
import play.api.http.{DefaultHttpFilters, EnabledFilters}
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.i18n._
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import play.api.mvc._
import repository.StudentRepo
import utils.Constants
import utils.JsonFormat._
import utils.SecureAction

import scala.concurrent.{ExecutionContext, Future}

class StudentController @Inject()(
                                   cc: ControllerComponents,
                                   studentRepo: StudentRepo,
                                   secureAction:SecureAction,
                                 )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  import Constants._


  def list: Action[AnyContent] =
    secureAction.async { request =>
      studentRepo.getAll(request.user.userID).map { res =>
        Ok(Json.toJson(res)).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }

  def create: Action[JsValue] =
    secureAction.async(parse.json) { request =>
      println("request is "+ request.body)
      request.body.validate[Student].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { student =>
        studentRepo.create(Student(student.name, student.email, student.universityId, student.dob, request.user.userID, student.id)).map { createdId =>
          Ok(Json.toJson(student)).withHeaders("Access-Control-Allow-Origin" -> "*")
        }
      })
    }
  //  Ok(Json.toJson(response)).withHeaders("Access-Control-Allow-Origin" -> "*")
  def delete(studentId: Int): Action[AnyContent] =
    secureAction.async { request=>
      studentRepo.delete(studentId, request.user.userID).map {id =>
        Ok(Json.toJson(Map("id" -> id))).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }
  def getById(studentId: Int): Action[AnyContent] =
    secureAction.async { request =>
      studentRepo.getById(studentId, request.user.userID).map { studentOpt =>
        studentOpt.fold(Ok(Json.toJson("{}")))(student => Ok(
          Json.toJson(student))).withHeaders("Access-Control-Allow-Origin" -> "*")
      }
    }

  def update: Action[JsValue] =
    secureAction.async(parse.json) { request =>
      request.body.validate[Student].fold(error => Future.successful(BadRequest(JsError.toJson(error)).withHeaders("Access-Control-Allow-Origin" -> "*")), { student =>
        studentRepo.update(Student(student.name, student.email, student.universityId, student.dob, request.user.userID, student.id), request.user.userID).map {id => Ok(Json.toJson(Map("id" -> id))).withHeaders("Access-Control-Allow-Origin" -> "*") }
      })
    }
//
//  def getStudentUniversityName(): Action[AnyContent] =
//    Action.async { _ =>
//      studentRepo.getStudentUniversityName().map{result =>
//        val data = for(x<-result)
//          yield (StudentUniversityName(x._1,x._2 ))
//        Ok(Json.toJson(data)).withHeaders("Access-Control-Allow-Origin" -> "*")
//
//      }
//    }
//
//
//  def getUniversityStudentCount(): Action[AnyContent] =
//    Action.async { _ =>
//      studentRepo.getUniversityStudentCount().map{result =>
//        val data = for(x<-result)
//          yield (UniversityStudentCount(x._1,x._2))
//        Ok(Json.toJson(data)).withHeaders("Access-Control-Allow-Origin" -> "*")
//
//      }
//    }

  def preflight(all: String): Action[AnyContent] = Action {req =>
    println("validate origin ...." + req)
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Allow" -> "*",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" ->
        "Origin, X-Requested-With, Content-Type, Accept, Referrer, User-Agent, X-Auth-Token, X-Api-Key,Authorization,withcredentials")
  }




}

