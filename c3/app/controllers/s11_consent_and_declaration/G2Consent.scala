package controllers.s11_consent_and_declaration

import language.reflectiveCalls
import play.api.mvc.{AnyContent, Controller}
import play.api.data.Form
import play.api.data.Forms._
import models.view.CachedClaim
import models.domain.{Employment, Consent}
import utils.helpers.CarersForm._
import models.yesNo.{OptYesNoWithText, YesNoWithText}
import models.view.Navigable
import controllers.Mappings._
import play.api.data.FormError
import play.api.mvc.Request
import controllers.CarersForms._
import play.api.data.FormError
import scala.Some

object G2Consent extends Controller with CachedClaim with Navigable {

  def validateEmpRequired(input: OptYesNoWithText)(implicit request: Request[AnyContent]): Boolean = {
    fromCache(request) match {
      case Some(claim) if claim.questionGroup[Employment].getOrElse(Employment()).beenEmployedSince6MonthsBeforeClaim == `yes` =>
        input.answer.isDefined
      case _ => true
    }
  }

  def informationFromEmployerMapping(implicit request: Request[AnyContent]) =
    "gettingInformationFromAnyEmployer" -> mapping(
      "informationFromEmployer" -> optional(nonEmptyText),
      "why" -> optional(carersNonEmptyText(maxLength = 300)))(OptYesNoWithText.apply)(OptYesNoWithText.unapply)
      .verifying("employerRequired", validateEmpRequired _)
      .verifying("required", OptYesNoWithText.validateOnNo _)


  val informationFromPersonMapping =
    "tellUsWhyEmployer" -> mapping(
      "informationFromPerson" -> nonEmptyText,
      "whyPerson" -> optional(carersNonEmptyText(maxLength = 300)))(YesNoWithText.apply)(YesNoWithText.unapply)
      .verifying("required", YesNoWithText.validateOnNo _)
      
  def form(implicit request: Request[AnyContent]) = Form(mapping(
    informationFromEmployerMapping,
    informationFromPersonMapping
  )(Consent.apply)(Consent.unapply))

  def present = claiming { implicit claim => implicit request =>
    track(Consent) { implicit claim => Ok(views.html.s11_consent_and_declaration.g2_consent(form.fill(Consent))) }
  }

  def submit = claiming { implicit claim => implicit request =>
    form.bindEncrypted.fold(
      formWithErrors => {
        val formWithErrorsUpdate = formWithErrors
            .replaceError("gettingInformationFromAnyEmployer","employerRequired", FormError("gettingInformationFromAnyEmployer.informationFromEmployer", "error.required"))
            .replaceError("gettingInformationFromAnyEmployer","required", FormError("gettingInformationFromAnyEmployer.why", "error.required"))
            .replaceError("tellUsWhyEmployer", FormError("tellUsWhyEmployer.whyPerson", "error.required"))
        BadRequest(views.html.s11_consent_and_declaration.g2_consent(formWithErrorsUpdate))},
      consent => claim.update(consent) -> Redirect(routes.G3Disclaimer.present()))
  }
}