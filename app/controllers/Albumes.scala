package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.Play.current
import model._
import play.filters.csrf._
import scala.util._

object Albumes extends Controller {

  val albumForm = Form(single("name" -> nonEmptyText))

  /*
   * Muestra la lista de todos los usuarios 
   * registrados en el sistema
   */
  def usuariosTodos = DBAction { implicit req =>
    req.session.get("user_name").map(uname => {
      val lu = Users.getUsersList
      Ok(views.html.usuarios(uname, lu))
    }).getOrElse {
      Redirect(routes.Login.loginIndex())
    }
  }

  def nuevoAlbum = CSRFCheck {
    DBAction { implicit req =>
      req.session.get("user_name").map(uname => {
        albumForm.bindFromRequest.fold(
          formWithErrors => {
            Redirect(routes.Login.loginIndex())
          },
          album_name => {
            val id_user = Users.getId(uname)
            val newAlbum = Album(None, Some(id_user), album_name)
            Albums.newAlbum(newAlbum)
            Redirect(routes.Albumes.albumesDeUsuario(id_user))
          })
      }).getOrElse {
        Redirect(routes.Login.loginIndex())
      }
    }
  }

  /* 
   * Muestra todos los albumes de un usuario, y deduce si es el 
   * album del mismo usuario logueado.
   */
  def albumesDeUsuario(id: Int) =
    DBAction { implicit req =>
      req.session.get("user_name").map(uname => {
        val user = Users.getUser(id)
        val la = Albums.getAlbumList(id)
        Ok(views.html.albumes(la, uname == user.name, user, albumForm))
      }).getOrElse {
        Redirect(routes.Login.loginIndex())
      }

    }

  def albumDeFotos(uname: String, id_album: Int) = DBAction { implicit req =>
    req.session.get("user_name").map(u_name => {
      val isOwner = (uname == u_name)
      val l_p = Pictures.getPictures(id_album, isOwner)
      Ok(views.html.album(uname, isOwner, l_p, id_album))
    }).getOrElse {
      Redirect(routes.Login.loginIndex())
    }
  }

  /*def fotosPrivadas = DBAction { implicit req =>
    req.session.get("user_name").map(u_name => {
      val l_p = Pictures.getAllPrivates(u_name)
      Ok(views.html.album(u_name, false, l_p, 0))
    }).getOrElse {
      Redirect(routes.Login.loginIndex())
    }
  }*/

  def verImagen(id_pic: Int) = DBAction { implicit req =>
    req.session.get("user_name").map(u_name => {

      Pictures.getPicture(id_pic) match {
        case Some(pic) => Ok(views.html.foto(pic, false))
        case None => {
          Pictures.getPrivate(u_name, id_pic) match {
            case (Some((p, isPrivate))) => Ok(views.html.foto(p, isPrivate))
            case None                   => Redirect(routes.Login.loginIndex())
          }
        }
      }

    }).getOrElse {
      Redirect(routes.Login.loginIndex())
    }

  }

  //val privateForm = Form(single("privado" -> boolean))

  def nuevaFoto(id_album: Int) = DBAction(parse.multipartFormData) {
    implicit request =>
      request.session.get("user_name").map(u_name => {

        request.body.file("picture").map { picture =>

          import java.io.File
          val filename = picture.filename
          play.Logger.debug("Subiendo una foto, picture.contentType=" + picture.contentType)
          picture.contentType match {
            case x if x.get.contains("image") => {
              val path = s"public/images/uploaded/$filename"
              picture.ref.moveTo(new File(path))

              val id_pic = Pictures.newPicture(Picture(None, path, filename))
              val isPrivate = request.body.dataParts.get("privado") match {
                case None => false
                case _    => true
              }

              //play.Logger.debug(isPrivate.toString)
              AlbumsPictures.newRelation(if (id_album > 0) Option(id_album) else None, Option(id_pic), isPrivate)
              Redirect(routes.Albumes.albumDeFotos(u_name, id_album))
            }
            case _ => NotAcceptable("Tipo de archivo incorrecto")
          }

        }.getOrElse {
          Redirect(routes.Login.loginIndex())
        }
      }).getOrElse {
        Redirect(routes.Login.loginIndex())
      }
  }
  
  def test = DBAction{ implicit req =>
    
    val asd = model.Pictures.getAllPrivates("a")
    
    Ok(asd.toString)
  }
}