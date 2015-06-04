package utils.pageobjects.s2_about_you

import utils.WithBrowser
import utils.pageobjects._

class G5AbroadForMoreThan52WeeksPage (ctx:PageObjectsContext, iteration:Int) extends ClaimPage(ctx, G5AbroadForMoreThan52WeeksPage.url,iteration) {
  declareYesNo("#anyTrips", "AboutYouMoreTripsOutOfGBforMoreThan52WeeksAtATime_" + iteration)
  declareInput("#tripDetails", "AboutYouTripDetails_" + iteration)
}

/**
 * Companion object that integrates factory method.
 * It is used by PageFactory object defined in PageFactory.scala
 */
object G5AbroadForMoreThan52WeeksPage {
  val url  = "/about-you/abroad-for-more-than-52-weeks"

  def apply(ctx:PageObjectsContext, iteration:Int=1) = new G5AbroadForMoreThan52WeeksPage(ctx,iteration)
}

/** The context for Specs tests */
trait G5AbroadForMoreThan52WeeksPageContext extends PageContext {
  this: WithBrowser[_] =>

  val page = G5AbroadForMoreThan52WeeksPage (PageObjectsContext(browser) , iteration = 1)
}