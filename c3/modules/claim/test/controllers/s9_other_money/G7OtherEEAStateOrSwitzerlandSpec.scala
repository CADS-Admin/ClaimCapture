package controllers.s9_other_money

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import play.api.cache.Cache
import models.domain.{Claim, OtherEEAStateOrSwitzerland, Claiming}
import scala.Some
import models.view.CachedClaim

class G7OtherEEAStateOrSwitzerlandSpec extends Specification with Tags {
  "Other EEA State of Switzerland" should {
    "present" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)

      val result = G7OtherEEAStateOrSwitzerland.present(request)
      status(result) mustEqual OK
    }

    "return bad request on invalid data" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)

      val result = G7OtherEEAStateOrSwitzerland.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    """be added to cached claim upon answering "no" to "benefits from other EEA state or Switzerland".""" in new WithApplication with Claiming {
      val request = FakeRequest().withSession(CachedClaim.key -> claimKey)
        .withFormUrlEncodedBody("benefitsFromOtherEEAStateOrSwitzerland" -> "no", "workingForOtherEEAStateOrSwitzerland" -> "no")

      val result = G7OtherEEAStateOrSwitzerland.submit(request)

      val claim = Cache.getAs[Claim](claimKey).get

      claim.questionGroup(OtherEEAStateOrSwitzerland) must beLike {
        case Some(o: OtherEEAStateOrSwitzerland) => {
          o.benefitsFromOtherEEAStateOrSwitzerland shouldEqual "no"
          o.workingForOtherEEAStateOrSwitzerland shouldEqual "no"
        }
      }
    }
  } section("unit", models.domain.OtherMoney.id)
}