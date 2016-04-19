package app.claim

import utils.WithJsBrowser
import utils.pageobjects.preview.PreviewTestableData
import utils.pageobjects.s_eligibility.GBenefitsPage
import utils.pageobjects._
import app.FunctionalTestCommon

/**
 * End-to-End functional tests using input files created by Steve Moody.
 * @author Jorge Migueis
 *         Date: 02/08/2013
 */
class FunctionalTestCase7Spec extends FunctionalTestCommon {
  isolated

  section("functional", "claim")
  "The application" should {
    "Successfully run absolute Test Case 7" in new WithJsBrowser with PageObjects {
      val page = GBenefitsPage(context)
      val claim = TestData.readTestDataFromFile("/functional_scenarios/ClaimScenario_TestCase7.csv")
      test(page, claim, buildPreviewUseData)
    }
  }
  section("functional", "claim")

  private def buildPreviewUseData = {
    PreviewTestableData()   +
      "AboutYouTitle"       + "AboutYouFirstName" + "AboutYouMiddleName" + "AboutYouSurname" +
      dateConversion("AboutYouDateOfBirth") +
      dateConversion("ClaimDateWhenDoYouWantYourCarersAllowanceClaimtoStart") +
      addressConversion("AboutYouAddress") + "AboutYouPostcode" +
      "AboutYouNationalityAndResidencyNationality" +
      "OtherMoneyOtherAreYouReceivingPensionFromAnotherEEA" +
      "AboutYourPartnerTitle"       + "AboutYourPartnerFirstName" + "AboutYourPartnerFirstName" + "AboutYourPartnerSurname" +
      dateConversion("AboutYourPartnerDateofBirth") +
      "OtherMoneyOtherAreYouPayingInsuranceToAnotherEEA" +
      "AboutTheCareYouProvideWhatTheirRelationshipToYou" +
      "AboutTheCareYouProvideTitlePersonCareFor"       + "AboutTheCareYouProvideFirstNamePersonCareFor" + "AboutTheCareYouProvideMiddleNamePersonCareFor" + "AboutTheCareYouProvideSurnamePersonCareFor" +
      dateConversion("AboutTheCareYouProvideDateofBirthPersonYouCareFor") +
      addressConversion("AboutTheCareYouProvideAddressPersonCareFor") + "AboutTheCareYouProvidePostcodePersonCareFor" +
      "AboutTheCareYouProvideDoYouSpend35HoursorMoreEachWeek" +
      "AboutTheCareYouProvideHaveYouHadAnyMoreBreaksInCare_1" +
      "EducationHaveYouBeenOnACourseOfEducation" +
      "EducationCourseTitle" +
      "EducationNameofSchool" +
      "EducationNameOfMainTeacherOrTutor" +
      "EducationPhoneNumber" +
      dateConversion("EducationWhenDidYouStartTheCourse") +
      dateConversion("EducationWhenDoYouExpectTheCourseToEnd") +
      "EmploymentHaveYouBeenEmployedAtAnyTime_0" +
      "EmploymentEmployerName_1"  +
      "EmploymentHaveYouBeenSelfEmployedAtAnyTime"
  }
}
