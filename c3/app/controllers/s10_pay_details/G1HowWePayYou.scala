package controllers.s10_pay_details

import language.reflectiveCalls
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import models.view.{Navigable, CachedClaim}
import models.domain.HowWePayYou
import utils.helpers.CarersForm._
import PayDetails._

object G1HowWePayYou extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "likeToPay" -> nonEmptyText(maxLength = 60),
    "paymentFrequency" -> nonEmptyText(maxLength = 15)
  )(HowWePayYou.apply)(HowWePayYou.unapply))

  def present = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    presentConditionally {
      track(HowWePayYou) { implicit claim => Ok(views.html.s10_pay_details.g1_howWePayYou(form.fill(HowWePayYou))) }
    }
  }

  def submit = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s10_pay_details.g1_howWePayYou(formWithErrors)),
      howWePayYou => claim.update(howWePayYou) -> Redirect(routes.G2BankBuildingSocietyDetails.present()))
  }
}