package controllers.s_self_employment

import org.specs2.mutable._
import utils.{WithApplication, WithBrowser}
import utils.pageobjects.{PageObjects, TestData}
import controllers.ClaimScenarioFactory
import utils.pageobjects.your_income.{GStatutorySickPayPage, GYourIncomePage}
import utils.pageobjects.s_claim_date.GClaimDatePageContext

class GSelfEmploymentYourAccountsIntegrationSpec extends Specification {
/*
  section("integration", models.domain.SelfEmployment.id)
  "Self Employment - Your Accounts" should {
    "be presented" in new WithBrowser with PageObjects{
			val page =  GSelfEmploymentYourAccountsPage(context)
      page goToThePage()
    }

    "not be presented if section not visible" in new WithBrowser with GClaimDatePageContext {
      val claim = ClaimScenarioFactory.s4CareYouProvideWithNoBreaksInCareWithNoEducationAndNotEmployed()
      page goToThePage()

      val employmentHistoryPage = page runClaimWith(claim, GYourIncomePage.url, waitForPage = true)
      employmentHistoryPage fillPageWith(claim)

      val nextPage = employmentHistoryPage submitPage()
      nextPage must beAnInstanceOf[GStatutorySickPayPage]
    }

    "contain errors on invalid submission" in new WithApplication {
      "your accounts invalid date" in new WithBrowser with PageObjects{
			  val page =  GSelfEmploymentYourAccountsPage(context)
        val claim = new TestData
        claim.SelfEmployedAretheIncomeOutgoingSimilartoYourCurrent = "no"
        claim.SelfEmployedTellUsWhyandWhentheChangeHappened = "A Year back"
        claim.SelfEmployedDoYouKnowYourTradingYear = "yes"
        claim.SelfEmployedWhatWasIsYourTradingYearfrom = "01/01/0000"
        page goToThePage()
        page fillPageWith claim
        val pageWithErrors = page.submitPage()
        pageWithErrors.listErrors.size mustEqual 1
        pageWithErrors.listErrors(0).contains("date")
      }

      "your accounts do you know your trading year not set" in new WithBrowser with PageObjects {
        val page =  GSelfEmploymentYourAccountsPage(context)
        val claim = new TestData
        claim.SelfEmployedDoYouKnowYourTradingYear = ""
        page goToThePage()
        page fillPageWith claim
        val pageWithErrors = page.submitPage()
        pageWithErrors.listErrors.size mustEqual 1
        pageWithErrors.listErrors(0).contains("do you know your trading year")
      }
    }

    "your accounts tell us what happened not required if incoming and outgoing are current " in new WithBrowser with PageObjects {
			val page =  GSelfEmploymentYourAccountsPage(context)
      val claim = new TestData
      claim.SelfEmployedDoYouKnowYourTradingYear = "yes"
      claim.SelfEmployedAretheIncomeOutgoingSimilartoYourCurrent = "yes"
      page goToThePage()
      page fillPageWith claim
      val pageWithErrors = page.submitPage()
      pageWithErrors.listErrors.size mustEqual 0
    }

    "your accounts contact your accountant is not required if there is no accountant " in new WithBrowser with PageObjects {
			val page =  GSelfEmploymentYourAccountsPage(context)
      val claim = new TestData
      claim.SelfEmployedDoYouKnowYourTradingYear = "yes"
      claim.SelfEmployedAretheIncomeOutgoingSimilartoYourCurrent = "yes"
      page goToThePage()
      page fillPageWith claim
      val pageWithErrors = page.submitPage()
      pageWithErrors.listErrors.size mustEqual 0
    }

    "accept submit if all mandatory fields are populated" in new WithBrowser with PageObjects {
			val page =  GSelfEmploymentYourAccountsPage(context)
      val claim = ClaimScenarioFactory.s9SelfEmploymentYourAccounts
      page goToThePage()
      page fillPageWith claim
      page submitPage()
    }

    "navigate to next page on valid submission" in new WithBrowser with PageObjects {
			val page =  GSelfEmploymentYourAccountsPage(context)
      val claim = ClaimScenarioFactory.s9SelfEmploymentYourAccounts
      page goToThePage()
      page fillPageWith claim

      val nextPage = page submitPage()

      nextPage must not(beAnInstanceOf[GSelfEmploymentYourAccountsPage])
    }

    "present accounts page with correct start and end tax year help tags" in new WithBrowser with PageObjects {
      val page = GSelfEmploymentYourAccountsPage(context)
      page goToThePage()
      page.source must contain("whatWasOrIsYourTradingYearFrom_defaultDateContextualHelp")
      page.source must contain("For example, 6 4 1968")
      page.source must contain("whatWasOrIsYourTradingYearTo_defaultDateContextualHelp")
      page.source must contain("For example, 5 4 1969")
    }
  }
  section("integration", models.domain.SelfEmployment.id)
  */
}