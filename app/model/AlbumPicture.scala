package model

import java.sql.Timestamp
import play.api.db.slick.Config.driver
import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction._
import scala.util._

/*
 * Tipo de clase para la utilización de albumes.
 */
case class AlbumPicture(
  id: Option[Int],
  album: Option[Int],
  picture: Option[Int],
  isPrivate: Boolean = false,
  creation: Timestamp = new Timestamp(System.currentTimeMillis()))

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class AlbumsPicturesTable(tag: Tag) extends Table[AlbumPicture](tag, "album_picture") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def album = column[Int]("album", O.Nullable)
  def picture = column[Int]("picture", O.Nullable)
  def isPrivate = column[Boolean]("isPrivate", O.NotNull)
  def creation = column[Timestamp]("creation", O.NotNull)

  //TODO
  def fk_album = foreignKey("fk_ap__album", album, DAO.AlbumsQuery)(_.id, Cascade, SetNull)
  def fk_picture = foreignKey("fk_ap__picture", picture, DAO.PicturesQuery)(_.id, Cascade, SetNull)

  //indice de que debe ser único
  def idx_ap = index("idx_album_picture", (album, picture), unique = true)

  def * = (id.?, album.?, picture.?, isPrivate, creation) <> (AlbumPicture.tupled, AlbumPicture.unapply)
}

object AlbumsPictures {

  /*
   * Inserta una nueva foto.
   */
  def newRelation(id_album: Option[Int], id_pic: Option[Int], isPrivate: Boolean)(implicit s: Session): Int = {
    val obj = AlbumPicture(None, id_album, id_pic, isPrivate)
    (DAO.AlbumsPicturesQuery returning DAO.AlbumsPicturesQuery.map(_.id)) += obj

  }

}
