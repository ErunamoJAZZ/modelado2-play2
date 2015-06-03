package model

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction._
import scala.slick.lifted._
import play.api.Play.current
import java.sql.Timestamp

/*
 * Tipo de clase para la utilización de fotos.
 */
case class Picture(
  id: Option[Int],
  //album: Option[Int],
  path: String,
  description: String,
  last_edition: Timestamp = new Timestamp(System.currentTimeMillis()),
  stars: Int = 0)

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class PicturesTable(tag: Tag) extends Table[Picture](tag, "picture") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  //def album = column[Int]("album", O.Nullable)
  def path = column[String]("path", O.NotNull)
  def description = column[String]("description", O.NotNull)
  def last_edition = column[Timestamp]("last_edition", O.NotNull)
  def stars = column[Int]("album", O.NotNull)

  //Debido a la relación muchos a muchos, esta clave foranea ya no se usa.
  //def fk_album = foreignKey("fk_picture__album", album, DAO.AlbumsQuery)(_.id, Cascade, Cascade)

  def * = (id.?, /*album.?,*/ path, description, last_edition,
    stars) <> (Picture.tupled, Picture.unapply)
}

object Pictures {

  /*
   * Inserta una nueva foto.
   */
  def newPicture(p: Picture)(implicit s: Session): Int = {
    (DAO.PicturesQuery returning DAO.PicturesQuery.map(_.id)) += p

  }

  def getPictures(id_album: Int, isOwner: Boolean)(implicit s: Session) = {
    val q_owner = for {
      p <- DAO.PicturesQuery
      ap <- DAO.AlbumsPicturesQuery
      if (ap.album === id_album
        && ap.picture === p.id)
    } yield p

    val q_guest = for {
      p <- DAO.PicturesQuery
      ap <- DAO.AlbumsPicturesQuery
      if (ap.album === id_album
        && ap.picture === p.id
        && !ap.isPrivate)
    } yield p

    // Cual de los dos usar?, depende de si es dueño o no.
    val q = if (isOwner) q_owner else q_guest

    play.Logger.info(">> Consulta de las imágenes de un album: " + q.selectStatement)
    q.list
  }

  def getAllPrivates(uname: String)(implicit s: Session) = {
    val q = for {
      p <- DAO.PicturesQuery
      a <- DAO.AlbumsQuery
      ap <- DAO.AlbumsPicturesQuery
      u <- DAO.UsersQuery
      if (p.id === ap.picture
        //&& u.name === uname
        && u.id === a.owner
        && ap.album === a.id)
    } yield (p,a,u)

    play.Logger.info(">> Consulta de todos los privados: " + q.selectStatement)
    q.list
  }

  def getPicture(id_pic: Int)(implicit s: Session) = {
    val q = for {
      p <- DAO.PicturesQuery
      ap <- DAO.AlbumsPicturesQuery
      if (ap.isPrivate === false
        && ap.picture === p.id
        && p.id === id_pic)
    } yield p

    play.Logger.info(">> Consulta de las imágenes de un album: " + q.selectStatement)
    q.firstOption
  }

  def getPrivate(uname: String, id_pic: Int)(implicit s: Session): Option[(model.Picture, Boolean)] = {
    val q = for {
      p <- DAO.PicturesQuery
      a <- DAO.AlbumsQuery
      ap <- DAO.AlbumsPicturesQuery
      u <- DAO.UsersQuery
      if (p.id === ap.picture
        && u.name === uname
        && u.id === a.owner
        && ap.album === a.id
        && p.id === id_pic)
    } yield (p, ap.isPrivate)

    play.Logger.info(">> Consulta de todos los privados: " + q.selectStatement)
    q.firstOption
  }
}