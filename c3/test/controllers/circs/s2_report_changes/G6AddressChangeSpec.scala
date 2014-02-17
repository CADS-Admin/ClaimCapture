package controllers.circs.s2_report_changes

import org.specs2.mutable.{Tags, Specification}
import play.api.test.{FakeRequest, WithApplication}
import models.domain.MockForm
import models.view.CachedChangeOfCircs
import play.api.test.Helpers._
import controllers.circs.s2_report_changes

/**
 * Created by neddakaltcheva on 2/14/14.
 */
class G6AddressChangeSpec extends Specification with Tags {

  val yes = "yes"
  val no = "no"
  val newPostcode = "PR1A4JQ"
  val stillCaringDateDay = 10
  val stillCaringDateMonth = 11
  val stillCaringDateYear = 2012
  val moreAboutChanges = "This is more about the change"
  val addressLineOne = "lineOne"
  val addressLineTwo = "lineTwo"
  val addressLineThree = "lineThree"

  val validStillCaringFormInput = Seq(
    "stillCaring.answer" -> yes,
    "newAddress.lineOne" -> addressLineOne,
    "newAddress.lineTwo" -> addressLineTwo,
    "newAddress.lineThree" -> addressLineThree,
    "newPostcode" -> newPostcode,
    "caredForChangedAddress.answer" -> yes,
    "sameAddress.answer" -> yes,
    "moreAboutChanges" -> moreAboutChanges)

  val validNotCaringFormInput = Seq(
    "stillCaring.answer" -> no,
    "stillCaring.date.day" -> stillCaringDateDay.toString,
    "stillCaring.date.month" -> stillCaringDateMonth.toString,
    "stillCaring.date.year" -> stillCaringDateYear.toString,
    "newAddress.lineOne" -> addressLineOne,
    "newAddress.lineTwo" -> addressLineTwo,
    "newAddress.lineThree" -> addressLineThree,
    "newPostcode" -> newPostcode,
    "moreAboutChanges" -> moreAboutChanges
  )

  "Circumstances - Address Change - Controller" should {

    "present 'CoC Address Change' " in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)

      val result = G6AddressChange.present(request)
      status(result) mustEqual OK
    }


    "redirect to the next page after a valid still caring submission" in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)
        .withFormUrlEncodedBody(validStillCaringFormInput: _*)

      val result = s2_report_changes.G6AddressChange.submit(request)
      status(result) mustEqual SEE_OTHER
    }

    "redirect to the next page after a valid not caring submission" in new WithApplication with MockForm {
      val request = FakeRequest().withSession(CachedChangeOfCircs.key -> claimKey)
        .withFormUrlEncodedBody(validNotCaringFormInput: _*)

      val result = s2_report_changes.G6AddressChange.submit(request)
      status(result) mustEqual SEE_OTHER
    }
  }section("unit", models.domain.CircumstancesAddressChange.id)
}

