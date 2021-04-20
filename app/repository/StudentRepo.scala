package repository

import javax.inject.{Inject, Singleton}
import models.{Student}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Date
import scala.concurrent.Future

@Singleton()
class StudentRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends StudentTable with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def create(student: Student): Future[Option[Int]] = db.run {
    studentTableQueryInc += student
  }


  def update(student: Student,userRef:String):Future[Int] = db.run {
    studentTableQuery.filter(_.id === student.id).filter(_.userRef === userRef).update(student)
  }

  def getById(id: Int, userRef:String) = db.run {
    studentTableQuery.filter(_.id === id).filter(_.userRef === userRef).result.headOption
  }


  def getAll(userRef:String): Future[List[Student]] = db.run {
    studentTableQuery.filter(_.userRef === userRef).to[List].result
  }


  def delete(id: Int,userRef:String) = db.run {
    studentTableQuery.filter(_.id === id).filter(_.userRef === userRef).delete
  }
//
//  def getUniversityStudentCount() = {
//    val ans = (for {
//      (s, u) <- studentTableQuery join universityTableQuery on (_.universityId === _.id)
//    } yield (s, u)).groupBy(_._2.name).map {
//      case (uni, data) => (uni, data.map(_._1.universityId).length)
//    }
//    db.run(ans.to[List].result)
//    //
//    //      db.run(ans.result).onComplete{
//    //        case Success(value) => println(value)
//    //      }
//
//
//  }
//  def getStudentUniversityName() = {
//    val ans = (for {
//      (student, university) <- studentTableQuery join universityTableQuery on (_.universityId === _.id)
//    } yield (student.name, university.name)).to[List]
//
//    db.run(ans.to[List].result)
//
//
//  }

  //  def ddl = studentTableQuery.schema
}





private[repository] trait StudentTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected val studentTableQuery = TableQuery[StudentTable]
  lazy protected val studentTableQueryInc = studentTableQuery returning studentTableQuery.map(_.id)

  //  protected def bankTableAutoInc = bankTableQuery returning bankTableQuery.map(_.id)

  private[StudentTable] class StudentTable(tag: Tag) extends Table[Student](tag, "student") {
    val id:Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    val name: Rep[String] = column[String]("name")
    val email: Rep[String] = column[String]("email")
    val universityId : Rep[Int]= column[Int]("university_id")
    val dob: Rep[Date] = column[Date]("date_of_birth")
    val userRef:Rep[String] = column[String]("user_ref")//2nd change

    def * = (name, email, universityId, dob,userRef, id).mapTo[Student]

  }

}