package controllers.s7_employment

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import models.domain._
import play.api.cache.Cache
import models.view.CachedClaim
import app.PensionPaymentFrequency._

class G12PersonYouCareForExpensesSpec extends Specification with Tags {
  val jobID = "Dummy job ID"

  "Expenses related to the person you care for, while you are at work - Controller" should {
    "present" in new WithApplication with Claiming {
      val aboutExpenses = mock[AboutExpenses]
      aboutExpenses.identifier returns AboutExpenses
      aboutExpenses.jobID returns jobID
      aboutExpenses.payAnyoneToLookAfterPerson returns "yes"

      val job = mock[Job]
      job.questionGroups returns aboutExpenses :: Nil

      val jobs = mock[Jobs]
      jobs.identifier returns Jobs
      jobs.jobs returns job :: Nil
      jobs.questionGroup(jobID, AboutExpenses) returns Some(aboutExpenses)

      val claim = Claim().update(jobs)
      Cache.set(claimKey, claim)

      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)
      val result = G12PersonYouCareForExpenses.present(jobID)(request)
      status(result) mustEqual OK
    }

    "require all mandatory data" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)
        .withFormUrlEncodedBody("jobID" -> jobID)

      val result = G12PersonYouCareForExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject submission with howOftenPayCare not selected" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody("jobID" -> jobID,
        "whoDoYouPay" -> "blah", "howMuchCostCare" -> "123.45", "relationToYou" -> "Father-In-Law", "relationToPersonYouCare" -> "grandFather")

      val result = G12PersonYouCareForExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "reject submission with other selected but no text" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody("jobID" -> jobID,
        "whoDoYouPay" -> "blah", "howMuchCostCare" -> "123.45", "howOftenPayCare.frequency" -> Other, "howOftenPayCare.frequency.other" -> "", "relationToYou" -> "Father-In-Law", "relationToPersonYouCare" -> "grandFather")

      val result = G12PersonYouCareForExpenses.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "accept submission with other selected and filled in" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody("jobID" -> jobID,
        "whoDoYouPay" -> "blah", "howMuchCostCare" -> "123.45", "howOftenPayCare.frequency" -> Other, "howOftenPayCare.frequency.other" -> "other text", "relationToYou" -> "Father-In-Law", "relationToPersonYouCare" -> "grandFather")

      val result = G12PersonYouCareForExpenses.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "accept all mandatory data." in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody("jobID" -> jobID,
        "whoDoYouPay" -> "blah", "howMuchCostCare" -> "123.45", "howOftenPayCare.frequency" -> Weekly, "relationToYou" -> "Father-In-Law", "relationToPersonYouCare" -> "grandFather")

      val result = G12PersonYouCareForExpenses.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "be added to a (current) job" in new WithApplication with Claiming {
      G2JobDetails.submit(FakeRequest().withSession(CachedClaim.key -> claimKey)
        withFormUrlEncodedBody(
        "jobID" -> jobID,
        "employerName" -> "Toys r not us",
        "jobStartDate.day" -> "1",
        "jobStartDate.month" -> "1",
        "jobStartDate.year" -> "2000",
        "finishedThisJob" -> "no"))

      val result = G12PersonYouCareForExpenses.submit(FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody("jobID" -> jobID,
        "whoDoYouPay" -> "blah", "howMuchCostCare" -> "123.45", "howOftenPayCare.frequency" -> Weekly, "relationToYou" -> "Father-In-Law", "relationToPersonYouCare" -> "grandFather"))

      status(result) mustEqual SEE_OTHER

      val claim = Cache.getAs[Claim](claimKey).get

      claim.questionGroup(Jobs) must beLike {
        case Some(js: Jobs) => {
          js.size shouldEqual 1

          js.find(_.jobID == jobID) must beLike { case Some(j: Job) => j.questionGroups.size shouldEqual 2 }
        }
      }
    }
  } section("unit", models.domain.Employed.id)
}