package controllers.circs.s1_start_of_process

import models.domain._
import models.view.CachedChangeOfCircs
import models.{DayMonthYear, NationalInsuranceNumber}
import org.specs2.mutable.{Specification, Tags}
import play.api.test.Helpers._
import play.api.test.{FakeRequest}
import utils.WithApplication

class G2ReportAChangeInYourCircumstancesSpec extends Specification with Tags{

  "Circumstances - About You - Controller" should {

    val fullName = "Mr John Smith"
    val nino = "AB123456C"
    val dateOfBirthDay = 5
    val dateOfBirthMonth = 12
    val dateOfBirthYear = 1990
    val theirFullName = "Mr Jane Smith"
    val theirRelationshipToYou = "Wife"

    val aboutYouInput = Seq(
      "fullName" -> fullName,
      "nationalInsuranceNumber.nino" -> nino.toString,
      "dateOfBirth.day" -> dateOfBirthDay.toString,
      "dateOfBirth.month" -> dateOfBirthMonth.toString,
      "dateOfBirth.year" -> dateOfBirthYear.toString,
      "theirFullName" -> theirFullName,
      "theirRelationshipToYou" -> theirRelationshipToYou
    )

    //CachedChangeOfCircs.key
    
    "present 'Circumstances About You' " in new WithApplication with MockForm {
      val request = FakeRequest()

      val result = controllers.circs.s1_start_of_process.G2ReportAChangeInYourCircumstances.present(request)
      status(result) mustEqual OK
    }

    "add submitted form to the cached claim" in new WithApplication with MockForm {
      val request = FakeRequest()
        .withFormUrlEncodedBody(aboutYouInput: _*)

      val result = controllers.circs.s1_start_of_process.G2ReportAChangeInYourCircumstances.submit(request)
      val claim = getClaimFromCache(result,CachedChangeOfCircs.key)
      val section: Section = claim.section(models.domain.CircumstancesIdentification)
      section.questionGroup(CircumstancesReportChange) must beLike {
        case Some(f: CircumstancesReportChange) => {
          f.fullName must equalTo(fullName)
          f.nationalInsuranceNumber must equalTo(NationalInsuranceNumber(Some(nino)))
          f.dateOfBirth must equalTo(DayMonthYear(dateOfBirthDay, dateOfBirthMonth, dateOfBirthYear))
        }
      }
    }

    "missing mandatory field" in new WithApplication with MockForm {
      val request = FakeRequest()
        .withFormUrlEncodedBody("firstName" -> "")

      val result = controllers.circs.s1_start_of_process.G2ReportAChangeInYourCircumstances.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with MockForm {
      val request = FakeRequest()
        .withFormUrlEncodedBody(aboutYouInput: _*)

      val result = controllers.circs.s1_start_of_process.G2ReportAChangeInYourCircumstances.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "when present called Lang should not be set on Claim" in new WithApplication with MockForm {
      val request = FakeRequest().withHeaders(REFERER -> "http://localhost:9000/circumstances/identification/about-you")

      val result = controllers.circs.s1_start_of_process.G2ReportAChangeInYourCircumstances.present(request)
      val claim = getClaimFromCache(result,CachedChangeOfCircs.key)
      claim.lang mustEqual None
    }
  } section("unit", models.domain.CircumstancesIdentification.id)
}
