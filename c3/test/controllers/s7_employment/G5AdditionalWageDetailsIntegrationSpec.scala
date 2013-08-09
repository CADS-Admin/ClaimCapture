package controllers.s7_employment

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.ClaimScenarioFactory
import utils.pageobjects.s7_employment._

class G5AdditionalWageDetailsIntegrationSpec extends Specification with Tags {
  "Additional wage details" should {
    "be presented" in new WithBrowser with G5AdditionalWageDetailsPageContext {
      page goToThePage()
    }

    "contain 1 completed form" in new WithBrowser with G5AdditionalWageDetailsPageContext {
      val claim = ClaimScenarioFactory s7Employment()
      page goToThePage()
      page fillPageWith claim
      page submitPage() match {
        case p: G6MoneyOwedByEmployerPage => p numberSectionsCompleted()  mustEqual 1
        case _ => ko("Next Page is not of the right type.")
      }
    }

    "be able to navigate back to a completed form" in new WithBrowser  with G4LastWagePageContext {
      val claim = ClaimScenarioFactory s7Employment()
      page goToThePage()
      page fillPageWith claim
      val submitted = page submitPage()
      val backPage = submitted goBack ()
      backPage must beAnInstanceOf[G4LastWagePage]
    }
  } section("integration",models.domain.Employed.id)
}