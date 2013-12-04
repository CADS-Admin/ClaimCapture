package controllers.s8_self_employment

import language.reflectiveCalls
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.mvc.Controller
import controllers.Mappings._
import models.domain._
import models.view.CachedClaim
import utils.helpers.CarersForm._
import controllers.s8_self_employment.SelfEmployment._
import utils.helpers.PastPresentLabelHelper._
import models.view.Navigable
import play.api.mvc.Request
import play.api.mvc.AnyContent
import controllers.CarersForms._


object G5ChildcareExpensesWhileAtWork extends Controller with CachedClaim with Navigable {
  def form(implicit claim: Claim) = Form(mapping(
    "whoLooksAfterChildren" -> carersNonEmptyText(maxLength = sixty),
    "howMuchYouPay" -> nonEmptyText(maxLength = 8).verifying(validDecimalNumber),
    "howOftenPayChildCare" -> (pensionPaymentFrequency verifying validPensionPaymentFrequencyOnly),
    "whatRelationIsToYou" -> nonEmptyText(maxLength = sixty),
    "relationToPartner" -> optional(nonEmptyText(maxLength = sixty)),
    "whatRelationIsTothePersonYouCareFor" -> nonEmptyText
  )(ChildcareExpensesWhileAtWork.apply)(ChildcareExpensesWhileAtWork.unapply)
    .verifying("relationToPartner.required", validateRelationToPartner(claim, _)))

  def validateRelationToPartner(implicit claim: Claim, childcareExpensesWhileAtWork: ChildcareExpensesWhileAtWork) = {
    claim.questionGroup(MoreAboutYou) -> claim.questionGroup(PersonYouCareFor) match {
      case (Some(m: MoreAboutYou), Some(p: PersonYouCareFor)) if m.hadPartnerSinceClaimDate == "yes" && p.isPartnerPersonYouCareFor == "no" => childcareExpensesWhileAtWork.relationToPartner.nonEmpty
      case _ => true
    }
  }

  def present = claiming { implicit claim => implicit request =>
    presentConditionally(childcareExpensesWhileAtWork)
  }

  def childcareExpensesWhileAtWork(implicit claim: Claim, request: Request[AnyContent]): ClaimResult = {
    val payToLookAfterChildren = claim.questionGroup(SelfEmploymentPensionsAndExpenses) match {
      case Some(s: SelfEmploymentPensionsAndExpenses) => s.doYouPayToLookAfterYourChildren == `yes`
      case _ => false
    }

    payToLookAfterChildren match {
      case true => track(ChildcareExpensesWhileAtWork) { implicit claim => Ok(views.html.s8_self_employment.g5_childcareExpensesWhileAtWork(form.fill(ChildcareExpensesWhileAtWork)))}
      case false => claim.delete(ChildcareExpensesWhileAtWork) -> Redirect(routes.G7ExpensesWhileAtWork.present())
    }
  }

  def submit = claiming { implicit claim => implicit request =>
    form.bindEncrypted.fold(
      formWithErrors => {
        val formWithErrorsUpdate = formWithErrors
          .replaceError("howMuchYouPay", "error.required", FormError("howMuchYouPay", "error.required", Seq(didYouDoYouIfSelfEmployed.toLowerCase)))
          .replaceError("howMuchYouPay", "decimal.invalid", FormError("howMuchYouPay", "decimal.invalid", Seq(didYouDoYouIfSelfEmployed.toLowerCase)))
          .replaceError("howOftenPayChildCare.frequency","error.required", FormError("howOftenPayChildCare", "error.required", Seq("",didYouDoYouIfSelfEmployed.toLowerCase)))
          .replaceError("", "relationToPartner.required", FormError("relationToPartner", "error.required"))
          .replaceError("howOftenPayChildCare.frequency.other","error.maxLength",FormError("howOftenPayChildCare","error.maxLength",Seq("60",didYouDoYouIfSelfEmployed.toLowerCase)))
          .replaceError("howOftenPayChildCare","error.paymentFrequency",FormError("howOftenPayChildCare","error.paymentFrequency",Seq("",didYouDoYouIfSelfEmployed.toLowerCase)))
        BadRequest(views.html.s8_self_employment.g5_childcareExpensesWhileAtWork(formWithErrorsUpdate))
      },
      f => claim.update(f) -> Redirect(routes.G7ExpensesWhileAtWork.present()))
  }
}