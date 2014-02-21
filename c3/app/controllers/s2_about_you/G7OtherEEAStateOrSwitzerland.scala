package controllers.s2_about_you

import language.reflectiveCalls
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import models.view.CachedClaim
import models.domain.OtherEEAStateOrSwitzerland
import controllers.Mappings._
import utils.helpers.CarersForm._
import models.view.Navigable

object G7OtherEEAStateOrSwitzerland extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "benefitsFromEEA" -> nonEmptyText.verifying(validYesNo),
    "claimedForBenefitsFromEEA" -> nonEmptyText.verifying(validYesNo),
    "workingForEEA" -> nonEmptyText.verifying(validYesNo)
  )(OtherEEAStateOrSwitzerland.apply)(OtherEEAStateOrSwitzerland.unapply))

  def present = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    track(OtherEEAStateOrSwitzerland) { implicit claim => Ok(views.html.s2_about_you.g7_otherEEAStateOrSwitzerland(form.fill(OtherEEAStateOrSwitzerland))) }
  }

  def submit = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s2_about_you.g7_otherEEAStateOrSwitzerland(formWithErrors)),
      benefitsFromEEA => claim.update(benefitsFromEEA) -> Redirect(routes.G8MoreAboutYou.present()))
  }
}
