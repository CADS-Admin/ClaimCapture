package controllers.s10_pay_details

import language.reflectiveCalls
import play.api.mvc.{AnyContent, Request, Controller}
import play.api.data.Form
import play.api.data.Forms._
import models.view.{Navigable, CachedClaim}
import models.domain._
import utils.helpers.CarersForm._
import controllers.Mappings._
import controllers.s10_pay_details.PayDetails._
import app.AccountStatus
import controllers.CarersForms._
import models.domain.Claim
import scala.Some
import play.api.i18n.Messages

object G2BankBuildingSocietyDetails extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "accountHolderName" -> carersNonEmptyText(maxLength = 40),
    "whoseNameIsTheAccountIn" -> carersNonEmptyText(maxLength = 40),
    "bankFullName" -> carersNonEmptyText(maxLength = 100),
    "sortCode" -> (sortCode verifying requiredSortCode),
    "accountNumber" -> carersNonEmptyText(minLength = 6, maxLength = 10),
    "rollOrReferenceNumber" -> carersText(maxLength = 18)
  )(BankBuildingSocietyDetails.apply)(BankBuildingSocietyDetails.unapply))

  def present = claiming { implicit claim => implicit request =>
    presentConditionally(bankBuildingSocietyDetails)
  }


  def bankBuildingSocietyDetails(implicit claim: Claim, request: Request[AnyContent]): ClaimResult = {
    val iAmVisible = claim.questionGroup(HowWePayYou) match {
      case Some(y: HowWePayYou) => y.likeToBePaid == Messages(AccountStatus.BankBuildingAccount)
      case _ => true
    }

    if (iAmVisible) track(BankBuildingSocietyDetails) { implicit claim => Ok(views.html.s10_pay_details.g2_bankBuildingSocietyDetails(form.fill(BankBuildingSocietyDetails))) }
    else claim.delete(BankBuildingSocietyDetails) -> redirectPath
  }

  def submit = claiming { implicit claim => implicit request =>
    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s10_pay_details.g2_bankBuildingSocietyDetails(formWithErrors)),
      howWePayYou => claim.update(howWePayYou) -> redirectPath)
  }
}