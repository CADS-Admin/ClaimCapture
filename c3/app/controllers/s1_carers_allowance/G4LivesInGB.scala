package controllers.s1_carers_allowance

import language.reflectiveCalls
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.data.FormError
import models.view.CachedClaim
import utils.helpers.CarersForm._
import models.domain.LivesInGB
import controllers.Mappings._
import models.view.Navigable

object G4LivesInGB extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "livesInGB.answer" -> nonEmptyText.verifying(validYesNo)
  )(LivesInGB.apply)(LivesInGB.unapply))

  def present = claiming { implicit claim => implicit request => implicit lang =>
    track(LivesInGB) { implicit claim => Ok(views.html.s1_carers_allowance.g4_livesInGB(form.fill(LivesInGB))) }
  }

  def submit = claiming { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => {
        BadRequest(views.html.s1_carers_allowance.g4_livesInGB(formWithErrors))
      },
      f => claim.update(f) -> Redirect(routes.CarersAllowance.approve()))
  }
}