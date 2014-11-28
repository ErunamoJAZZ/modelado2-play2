package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.Play.current
import play.filters.csrf._
import model._
import scala.util._

object Login extends Controller {

  val userForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText)(UserInet.apply)(UserInet.unapply))

  /*
   * Página de login
   */
  def loginIndex =
    DBAction { implicit req =>
      req.session.get("user_name").map(uname => {
        val id_user = Users.getId(uname)
        Redirect(routes.Albumes.albumesDeUsuario(id_user))
      }).getOrElse {
        Ok(views.html.login(userForm))
      }

    }

  /* 
   * Controlador que recibe el login
   */
  def loging = CSRFCheck {
    DBAction { implicit req =>
      //play.Logger.debug(req.body.toString)

      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.login(userForm, true)),
        u => {
          if (Users.verificar(u)) {
            val id_user = Users.getId(u.name)
            Redirect(routes.Albumes.albumesDeUsuario(id_user)).withSession("user_name" -> u.name)
          } else {
            BadRequest(views.html.login(userForm, true))
          }

        })
    }
  }

  def register = CSRFCheck {
    DBAction { implicit req =>

      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.login(userForm, true)),
        u => {
          Try(Users.newUser(u)) match {
            case Success(id_user) => {
              play.Logger.debug("> Se creó un nuevo usuario con id: " + id_user)
              Redirect(routes.Albumes.albumesDeUsuario(id_user)).withSession("user_name" -> u.name)
            }
            case Failure(e) => BadRequest(views.html.login(userForm, true))
          }
        })

    }
  }
  
  def logout = Action {
    Redirect(routes.Login.loginIndex()).withNewSession
  }
}