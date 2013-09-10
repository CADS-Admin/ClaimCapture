package controllers.s8_self_employment

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import models.domain._
import play.api.test.Helpers._
import play.api.cache.Cache
import scala.Some
import models.view.CachedClaim
import models.PensionPaymentFrequency

class G4SelfEmploymentPensionsAndExpensesSpec extends Specification with Tags {

  "Self Employment - Pensions and Expenses - Controller" should {
    val doYouPayToPensionScheme = "yes"
    val howMuchDidYouPay = "123.45"
    val howOften_frequency = app.PensionPaymentFrequency.Other
    val howOften_frequency_other = "Every day and twice on Sundays"
    val doYouPayToLookAfterYourChildren = "yes"
    val didYouPayToLookAfterThePersonYouCaredFor = "yes"

    val selfEmploymentPensionsAndExpensesInput = Seq(
      "doYouPayToPensionScheme" -> doYouPayToPensionScheme,
      "howMuchDidYouPay" -> howMuchDidYouPay,
      "howOften.frequency" -> howOften_frequency,
      "howOften.frequency.other" -> howOften_frequency_other,
      "doYouPayToLookAfterYourChildren" -> doYouPayToLookAfterYourChildren,
      "didYouPayToLookAfterThePersonYouCaredFor" -> didYouPayToLookAfterThePersonYouCaredFor
    )

    "present 'Pensions and Expenses' " in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.present(request)
      status(result) mustEqual OK
    }

    "add submitted form to the cached claim" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(selfEmploymentPensionsAndExpensesInput: _*)

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(models.domain.SelfEmployment)

      section.questionGroup(SelfEmploymentPensionsAndExpenses) must beLike {
        case Some(f: SelfEmploymentPensionsAndExpenses) => {
          f.doYouPayToPensionScheme must equalTo(doYouPayToPensionScheme)
          f.howMuchDidYouPay must equalTo(Some(howMuchDidYouPay))
          f.howOften must equalTo(Some(PensionPaymentFrequency(howOften_frequency, Some(howOften_frequency_other))))
          f.doYouPayToLookAfterYourChildren must equalTo(doYouPayToLookAfterYourChildren)
          f.didYouPayToLookAfterThePersonYouCaredFor must equalTo(didYouPayToLookAfterThePersonYouCaredFor)
        }
      }
    }

    "reject missing mandatory field doYouPayToPensionScheme" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "howMuchDidYouPay" -> howMuchDidYouPay,
        "howOften.frequency" -> howOften_frequency,
        "howOften.frequency.other" -> howOften_frequency_other,
        "doYouPayToLookAfterYourChildren" -> doYouPayToLookAfterYourChildren,
        "didYouPayToLookAfterThePersonYouCaredFor" -> didYouPayToLookAfterThePersonYouCaredFor
      )

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject missing mandatory field doYouPayToLookAfterYourChildren" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "doYouPayToPensionScheme" -> doYouPayToPensionScheme,
        "howMuchDidYouPay" -> howMuchDidYouPay,
        "howOften.frequency" -> howOften_frequency,
        "howOften.frequency.other" -> howOften_frequency_other,
        "didYouPayToLookAfterThePersonYouCaredFor" -> didYouPayToLookAfterThePersonYouCaredFor
      )

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject missing mandatory field didYouPayToLookAfterThePersonYouCaredFor" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "doYouPayToPensionScheme" -> doYouPayToPensionScheme,
        "howMuchDidYouPay" -> howMuchDidYouPay,
        "howOften.frequency" -> howOften_frequency,
        "howOften.frequency.other" -> howOften_frequency_other,
        "doYouPayToLookAfterYourChildren" -> doYouPayToLookAfterYourChildren
      )

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject missing howMuch" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "doYouPayToPensionScheme" -> doYouPayToPensionScheme,
        "howOften.frequency" -> howOften_frequency,
        "howOften.frequency.other" -> howOften_frequency_other,
        "doYouPayToLookAfterYourChildren" -> doYouPayToLookAfterYourChildren,
        "didYouPayToLookAfterThePersonYouCaredFor" -> didYouPayToLookAfterThePersonYouCaredFor
      )

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject missing howOften frequency" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "doYouPayToPensionScheme" -> doYouPayToPensionScheme,
        "howMuchDidYouPay" -> howMuchDidYouPay,
        "howOften.frequency.other" -> howOften_frequency_other,
        "doYouPayToLookAfterYourChildren" -> doYouPayToLookAfterYourChildren,
        "didYouPayToLookAfterThePersonYouCaredFor" -> didYouPayToLookAfterThePersonYouCaredFor
      )

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject other selected but not filled in" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "doYouPayToPensionScheme" -> doYouPayToPensionScheme,
        "howMuchDidYouPay" -> howMuchDidYouPay,
        "howOften.frequency" -> howOften_frequency,
        "doYouPayToLookAfterYourChildren" -> doYouPayToLookAfterYourChildren,
        "didYouPayToLookAfterThePersonYouCaredFor" -> didYouPayToLookAfterThePersonYouCaredFor
      )

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(selfEmploymentPensionsAndExpensesInput: _*)

      val result = controllers.s8_self_employment.G4SelfEmploymentPensionsAndExpenses.submit(request)
      status(result) mustEqual SEE_OTHER
    }
  } section("unit", models.domain.SelfEmployment.id)
}