package controllers.s2_about_you

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.{ClaimScenarioFactory, BrowserMatchers, Formulate}
import play.api.i18n.Messages
import utils.pageobjects.s2_about_you.{G5AbroadForMoreThan52WeeksPage, G4NationalityAndResidencyPage, G4NationalityAndResidencyPageContext}

class G4NationalityAndResidencyIntegrationSpec extends Specification with Tags {
  sequential

  val urlUnderTest = "/about-you/nationality-and-residency"
  val submitButton = "button[type='submit']"
  val errorDiv = "div[class=validation-summary] ol li"

  "Nationality and Residency" should {
    "be presented" in new WithBrowser with G4NationalityAndResidencyPageContext {
      page goToThePage()
    }

    "contain errors on invalid submission" in new WithBrowser with G4NationalityAndResidencyPageContext {
      page goToThePage()
      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[G4NationalityAndResidencyPage]
    }

    "navigate to next page on valid resident submission" in new WithBrowser with G4NationalityAndResidencyPageContext {
      val claim = ClaimScenarioFactory.yourNationalityAndResidencyResident
      page goToThePage()
      page fillPageWith claim

      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[G5AbroadForMoreThan52WeeksPage]
    }

    "navigate to next page on valid non resident submission" in new WithBrowser with G4NationalityAndResidencyPageContext {
      val claim = ClaimScenarioFactory.yourNationalityAndResidencyNonResident
      page goToThePage()
      page fillPageWith claim

      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[G5AbroadForMoreThan52WeeksPage]
    }

    "contain errors on invalid non resident submission" in new WithBrowser with G4NationalityAndResidencyPageContext {
      val claim = ClaimScenarioFactory.yourNationalityAndResidencyNonResident
      claim.AboutYouNationalityAndResidencyNormalResidency = ""
      page goToThePage()
      page fillPageWith claim

      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[G4NationalityAndResidencyPage]
    }

  } section("integration", models.domain.AboutYou.id)
}
