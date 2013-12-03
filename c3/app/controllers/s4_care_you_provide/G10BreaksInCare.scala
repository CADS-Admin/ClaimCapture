package controllers.s4_care_you_provide

import play.api.mvc.Controller
import play.api.data.{FormError, Form}
import play.api.i18n.Messages
import play.api.data.Forms._
import utils.helpers.CarersForm._
import controllers.Mappings._
import models.domain.{MoreAboutTheCare, BreaksInCare}
import models.view.{Navigable, CachedClaim}
import models.yesNo.YesNo
import CareYouProvide.breaksInCare
import scala.language.postfixOps

object G10BreaksInCare extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "answer" -> nonEmptyText.verifying(validYesNo)
  )(YesNo.apply)(YesNo.unapply))

  def present = claiming { implicit claim => implicit request =>
    val filledForm = request.headers.get("referer") match {
      case Some(referer) if referer endsWith routes.G11Break.present().url => form
      case _ if claim.questionGroup[BreaksInCare].isDefined => form.fill(YesNo(no))
      case _ => form
    }

    track(BreaksInCare) { implicit claim => Ok(views.html.s4_care_you_provide.g10_breaksInCare(filledForm, breaksInCare)) }
  }

  def submit = claiming { implicit claim => implicit request =>
    import controllers.Mappings.yes

    def next(hasBreaks: YesNo) = hasBreaks.answer match {
      case `yes` if breaksInCare.breaks.size < 10 => Redirect(routes.G11Break.present())
      case `yes` => Redirect(routes.G10BreaksInCare.present())
      case _ => Redirect(routes.CareYouProvide.completed())
    }

    form.bindEncrypted.fold(
      formWithErrors => {
        val sixMonth = claim.questionGroup(MoreAboutTheCare) match {
          case Some(m: MoreAboutTheCare) => m.spent35HoursCaringBeforeClaim.answer.toLowerCase == "yes"
          case _ => false
        }
        val formWithErrorsUpdate = formWithErrors.replaceError("answer", FormError("answer.label", "error.required",Seq(claim.dateOfClaim.fold("{NO CLAIM DATE}")(dmy =>
          if (sixMonth) (dmy - 6 months).`dd/MM/yyyy` else dmy.`dd/MM/yyyy`))))
        BadRequest(views.html.s4_care_you_provide.g10_breaksInCare(formWithErrorsUpdate, breaksInCare))
      },
      hasBreaks => claim.update(breaksInCare) -> next(hasBreaks))
  }

  def delete(id: String) = claiming { implicit claim => implicit request =>
    import play.api.libs.json.Json

    claim.questionGroup(BreaksInCare) match {
      case Some(b: BreaksInCare) => {
        val updatedBreaksInCare = b.delete(id)

        if (updatedBreaksInCare.breaks == b.breaks) BadRequest(s"""Failed to delete break with ID "$id" as it does not exist in claim""")
        else if (updatedBreaksInCare.breaks.isEmpty) claim.update(updatedBreaksInCare) -> Ok(Json.obj("answer" -> Messages("answer.label")))
        else claim.update(updatedBreaksInCare) -> Ok(Json.obj("answer" -> Messages("answer.more.label")))
      }

      case _ => BadRequest(s"""Failed to delete break with ID "$id" as claim currently has no breaks""")
    }
  }
}