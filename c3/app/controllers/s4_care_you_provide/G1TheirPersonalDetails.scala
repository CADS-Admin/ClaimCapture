package controllers.s4_care_you_provide

import language.reflectiveCalls
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import controllers.Mappings._
import models.view.{Navigable, CachedClaim}
import utils.helpers.CarersForm._
import models.domain._
import controllers.CarersForms._
import scala.Some

object G1TheirPersonalDetails extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "title" -> nonEmptyText(maxLength = 4),
    "firstName" -> carersNonEmptyText(maxLength = 17),
    "middleName" -> optional(carersText(maxLength = 17)),
    "surname" -> carersNonEmptyText(maxLength = Name.maxLength),
    "nationalInsuranceNumber" -> optional(nino.verifying(validNino)),
    "dateOfBirth" -> dayMonthYear.verifying(validDate),
    "liveAtSameAddressCareYouProvide" -> nonEmptyText.verifying(validYesNo)
  )(TheirPersonalDetails.apply)(TheirPersonalDetails.unapply))

  def present = claiming { implicit claim => implicit request => implicit lang =>
    val isPartnerPersonYouCareFor = YourPartner.visible &&
                                    claim.questionGroup[YourPartnerPersonalDetails].exists(_.isPartnerPersonYouCareFor == "yes")

    val currentForm = if (isPartnerPersonYouCareFor) {
      claim.questionGroup(YourPartnerPersonalDetails) match {
        case Some(t: YourPartnerPersonalDetails) =>
          form.fill(TheirPersonalDetails(title = t.title, firstName = t.firstName, middleName = t.middleName, surname = t.surname,
                                         nationalInsuranceNumber = t.nationalInsuranceNumber,
                                         dateOfBirth = t.dateOfBirth)) // Pre-populate form with values from YourPartnerPersonalDetails
        case _ => form // Blank form (user can only get here if they skip sections by manually typing URL).
      }
    } else {
      form.fill(TheirPersonalDetails)
    }

    track(TheirPersonalDetails) { implicit claim => Ok(views.html.s4_care_you_provide.g1_theirPersonalDetails(currentForm)) }
  }

  def submit = claiming { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s4_care_you_provide.g1_theirPersonalDetails(formWithErrors)),
      theirPersonalDetails => claim.update(theirPersonalDetails) -> Redirect(routes.G2TheirContactDetails.present()))
  }
}