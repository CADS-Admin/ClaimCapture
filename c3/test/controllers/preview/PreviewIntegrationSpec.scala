package controllers.preview

import org.specs2.mutable.{Tags, Specification}
import utils.WithJsBrowser
import utils.pageobjects.PageObjects
import utils.pageobjects.preview.PreviewPage
import utils.pageobjects.s_about_you.GYourDetailsPage
import utils.pageobjects.s_information.GAdditionalInfoPage
import controllers.{BrowserMatchers, ClaimScenarioFactory}
import utils.pageobjects.s_pay_details.GHowWePayYouPage
import utils.pageobjects.s_consent_and_declaration.GDeclarationPage
import play.api.i18n.{MMessages => Messages}
import utils.helpers.PreviewField._

class PreviewIntegrationSpec extends Specification with Tags {
  "Preview" should{
    "be presented" in new WithJsBrowser with PageObjects{
      val page =  PreviewPage(context)
      page goToThePage()
    }

    "navigate back to Additional Info page" in new WithJsBrowser with PageObjects{

      val additionalInfoPage = GAdditionalInfoPage(context)
      val additionalInfoData = ClaimScenarioFactory.s11ConsentAndDeclaration
      additionalInfoPage goToThePage ()
      additionalInfoPage fillPageWith additionalInfoData
      val previewPage = additionalInfoPage submitPage()
      previewPage must beAnInstanceOf[PreviewPage]
      previewPage goBack() must beAnInstanceOf[GAdditionalInfoPage]
    }

    "navigate to Declaration page" in new WithJsBrowser with PageObjects {
       val previewPage = PreviewPage(context)
       previewPage goToThePage()
       val declarationPage = previewPage submitPage()
      declarationPage must beAnInstanceOf[GDeclarationPage]
    }

    "navigate back to preview page clicking next" in new WithJsBrowser with PageObjects {
      val additionalInfoPage = GAdditionalInfoPage(context)
      val additionalInfoData = ClaimScenarioFactory.s11ConsentAndDeclaration
      additionalInfoPage goToThePage ()
      additionalInfoPage fillPageWith additionalInfoData
      val previewPage = additionalInfoPage submitPage()
      previewPage must beAnInstanceOf[PreviewPage]
      val additionalPage = previewPage goBack()
      additionalPage must beAnInstanceOf[GAdditionalInfoPage]
      additionalPage submitPage () must beAnInstanceOf[PreviewPage]
    }

    "navigate back to how we pay you page" in new WithJsBrowser with PageObjects {
      val howWePayYouPage = GHowWePayYouPage(context)
      howWePayYouPage goToThePage()
      howWePayYouPage fillPageWith ClaimScenarioFactory.s6PayDetails
      howWePayYouPage submitPage()
      val additionalInfoPage = GAdditionalInfoPage(context)
      val additionalInfoData = ClaimScenarioFactory.s11ConsentAndDeclaration
      additionalInfoPage goToThePage ()
      additionalInfoPage fillPageWith additionalInfoData
      val previewPage = additionalInfoPage submitPage()
      previewPage must beAnInstanceOf[PreviewPage]
      val additionalPage = previewPage goBack()
      additionalPage must beAnInstanceOf[GAdditionalInfoPage]
      additionalPage goBack () must beAnInstanceOf[GHowWePayYouPage]
    }

    "change Next button text to 'Return to summary'" in new WithJsBrowser with PageObjects {

      val previewPage = PreviewPage(context) goToThePage()
      previewPage must beAnInstanceOf[PreviewPage]
      browser.findFirst(getLinkId("about_you_contact")).click()
      browser.findFirst("button[value='next']").getText mustEqual Messages("form.returnToSummary")

    }

  }section "preview"
}
