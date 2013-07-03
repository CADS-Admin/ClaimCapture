package controllers.s4_care_you_provide

import org.specs2.mutable.{Tags, Specification}
import controllers.{WithBrowserAndMatchers, FormHelper}

class G9ContactDetailsOfPayingPersonIntegrationSpec extends Specification with Tags {
  "Contact details of paying person" should {
    "be presented" in new WithBrowserAndMatchers {
      FormHelper.fillMoreAboutTheCare(browser)

      browser.goTo("/careYouProvide/contactDetailsOfPayingPerson")
      titleMustEqual("Contact Details of Paying Person - Care You Provide")
    }

    """be submitted and proceed to "breaks" """ in new WithBrowserAndMatchers {
      browser.goTo("/careYouProvide/contactDetailsOfPayingPerson")
      browser.submit("button[value='next']")

      titleMustEqual("Breaks in Care - Care You Provide")
    }

    """be submitted with data, proceed to "breaks" and go back""" in new WithBrowserAndMatchers {
      FormHelper.fillMoreAboutTheCare(browser)

      browser.goTo("/careYouProvide/contactDetailsOfPayingPerson")
      browser.fill("#postcode") `with` "BLAH"
      browser.submit("button[value='next']")
      titleMustEqual("Breaks in Care - Care You Provide")

      browser.click("#backButton")
      browser.$("#postcode").getValue mustEqual "BLAH"
    }
  } section "integration"
}