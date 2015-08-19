package controllers.preview

import org.specs2.mutable.{Tags, Specification}
import utils.WithBrowser
import utils.pageobjects.{TestData, PageObjectsContext, PageObjects}
import utils.pageobjects.preview.PreviewPage
import controllers.ClaimScenarioFactory
import utils.pageobjects.s_claim_date.GClaimDatePage
import utils.pageobjects.s_your_partner.GYourPartnerPersonalDetailsPage


class PreviewPagePartnerContentSpec extends Specification with Tags {

  "Preview Page" should {
    "display partner data - when you have a partner" in new WithBrowser with PageObjects{
      fillPartnerSection(context)
      val page =  PreviewPage(context)
      page goToThePage()
      val source = page.source

      source must contain("About your partner")
      source must contain("Name")
      source must contain("Mrs Cloe Scott Smith")
      source must contain("Date of birth")
      source must contain("12 July, 1990")
      source must contain("Have you separated since your claim date?")
      source must contain("Yes")
      source must contain("Is this the person you care for?")
      source must contain("Yes")
    }

    "display no data - when no partner" in new WithBrowser with PageObjects{
      val partnerData = new TestData
      partnerData.AboutYourPartnerHadPartnerSinceClaimDate = "No"
      fillPartnerSection(context, partnerData)
      val page =  PreviewPage(context)
      page goToThePage()
      val source = page.source

      source must contain ("About your partner")
      source must contain ("Have you lived with a partner at any time since your claim date?")
      source must contain ("No")

      source must not contain "Mrs Cloe Scott Smith"
      source must not contain "12 July, 1990"
      source must not contain "Have you separated since your claim date?"
    }
  } section "preview"

  def fillPartnerSection(context:PageObjectsContext, partnerClaim:TestData = ClaimScenarioFactory.s2ands3WithTimeOUtsideUKAndProperty()) = {
    val claimDatePage = GClaimDatePage(context)
    claimDatePage goToThePage()
    val claimDate = ClaimScenarioFactory.s12ClaimDate()
    claimDatePage fillPageWith claimDate

    val aboutYouPage =  claimDatePage submitPage()
    val claim = ClaimScenarioFactory.yourDetailsWithNotTimeOutside()
    claim.AboutYouMiddleName = "partnername"
    aboutYouPage goToThePage()
    aboutYouPage fillPageWith claim
    aboutYouPage submitPage()

    val partnerPage =  GYourPartnerPersonalDetailsPage(context)
    partnerPage goToThePage ()
    partnerPage fillPageWith partnerClaim
    partnerPage submitPage()

  }

}
