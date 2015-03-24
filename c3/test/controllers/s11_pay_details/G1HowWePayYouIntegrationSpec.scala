package controllers.s11_pay_details

import org.specs2.mutable.{Tags, Specification}
import play.api.Logger
import play.api.test.WithBrowser
import controllers.{ClaimScenarioFactory, BrowserMatchers, Formulate}
import utils.pageobjects.s9_other_money._
import utils.pageobjects.s11_pay_details.{G2BankBuildingSocietyDetailsPage, G1HowWePayYouPage}
import utils.pageobjects.s10_information.G1AdditionalInfoPage
import utils.pageobjects.{PageObjects, PageObjectsContext}

class G1HowWePayYouIntegrationSpec extends Specification with Tags {
  "Pay details" should {
    "be presented" in new WithBrowser with PageObjects {
      val page =  G1AboutOtherMoneyPage(context)
      page goToThePage()
    }

    "be hidden when having state pension" in new WithBrowser with BrowserMatchers {
      Formulate.claimDate(browser)
      Formulate.yourDetails(browser)
      Formulate.nationalityAndResidency(browser)
      Formulate.otherEEAStateOrSwitzerland(browser)

      browser.goTo(G1HowWePayYouPage.url)
      urlMustEqual(G1AdditionalInfoPage.url)
    }

    "contain errors on invalid submission" in new WithBrowser with PageObjects  {
      val page =  G1AboutOtherMoneyPage(context)
      page goToThePage()
      val samePage = page.submitPage()
      Logger.info(samePage.toString)
      samePage.listErrors.nonEmpty must beTrue
    }

    "navigate to next page on valid submission" in new WithBrowser with BrowserMatchers {
      Formulate.howWePayYou(browser)
      urlMustEqual(G2BankBuildingSocietyDetailsPage.url)
    }

    /**
     * This test case has been modified to be in line with the new Page Object pattern.
     * Please modify the other test cases when you address them
     */
    "navigate back to Other Statutory Pay - Other Money" in new WithBrowser with PageObjects{
			val page =  G1AboutOtherMoneyPage(context)
      val claim = ClaimScenarioFactory.s9otherMoney
      page goToThePage()
      page fillPageWith claim
      page submitPage()

      val OtherStatutoryPage = page goToPage new G1AboutOtherMoneyPage(PageObjectsContext(browser))
      OtherStatutoryPage fillPageWith claim
      OtherStatutoryPage submitPage()

      val howWePayPage = OtherStatutoryPage goToPage new G1HowWePayYouPage(PageObjectsContext(browser))
      val previousPage = howWePayPage goBack()
      previousPage must beAnInstanceOf[G1AboutOtherMoneyPage]
    }

    "navigate to 'Consent And Declaration'" in new WithBrowser with PageObjects{
			val page =  G1HowWePayYouPage(context)
      val claim = ClaimScenarioFactory.s6PayDetails()
      page goToThePage()
      page fillPageWith claim
      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[G1AdditionalInfoPage]
    }

  } section("integration", models.domain.PayDetails.id)
}