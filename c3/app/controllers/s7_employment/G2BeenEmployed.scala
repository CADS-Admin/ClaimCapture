package controllers.s7_employment

import models.view.{Navigable, CachedClaim}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.domain.{Employment => Emp, Claim, Jobs, BeenEmployed}
import utils.helpers.CarersForm._
import controllers.Mappings._
import controllers.s7_employment.Employment.jobs
import models.domain.Claim
import models.domain.Claim
import scala.reflect.ClassTag

object G2BeenEmployed extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "beenEmployed" -> (nonEmptyText verifying validYesNo)
  )(BeenEmployed.apply)(BeenEmployed.unapply))

  def presentConditionally(c: => Either[Result,ClaimResult])(implicit claim: Claim, request: Request[AnyContent]): Either[Result,ClaimResult] = {
    claim.questionGroup[Emp].collect {
      case e: Emp if e.beenEmployedSince6MonthsBeforeClaim == yes => c
    }.getOrElse(redirect)
  }

  def redirect(implicit claim: Claim, request: Request[AnyContent]): Either[Result,ClaimResult] =
    Left(Redirect(controllers.s8_self_employment.routes.G1AboutSelfEmployment.present()))

  def present = claimingWithCheck { implicit claim => implicit request => implicit lang =>
      presentConditionally(beenEmployed)
  }

  def beenEmployed(implicit claim: Claim, request: Request[AnyContent]): Either[Result,ClaimResult] = {
    if(getCompletedJobs) {
      val f:Claim => Result = { implicit claim => Ok(views.html.s7_employment.g2_beenEmployed(form.fill(BeenEmployed)))}
      Right(trackBackToBeginningOfEmploymentSection(BeenEmployed)(f)(claim, request,ClassTag[BeenEmployed.type](BeenEmployed.getClass)) )
    }
    else Left(Redirect(routes.G3JobDetails.present(JobID(form))))
  }

  def submit = claimingWithCheck { implicit claim => implicit request => implicit lang =>
    import controllers.Mappings.yes

    def next(beenEmployed: BeenEmployed) = beenEmployed.beenEmployed match {
      case `yes` if jobs.size < 5 => Redirect(routes.G3JobDetails.present(JobID(form)))
      case `yes` => Redirect(routes.G2BeenEmployed.present())
      case _ => Redirect(controllers.s8_self_employment.routes.G1AboutSelfEmployment.present())
    }

    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s7_employment.g2_beenEmployed(formWithErrors)),
      beenEmployed => clearUnfinishedJobs.update(beenEmployed) -> next(beenEmployed))
  }

  def clearUnfinishedJobs(implicit claim: Claim) = {
    val jobs = claim.questionGroup[Jobs].getOrElse(Jobs())
    claim.update(Jobs(jobs.jobs.filter(_.completed == true)))
  }

  def getCompletedJobs(implicit claim: Claim) = {
    val jobs = claim.questionGroup[Jobs].getOrElse(Jobs())
    Jobs(jobs.jobs.filter(_.completed == true)).size > 0
  }
}