package model

import java.sql.Timestamp
import play.api.db.slick.Config.driver
import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction._
import scala.util._

/*
 * Tipo de clase para la utilización de albumes.
 */
case class Album(
  id: Option[Int],
  owner: Option[Int],
  name: String,
  creation: Timestamp = new Timestamp(System.currentTimeMillis()))

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class AlbumsTable(tag: Tag) extends Table[Album](tag, "album") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Int]("owner", O.Nullable)
  def name = column[String]("name", O.NotNull)
  def creation = column[Timestamp]("creation", O.NotNull)

  //TODO
  def fk_owner = foreignKey("fk_album__user", owner, DAO.UsersQuery)(_.id, Cascade, Cascade)

  def * = (id.?, owner.?, name, creation) <> (Album.tupled, Album.unapply)
}

object Albums {
  
  /*
   * Inserta un nuevo Album a la BD, y devolviendo el id.
   */
  def newAlbum(a: Album)(implicit s: Session): Int = {
    (DAO.AlbumsQuery returning DAO.AlbumsQuery.map(_.id)) += a
  }
  
  /*
   * Devuelve la lista de albumes.
   */
  def getAlbumList(user_id:Int)(implicit s: Session): List[Album] = {
    Try({

      val q = for {
        al <- DAO.AlbumsQuery
        if al.owner === user_id
      } yield al

      q.list
    }).getOrElse(Nil)
  }
  
}