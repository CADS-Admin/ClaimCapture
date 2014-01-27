package app.claim

import play.api.test.WithBrowser
import utils.pageobjects.s1_carers_allowance.G1BenefitsPageContext
import utils.pageobjects.{IterationManager, XmlPage, TestData, Page}
import utils.pageobjects.xml_validation.{XMLClaimBusinessValidation, XMLBusinessValidation}
import app.FunctionalTestCommon

/**
 * End-to-End functional tests using input files created by Steve Moody.
 * @author Jorge Migueis
 *         Date: 02/08/2013
 */
class FunctionalTestCase7Spec extends FunctionalTestCommon {
  isolated

  "The application " should {

    val claim = TestData.readTestDataFromFile("/functional_scenarios/ClaimScenario_TestCase7.csv")
    "Successfully run absolute Test Case 7 " in new WithBrowser with G1BenefitsPageContext {

      IterationManager.init()
      page goToThePage()
      val lastPage = page runClaimWith(claim, XmlPage.title)

      lastPage match {
        case p: XmlPage => {
          val validator: XMLBusinessValidation = new XMLClaimBusinessValidation
          validateAndPrintErrors(p, claim, validator) should beTrue
        }
        case p: Page => println(p.source()); failure("bad")
      }
    }
  } section "functional"
}