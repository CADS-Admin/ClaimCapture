package controllers.breaks_in_care

import app.BreaksInCareGatherOptions
import controllers.mappings.Mappings
import models.domain._
import models.view.CachedClaim
import org.specs2.mutable._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.WithApplication

class GBreaksInCareSummarySpec extends Specification {
  section("unit", models.domain.Breaks.id)

  "GBreaksInCareSummary screen" should {
    "present correctly" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)

      val result = GBreaksInCareSummary.present(request)
      status(result) mustEqual OK
    }

    "fail submit for no questions answered" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)

      val result = GBreaksInCareSummary.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "fail submit for no break-type checkbox selected and other breaks question answered yes " in new WithApplication with Claiming {
      val input = Seq("breaktype_other" -> Mappings.yes)
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "allow submit for hospital checked and other breaks question answered no" in new WithApplication with Claiming {
      val input = Seq("breaktype_hospital" -> "true", "breaktype_other" -> Mappings.no)
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "allow submit for hospital checked and other breaks question answered yes" in new WithApplication with Claiming {
      val input = Seq("breaktype_hospital" -> "true", "breaktype_other" -> Mappings.yes)
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "allow submit for carehome checked and other breaks question answered yes" in new WithApplication with Claiming {
      val input = Seq("breaktype_carehome" -> "true", "breaktype_other" -> Mappings.yes)
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    """allow submit for "none" checked and other breaks question answered yes""" in new WithApplication with Claiming {
      val input = Seq("breaktype_none" -> "true", "breaktype_other" -> Mappings.yes)
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey).withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "add submitted data to the cached claim as Break Type Data for Hospital" in new WithApplication with Claiming {
      val input = Seq("breaktype_hospital" -> "true", "breaktype_other" -> Mappings.no)
      val request = FakeRequest().withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      val claim = getClaimFromCache(result)
      claim.questionGroup(BreaksInCareType) must beLike {
        case Some(f: BreaksInCareType) => {
          f.hospital shouldEqual Mappings.someTrue
          f.carehome shouldEqual None
          f.none shouldEqual None
          f.other shouldEqual Some(Mappings.no)
        }
      }
    }

    "add submitted data to the cached claim as Break Type Data for Care Home" in new WithApplication with Claiming {
      val input = Seq("breaktype_carehome" -> "true", "breaktype_other" -> Mappings.no)
      val request = FakeRequest().withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      val claim = getClaimFromCache(result)
      claim.questionGroup(BreaksInCareType) must beLike {
        case Some(f: BreaksInCareType) => {
          f.hospital shouldEqual None
          f.carehome shouldEqual Mappings.someTrue
          f.none shouldEqual None
          f.other shouldEqual Some(Mappings.no)
        }
      }
    }

    "add submitted data to the cached claim as Break Type Data for None" in new WithApplication with Claiming {
      val input = Seq("breaktype_none" -> "true", "breaktype_other" -> Mappings.no)
      val request = FakeRequest().withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      val claim = getClaimFromCache(result)
      claim.questionGroup(BreaksInCareType) must beLike {
        case Some(f: BreaksInCareType) => {
          f.hospital shouldEqual None
          f.carehome shouldEqual None
          f.none shouldEqual Mappings.someTrue
          f.other shouldEqual Some(Mappings.no)
        }
      }
    }

    "add submitted data to the cached claim as Break Type Data for Other" in new WithApplication with Claiming {
      val input = Seq("breaktype_none" -> "true", "breaktype_other" -> Mappings.yes)
      val request = FakeRequest().withFormUrlEncodedBody(input: _*)

      val result = GBreaksInCareSummary.submit(request)
      val claim = getClaimFromCache(result)
      claim.questionGroup(BreaksInCareType) must beLike {
        case Some(f: BreaksInCareType) => {
          f.hospital shouldEqual None
          f.carehome shouldEqual None
          f.none shouldEqual Mappings.someTrue
          f.other shouldEqual Some(Mappings.yes)
        }
      }
    }

    "not show summary table when no breaks" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)
      val result = GBreaksInCareSummary.present(request)
      status(result) mustEqual OK
      contentAsString(result) must not contain ("summary-heading")
      contentAsString(result) must not contain ("summary-table")
    }

    "show summary table when break added" in new WithApplication with Claiming {
      val hospitalInput = Seq(
        "iterationID" -> "b9cae6d3-7657-4f99-8d03-414022b2d1d6",
        "whoWasInHospital" -> BreaksInCareGatherOptions.You,
        "whenWereYouAdmitted.day" -> "1",
        "whenWereYouAdmitted.month" -> "1",
        "whenWereYouAdmitted.year" -> "2001",
        "yourStayEnded.answer" -> "yes",
        "yourStayEnded.date.day" -> "1",
        "yourStayEnded.date.month" -> "1",
        "yourStayEnded.date.year" -> "2001")
      val request = FakeRequest().withFormUrlEncodedBody(hospitalInput: _*)
      val hospitalResult = GBreaksInCareHospital.submit(request)
      status(hospitalResult) mustEqual SEE_OTHER
      getClaimFromCache(hospitalResult).questionGroup(BreaksInCare) must beLike {
        case Some(b: BreaksInCare) => {
          b.breaks.head.yourStayEnded.get.date.get.year.get shouldEqual 2001
        }
      }

      val requestWithUpdate = FakeRequest().withSession(CachedClaim.key -> extractCacheKey(hospitalResult))
      val result = GBreaksInCareSummary.present(requestWithUpdate)
      status(result) mustEqual OK
      contentAsString(result) must contain("summary-heading")
      contentAsString(result) must contain("summary-table")
    }
  }
  section("unit", models.domain.Breaks.id)
}
