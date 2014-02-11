package utils.pageobjects.circumstances.s1_about_you

import play.api.test.WithBrowser
import utils.pageobjects.{PageObjectsContext, CircumstancesPage, PageContext, Page}

final class G1ReportAChangeInYourCircumstancesPage(ctx:PageObjectsContext) extends CircumstancesPage(ctx, G1ReportAChangeInYourCircumstancesPage.url, G1ReportAChangeInYourCircumstancesPage.title) {
  declareInput("#fullName","CircumstancesAboutYouFullName")
  declareNino("#nationalInsuranceNumber","CircumstancesAboutYouNationalInsuranceNumber")
  declareDate("#dateOfBirth", "CircumstancesAboutYouDateOfBirth")
}

/**
 * Companion object that integrates factory method.
 * It is used by PageFactory object defined in PageFactory.scala
 */
object G1ReportAChangeInYourCircumstancesPage {
  val title = "Report a change in your circumstances - Change in circumstances".toLowerCase

//  val url  = "/circumstances/identification/report-a-change-in-your-circumstances"
  val url  = "/circumstances/identification/about-you"

  def apply(ctx:PageObjectsContext) = new G1ReportAChangeInYourCircumstancesPage(ctx)
}

/** The context for Specs tests */
trait G1ReportAChangeInYourCircumstancesPageContext extends PageContext {
  this: WithBrowser[_] =>

  val page = G1ReportAChangeInYourCircumstancesPage(PageObjectsContext(browser))
}
