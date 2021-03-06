package controllers.s_employment

import org.specs2.mutable._
import models.yesNo.YesNoWithText
import controllers.mappings.Mappings
import utils.WithApplication

class GEmploymentAdditionalInfoFormSpec extends Specification{

  val additionalInfoYes = YesNoWithText("yes", Some("No more information"))
  val additionalInfoNo = YesNoWithText("no", None)

  val textValue = "1234567890"

  def maxCharactersString = {
    val result = new StringBuffer()
    for (i <- 1 to 300) result.append(textValue)
    result
  }

  section("unit", models.domain.EmploymentAdditionalInfo.id)
  "Employment - Additional Info" should {
    "must have 1 mandatory field" in new WithApplication {
      GEmploymentAdditionalInfo.form.bind(
        Map( "" -> "")
      ).fold(
        formWithErrors =>
        {
          formWithErrors.errors.length mustEqual 1
          formWithErrors.errors(0).message must equalTo(Mappings.errorRequired)
        },
        f => "This mapping should not happen." must equalTo("Valid")
      )
    }

    "must have 1 mandatory field when answered yes" in new WithApplication {
      GEmploymentAdditionalInfo.form.bind(
        Map( "empAdditionalInfo.answer" -> additionalInfoYes.answer)
      ).fold(
        formWithErrors => formWithErrors.errors.length mustEqual 1,
        f => "This mapping should not happen." must equalTo("Valid")
      )
    }

    "map data into case class" in new WithApplication {
      GEmploymentAdditionalInfo.form.bind(
        Map(
          "empAdditionalInfo.answer" -> additionalInfoYes.answer,
          "empAdditionalInfo.text" -> additionalInfoYes.text.get
        )
      ).fold(
        formWithErrors => "This mapping should not happen." must equalTo("Error"),
        f => {
          f.empAdditionalInfo mustEqual additionalInfoYes
        }
      )
    }

    "map data into case class when answered no" in new WithApplication {
      GEmploymentAdditionalInfo.form.bind(
        Map(
          "empAdditionalInfo.answer" -> additionalInfoNo.answer
        )
      ).fold(
        formWithErrors => "This mapping should not happen." must equalTo("Error"),
        f => {
          f.empAdditionalInfo mustEqual additionalInfoNo
        }
      )
    }

    "not map into case class when special characters entered" in new WithApplication {
      GEmploymentAdditionalInfo.form.bind(
        Map(
          "empAdditionalInfo.answer" -> additionalInfoYes.answer,
          "empAdditionalInfo.text" -> "{}"
        )
      ).fold(
        formWithErrors => formWithErrors.errors.length mustEqual 1,
        f => "This mapping should not happen." must equalTo("Valid")
      )
    }

    "not map into case class when more than 3000 characters entered" in new WithApplication {
      GEmploymentAdditionalInfo.form.bind(
        Map(
          "empAdditionalInfo.answer" -> additionalInfoYes.answer,
          "empAdditionalInfo.text" -> maxCharactersString.append(" ").toString // this will be 3001 characters
        )
      ).fold(
        formWithErrors => formWithErrors.errors.length mustEqual 1,
        f => "This mapping should not happen." must equalTo("Valid")
      )
    }

    "map into case class when 3000 characters entered" in new WithApplication {
      val text = maxCharactersString.toString // this will be 3000 characters
      GEmploymentAdditionalInfo.form.bind(
        Map(
          "empAdditionalInfo.answer" -> additionalInfoYes.answer,
          "empAdditionalInfo.text" -> text
        )
      ).fold(
        formWithErrors => "This mapping should not happen." must equalTo("Error"),
        f => {
          f.empAdditionalInfo.answer mustEqual additionalInfoYes.answer
          f.empAdditionalInfo.text mustEqual Some(text)
        }
      )
    }
  }
  section("unit", models.domain.EmploymentAdditionalInfo.id)
}
