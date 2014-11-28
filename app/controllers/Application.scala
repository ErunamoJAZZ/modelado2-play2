package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  /*
   * Si el usuario ya está logueado, entonces le debe mostar
   * la lista de su propios albumes
   * Si no, le muestra el login
   */
  def index = Action {
    Redirect(routes.Login.loginIndex)
  }

}