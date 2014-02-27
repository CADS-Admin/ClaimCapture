package controllers.s9_other_money

import language.reflectiveCalls
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import controllers.CarersForms._
import models.view.CachedClaim
import models.domain.{AboutOtherMoney, MoreAboutYou}
import controllers.Mappings._
import utils.helpers.CarersForm._
import models.view.Navigable
import play.api.data.FormError
import models.domain.Claim
import scala.Some
import models.yesNo.{YesNo, YesNoWithEmployerAndMoney}

object  G1AboutOtherMoney extends Controller with CachedClaim with Navigable {
  val yourBenefitsMapping =
    "yourBenefits" -> mapping(
      "answer" -> nonEmptyText.verifying(validYesNo)
    )(YesNo.apply)(YesNo.unapply)
    
  val anyPaymentsSinceClaimDateMapping =
    "anyPaymentsSinceClaimDate" -> mapping(
      "answer" -> nonEmptyText.verifying(validYesNo)
    )(YesNo.apply)(YesNo.unapply)

  val statutorySickPayMapping =
    "statutorySickPay" -> mapping(
      "answer" ->  nonEmptyText.verifying(validYesNo),
      "howMuch" -> optional(nonEmptyText verifying validDecimalNumber),
      "howOften" -> optional(paymentFrequency verifying validPaymentFrequencyOnly),
      "employersName" -> optional(carersNonEmptyText(maxLength = sixty)),
      "employersAddress" -> optional(address),
      "employersPostcode" -> optional(text verifying validPostcode)
    )(YesNoWithEmployerAndMoney.apply)(YesNoWithEmployerAndMoney.unapply)
      .verifying("statEmployerNameRequired", YesNoWithEmployerAndMoney.validateEmployerNameOnYes _)
      .verifying("statHowMuchRequired", YesNoWithEmployerAndMoney.validateHowMuchOnYes _)

  val otherStatutoryPayMapping =
    "otherStatutoryPay" -> mapping(
      "answer" -> nonEmptyText.verifying(validYesNo),
      "howMuch" -> optional(nonEmptyText verifying validDecimalNumber),
      "howOften" -> optional(paymentFrequency verifying validPaymentFrequencyOnly),
      "employersName" -> optional(carersNonEmptyText(maxLength = sixty)),
      "employersAddress" -> optional(address),
      "employersPostcode" -> optional(text verifying validPostcode)
    )(YesNoWithEmployerAndMoney.apply)(YesNoWithEmployerAndMoney.unapply)
      .verifying("otherPayEmployerNameRequired", YesNoWithEmployerAndMoney.validateEmployerNameOnYes _)
      .verifying("otherPayHowMuchRequired", YesNoWithEmployerAndMoney.validateHowMuchOnYes _)

  val form = Form(mapping(
    yourBenefitsMapping,
    anyPaymentsSinceClaimDateMapping,
    "whoPaysYou" -> optional(carersNonEmptyText(maxLength = Name.maxLength)),
    "howMuch" -> optional(nonEmptyText verifying validDecimalNumber),
    "howOften" -> optional(paymentFrequency verifying validPaymentFrequencyOnly),
    statutorySickPayMapping,
    otherStatutoryPayMapping
  )(AboutOtherMoney.apply)(AboutOtherMoney.unapply)
    .verifying("howMuch.required", validateHowMuch _)
    .verifying("whoPaysYou.required", validateWhoPays _)
  )

  def validateWhoPays(aboutOtherMoney: AboutOtherMoney) = {
    aboutOtherMoney.anyPaymentsSinceClaimDate.answer match {
      case `yes` => aboutOtherMoney.whoPaysYou.isDefined
      case _ => true
    }
  }

  def validateHowMuch(aboutOtherMoney: AboutOtherMoney) = {
    aboutOtherMoney.anyPaymentsSinceClaimDate.answer match {
      case `yes` => aboutOtherMoney.howMuch.isDefined
      case _ => true
    }
  }

  def hadPartnerSinceClaimDate(implicit claim: Claim): Boolean = claim.questionGroup(MoreAboutYou) match {
    case Some(m: MoreAboutYou) => m.hadPartnerSinceClaimDate == yes
    case _ => false
  }

  def present = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    track(AboutOtherMoney) { implicit claim => Ok(views.html.s9_other_money.g1_aboutOtherMoney(form.fill(AboutOtherMoney), hadPartnerSinceClaimDate)) }
  }

  def submit = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => {
        val claimDate: String = claim.dateOfClaim.fold("{NO CLAIM DATE}")(_.`dd/MM/yyyy`)
        val yourBenefitsAnswerErrorParams = Seq(if (hadPartnerSinceClaimDate) "or your Partner/Spouse" else "", claimDate)
        val anyPaymentsErrorParams = Seq(claimDate)

        val formWithErrorsUpdate = formWithErrors
          .replaceError("yourBenefits.answer","error.required", FormError("yourBenefits.answer","error.required",yourBenefitsAnswerErrorParams))
          .replaceError("anyPaymentsSinceClaimDate.answer","error.required", FormError("anyPaymentsSinceClaimDate.answer","error.required",anyPaymentsErrorParams))
          .replaceError("", "whoPaysYou.required", FormError("whoPaysYou", "error.required"))
          .replaceError("", "howMuch.required", FormError("howMuch", "error.required"))
          .replaceError("howOften.frequency.other","error.maxLength",FormError("howOften","error.maxLength"))
          .replaceError("statutorySickPay","statHowMuchRequired", FormError("statutorySickPay.howMuch", "error.required"))
          .replaceError("statutorySickPay","statEmployerNameRequired", FormError("statutorySickPay.employersName", "error.required"))
          .replaceError("otherStatutoryPay","otherPayHowMuchRequired", FormError("otherStatutoryPay.howMuch", "error.required"))
          .replaceError("otherStatutoryPay","otherPayEmployerNameRequired", FormError("otherStatutoryPay.employersName", "error.required"))

        BadRequest(views.html.s9_other_money.g1_aboutOtherMoney(formWithErrorsUpdate, hadPartnerSinceClaimDate))
      },
      f => claim.update(f) -> Redirect(controllers.s10_pay_details.routes.G1HowWePayYou.present))
  }
}