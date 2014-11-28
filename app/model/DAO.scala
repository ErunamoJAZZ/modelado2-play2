package model
import scala.slick.lifted._

/*
 * Objeto DAO para el acceso unico a los objetos de consulta en la BD.
 */
object DAO {
  val UsersQuery = TableQuery[UsersTable]
  val AlbumsQuery = TableQuery[AlbumsTable]
  val PicturesQuery = TableQuery[PicturesTable]
  val AlbumsPicturesQuery = TableQuery[AlbumsPicturesTable]
}