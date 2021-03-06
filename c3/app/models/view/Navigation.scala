package models.view

import play.api.mvc.{Request, Result, AnyContent}
import models.domain.Claim
import play.api.routing.Router
import scala.reflect.ClassTag
import models.view.ClaimHandling.ClaimResult

trait Navigable {
  this: ClaimHandling =>

  def resetPreviewState(f: => Claim => Result)(implicit claim: Claim): ClaimResult = {

    val updatedNavigation = claim.navigation.resetPreviewState()
    val updatedClaim = claim.copy(claim.key, claim.sections)(updatedNavigation)
    updatedClaim -> f(updatedClaim)
  }

  def track[T](t: T, beenInPreview: Boolean = false)(f: => Claim => Result)(implicit claim: Claim, request: Request[AnyContent], classTag: ClassTag[T]): ClaimResult = {

    val updatedNavigation = claim.navigation.track(t, beenInPreview)(request.uri)
    val checkYAnswers = if (beenInPreview) claim.checkYAnswers.copy(previouslySavedClaim = Some(claim)) else claim.checkYAnswers
    val updatedClaim = claim.copy(claim.key, claim.sections, checkYAnswers = checkYAnswers)(updatedNavigation)

    updatedClaim -> f(updatedClaim)
  }

  def trackBackToBeginningOfEmploymentSection[T](t: T)(f: => Claim => Result)(implicit claim: Claim, request: Request[AnyContent], classTag: ClassTag[T]): ClaimResult = {
    val updatedNavigationSec = if (claim.navigation.beenInPreview) {
      claim.navigation.copy(routesAfterPreview = claim.navigation.routesAfterPreview.takeWhile(r => r.uri == "/your-income/your-income" || !r.uri.contains("employment"))).track(t)(request.uri)
    } else {
      claim.navigation.copy(routes = claim.navigation.routes.takeWhile(r => r.uri == "/your-income/your-income" || !r.uri.contains("employment"))).track(t)(request.uri)
    }
    // Similar to track but this removes all routes navigation entries up to /your-income/your-income.
    // Done so that when at the jobs summary page of employment, back can take you back to the initial guard questions page
    val updatedClaim = claim.copy(claim.key, claim.sections)(updatedNavigationSec)

    updatedClaim -> f(updatedClaim)
  }

  def circsPathAfterReason() = {
    controllers.circs.start_of_process.routes.GGoToCircsFunction.present()
  }

  def circsPathAfterFunction() = {
    controllers.circs.consent_and_declaration.routes.GCircsDeclaration.present()
  }

  def circsPathAfterYourDetails() = {
    controllers.circs.consent_and_declaration.routes.GCircsDeclaration.present()
  }
}

case class Navigation(routes: List[Route] = List(), beenInPreview: Boolean = false, routesAfterPreview: List[Route] = List.empty[Route], showSaveButton: Boolean = true) {

  def resetPreviewState(): Navigation = copy(beenInPreview = false)

  def track[T](t: T, beenInPreviewParam: Boolean = false)(route: String)(implicit classTag: ClassTag[T]): Navigation = {
    val newRoute = route.replace("?changing=true", "").replace("?lang=cy", "")
    val routeObj = Route(newRoute, classTag.runtimeClass.getName)

    //Tracking after CYA is a special case, and since it's takeWhile(_.uri != route), using the normal tracking will delete any later routes.
    //So we want to use an alternative tracking in case we have been in preview, to still be able to use the back button link
    if (beenInPreviewParam) {
      copy(routes.takeWhile(_.uri != controllers.preview.routes.Preview.present().url) :+ routeObj, beenInPreviewParam, routesAfterPreview = List.empty[Route], showSaveButton = showSaveButton(newRoute))
    } else if (beenInPreview) {
      copy(routesAfterPreview = routesAfterPreview.takeWhile(_.uri != newRoute) :+ Route(newRoute, classTag.runtimeClass.getName), showSaveButton = showSaveButton(newRoute))
    } else {
      copy(routes.takeWhile(_.uri != newRoute) :+ Route(newRoute, classTag.runtimeClass.getName), showSaveButton = showSaveButton(newRoute))
    }
  }

  def showSaveButton(route: String) = {
    val router = play.api.Play.current.injector.instanceOf[Router]
    val pathList = router.documentation.filter(s => s._2 == route && s._1 == "GET").map(_._3).mkString
    pathList match {
      case "controllers.s_eligibility.GBenefits.present" |
           "controllers.s_eligibility.GEligibility.present" |
           "controllers.s_eligibility.CarersAllowance.approve" |
           "controllers.s_disclaimer.GDisclaimer.present" |
           "controllers.s_claim_date.GClaimDate.present" |
           "controllers.s_about_you.GYourDetails.present" |
           "controllers.s_about_you.GMaritalStatus.present" |
           "controllers.s_about_you.GContactDetails.present" |
           "controllers.third_party.GThirdParty.present" => false
      case _ => true
    }
  }

  def current: Route = if (routes.isEmpty) Route("", "") else routes.last

  def saveForLaterRoute(resumeRoute: String): Route = {
    if (resumeRoute.length() > 0) {
      Route(resumeRoute, "")
    }
    else if (routesAfterPreview.isEmpty) {
      current
    } else {
      routesAfterPreview.last
    }
  }

  def previous: Route = {
    val routesList = if (beenInPreview) routesAfterPreview else routes
    if (routesList.size > 1) routesList.dropRight(1).last
    else if (routesList.size == 1) current
    else Route("", "")
  }

  def apply[T](t: T)(implicit classTag: ClassTag[T]): Option[Route] = {
    val found = routes.find(_.className == t.getClass.getName)
    found
  }
}

case class Route(uri: String, className: String) {
  override def toString = uri
}
