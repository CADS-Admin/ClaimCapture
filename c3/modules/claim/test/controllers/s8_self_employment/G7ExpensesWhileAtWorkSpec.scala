package controllers.s8_self_employment

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import models.domain._
import play.api.test.Helpers._
import play.api.cache.Cache
import models.domain.Claim
import models.view.CachedClaim
import app.PensionPaymentFrequency._
import scala.Some
import models.PensionPaymentFrequency

class G7ExpensesWhileAtWorkSpec extends Specification with Tags {
  "Expenses related to the Person you care for while at work - Self Employment - Controller" should {
    val nameOfPerson = "myself"
    val howMuchYouPay = "123.45"
    val howOften_frequency = Other
    val howOften_frequency_other = "Every day and twice on Sundays"
    val whatRelationIsToYou = "son"
    val relationToPartner = "married"
    val whatRelationIsTothePersonYouCareFor = "mother"

    val expensesWhileAtWorkInput = Seq(
      "nameOfPerson" -> nameOfPerson,
      "howMuchYouPay" -> howMuchYouPay,
      "howOftenPayExpenses.frequency" -> howOften_frequency,
      "howOftenPayExpenses.frequency.other" -> howOften_frequency_other,
      "whatRelationIsToYou" -> whatRelationIsToYou,
      "relationToPartner" -> relationToPartner,
      "whatRelationIsTothePersonYouCareFor" -> whatRelationIsTothePersonYouCareFor
    )

    "present 'Expenses related to the Person you care for while at work' " in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody("doYouPayToPensionScheme" -> "no", "doYouPayToLookAfterYourChildren" -> "yes", "didYouPayToLookAfterThePersonYouCaredFor" -> "yes")

      val result = G4SelfEmploymentPensionsAndExpenses.submit(request)

      val request2 = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)

      val result2 = G7ExpensesWhileAtWork.present(request)
      status(result2) mustEqual OK
    }

    "add submitted form to the cached claim" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(expensesWhileAtWorkInput: _*)

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(models.domain.SelfEmployment)

      section.questionGroup(ExpensesWhileAtWork) must beLike {
        case Some(f: ExpensesWhileAtWork) => {
          f.howMuchYouPay must equalTo(howMuchYouPay)
          f.howOftenPayExpenses must equalTo(PensionPaymentFrequency(howOften_frequency, Some(howOften_frequency_other)))
          f.nameOfPerson must equalTo(nameOfPerson)
          f.whatRelationIsToYou must equalTo(whatRelationIsToYou)
          f.whatRelationIsTothePersonYouCareFor must equalTo(whatRelationIsTothePersonYouCareFor)
        }
      }
    }

    "reject when missing mandatory field nameOfPerson" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "nameOfPerson" -> "",
        "howMuchYouPay" -> howMuchYouPay,
        "howOftenPayExpenses.frequency" -> howOften_frequency,
        "howOftenPayExpenses.frequency.other" -> howOften_frequency_other,
        "whatRelationIsToYou" -> whatRelationIsToYou,
        "relationToPartner" -> relationToPartner,
        "whatRelationIsTothePersonYouCareFor" -> whatRelationIsTothePersonYouCareFor
      )

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject when missing mandatory field howMuchYouPay" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "nameOfPerson" -> nameOfPerson,
        "howMuchYouPay" -> "",
        "howOftenPayExpenses.frequency" -> howOften_frequency,
        "howOftenPayExpenses.frequency.other" -> howOften_frequency_other,
        "whatRelationIsToYou" -> whatRelationIsToYou,
        "relationToPartner" -> relationToPartner,
        "whatRelationIsTothePersonYouCareFor" -> whatRelationIsTothePersonYouCareFor
      )

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject when missing mandatory field howOftenPayExpenses.frequency" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "nameOfPerson" -> nameOfPerson,
        "howMuchYouPay" -> howMuchYouPay,
        "howOftenPayExpenses.frequency" -> "",
        "howOftenPayExpenses.frequency.other" -> howOften_frequency_other,
        "whatRelationIsToYou" -> whatRelationIsToYou,
        "relationToPartner" -> relationToPartner,
        "whatRelationIsTothePersonYouCareFor" -> whatRelationIsTothePersonYouCareFor
      )

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject when other selected but other not filled in" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "nameOfPerson" -> nameOfPerson,
        "howMuchYouPay" -> howMuchYouPay,
        "howOftenPayExpenses.frequency" -> howOften_frequency,
        "howOftenPayExpenses.frequency.other" -> "",
        "whatRelationIsToYou" -> whatRelationIsToYou,
        "relationToPartner" -> relationToPartner,
        "whatRelationIsTothePersonYouCareFor" -> whatRelationIsTothePersonYouCareFor
      )

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject when missing mandatory field whatRelationIsToYou" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "nameOfPerson" -> nameOfPerson,
        "howMuchYouPay" -> howMuchYouPay,
        "howOftenPayExpenses.frequency" -> howOften_frequency,
        "howOftenPayExpenses.frequency.other" -> howOften_frequency_other,
        "whatRelationIsToYou" -> "",
        "relationToPartner" -> relationToPartner,
        "whatRelationIsTothePersonYouCareFor" -> whatRelationIsTothePersonYouCareFor
      )

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject when missing mandatory field whatRelationIsTothePersonYouCareFor" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(
        "nameOfPerson" -> nameOfPerson,
        "howMuchYouPay" -> howMuchYouPay,
        "howOftenPayExpenses.frequency" -> howOften_frequency,
        "howOftenPayExpenses.frequency.other" -> howOften_frequency_other,
        "whatRelationIsToYou" -> whatRelationIsToYou,
        "relationToPartner" -> relationToPartner,
        "whatRelationIsTothePersonYouCareFor" -> ""
      )

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody(expensesWhileAtWorkInput: _*)

      val result = controllers.s8_self_employment.G7ExpensesWhileAtWork.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "redirect to next page when payAnyoneToLookAfterPerson is not in About Employment" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)
        .withFormUrlEncodedBody("doYouPayToPensionScheme_answer" -> "no", "doYouPayToLookAfterYourChildren" -> "yes", "didYouPayToLookAfterThePersonYouCaredFor" -> "yes")

      val result = G4SelfEmploymentPensionsAndExpenses.submit(request)

      val request2 = FakeRequest().withSession(CachedClaim.claimKey -> claimKey)

      val result2 = G7ExpensesWhileAtWork.present(request)
      status(result2) mustEqual SEE_OTHER
    }
  } section("unit", models.domain.SelfEmployment.id)
}