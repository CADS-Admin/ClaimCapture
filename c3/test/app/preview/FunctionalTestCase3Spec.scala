package app.preview

import utils.WithJsBrowser
import utils.pageobjects.s_eligibility.GBenefitsPage
import utils.pageobjects._
import app.FunctionalTestCommon
import utils.pageobjects.preview.PreviewPage

class FunctionalTestCase3Spec extends FunctionalTestCommon {
  isolated

  section ("functional","preview")
  "The application Claim" should {
    "Successfully run absolute Claim Test Case 3" in new WithJsBrowser with PageObjects {
      import Data.displaying

      val page = GBenefitsPage(context)
      implicit val claim = TestData.readTestDataFromFile("/functional_scenarios/ClaimScenario_TestCase3.csv")
      page goToThePage()
      val lastPage = page runClaimWith(claim, PreviewPage.url)
      val toFindData = Data.build(
        "Name"              displays ("AboutYouTitle","AboutYouFirstName","AboutYouMiddleName","AboutYouSurname"),
        "Date of birth"     displays DateTransformer("AboutYouDateOfBirth"),
        "Address"           displays (AddressTransformer("AboutYouAddress"),"AboutYouPostcode"),
        "Claim date"   displays DateTransformer("ClaimDateWhenDoYouWantYourCarersAllowanceClaimtoStart"),
        "Your nationality"  displays "AboutYouNationalityAndResidencyNationality",
        "Have you always lived in England, Scotland or Wales?"  displays "AboutYouNationalityAndResidencyAlwaysLivedInUK",
        "Have you been away from England, Scotland or Wales for more than 52 weeks in the 3 years before your claim date?"  displays "AboutYouNationalityAndResidencyTrip52Weeks",
        "Have you or any of your close family worked abroad or been paid benefits from outside the United Kingdom since your claim date?"          displays "OtherMoneyOtherEEAGuardQuestion",
        "Name"            displays ("AboutYourPartnerTitle","AboutYourPartnerFirstName","AboutYourPartnerMiddleName","AboutYourPartnerSurname"),
        "Date of birth"   displays DateTransformer("AboutYourPartnerDateofBirth"),
        "Have you separated since your claim date?" displays "AboutYourPartnerHaveYouSeparatedfromYourPartner",
        "Do they live at the same address as you?"         displays "AboutTheCareYouProvideDoTheyLiveAtTheSameAddressAsYou",
        "What's their relationship to you?"                               displays "AboutTheCareYouProvideWhatTheirRelationshipToYou",
        "Do you spend 35 hours or more each week providing care for Test CaseThree?" displays "AboutTheCareYouProvideDoYouSpend35HoursorMoreEachWeek",
        "Have you been on a course of education since your claim date?"   displays "EducationHaveYouBeenOnACourseOfEducation",
        "Have you been an employee since"      displays "EmploymentHaveYouBeenEmployedAtAnyTime_0",
        "Have you been self-employed since" displays "EmploymentHaveYouBeenSelfEmployedAtAnyTime"
      )

      toFindData.assertReview(claim, context) must beTrue
    }
  }
  section ("functional","preview")
}



