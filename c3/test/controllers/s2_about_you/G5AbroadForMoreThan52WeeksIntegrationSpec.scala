package controllers.s2_about_you

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.ClaimScenarioFactory
import utils.pageobjects.s2_about_you._

class G5AbroadForMoreThan52WeeksIntegrationSpec extends Specification with Tags {
  "Abroad for more that 52 weeks" should {
    "present" in new WithBrowser with G5AbroadForMoreThan52WeeksPageContext {
      page goToThePage()
    }

    "provide for trip entry" in new WithBrowser with G5AbroadForMoreThan52WeeksPageContext {
      val claim = ClaimScenarioFactory abroadForMoreThan52WeeksConfirmationYes()
      page goToThePage()
      page fillPageWith claim
      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[G6TripPage]
    }

    """present "completed" when no more 52 week trips are required""" in new WithBrowser with G5AbroadForMoreThan52WeeksPageContext {
      val claim = ClaimScenarioFactory abroadForMoreThan52WeeksConfirmationNo()
      page goToThePage()

      page fillPageWith claim
      val nextPage = page submitPage()
      nextPage must beAnInstanceOf[G7OtherEEAStateOrSwitzerlandPage]
    }

    """go back to "Nationality and Residency".""" in new WithBrowser with G4NationalityAndResidencyPageContext {
      val claim = ClaimScenarioFactory yourNationalityAndResidencyResident()
      page goToThePage()

      page fillPageWith claim
      val nextPage = page submitPage()
      nextPage must beAnInstanceOf[G5AbroadForMoreThan52WeeksPage]

      val backPage = nextPage goBack()
      backPage must beAnInstanceOf[G4NationalityAndResidencyPage]
    }

    """remember "no more 52 weeks trips" upon stating "52 weeks trips" and returning""" in new WithBrowser with G5AbroadForMoreThan52WeeksPageContext {
      val claim = ClaimScenarioFactory abroadForMoreThan52WeeksConfirmationNo()
      page goToThePage()

      page fillPageWith claim
      val nextPage = page submitPage()
      nextPage must beAnInstanceOf[G7OtherEEAStateOrSwitzerlandPage]

      val backPage = nextPage goBack()
      backPage must beAnInstanceOf[G5AbroadForMoreThan52WeeksPage]

      backPage.browser.findFirst("#anyTrips_yes").isSelected should beFalse
      backPage.browser.findFirst("#anyTrips_no").isSelected should beTrue
    }

  } section("integration", models.domain.AboutYou.id)
}
