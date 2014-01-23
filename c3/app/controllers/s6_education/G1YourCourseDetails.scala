package controllers.s6_education

import language.reflectiveCalls
import play.api.mvc.{AnyContent, Request, Controller}
import play.api.data.Form
import play.api.data.Forms._
import models.view.{Navigable, CachedClaim}
import models.domain.{Claim, YourCourseDetails}
import utils.helpers.CarersForm._
import controllers.Mappings._
import controllers.CarersForms._

object G1YourCourseDetails extends Controller with CachedClaim with Navigable {
  val form = Form(mapping(
    "courseTitle" -> carersNonEmptyText(maxLength = 50),
    "nameOfSchoolCollegeOrUniversity" -> carersNonEmptyText(maxLength = sixty),
    "nameOfMainTeacherOrTutor" -> carersNonEmptyText(maxLength = sixty),
    "courseContactNumber" -> optional(text verifying validPhoneNumber),
    "startDate" -> dayMonthYear.verifying(validDate),
    "expectedEndDate" -> dayMonthYear.verifying(validDate)
  )(YourCourseDetails.apply)(YourCourseDetails.unapply))

  def present = claiming { implicit claim => implicit request =>
    presentConditionally {
      track(YourCourseDetails) { implicit claim => Ok(views.html.s6_education.g1_yourCourseDetails(form.fill(YourCourseDetails))) }
    }
  }

  def presentConditionally(c: => ClaimResult)(implicit claim: Claim, request: Request[AnyContent]): ClaimResult = {
    if (models.domain.Education.visible) c
    else redirect
  }

  def redirect(implicit claim: Claim, request: Request[AnyContent]): ClaimResult =
    claim -> Redirect("/employment/been-employed")

  def submit = claiming { implicit claim => implicit request =>
    form.bindEncrypted.fold(
      formWithErrors => BadRequest(views.html.s6_education.g1_yourCourseDetails(formWithErrors)),
      yourCourseDetails => claim.update(yourCourseDetails) -> Redirect(routes.Education.completed()))
  }
}