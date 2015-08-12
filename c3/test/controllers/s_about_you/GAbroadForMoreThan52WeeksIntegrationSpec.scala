package controllers.s_about_you

import org.specs2.mutable.{Tags, Specification}
import utils.{WebDriverHelper, WithBrowser}
import controllers.ClaimScenarioFactory
import utils.pageobjects.preview.PreviewPage
import utils.pageobjects.s_about_you._
import utils.pageobjects.{TestData, ClaimPageFactory, PageObjects}
import utils.WithJsBrowser

class GAbroadForMoreThan52WeeksIntegrationSpec extends Specification with Tags {
  "Abroad for more that 52 weeks" should {
    "present" in new WithJsBrowser  with PageObjects{
			val page =  GAbroadForMoreThan52WeeksPage(context)
      page goToThePage()
    }

    "provide for trip entry" in new WithJsBrowser  with PageObjects{
			val page =  GAbroadForMoreThan52WeeksPage(context)
      val claim = ClaimScenarioFactory abroadForMoreThan52WeeksConfirmationYes()
      page goToThePage()
      page fillPageWith claim
      val nextPage = page submitPage()

      nextPage must beAnInstanceOf[GOtherEEAStateOrSwitzerlandPage]
    }

    """go back to "Nationality and Residency".""" in new WithJsBrowser  with PageObjects{
			val page =  GNationalityAndResidencyPage(context)
      val claim = ClaimScenarioFactory yourNationalityAndResidencyResident()
      page goToThePage()

      page fillPageWith claim
      val nextPage = page submitPage()
      nextPage must beAnInstanceOf[GAbroadForMoreThan52WeeksPage]

      val backPage = nextPage goBack()
      backPage must beAnInstanceOf[GNationalityAndResidencyPage]
    }

    """remember "no more 52 weeks trips" upon stating "52 weeks trips" and returning""" in new WithJsBrowser  with PageObjects{
			val page =  GAbroadForMoreThan52WeeksPage(context)
      val claim = ClaimScenarioFactory abroadForMoreThan52WeeksConfirmationNo()
      page goToThePage()

      page fillPageWith claim
      val nextPage = page submitPage()
      nextPage must beAnInstanceOf[GOtherEEAStateOrSwitzerlandPage]

      val backPage = nextPage goBack()
      backPage must beAnInstanceOf[GAbroadForMoreThan52WeeksPage]

      backPage.ctx.browser.findFirst("#anyTrips_yes").isSelected should beFalse
      backPage.ctx.browser.findFirst("#anyTrips_no").isSelected should beTrue
    }

    "Trip details must not be visible when time abroad page is displayed" in new WithJsBrowser  with PageObjects{
      val page =  GAbroadForMoreThan52WeeksPage(context)
      page goToThePage()
      page.ctx.browser.findFirst("#anyTrips_yes").isSelected should beFalse
      page.ctx.browser.findFirst("#anyTrips_no").isSelected should beFalse
      page.ctx.browser.findFirst("#tripDetails").isDisplayed should beFalse
    }

    "Trip details must be visible when returning back to the time abroad page" in new WithJsBrowser  with PageObjects{
      val page =  GAbroadForMoreThan52WeeksPage(context)
      val claim = ClaimScenarioFactory abroadForMoreThan52WeeksConfirmationYes()
      page goToThePage()

      page fillPageWith claim
      val nextPage = page submitPage()
      nextPage must beAnInstanceOf[GOtherEEAStateOrSwitzerlandPage]

      val backPage = nextPage goBack()
      backPage must beAnInstanceOf[GAbroadForMoreThan52WeeksPage]

      backPage.ctx.browser.findFirst("#anyTrips_yes").isSelected should beTrue
      backPage.ctx.browser.findFirst("#anyTrips_no").isSelected should beFalse
      backPage.ctx.browser.findFirst("#tripDetails").isDisplayed should beTrue
    }

    "Modify time outside from preview page" in new WithJsBrowser  with PageObjects{

      val page =  GAbroadForMoreThan52WeeksPage(context)
      val claim = ClaimScenarioFactory.abroadForMoreThan52WeeksConfirmationNo()
      page goToThePage()
      page fillPageWith claim

      val nextPage = page submitPage()

      val id = "about_you_abroad"
      val previewPage = PreviewPage(context)
      previewPage goToThePage()
      previewPage.xpath(s"//dt[./a[@id='$id']]/following-sibling::dd").getText mustEqual "No"
      val abroadForMoreThan52WeeksPage = previewPage.clickLinkOrButton(s"#$id")

      abroadForMoreThan52WeeksPage must beAnInstanceOf[GAbroadForMoreThan52WeeksPage]

      abroadForMoreThan52WeeksPage fillPageWith ClaimScenarioFactory.abroadForMoreThan52WeeksConfirmationYes()

      val previewModifiedPage = abroadForMoreThan52WeeksPage submitPage()

      previewModifiedPage.xpath(s"//dt[./a[@id='$id']]/following-sibling::dd").getText mustEqual "Yes"

    }


  } section("integration", models.domain.AboutYou.id)
}