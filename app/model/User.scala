package model

import play.api.db.slick.Config.driver
import play.api.db.slick.Config.driver.simple._
import com.github.t3hnar.bcrypt._
import scala.util.{ Try }

/*
 * Tipo de clase para la utilización de albumes.
 */
case class User(
  id: Option[Int],
  name: String,
  password: String)

//clase para los form
case class UserInet(
  name: String,
  password: String)

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class UsersTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def password = column[String]("password", O.NotNull)

  def idx_name = index("idx_name", name, unique = true)

  def * = (id.?, name, password) <> (User.tupled, User.unapply)
}

object Users {

  /*
   * Inserta un nuevo usuario a la BD, encriptando su clave, y 
   * devolviendo el id.
   */
  def newUser(u: UserInet)(implicit s: Session): Int = {
    val nn = User(None, u.name, u.password.bcrypt(generateSalt))
    (DAO.UsersQuery returning DAO.UsersQuery.map(_.id)) += nn

  }

  /*
   * Se fija que la clave enviada si sea la del usuario.
   * 
   */
  def verificar(u: UserInet)(implicit s: Session): Boolean = {
    Try({

      val q = for {
        us <- DAO.UsersQuery
        if us.name === u.name
      } yield us.password

      val crypPass = q.first

      u.password.isBcrypted(crypPass)
    }).getOrElse(false)
  }

  /*
   * Devuelve la lista de usuarios.
   */
  def getUsersList(implicit s: Session): List[(Int, String)] = {
    Try({

      val q = for {
        us <- DAO.UsersQuery
      } yield (us.id, us.name)

      q.list
    }).getOrElse(Nil)
  }
  
  /*
   * Dice el id segun el nombre
   */
  def getId(name:String)(implicit s: Session):Int = {
    DAO.UsersQuery.filter(_.name === name).map(_.id).first
  }
  
  def getUser(id_user:Int)(implicit s: Session) = {
    DAO.UsersQuery.filter(_.id === id_user).first
  }
}