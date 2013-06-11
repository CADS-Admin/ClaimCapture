package controllers

import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, WithApplication}
import play.api.cache.Cache
import models.claim.{YourDetailsForm, Section, Claim}
import play.api.test.Helpers._

class AboutYouSpec extends Specification {
  "About you" should {
    "accept all initial mandatory data" in new WithApplication with Claiming {
      val request = FakeRequest()
                      .withSession("connected" -> claimKey)
                      .withFormUrlEncodedBody("firstName" -> "Scooby")

      val result = AboutYou.yourDetailsSubmit(request)
      redirectLocation(result) must beSome("/aboutyou/contactDetails")

      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section("s2").get

      section.form("s2.g1") must beLike {
        case Some(f: YourDetailsForm) => f.firstName mustEqual "Scooby"
      }
    }
  }
}