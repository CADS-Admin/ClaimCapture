package integration.s4_care_you_provide

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import integration.Helper

class G5PreviousCarerContactDetailsSpec extends Specification with Tags {

   "Previous Carer Contact Details" should {
     "be presented" in new WithBrowser {
       browser.goTo("/careYouProvide/previousCarerContactDetails")
       browser.title() mustEqual "Contact Details Of The Person Who Claimed Before - Care You Provide"
     }

     "contain errors on invalid submission" in new WithBrowser {
       browser.goTo("/careYouProvide/previousCarerContactDetails")
       browser.fill("#postcode") `with` "INVALID"
       browser.submit("button[type='submit']")
       browser.find("div[class=validation-summary] ol li").size mustEqual 1
     }
/*
     "be prepopulated if they live at same address" in new WithBrowser {
        Helper.fillYourContactDetails(browser)
        Helper.fillTheirPersonalDetails(browser)
        browser.title() mustEqual "Contact Details Of The Person Who Claimed Before - Care You Provide"
        browser.find("#address_lineOne").getValue mustEqual("My Address")
        browser.find("#postcode").getValue mustEqual("SE1 6EH")
     }

     "be blank if they live at different address" in new WithBrowser {
       Helper.fillYourContactDetails(browser)
       browser.goTo("/careYouProvide/theirPersonalDetails")
       browser.click("#title option[value='mr']")
       browser.fill("#firstName") `with` "John"
       browser.fill("#surname") `with` "Appleseed"
       browser.click("#dateOfBirth_day option[value='3']")
       browser.click("#dateOfBirth_month option[value='4']")
       browser.fill("#dateOfBirth_year") `with` "1950"
       browser.click("#liveAtSameAddress_no]")
       browser.submit("button[type='submit']")
       browser.find("#address_lineOne").getValue mustEqual("")
       browser.find("#postcode").getValue mustEqual("")
     }

     "be blank if they live at same address but did not enter one" in new WithBrowser {
       Helper.fillTheirPersonalDetails(browser)
       browser.find("#address_lineOne").getValue mustEqual("")
       browser.find("#postcode").getValue mustEqual("")
     }

     "navigate back to Their Personal Details" in new WithBrowser {
       browser.goTo("/careYouProvide/theirContactDetails")
       browser.click("#backButton")
       browser.title() mustEqual "Their Personal Details - Care You Provide"
     }

     "navigate to next page on valid submission" in new WithBrowser {
       Helper.fillTheirContactDetails(browser)

       browser.title() mustEqual "More About The Person You Care For - Care You Provide"
     }

     "overwrite cached contact details after going back and changing answer to living at same address" in new WithBrowser {
       Helper.fillTheirContactDetails(browser)
       browser.click("#backButton")
       browser.find("#address_lineOne").getValue mustEqual("Their Address")
       browser.click("#backButton")
       Helper.fillYourContactDetails(browser)
       Helper.fillTheirPersonalDetails(browser)

       browser.title() mustEqual "Their Contact Details - Care You Provide"
       browser.find("#address_lineOne").getValue mustEqual("My Address")
       browser.find("#postcode").getValue mustEqual("SE1 6EH")
     }
*/
   }
 }
