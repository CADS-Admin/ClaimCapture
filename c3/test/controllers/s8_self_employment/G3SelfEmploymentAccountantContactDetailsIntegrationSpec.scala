package controllers.s8_self_employment

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import utils.pageobjects.s8_self_employment._
import utils.pageobjects.ClaimScenario
import controllers.ClaimScenarioFactory
import utils.pageobjects.s2_about_you.{G8AboutYouCompletedPage, G4ClaimDatePageContext}
import utils.pageobjects.s9_other_money.G1AboutOtherMoneyPage

class G3SelfEmploymentAccountantContactDetailsIntegrationSpec extends Specification with Tags {

  "About Self Employment - Account Contact Details " should {
    "be presented" in new WithBrowser with G2SelfEmploymentYourAccountsPageContext {

      val claim = ClaimScenarioFactory.s9SelfEmploymentYourAccounts
      page goToThePage()
      page fillPageWith claim
      page.submitPage(waitForPage = true, waitDuration = 1000)

      val nextPage = page goToPage(throwException = false, page = new G3SelfEmploymentAccountantContactDetailsPage(browser), waitForPage = true, waitDuration = 1000)
      nextPage must beAnInstanceOf[G3SelfEmploymentAccountantContactDetailsPage]
    }

    "not be presented if section not visible" in new WithBrowser with G4ClaimDatePageContext {
      val claim = ClaimScenarioFactory.s2AnsweringNoToQuestions()
      page goToThePage(waitForPage = true, waitDuration = 1000)
      page runClaimWith (claim, G8AboutYouCompletedPage.title, waitForPage = true, waitDuration = 1000)

      val nextPage = page goToPage(throwException = false, page = new G3SelfEmploymentAccountantContactDetailsPage(browser), waitForPage = true, waitDuration = 1000)
      nextPage must beAnInstanceOf[G1AboutOtherMoneyPage]
    }

    "contain errors on invalid submission" in {
      "missing mandatory field" in new WithBrowser with G3SelfEmploymentAccountantContactDetailsPageContext {

        val claimYourAccounts = ClaimScenarioFactory.s9SelfEmploymentYourAccounts
        val pageYourAccounts = new G2SelfEmploymentYourAccountsPage(browser)
        pageYourAccounts goToThePage(waitForPage = true, waitDuration = 1000)
        pageYourAccounts fillPageWith claimYourAccounts
        pageYourAccounts.submitPage(waitForPage = true, waitDuration = 1000)

        val claim = new ClaimScenario
        page goToThePage(waitForPage = true, waitDuration = 1000)
        page fillPageWith claim
        val pageWithErrors = page.submitPage(waitForPage = true, waitDuration = 1000)
        pageWithErrors.listErrors.size mustEqual 2
      }
    }

    "accept submit if all mandatory fields are populated" in new WithBrowser with G3SelfEmploymentAccountantContactDetailsPageContext {

      val claimYourAccounts = ClaimScenarioFactory.s9SelfEmploymentYourAccounts
      val pageYourAccounts = new G2SelfEmploymentYourAccountsPage(browser)
      pageYourAccounts goToThePage(waitForPage = true, waitDuration = 1000)
      pageYourAccounts fillPageWith claimYourAccounts
      pageYourAccounts.submitPage(waitForPage = true, waitDuration = 1000)

      val claim = ClaimScenarioFactory.s9SelfEmploymentAccountantContactDetails
      page goToThePage(waitForPage = true, waitDuration = 1000)
      page fillPageWith claim
      page submitPage()
    }

    "navigate to next page on valid submission" in new WithBrowser with G3SelfEmploymentAccountantContactDetailsPageContext {

      val claimYourAccounts = ClaimScenarioFactory.s9SelfEmploymentYourAccounts
      val pageYourAccounts = new G2SelfEmploymentYourAccountsPage(browser)
      pageYourAccounts goToThePage(waitForPage = true, waitDuration = 1000)
      pageYourAccounts fillPageWith claimYourAccounts
      pageYourAccounts.submitPage(waitForPage = true, waitDuration = 1000)


      val claim = ClaimScenarioFactory.s9SelfEmploymentAccountantContactDetails
      page goToThePage(waitForPage = true, waitDuration = 1000)
      page fillPageWith claim

      val nextPage = page submitPage(waitForPage = true, waitDuration = 1000)

      nextPage must not(beAnInstanceOf[G6ChildcareProvidersContactDetailsPage])
    }

  } section("unit", models.domain.SelfEmployment.id)
}