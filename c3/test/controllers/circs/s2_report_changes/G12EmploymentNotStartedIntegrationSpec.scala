package controllers.circs.s2_report_changes

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeApplication, WithBrowser}
import utils.pageobjects.PageObjects
import utils.pageobjects.circumstances.s2_report_changes.G12EmploymentNotStartedPage

class G12EmploymentNotStartedIntegrationSpec extends Specification with Tags {
   "Report an Employment change in your circumstances where employment has not started - Employment" should {
     "be presented" in new WithBrowser(app = FakeApplication(additionalConfiguration = Map("circs.employment.active" -> "true"))) with PageObjects {
       pending("throws JavaScript error because test browser is not capable of handling the level of JS used")
       val page = G12EmploymentNotStartedPage(context)
       page goToThePage()
     }
   } section("integration", models.domain.CircumstancesIdentification.id)
 }
