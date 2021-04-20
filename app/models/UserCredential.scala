package models

import java.sql.Date

case class UserCredential(firstName:String, lastName:String, userID:String, password:String, createdDate:Date);

case class UserInfo (userID:String, firstName:String, lastName:String, createdDate:Date)


