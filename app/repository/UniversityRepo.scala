package repository

import javax.inject.{Inject, Singleton}
import models.University
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Date
import scala.concurrent.Future

@Singleton()
class UniversityRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UniversityTable with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def create(university: University): Future[Int] = db.run {
   universityTableQuery += university
  }


  def update(university: University, userRef:String):Future[Int] = db.run {
    universityTableQuery.filter(_.id === university.id).filter(_.userRef === userRef).update(university)
  }




  def getById(id: Int, userRef:String) = db.run {
    universityTableQuery.filter(_.id === id).filter(_.userRef === userRef).result.headOption
  }


  def getAll(userRef:String): Future[List[University]] = db.run {
    universityTableQuery.filter(_.userRef === userRef).to[List].result
  }


  def delete(id: Int, userRef:String) = db.run {
    universityTableQuery.filter(_.id === id).filter(_.userRef === userRef).delete
  }

}

private[repository] trait UniversityTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected val universityTableQuery = TableQuery[UniversityTable]

  //  protected def bankTableAutoInc = bankTableQuery returning bankTableQuery.map(_.id)

  private[UniversityTable] class UniversityTable(tag: Tag) extends Table[University](tag, "university") {
    val id:Rep[Int] = column[Int]("id", O.PrimaryKey)
    val name: Rep[String] = column[String]("university_name")
    val location: Rep[String] = column[String]("location")
    val userRef:Rep[String] = column[String]("user_ref")

    def * = (id, name, location, userRef).mapTo[University]


  }

}