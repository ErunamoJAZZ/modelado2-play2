package controllers

import play.api._
import play.api.mvc._
import play.filters.csrf._

object Global extends WithFilters(CSRFFilter()) with GlobalSettings {
  // ... onStart, onStop etc
  override def onStart(app: Application) {
    play.Logger.info("Hi. App start now.\n Cualquier pregunta sobre el programa, "+
        "envía un email a cdsanchezr@unal.edu.co \n\n")
  }
  
  override def onStop(app: Application) {
    play.Logger.info("Bye~ ")
  }
}