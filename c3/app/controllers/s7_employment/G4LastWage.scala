package controllers.s7_employment

import models.view.CachedClaim
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import models.domain.LastWage
import utils.helpers.CarersForm._
import controllers.Mappings._
import Employment._
import models.PeriodFromTo

object G4LastWage extends Controller with CachedClaim {
  val form = Form(
    mapping(
      "jobID" -> nonEmptyText,
      "lastPaidDate" -> optional(dayMonthYear.verifying(validDateOnly)),
      "periodCovered" -> optional(
        mapping(
          "from"->dayMonthYear.verifying(validDate),
          "to" -> dayMonthYear.verifying(validDate)
        )(PeriodFromTo.apply)(PeriodFromTo.unapply)),
      "grossPay" -> optional(text),
      "payInclusions" -> optional(text),
      "sameAmountEachTime" -> optional(text),
      call(routes.G4LastWage.present())
    )(LastWage.apply)(LastWage.unapply))

  def present = claiming { implicit claim => implicit request =>
    Ok(views.html.s7_employment.g4_lastWage(form, completedQuestionGroups(LastWage)))
  }

  def submit = claiming { implicit claim => implicit request =>
    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s7_employment.g4_lastWage(formWithErrors, completedQuestionGroups(LastWage))),
      lastWage => claim.update(lastWage) -> Redirect(routes.G4LastWage.present()))
  }
}