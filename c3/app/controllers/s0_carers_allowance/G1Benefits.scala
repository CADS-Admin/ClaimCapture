package controllers.s0_carers_allowance

import controllers.mappings.Mappings._
import language.reflectiveCalls
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import models.view.CachedClaim
import utils.helpers.CarersForm._
import models.domain._
import models.view.Navigable
import play.api.Logger

object G1Benefits extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "benefitsAnswer" -> nonEmptyText
  )(Benefits.apply)(Benefits.unapply))

  def present = newClaim {implicit claim =>  implicit request =>  lang =>
    Logger.debug(s"Starting new $cacheKey - ${claim.uuid}")
    track(Benefits) { implicit claim => Ok(views.html.s0_carers_allowance.g1_benefits(form.fill(Benefits))(lang)) }
  }

  def submit = claiming ({implicit claim =>  implicit request =>  lang =>
    form.bindEncrypted.fold(
      formWithErrors => {
        BadRequest(views.html.s0_carers_allowance.g1_benefits(formWithErrors)(lang))
      },
      f => claim.update(f) -> Redirect(routes.G2Eligibility.present()))
  }, checkCookie=true)
}