package controllers.s1_carers_allowance

import language.reflectiveCalls
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.data.FormError
import models.view.CachedClaim
import utils.helpers.CarersForm._
import models.domain.Over16
import controllers.Mappings._
import models.view.Navigable

object G3Over16 extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "answer" -> nonEmptyText.verifying(validYesNo)
  )(Over16.apply)(Over16.unapply))

  def present = claiming { implicit claim => implicit request =>
    track(Over16) { implicit claim => Ok(views.html.s1_carers_allowance.g3_over16(form.fill(Over16))) }
  }

  def submit = claiming { implicit claim => implicit request =>
    form.bindEncrypted.fold(
      formWithErrors => {
        val formWithErrorsUpdate = formWithErrors
          .replaceError("answer", FormError("over16.answer", "error.required"))
        BadRequest(views.html.s1_carers_allowance.g3_over16(formWithErrorsUpdate))
      },
      f => claim.update(f) -> Redirect(routes.G4LivesInGB.present()))
  }
}