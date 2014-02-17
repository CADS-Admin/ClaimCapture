package controllers.circs.s2_report_changes

import models.{MultiLineAddress, DayMonthYear}
import org.specs2.mutable.{Tags, Specification}
import scala.Predef._
import models.MultiLineAddress
import scala.Some

/**
 * Created by neddakaltcheva on 2/14/14.
 */
class G6AddressChangeFormSpec extends Specification with Tags {
  "Report a change in your address - Address Change Form" should {
    val yes = "yes"
    val no = "no"
    val newPostcode = "PR1A4JQ"
    val stillCaringDateDay = 10
    val stillCaringDateMonth = 11
    val stillCaringDateYear = 2012
    val invalidYear = 99999
    val moreAboutChanges = "This is more about the change"
    val addressLineOne = "lineOne"
    val addressLineTwo = "lineTwo"
    val addressLineThree = "lineThree"

    "map data into case class" in {
      G6AddressChange.form.bind(
        Map(
          "stillCaring.answer" -> yes,
          "newAddress.lineOne" -> addressLineOne,
          "newAddress.lineTwo" -> addressLineTwo,
          "newAddress.lineThree" -> addressLineThree,
          "newPostcode" -> newPostcode,
          "caredForChangedAddress.answer" -> yes,
          "sameAddress.answer" -> yes,
          "moreAboutChanges" -> moreAboutChanges
        )
      ).fold(
          formWithErrors => "This mapping should not happen." must equalTo("Error"),
          form => {
            form.stillCaringMapping.answer must equalTo(yes)
            form.newAddress must equalTo(MultiLineAddress(Some(addressLineOne), Some(addressLineTwo), Some(addressLineThree)))
            form.newPostcode must equalTo(Some(newPostcode))
            form.caredForChangedAddress.answer must equalTo(yes)
            form.sameAddress.answer must equalTo(yes)
            form.moreAboutChanges must equalTo(Some(moreAboutChanges))
          }
        )
    }

    "mandatory fields must be populated when still caring is not set" in {
      G6AddressChange.form.bind(
        Map("moreAboutChanges" -> moreAboutChanges, "newPostcode" -> newPostcode)
      ).fold(
          formWithErrors => {
            formWithErrors.errors(0).message must equalTo("error.required")
            formWithErrors.errors(1).message must equalTo("error.required")
            formWithErrors.errors(2).message must equalTo("error.required")
            formWithErrors.errors(3).message must equalTo("error.required")
          },
          f => "This mapping should not happen." must equalTo("Valid")
        )
    }

    "mandatory fields must be populated when still caring is set to 'no'" in {
      G6AddressChange.form.bind(
        Map(
          "stillCaring_answer" -> no,
          "newPostcode" -> newPostcode,
          "moreAboutChanges" -> moreAboutChanges
        )
      ).fold(
          formWithErrors => {
            formWithErrors.errors(0).message must equalTo("error.required")
            formWithErrors.errors(1).message must equalTo("error.required")
          },
          f => "This mapping should not happen." must equalTo("Valid")
        )
    }

    "mandatory fields must be populated when still caring is set to 'yes'" in {
      G6AddressChange.form.bind(
        Map(
          "stillCaring_answer" -> yes,
          "newPostcode" -> newPostcode,
          "moreAboutChanges" -> moreAboutChanges
        )
      ).fold(
          formWithErrors => {
            formWithErrors.errors(0).message must equalTo("error.required")
            formWithErrors.errors(1).message must equalTo("error.required")
          },
          f => "This mapping should not happen." must equalTo("Valid")
        )
    }

    "reject special characters in text field" in {
      G6AddressChange.form.bind(
        Map(
          "stillCaring.answer" -> yes,
          "stillCaring.date.day" -> stillCaringDateDay.toString,
          "stillCaring.date.month" -> stillCaringDateMonth.toString,
          "stillCaring.date.year" -> stillCaringDateYear.toString,
          "newAddress.lineOne" -> addressLineOne,
          "newAddress.lineTwo" -> addressLineTwo,
          "newAddress.lineThree" -> addressLineThree,
          "newPostcode" -> newPostcode,
          "caredForChangedAddress.answer" -> yes,
          "sameAddress.answer" -> yes,
          "moreAboutChanges" -> "<>"
        )
      ).fold(
          formWithErrors => {
            formWithErrors.errors.length must equalTo(1)
            formWithErrors.errors(0).message must equalTo("error.restricted.characters")
          },
          f => "This mapping should not happen." must equalTo("Valid")
        )
    }

    "reject invalid still caring date" in {
      G6AddressChange.form.bind(
        Map(
          "stillCaring.answer" -> yes,
          "stillCaring.date.day" -> stillCaringDateDay.toString,
          "stillCaring.date.month" -> stillCaringDateMonth.toString,
          "stillCaring_date_year" -> invalidYear.toString,
          "newAddress.lineOne" -> addressLineOne,
          "newAddress.lineTwo" -> addressLineTwo,
          "newAddress.lineThree" -> addressLineThree,
          "newPostcode" -> newPostcode,
          "caredForChangedAddress.answer" -> yes,
          "sameAddress.answer" -> yes,
          "moreAboutChanges" -> moreAboutChanges
        )
      ).fold(
          formWithErrors => {
            formWithErrors.errors.length must equalTo(1)
            formWithErrors.errors.head.message must equalTo("error.invalid")
          },
          f => "This mapping should not happen." must equalTo("Valid")
        )
    }

  } section("unit", models.domain.CircumstancesAddressChange.id)
}
