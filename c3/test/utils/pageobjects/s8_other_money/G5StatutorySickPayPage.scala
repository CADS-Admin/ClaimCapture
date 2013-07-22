package utils.pageobjects.s8_other_money

import play.api.test.TestBrowser
import utils.pageobjects.{ClaimScenario, PageContext, Page}
import controllers.s8_other_money.routes

final class G5StatutorySickPayPage(browser: TestBrowser, previousPage: Option[Page] = None) extends Page(browser, G5StatutorySickPayPage.url, G5StatutorySickPayPage.title, previousPage) {

  def fillPageWith(theClaim: ClaimScenario) {
    fillYesNo("#haveYouHadAnyStatutorySickPay", theClaim.OtherMoneyStatutorySickPayHaveYouHadAnyStatutorySickPay)
    fillInput("#howMuch", theClaim.OtherMoneyStatutorySickPayHowMuch)
    fillInput("#howMuch_howOften", theClaim.OtherMoneyStatutorySickPayHowMuchHowOften)
    fillInput("#employersName", theClaim.OtherMoneyStatutorySickPayEmployersName)
    fillAddress("#employersAddress", theClaim.OtherMoneyStatutorySickPayPersonEmployersAddress)
    fillInput("#employersPostcode", theClaim.OtherMoneyStatutorySickPayEmployersPostCode)
  }
}

object G5StatutorySickPayPage {
  val title = "Statutory Sick Pay - Other Money"
  val url = "/otherMoney/statutorySickPay"

  def buildPageWith(browser: TestBrowser, previousPage: Option[Page] = None) = new G5StatutorySickPayPage(browser, previousPage)
}

trait G5StatutorySickPayPageContext extends PageContext {
  this: {val browser: TestBrowser} =>
  val page = G5StatutorySickPayPage buildPageWith browser
}
