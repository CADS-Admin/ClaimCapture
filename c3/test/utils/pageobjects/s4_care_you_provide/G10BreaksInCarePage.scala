package utils.pageobjects.s4_care_you_provide

import play.api.test.{WithBrowser, TestBrowser}
import utils.pageobjects.{PageContext, Page}

final class G10BreaksInCarePage(browser: TestBrowser, previousPage: Option[Page] = None, iteration: Int) extends Page(browser, G10BreaksInCarePage.url, G10BreaksInCarePage.title, previousPage, iteration) {
   declareYesNo("#answer", "AboutTheCareYouProvideHaveYouHadAnyMoreBreaksInCare_" + iteration)
}

/**
 * Companion object that integrates factory method.
 * It is used by PageFactory object defined in PageFactory.scala
 */
object G10BreaksInCarePage {
  val title = "Breaks in care - About the care you provide"

  val url  = "/care-you-provide/breaks-in-care"

  def buildPageWith(browser: TestBrowser, previousPage: Option[Page] = None, iteration:Int) = new G10BreaksInCarePage(browser,previousPage,iteration)
}

/** The context for Specs tests */
trait G10BreaksInCarePageContext extends PageContext {
  this: WithBrowser[_] =>

  val page = G10BreaksInCarePage buildPageWith (browser = browser, iteration = 1)
}