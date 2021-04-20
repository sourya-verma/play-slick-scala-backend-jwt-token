package models

import java.sql.Date

case class Student(name : String, email : String, universityId: Int, dob : Date, userRef:String, id: Option[Int] = None)//1st change
//case class StudentInfo(name : String, email : String, universityId: Int, dob : Date, id: Option[Int] = None)//1st change

case class LoginDetail (userID:String, password:String)