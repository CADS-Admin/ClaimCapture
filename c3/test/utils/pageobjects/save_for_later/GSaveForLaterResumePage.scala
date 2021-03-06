package utils.pageobjects.save_for_later

import utils.WithJsBrowser
import utils.pageobjects._

final class GSaveForLaterResumePage(ctx:PageObjectsContext) extends ClaimPage(ctx, GSaveForLaterResumePage.url) {
  declareInput("#firstName","AboutYouFirstName")
  declareInput("#surname","AboutYouSurname")
  declareNino("#nationalInsuranceNumber","AboutYouNINO")
  declareDate("#dateOfBirth", "AboutYouDateOfBirth")
}

object GSaveForLaterResumePage {
  val decodeint = "174650142322392746796619227917559908601"
  val url = "/resume?x="+decodeint

  def apply(ctx:PageObjectsContext) = new GSaveForLaterResumePage(ctx)
}

/** The context for Specs tests */
trait GSaveForLaterResumePageContext extends PageContext {
  this: WithJsBrowser[_] =>

  val page = GSaveForLaterResumePage(PageObjectsContext(browser))
}
