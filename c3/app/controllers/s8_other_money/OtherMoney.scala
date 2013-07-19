package controllers.s8_other_money

import play.api.mvc._
import models.view.CachedClaim
import models.domain.Claim
import play.api.templates.Html

object OtherMoney extends Controller with CachedClaim {

  def whenVisible(claim: Claim)(closure: () => SimpleResult[Html]) = {
    val iAmVisible = claim.isSectionVisible(models.domain.OtherMoney)

    if (iAmVisible) closure() else Ok(<title>TODO</title><body>TODO: Should Redirect To Employment SECTION</body>).as(HTML)
  }

  def completedQuestionGroups(implicit claim: Claim) = claim.completedQuestionGroups(models.domain.OtherMoney)

  def completed = claiming {
    implicit claim => implicit request =>
      Ok(<p>Hello, world!</p>).as(HTML)
  }

  def completedSubmit = claiming {
    implicit claim => implicit request =>
      ???
  }
}