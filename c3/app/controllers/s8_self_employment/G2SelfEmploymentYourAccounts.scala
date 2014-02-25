package controllers.s8_self_employment

import language.reflectiveCalls
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.data.Form
import controllers.Mappings._
import models.domain.SelfEmploymentYourAccounts
import models.view.CachedClaim
import utils.helpers.CarersForm._
import SelfEmployment._
import models.view.Navigable
import controllers.CarersForms._
import utils.helpers.PastPresentLabelHelper._
import play.api.data.FormError
import models.domain.Claim
import scala.Some
import play.api.Logger
import play.api.i18n.Lang


object G2SelfEmploymentYourAccounts extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "whatWasOrIsYourTradingYearFrom" -> optional(dayMonthYear.verifying(validDateOnly)),
    "whatWasOrIsYourTradingYearTo" -> optional(dayMonthYear.verifying(validDateOnly)),
    "areIncomeOutgoingsProfitSimilarToTrading" -> optional(text verifying validYesNo),
    "tellUsWhyAndWhenTheChangeHappened" -> optional(carersNonEmptyText(maxLength = 300))
  )(SelfEmploymentYourAccounts.apply)(SelfEmploymentYourAccounts.unapply)
    .verifying("required", validateChangeHappened _))

  def validateChangeHappened(selfEmploymentYourAccounts: SelfEmploymentYourAccounts) = {
    selfEmploymentYourAccounts.areIncomeOutgoingsProfitSimilarToTrading match {
      case Some(`no`) => selfEmploymentYourAccounts.tellUsWhyAndWhenTheChangeHappened.isDefined
      case Some(`yes`) => true
      case _ => true
    }
  }

  def present = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    presentConditionally(selfEmploymentYourAccounts)
  }

  def selfEmploymentYourAccounts(implicit claim: Claim, request: Request[AnyContent], lang: Lang): ClaimResult = {
    track(SelfEmploymentYourAccounts) { implicit claim => Ok(views.html.s8_self_employment.g2_selfEmploymentYourAccounts(form.fill(SelfEmploymentYourAccounts))) }
  }

  def submit = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => {
        val formWithErrorsUpdate = formWithErrors
          .replaceError("","required", FormError("tellUsWhyAndWhenTheChangeHappened", "error.required"))
          .replaceError("whatWasOrIsYourTradingYearFrom","error.invalid", FormError("whatWasOrIsYourTradingYearFrom", "error.invalid", Seq(isWasIfSelfEmployed)))
          .replaceError("whatWasOrIsYourTradingYearTo","error.invalid", FormError("whatWasOrIsYourTradingYearTo", "error.invalid", Seq(isWasIfSelfEmployed)))
        BadRequest(views.html.s8_self_employment.g2_selfEmploymentYourAccounts(formWithErrorsUpdate))
      },
      f => claim.update(f) -> Redirect(routes.G4SelfEmploymentPensionsAndExpenses.present()))
  }
}