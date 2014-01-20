package controllers.s2_about_you

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.{BrowserMatchers, Formulate}

class G10CompletedIntegrationSpec extends Specification with Tags {
  "About You" should {
    "be presented" in new WithBrowser with BrowserMatchers {
      browser.goTo("/about-you/completed")
      titleMustEqual("Completion - About you - the carer")
    }
    
    """navigate to "Your Partner" when next section is "Your Partner"""" in new WithBrowser with BrowserMatchers {
      Formulate.yourDetails(browser)
      Formulate.yourContactDetails(browser)
      Formulate.claimDate(browser)
      Formulate.nationalityAndResidency(browser)
      Formulate.otherEEAStateOrSwitzerland(browser)
      Formulate.moreAboutYou(browser)
      Formulate.employment(browser)
      titleMustEqual("Completion - About you - the carer")
      
      browser.find("#submit").getText mustEqual "Continue to partner/spouse"
      browser.submit("button[type='submit']")
      titleMustEqual("Partner/Spouse details - About your partner/spouse")
    }
    
    """navigate to "Care You Provide" when next section is "Care You Provide"""" in new WithBrowser with BrowserMatchers {
      Formulate.yourDetails(browser)
      Formulate.yourContactDetails(browser)
      Formulate.claimDate(browser)
      Formulate.nationalityAndResidency(browser)
      Formulate.otherEEAStateOrSwitzerland(browser)
      Formulate.moreAboutYouNotHadPartnerSinceClaimDate(browser)
      Formulate.employment(browser)
      titleMustEqual("Completion - About you - the carer")

      browser.find("#submit").getText mustEqual "Continue to Care you provide"
      browser.submit("button[type='submit']")
      titleMustEqual("Details of the person you care for - About the care you provide")
    }
  } section("integration", models.domain.AboutYou.id)
}