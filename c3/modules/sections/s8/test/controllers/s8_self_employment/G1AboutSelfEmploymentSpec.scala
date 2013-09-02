package controllers.s8_self_employment

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import models.domain._
import play.api.test.Helpers._
import models.DayMonthYear
import play.api.cache.Cache
import models.domain.Claim
import scala.Some
import models.view.CachedClaim


class G1AboutSelfEmploymentSpec extends Specification with Tags {

  "Self Employment - About Self Employment - Controller" should {
    val areYouSelfEmployedNow = "yes"
    val startDay = 11
    val startMonth = 11
    val startYear = 2011
    val finishDay = 11
    val finishMonth = 11
    val finishYear = 2030
    val haveYouCeasedTrading = "yes"
    val natureOfYourBusiness = "Consulting"

    val aboutSelfEmploymentInput = Seq("areYouSelfEmployedNow" -> areYouSelfEmployedNow,
      "whenDidYouStartThisJob.day" -> startDay.toString,
      "whenDidYouStartThisJob.month" -> startMonth.toString,
      "whenDidYouStartThisJob.year" -> startYear.toString,
      "whenDidTheJobFinish.day" -> finishDay.toString,
      "whenDidTheJobFinish.month" -> finishMonth.toString,
      "whenDidTheJobFinish.year" -> finishYear.toString,
      "haveYouCeasedTrading" -> haveYouCeasedTrading,
      "natureOfYourBusiness" -> natureOfYourBusiness
      )

    "present 'About Self Employment' " in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)

      val result = controllers.s8_self_employment.G1AboutSelfEmployment.present(request)
      status(result) mustEqual OK
    }

    "add submitted form to the cached claim" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(aboutSelfEmploymentInput: _*)

      val result = controllers.s8_self_employment.G1AboutSelfEmployment.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(models.domain.SelfEmployment)
      section.questionGroup(AboutSelfEmployment) must beLike {
        case Some(f: AboutSelfEmployment) => {
          f.areYouSelfEmployedNow must equalTo(areYouSelfEmployedNow)
          f.whenDidYouStartThisJob must equalTo(Some(DayMonthYear(Some(startDay), Some(startMonth), Some(startYear), None, None)))
          f.whenDidTheJobFinish must equalTo(Some(DayMonthYear(Some(finishDay), Some(finishMonth), Some(finishYear), None, None)))
          f.haveYouCeasedTrading must equalTo(Some(haveYouCeasedTrading))
          f.natureOfYourBusiness must equalTo(Some(natureOfYourBusiness))
        }
      }
    }

    "missing mandatory field" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody("areYouSelfEmployedNow" -> "")

      val result = controllers.s8_self_employment.G1AboutSelfEmployment.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(aboutSelfEmploymentInput: _*)

      val result = controllers.s8_self_employment.G1AboutSelfEmployment.submit(request)
      status(result) mustEqual SEE_OTHER
    }
  } section("unit", models.domain.SelfEmployment.id)
}