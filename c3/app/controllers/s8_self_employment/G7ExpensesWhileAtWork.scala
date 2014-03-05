package controllers.s8_self_employment

import language.reflectiveCalls
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.AnyContent
import controllers.Mappings._
import models.domain._
import models.view.CachedClaim
import utils.helpers.CarersForm._
import controllers.s8_self_employment.SelfEmployment._
import utils.helpers.PastPresentLabelHelper._
import models.view.Navigable
import controllers.CarersForms._
import play.api.data.FormError
import models.domain.Claim
import scala.Some
import play.api.i18n.Lang

object G7ExpensesWhileAtWork extends Controller with CachedClaim with Navigable {
  def form(implicit claim: Claim) = Form(mapping(
    "nameOfPerson" -> carersNonEmptyText(maxLength = sixty),
    "howMuchYouPay" -> nonEmptyText(maxLength = 8).verifying(validDecimalNumber),
    "howOftenPayExpenses" -> (pensionPaymentFrequency verifying validPensionPaymentFrequencyOnly),
    "whatRelationIsToYou" -> carersNonEmptyText(maxLength = sixty),
    "relationToPartner" -> optional(nonEmptyText(maxLength = sixty)),
    "whatRelationIsTothePersonYouCareFor" -> nonEmptyText
  )(ExpensesWhileAtWork.apply)(ExpensesWhileAtWork.unapply)
    .verifying("relationToPartner.required", validateRelationToPartner(claim, _)))

  def validateRelationToPartner(implicit claim: Claim, expensesWhileAtWork: ExpensesWhileAtWork) = {
    claim.questionGroup(MoreAboutYou) -> claim.questionGroup(YourPartnerPersonalDetails) match {
      case (Some(m: MoreAboutYou), Some(p: YourPartnerPersonalDetails)) if m.hadPartnerSinceClaimDate == "yes" && p.isPartnerPersonYouCareFor == "no" => expensesWhileAtWork.relationToPartner.isDefined
      case _ => true
    }
  }

  def present = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    presentConditionally(expensesWhileAtWork)
  }

  def expensesWhileAtWork(implicit claim: Claim, request: Request[AnyContent],lang:Lang): ClaimResult = {
    val payToLookPersonYouCareFor = claim.questionGroup(SelfEmploymentPensionsAndExpenses) match {
      case Some(s: SelfEmploymentPensionsAndExpenses) => s.didYouPayToLookAfterThePersonYouCaredFor == `yes`
      case _ => false
    }

    payToLookPersonYouCareFor match {
      case true => track(ExpensesWhileAtWork) { implicit claim => Ok(views.html.s8_self_employment.g7_expensesWhileAtWork(form.fill(ExpensesWhileAtWork)))}
      case false => claim.delete(ExpensesWhileAtWork) ->  Redirect(routes.SelfEmployment.completedSubmit())
    }
  }

  def submit = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => {
        val formWithErrorsUpdate = formWithErrors
          .replaceError("howMuchYouPay", "error.required", FormError("howMuchYouPay", "error.required", Seq(labelForSelfEmployment(claim, "howMuchYouPay"))))
          .replaceError("howMuchYouPay", "decimal.invalid", FormError("howMuchYouPay", "decimal.invalid", Seq(labelForSelfEmployment(claim, "howMuchYouPay"))))
          .replaceError("howOftenPayExpenses.frequency", "error.required", FormError("howOftenPayExpenses", "error.required", Seq("",labelForSelfEmployment(claim, "howOftenPayExpenses"))))
          .replaceError("", "relationToPartner.required", FormError("relationToPartner", "error.required"))
          .replaceError("howOftenPayExpenses.frequency.other","error.maxLength",FormError("howOftenPayExpenses","error.maxLength",Seq("60",labelForSelfEmployment(claim, "howOftenPayExpenses"))))
          .replaceError("howOftenPayExpenses","error.paymentFrequency",FormError("howOftenPayExpenses","error.paymentFrequency",Seq("",labelForSelfEmployment(claim, "howOftenPayExpenses"))))
        BadRequest(views.html.s8_self_employment.g7_expensesWhileAtWork(formWithErrorsUpdate))
      },
      f => claim.update(f) ->  Redirect(routes.SelfEmployment.completedSubmit())
    )
  }
}