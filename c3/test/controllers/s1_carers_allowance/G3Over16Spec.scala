package controllers.s1_carers_allowance

import org.specs2.mutable.{ Tags, Specification }
import play.api.test.{ WithApplication, FakeRequest }
import play.api.test.Helpers._
import play.api.cache.Cache
import java.util.concurrent.TimeUnit
import models.domain._
import models.domain.Claim

class G3Over16Spec extends Specification with Tags {
  "Carer's Allowance - Over16 - Controller" should {
    val answerYesNo = "yes"
    val hoursInput = Seq("answer" -> answerYesNo)

    "present" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)

      val result = controllers.s1_carers_allowance.G3Over16.present(request)
      status(result) mustEqual OK
    }

    "missing mandatory field" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody("answer" -> "")

      val result = controllers.s1_carers_allowance.G3Over16.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody(hoursInput: _*)

      val result = controllers.s1_carers_allowance.G3Over16.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "add submitted form to the cached claim when answered 'yes'" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody(hoursInput: _*)

      val result = controllers.s1_carers_allowance.G3Over16.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(models.domain.CarersAllowance)
      section.questionGroup(Over16) must beLike {
        case Some(f: Over16) => {
          f.answerYesNo must equalTo(answerYesNo)
        }
      }
    }

    "add submitted form to the cached claim when answered 'no'" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody("answer" -> "no")

      val result = controllers.s1_carers_allowance.G3Over16.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(models.domain.CarersAllowance)
      section.questionGroup(Over16) must beLike {
        case Some(f: Over16) => {
          f.answerYesNo must equalTo("no")
        }
      }
    }
  } section("unit",models.domain.CarersAllowance.id)
}