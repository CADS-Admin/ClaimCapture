package controllers.s8_self_employment

import play.api.mvc._
import models.view.CachedClaim
import models.domain._
import models.view.Navigable
import play.api.i18n.Lang
import models.view.CachedClaim.ClaimResult

object SelfEmployment extends Controller with CachedClaim with Navigable {
  def completed = claimingWithCheck {implicit claim =>  implicit request =>  lang =>
    redirect
  }

  def completedSubmit = claimingWithCheck { implicit claim =>  implicit request =>  lang =>
    redirect
  }

  def presentConditionally(c: => ClaimResult, lang: Lang)(implicit claim: Claim, request: Request[AnyContent]): ClaimResult = {
    if (models.domain.SelfEmployment.visible) c
    else redirect
  }

  private def redirect(implicit claim: Claim, request: Request[AnyContent]): ClaimResult =
    claim -> Redirect(controllers.s7_employment.routes.G2BeenEmployed.present())
}