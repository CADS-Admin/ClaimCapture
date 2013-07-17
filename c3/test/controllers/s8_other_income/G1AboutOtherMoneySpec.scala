package controllers.s8_other_income

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import models.domain._
import play.api.test.Helpers._
import play.api.cache.Cache
import models.{DayMonthYear, domain}
import models.domain.Claim
import scala.Some

class G1AboutOtherMoneySpec extends Specification with Tags {
  "About Other Money - Controller" should {
    val yourBenefits = "foo"
    val partnerBenefits = "bar"
    val formInput = Seq("yourBenefits" -> yourBenefits, "partnerBenefits" -> partnerBenefits)
    
    "present 'Your Course Details'" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)

      val result = G1AboutOtherMoney.present(request)
      
      status(result) mustEqual OK
    }
        
    "add submitted data to the cached claim" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody(formInput: _*)

      val result = G1AboutOtherMoney.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(domain.OtherIncome.id)

      section.questionGroup(AboutOtherMoney) must beLike {
        case Some(f: AboutOtherMoney) => {
          f.yourBenefits must equalTo(Some(yourBenefits))
          f.partnerBenefits must equalTo(Some(partnerBenefits))
        }
      }
    }
    
    "redirect to the next page after a valid submission" in new WithApplication with Claiming {
      val request = FakeRequest().withSession("connected" -> claimKey)
        .withFormUrlEncodedBody(formInput: _*)

      val result = G1AboutOtherMoney.submit(request)
      
      status(result) mustEqual SEE_OTHER
    }
  } section "unit"
}
