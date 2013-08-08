package controllers.s5_time_spent_abroad

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.{Formulate, BrowserMatchers}

class G2AbroadForMoreThan4WeeksIntegrationSpec extends Specification with Tags {
  "Abroad for more that 4 weeks" should {
    "present" in new WithBrowser with BrowserMatchers {
      browser.goTo("/timeSpentAbroad/abroadForMoreThan4Weeks")
      titleMustEqual("When you went abroad for more than 4 - Time Spent Abroad")
    }

    "provide for trip entry" in new WithBrowser with BrowserMatchers {
      browser.goTo("/timeSpentAbroad/abroadForMoreThan4Weeks")
      titleMustEqual("When you went abroad for more than 4 - Time Spent Abroad")

      browser.click("#anyTrips_yes")
      browser.submit("button[value='next']")
      titleMustEqual("Trip - Time Spent Abroad")
    }

    """present "52 weeks trips" when no more 4 week trips are required""" in new WithBrowser with BrowserMatchers {
      browser.goTo("/timeSpentAbroad/abroadForMoreThan4Weeks")
      titleMustEqual("When you went abroad for more than 4 - Time Spent Abroad")

      browser.click("#anyTrips_no")
      browser.submit("button[value='next']")
      titleMustEqual("When you went abroad for more than 52 - Time Spent Abroad")
    }

    """go back to "normal residence and current location".""" in new WithBrowser with BrowserMatchers {
      Formulate.normalResidenceAndCurrentLocation(browser)
      titleMustEqual("When you went abroad for more than 4 - Time Spent Abroad")

      browser.click("#backButton")
      titleMustEqual("Your normal residence and current location - Time Spent Abroad")
    }
  } section("integration",models.domain.TimeSpentAbroad.id)
}