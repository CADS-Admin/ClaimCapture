package controllers.s7_employment

import models.view.{Navigable, CachedClaim}
import play.api.mvc._
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import models.domain.{Employment => Emp, Jobs, BeenEmployed}
import utils.helpers.CarersForm._
import controllers.Mappings._
import controllers.s7_employment.Employment.jobs
import models.domain.Claim
import scala.reflect.ClassTag
import play.api.i18n.Lang
import scala.language.postfixOps
import models.view.CachedClaim.ClaimResult

object G2BeenEmployed extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "beenEmployed" -> (nonEmptyText verifying validYesNo)
  )(BeenEmployed.apply)(BeenEmployed.unapply))

  private def presentConditionally(c: => Either[Result,ClaimResult], lang:Lang)(implicit claim: Claim, request: Request[AnyContent]): Either[Result,ClaimResult] = {
    claim.questionGroup[Emp].collect {
      case e: Emp if e.beenEmployedSince6MonthsBeforeClaim == yes => c
    }.getOrElse(redirect(lang))
  }

  private def redirect(lang:Lang)(implicit claim: Claim, request: Request[AnyContent]): Either[Result,ClaimResult] =
    Left(Redirect(controllers.s9_other_money.routes.G1AboutOtherMoney.present()))

  def present = claimingWithCheck { implicit claim =>  implicit request =>  lang =>
      presentConditionally(beenEmployed(lang),lang)
  }

  private def beenEmployed(lang:Lang)(implicit claim: Claim, request: Request[AnyContent]): Either[Result,ClaimResult] = {
    if(getCompletedJobs) {
      val f:Claim => Result = { implicit claim => Ok(views.html.s7_employment.g2_beenEmployed(form.fill(BeenEmployed)))}
      Right(trackBackToBeginningOfEmploymentSection(BeenEmployed)(f)(claim, request,ClassTag[BeenEmployed.type](BeenEmployed.getClass)) )
    }
    else Left(Redirect(routes.G3JobDetails.present(JobID(form))))
  }

  def submit = claimingWithCheck { implicit claim =>  implicit request =>  lang =>
    import controllers.Mappings.yes

    def next(beenEmployed: BeenEmployed) = beenEmployed.beenEmployed match {
      case `yes` if jobs.size < 2 => Redirect(routes.G3JobDetails.present(JobID(form)))
      case `yes` => Redirect(routes.G2BeenEmployed.present())
      case _ => Redirect(controllers.s9_other_money.routes.G1AboutOtherMoney.present())
    }

    form.bindEncrypted.fold(
      formWithErrors => {
        val formWithErrorsUpdate = formWithErrors
          .replaceError("beenEmployed", "error.required", FormError("beenEmployedSince6MonthsBeforeClaim.label", "error.required",Seq(claim.dateOfClaim.fold("{NO CLAIM DATE}")(dmy =>
          (dmy - 6 months).`dd/MM/yyyy`))))
        BadRequest(views.html.s7_employment.g2_beenEmployed(formWithErrorsUpdate))
      },
      beenEmployed => clearUnfinishedJobs.update(beenEmployed) -> next(beenEmployed))
  }

  private def clearUnfinishedJobs(implicit claim: Claim) = {
    val jobs = claim.questionGroup[Jobs].getOrElse(Jobs())
    claim.update(Jobs(jobs.jobs.filter(_.completed == true)))
  }

  private def getCompletedJobs(implicit claim: Claim) = {
    val jobs = claim.questionGroup[Jobs].getOrElse(Jobs())
    Jobs(jobs.jobs.filter(_.completed == true)).size > 0
  }
}