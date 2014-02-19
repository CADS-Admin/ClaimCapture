package controllers.circs.s1_identification

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import models.domain._
import models.view.CachedChangeOfCircs
import play.api.test.Helpers._
import play.api.cache.Cache
import models.{NationalInsuranceNumber, DayMonthYear}
import scala.Some

class G1ReportAChangeInYourCircumstancesSpec extends Specification with Tags{

  "Circumstances - About You - Controller" should {

    val fullName = "Mr John Smith"
    val ni1 = "AB"
    val ni2 = 12
    val ni3 = 34
    val ni4 = 56
    val ni5 = "C"
    val dateOfBirthDay = 5
    val dateOfBirthMonth = 12
    val dateOfBirthYear = 1990
    val theirFullName = "Mr Jane Smith"
    val theirRelationshipToYou = "Wife"

    val aboutYouInput = Seq(
      "fullName" -> fullName,
      "nationalInsuranceNumber.ni1" -> ni1.toString,
      "nationalInsuranceNumber.ni2" -> ni2.toString,
      "nationalInsuranceNumber.ni3" -> ni3.toString,
      "nationalInsuranceNumber.ni4" -> ni4.toString,
      "nationalInsuranceNumber.ni5" -> ni5.toString,
      "dateOfBirth.day" -> dateOfBirthDay.toString,
      "dateOfBirth.month" -> dateOfBirthMonth.toString,
      "dateOfBirth.year" -> dateOfBirthYear.toString,
      "theirFullName" -> theirFullName,
      "theirRelationshipToYou" -> theirRelationshipToYou
    )

    "present 'Circumstances About You' " in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)

      val result = controllers.circs.s1_identification.G1ReportAChangeInYourCircumstances.present(request)
      status(result) mustEqual OK
    }

    "add submitted form to the cached claim" in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)
        .withFormUrlEncodedBody(aboutYouInput: _*)

      val result = controllers.circs.s1_identification.G1ReportAChangeInYourCircumstances.submit(request)
      val claim = Cache.getAs[Claim](claimKey).get
      val section: Section = claim.section(models.domain.CircumstancesIdentification)
      section.questionGroup(CircumstancesReportChange) must beLike {
        case Some(f: CircumstancesReportChange) => {
          f.fullName must equalTo(fullName)
          f.nationalInsuranceNumber must equalTo(NationalInsuranceNumber(Some(ni1),Some(ni2.toString), Some(ni3.toString), Some(ni4.toString), Some(ni5.toString)))
          f.dateOfBirth must equalTo(DayMonthYear(dateOfBirthDay, dateOfBirthMonth, dateOfBirthYear))
        }
      }
    }

    "missing mandatory field" in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)
        .withFormUrlEncodedBody("firstName" -> "")

      val result = controllers.circs.s1_identification.G1ReportAChangeInYourCircumstances.submit(request)
      status(result) mustEqual BAD_REQUEST
    }

    "redirect to the next page after a valid submission" in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)
        .withFormUrlEncodedBody(aboutYouInput: _*)

      val result = controllers.circs.s1_identification.G1ReportAChangeInYourCircumstances.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "when present called Lang should not be set on Claim" in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey).withHeaders(REFERER -> "http://localhost:9000/circumstances/identification/about-you")

      val result = controllers.circs.s1_identification.G1ReportAChangeInYourCircumstances.present(request)
      val claim = Cache.getAs[Claim](claimKey).get
      claim.lang mustEqual None
    }
  } section("unit", models.domain.CircumstancesIdentification.id)
}
