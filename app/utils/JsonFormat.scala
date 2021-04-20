package utils



import models._
import play.api.libs.json.Json


object JsonFormat {

  implicit val studentFormat = Json.format[Student]
  implicit val universityFormat = Json.format[University]
  implicit val userFormat = Json.format[UserCredential]
  implicit val loginFormat = Json.format[LoginDetail]
  implicit val userInfoFormat = Json.format[UserInfo]
//  implicit val studentInfoFormat = Json.format[StudentInfo]

}


