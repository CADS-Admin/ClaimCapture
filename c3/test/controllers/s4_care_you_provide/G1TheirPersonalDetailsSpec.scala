package controllers.s4_care_you_provide

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import play.api.cache.Cache
import models.domain._
import models.{DayMonthYear, domain}
import play.api.test.Helpers._
import models.domain.Claim

class G1TheirPersonalDetailsSpec extends Specification with Tags {

  val theirPersonalDetailsInput = Seq("title" -> "Mr", "firstName" -> "John", "surname" -> "Doo",
    "dateOfBirth.day" -> "5", "dateOfBirth.month" -> "12", "dateOfBirth.year" -> "1990", "liveAtSameAddressCareYouProvide" -> "yes")

  "Their Personal Details - Controller" should {

    "present 'Their Personal Details'." in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)

      val result = G1TheirPersonalDetails.present(request)
      status(result) mustEqual OK
    }

    "add 'Their Personal Details' to the cached claim" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody(theirPersonalDetailsInput: _*)

      val result = G1TheirPersonalDetails.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(domain.CareYouProvide)

      section.questionGroup(TheirPersonalDetails) must beLike {
        case Some(t: TheirPersonalDetails) => {
          t.title mustEqual "Mr"
          t.firstName mustEqual "John"
          t.surname mustEqual "Doo"
          t.dateOfBirth mustEqual DayMonthYear(Some(5), Some(12), Some(1990), None, None)
          t.liveAtSameAddressCareYouProvide mustEqual "yes"
        }
      }
    }

    "return a bad request after an invalid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody("title" -> "Mr")

      val result = G1TheirPersonalDetails.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody(theirPersonalDetailsInput: _*)

      val result = G1TheirPersonalDetails.submit(request)
      redirectLocation(result) must beSome("/careYouProvide/theirContactDetails")
    }
  } section("unit", models.domain.CareYouProvide.id)
}