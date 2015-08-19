package app.claim

import utils.WithJsBrowser
import utils.pageobjects.preview.PreviewTestableData
import utils.pageobjects.s_eligibility.{GBenefitsPage, GBenefitsPageContext}
import utils.pageobjects._
import utils.pageobjects.xml_validation.{XMLClaimBusinessValidation, XMLBusinessValidation}
import app.FunctionalTestCommon

/**
 * End-to-End functional tests using input files created by Steve Moody.
 * @author Jorge Migueis
 *         Date: 03/09/2013
 */
class FunctionalTestCase9Spec extends FunctionalTestCommon {
  isolated

  "The application " should {

    "Successfully run absolute Test Case 8 " in new WithJsBrowser with PageObjects {

      val page = GBenefitsPage(context)
      val claim = TestData.readTestDataFromFile("/functional_scenarios/ClaimScenario_TestCase8.csv")
      test(page, claim, buildPreviewUseData)
    }
  } section ("functional","claim")

  private def buildPreviewUseData = {

    PreviewTestableData()   +
      "AboutYouTitle"       + "AboutYouFirstName" + "AboutYouMiddleName" + "AboutYouSurname" +
      dateConversion("AboutYouDateOfBirth") +
      dateConversion("ClaimDateWhenDoYouWantYourCarersAllowanceClaimtoStart") +
      addressConversion("AboutYouAddress") + "AboutYouPostcode" +
      "AboutYouNationalityAndResidencyNationality" +
      "AboutYouMoreTripsOutOfGBforMoreThan52WeeksAtATime_1" +
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
      "EmploymentHaveYouBeenSelfEmployedAtAnyTime" +
      "OtherMoneyAnyPaymentsSinceClaimDate" +
      "OtherMoneyHaveYouSSPSinceClaim" +
      "OtherMoneyHaveYouSMPSinceClaim"

  }
}