package controllers

import utils.pageobjects.TestData
import app.{PensionPaymentFrequency, WhoseNameAccount, PaymentFrequency, AccountStatus}

/**
 * To change this template use Preferences | File and Code Templates.
 * @author Jorge Migueis
 *         Date: 16/07/2013
 */
object ClaimScenarioFactory {

  val partnerAddress = "Partner Address"
  val partnerPostcode = "RM11 1AA"

  def yourDetailsWithNotTimeOutside() = {
    val claim = new TestData
    claim.AboutYouTitle = "mr"
    claim.AboutYouFirstName = "John"
    claim.AboutYouSurname = "Appleseed"
    claim.AboutYouNationality = "English"
    claim.AboutYouDateOfBirth = "03/04/1950"
    claim.AboutYouWhatIsYourMaritalOrCivilPartnershipStatus = "s"
    claim.AboutYouHaveYouAlwaysLivedInTheUK = "Yes"
    claim.AboutYouNINO = "AB123456C"
    claim.AboutYouAddress = "101 Clifton Street&Blackpool"
    claim.AboutYouPostcode = "FY1 2RW"
    claim
  }

  def yourDetailsEnablingTimeOutsideUK() = {
    val claim = yourDetailsWithNotTimeOutside()
    claim.AboutYouHaveYouAlwaysLivedInTheUK = "No"
    claim.AboutYouAreYouCurrentlyLivingintheUk = "Yes"
    claim.AboutYouWhenDidYouArriveInYheUK = "01/11/2003"
    claim.AboutYouDoYouPlantoGoBacktoThatCountry = "No"
    claim
  }

  def s2AboutYouWithTimeOutside() = {
    // Your details + outside UK
    val claim = yourDetailsEnablingTimeOutsideUK()
    // Your contact details
    claim.AboutYouAddress = "An address"
    claim.AboutYouPostcode = "SE1 6EH"
    claim.AboutYouPhoneNumber = "01253 111 111"
    claim.AboutYouMobileNumber = "07111 111 111"
    // Claim date
    claim.AboutYouWhenDoYouWantYourCarersAllowanceClaimtoStart = "03/05/2014"
    // More about you
    claim.AboutYouHaveYouHadaPartnerSpouseatAnyTime = "Yes"
    claim.AboutYouHaveYouBeenOnACourseOfEducation = "Yes"
    claim.AboutYouDoYouGetStatePension = "Yes"
    // Employment
    claim.AboutYouHaveYouBeenSelfEmployedAtAnyTime = "Yes"
    claim.AboutYouHaveYouBeenEmployedAtAnyTime_1 = "Yes"
    claim
  }

  def s2AnsweringNoToQuestions() = {
    val claim = new TestData

    // Your contact details
    claim.AboutYouAddress = "An address"
    claim.AboutYouPostcode = "SE1 6EH"
    claim.AboutYouPhoneNumber = "01253 111 111"
    claim.AboutYouContactYouByTextphone = "No"
    claim.AboutYouMobileNumber = "07111 111 111"
    // Claim date
    claim.AboutYouWhenDoYouWantYourCarersAllowanceClaimtoStart = "03/05/2014"
    // More about you
    claim.AboutYouHaveYouHadaPartnerSpouseatAnyTime = "no"
    claim.AboutYouHaveYouBeenOnACourseOfEducation = "no"
    claim.AboutYouDoYouGetStatePension = "no"
    // Employment
    claim.AboutYouHaveYouBeenSelfEmployedAtAnyTime = "no"
    claim.AboutYouHaveYouBeenEmployedAtAnyTime_1 = "no"
    claim
  }

  def s2ands3WithTimeOUtsideUKAndProperty() = {
    val claim = s2AboutYouWithTimeOutside()
    // Partner personal details
    claim.AboutYourPartnerTitle = "mrs"
    claim.AboutYourPartnerFirstName = "Cloe"
    claim.AboutYourPartnerMiddleName = "Scott"
    claim.AboutYourPartnerSurname = "Smith"
    claim.AboutYourPartnerOtherNames = "Doe"
    claim.AboutYourPartnerNINO = "AB123456A"
    claim.AboutYourPartnerDateofBirth = "12/07/1990"
    claim.AboutYourPartnerNationality = "British"
    // Person you care for
    claim.AboutYourPartnerIsYourPartnerThePersonYouAreClaimingCarersAllowancefor = "Yes"
    claim
  }

  def s4CareYouProvide() = {
    val claim = s2ands3WithTimeOUtsideUKAndProperty()
    // Their Personal Details
    claim.AboutTheCareYouProvideTitlePersonCareFor = "mr"
    claim.AboutTheCareYouProvideFirstNamePersonCareFor = "Tom"
    claim.AboutTheCareYouProvideMiddleNamePersonCareFor = "Potter"
    claim.AboutTheCareYouProvideSurnamePersonCareFor = "Wilson"
    claim.AboutTheCareYouProvideNINOPersonCareFor = "AA123456A"
    claim.AboutTheCareYouProvideDateofBirthPersonYouCareFor = "02/03/1990"
    claim.AboutTheCareYouProvideDoTheyLiveAtTheSameAddressAsYou = "Yes"
    // Their Contact Details
    claim.AboutTheCareYouProvideAddressPersonCareFor = "123 Colne Street&Line 2"
    claim.AboutTheCareYouProvidePostcodePersonCareFor = "BB9 2AD"
    claim.AboutTheCareYouProvidePhoneNumberPersonYouCare = "07922 222 222"
    // More About The Person
    claim.AboutTheCareYouProvideWhatTheirRelationshipToYou = "father"
    claim.AboutTheCareYouProvideDoesPersonGetArmedForcesIndependencePayment = "No"
    claim.AboutTheCareYouProvideHasAnyoneelseClaimedCarerAllowance = "Yes"
    // Previous Carer Personal Details
    claim.AboutTheCareYouProvideFirstNamePreviousCarer = "Peter"
    claim.AboutTheCareYouProvideMiddleNamePreviousCarer = "Jackson"
    claim.AboutTheCareYouProvideSurnamePreviousCarer = "Benson"
    claim.AboutTheCareYouProvideNINOPreviousCarer = "BB123456B"
    claim.AboutTheCareYouProvideDateofBirthPreviousCarer = "02/06/1985"
    // Previous Carer Contact Details
    claim.AboutTheCareYouProvideAddressPreviousCarer = "123 Conway Road& Preston"
    claim.AboutTheCareYouProvidePostcodePreviousCarer = "BB9 1AB"
    claim.AboutTheCareYouProvidePhoneNumberPreviousCarer = "02933 333 333"
    claim.AboutTheCareYouProvideMobileNumberPreviousCarer = "07933 333 333"
    // Representatives For The Person
    claim.AboutTheCareYouProvideDoYouActforthePersonYouCareFor = "Yes"
    claim.AboutTheCareYouProvideYouActAs = "guardian"
    claim.AboutTheCareYouProvideDoesSomeoneElseActForThePersonYouCareFor = "Yes"
    claim.AboutTheCareYouProvidePersonActsAs = "guardian"
    claim.AboutTheCareYouProvideFullNameRepresentativesPersonYouCareFor = "Mary Jane Watson"
    // More About The Care
    claim.AboutTheCareYouProvideDoYouSpend35HoursorMoreEachWeek = "Yes"
    claim.AboutTheCareYouProvideDidYouCareForThisPersonfor35Hours = "Yes"
    claim.AboutTheCareYouProvideWhenDidYouStarttoCareForThisPerson = "03/04/2013"
    claim.AboutTheCareYouProvideHasSomeonePaidYoutoCare = "Yes"
    // One Who Pays Personal Details
    claim.AboutTheCareYouProvideOrganisationPaysYou = "Valtech"
    claim.AboutTheCareYouProvideTitlePersonPaysYou = "mr"
    claim.AboutTheCareYouProvideFirstNamePersonPaysYou = "Brian"
    claim.AboutTheCareYouProvideMiddleNamePersonPaysYou = "Green"
    claim.AboutTheCareYouProvideSurnamePersonPaysYou = "Eldred"
    claim.AboutTheCareYouProvideHowMuchDoYouGetPaidAWeek = "£120"
    claim.AboutTheCareYouProvideWhenDidThePaymentsStart = "29/04/2013"
    // Contact Details Of Paying Person
    claim.AboutTheCareYouProvideAddressPersonPaysYou = "123 Cleverme Street & Genius"
    claim.AboutTheCareYouProvidePostcodePersonPaysYou = "GN1 2DA"
    claim
  }

  def s5TimeSpentAbroad() = {
    val claim = s4CareYouProvide()
    // Normal Residence And Current Location
    claim.TimeSpentAbroadDoYouNormallyLiveintheUk = "No"
    claim.TimeSpentAbroadWhereDoYouNormallyLive = "Spain"
    claim.TimeSpentAbroadAreYouinGBNow = "Yes"
    // Details of time abroad with the person you care for
    claim.TimeSpentAbroadHaveYouBeenOutOfGBWithThePersonYouCareFor_1 = "Yes"
    // Abroad For More Than 52 Weeks
    claim.TimeSpentAbroadMoreTripsOutOfGBforMoreThan52WeeksAtATime_1 = "Yes"
    // Trip
    claim.TimeSpentAbroadDateYouLeftGBTripForMoreThan52Weeks_1 = "10/04/2013"
    claim.TimeSpentAbroadDateYouReturnedToGBTripForMoreThan52Weeks_1 = "20/04/2013"
    claim.TimeSpentAbroadWhereDidYouGoForMoreThan52Weeks_1 = "Everywhere"
    claim.TimeSpentAbroadWhyDidYouGoForMoreThan52Weeks_1 = "Visit Family"
    claim
  }

  def s6PayDetailsPageObjects() = {
    val claim = s5TimeSpentAbroad()
    // Address of School College or University
    claim.EducationNameofSchool = "Lancaster University"
    claim.EducationNameOfMainTeacherOrTutor = "Dr. Ray Charles"
    claim.EducationAddress = "Lancaster University& Bailrigg& Lancaster"
    claim.EducationPostcode = "LA1 4YW"
    claim.EducationPhoneNumber = "01524 65201"
    claim.EducationFaxNumber = "01524 36841"
    claim
  }

  def s6PayDetails() = {
    val claim = new TestData
    claim.HowWePayYouHowWouldYouLikeToGetPaid = AccountStatus.NotOpenAccount.name
    claim.HowWePayYouHowOftenDoYouWantToGetPaid = PaymentFrequency.EveryWeek.name
    claim
  }

  def s6BankBuildingSocietyDetails() = {
    val claim = new TestData

    claim.HowWePayYouNameOfAccountHolder = "John Smith"
    claim.WhoseNameOrNamesIsTheAccountIn = WhoseNameAccount.YourName.name
    claim.HowWePayYouFullNameOfBankorBuildingSociety = "Carers Bank"
    claim.HowWePayYouSortCode = "090126"
    claim.HowWePayYouAccountNumber = "12345678"
    claim
  }


  def s7EmploymentMinimal() = {

    val claim = new TestData
    claim.EmploymentEmployerName_1 = "Tesco's"
    claim.EmploymentWhenDidYouStartYourJob_1 = "01/01/2013"
    claim.EmploymentHaveYouFinishedThisJob_1 = "yes"
    claim.EmploymentWhenDidYouLastWork_1 = "01/07/2013"
    claim.EmploymentHowManyHoursAWeekYouNormallyWork_1 = "25"
    claim.EmploymentPayrollOrEmployeeNumber_1 = "12345678"
    claim.EmploymentEmployerAddress_1 = "23 Yeadon Way&Blackpool&Lancashire"
    claim.EmploymentEmployerPostcode_1 = "FY4 5TH"
    claim.EmploymentEmployerPhoneNumber_1 = "01253 667889"
    claim.EmploymentWhenWereYouLastPaid_1 = "08/07/2013"
    claim.EmploymentWhatWasTheGrossPayForTheLastPayPeriod_1 = "600"
    claim.EmploymentWhatWasIncludedInYourLastPay_1 = "All amounts due"
    claim.EmploymentDoYouGettheSameAmountEachTime_1 = "no"
    claim.EmploymentAddtionalWageHowOftenAreYouPaid_1 = "other"
    claim.EmploymentAddtionalWageOther_1 = "Quarterly"
    claim.EmploymentAddtionalWageWhenDoYouGetPaid_1 = "two weeks ago"
    claim.EmploymentAdditionalWageDoesYourEmployerOweYouAnyMoney_1 = "no"
//    claim.EmploymentHowMuchAreYouOwed_1 = "1250"
    claim.EmploymentWhatPeriodIsItForFrom_1 = "03/04/2013"
    claim.EmploymentWhatPeriodIsItForTo_1 = "03/05/2013"
    claim.EmploymentWhatIsTheMoneyOwedFor_1 = "This and that"
    claim.EmploymentWhenShouldTheMoneyOwedHaveBeenPaid_1 = "06/05/2013"
    claim.EmploymentWhenWillYouGetMoneyOwed_1 = "08/08/2013"
    claim.EmploymentDoYouPayTowardsanOccupationalPensionScheme_1 = "no"
//    claim.EmploymentHowMuchYouPayforOccupationalPension_1 = "350"
//    claim.EmploymentHowOftenOccupationalPension_1 = "other"
//    claim.EmploymentHowOftenOtherOccupationalPension_1 = "every 5 minutes"
    claim.EmploymentDoYouPayTowardsAPersonalPension_1 = "no"
//    claim.EmploymentHowMuchYouPayforPersonalPension_1 = "120"
//    claim.EmploymentHowOftenPersonalPension_1 = "other"
//    claim.EmploymentHowOftenOtherPersonalPension_1 = "every 5 minutes"
    claim.EmploymentDoYouPayforAnythingNecessaryToDoYourJob_1 = "no"
    claim.EmploymentDoYouPayAnyoneLookAfterYourChild_1 = "no"
    claim.EmploymentDoYouPayAnyonetoLookAfterPersonYouCareFor_1 = "no"
    claim.EmploymentJobTitle_1 = "Hacker"
    claim.EmploymentWhatAreNecessaryJobExpenses_1 = "Petrol money for driving"
    claim.EmploymentWhyYouNeedTheseExpensesToDoYourJob_1 = "So I could deliver items."
    claim.EmploymentHowMuchDidTheseExpensesCostYouEachWeek_1 = "160.66"
    claim.EmploymentChildcareExpensesHowMuchYouPayfor_1 = "120.12"
    claim.EmploymentChildcareExpensesHowOften_1 = PensionPaymentFrequency.Weekly
    claim.EmploymentNameOfthePersonWhoLooksAfterYourChild_1 = "Mr Grandfather Senior"
    claim.EmploymentChildcareExpensesWhatRelationIsthePersontoYou_1 = "Father"
    claim.EmploymentChildcareExpensesWhatRelationIsthePersontoYourPartner_1 = "fatherInLaw"
    claim.EmploymentChildcareExpensesWhatRelationIsthePersonToThePersonYouCareFor_1 = "Grandfather"
    claim.EmploymentAddressChildcareProvider_1 = "12 Banbury Close&St Annes&Lancashire"
    claim.EmploymentPostcodeChildcareProvider_1 = "FY8 7TH"
    claim.EmploymentCareExpensesHowMuchYouPayfor_1 = "150.55"
    claim.EmploymentCareExpensesHowOftenYouPayfor_1 = PensionPaymentFrequency.Weekly
    claim.EmploymentNameOfPersonYouPayForCaring_1 = "Carers UK Ltd"
    claim.EmploymentCareExpensesWhatRelationIsToYou_1 = "Uncle"
    claim.EmploymentCareExpensesWhatRelationIsToPersonYouCareFor_1 = "Other"
    claim.EmploymentAddressCareProvider_1 = "1 London Road&Preston&Lancashire"
    claim.EmploymentPostcodeCareProvider_1 = "PR4 5TH"

    claim
  }

  def s7Employment() = {
    val claim = new TestData
    claim.EmploymentEmployerName_1 = "Tesco's"
    claim.EmploymentWhenDidYouStartYourJob_1 = "01/01/2013"
    claim.EmploymentHaveYouFinishedThisJob_1 = "yes"
    claim.EmploymentWhenDidYouLastWork_1 = "01/07/2013"
    claim.EmploymentHowManyHoursAWeekYouNormallyWork_1 = "25"
    claim.EmploymentPayrollOrEmployeeNumber_1 = "12345678"
    claim.EmploymentEmployerAddress_1 = "23 Yeadon Way&Blackpool&Lancashire"
    claim.EmploymentEmployerPostcode_1 = "FY4 5TH"
    claim.EmploymentEmployerPhoneNumber_1 = "01253 667889"
    claim.EmploymentWhenWereYouLastPaid_1 = "08/07/2013"
    claim.EmploymentWhatWasTheGrossPayForTheLastPayPeriod_1 = "600"
    claim.EmploymentWhatWasIncludedInYourLastPay_1 = "All amounts due"
    claim.EmploymentDoYouGettheSameAmountEachTime_1 = "no"
    claim.EmploymentAddtionalWageHowOftenAreYouPaid_1 = "other"
    claim.EmploymentAddtionalWageOther_1 = "Quarterly"
    claim.EmploymentAddtionalWageWhenDoYouGetPaid_1 = "two weeks ago"
    claim.EmploymentAdditionalWageDoesYourEmployerOweYouAnyMoney_1 = "yes"
    claim.EmploymentHowMuchAreYouOwed_1 = "1250"
    claim.EmploymentWhatPeriodIsItForFrom_1 = "03/04/2013"
    claim.EmploymentWhatPeriodIsItForTo_1 = "03/05/2013"
    claim.EmploymentWhatIsTheMoneyOwedFor_1 = "This and that"
    claim.EmploymentWhenShouldTheMoneyOwedHaveBeenPaid_1 = "06/05/2013"
    claim.EmploymentWhenWillYouGetMoneyOwed_1 = "08/08/2013"
    claim.EmploymentDoYouPayTowardsanOccupationalPensionScheme_1 = "yes"
    claim.EmploymentHowMuchYouPayforOccupationalPension_1 = "350"
    claim.EmploymentHowOftenOccupationalPension_1 = "other"
    claim.EmploymentHowOftenOtherOccupationalPension_1 = "every 5 minutes"
    claim.EmploymentDoYouPayTowardsAPersonalPension_1 = "yes"
    claim.EmploymentHowMuchYouPayforPersonalPension_1 = "120"
    claim.EmploymentHowOftenPersonalPension_1 = "other"
    claim.EmploymentHowOftenOtherPersonalPension_1 = "every 5 minutes"
    claim.EmploymentDoYouPayforAnythingNecessaryToDoYourJob_1 = "yes"
    claim.EmploymentDoYouPayAnyoneLookAfterYourChild_1 = "yes"
    claim.EmploymentDoYouPayAnyonetoLookAfterPersonYouCareFor_1 = "yes"
    claim.EmploymentJobTitle_1 = "Hacker"
    claim.EmploymentWhatAreNecessaryJobExpenses_1 = "Petrol money for driving"
    claim.EmploymentWhyYouNeedTheseExpensesToDoYourJob_1 = "So I could deliver items."
    claim.EmploymentHowMuchDidTheseExpensesCostYouEachWeek_1 = "160.66"
    claim.EmploymentChildcareExpensesHowMuchYouPayfor_1 = "120.12"
    claim.EmploymentChildcareExpensesHowOften_1 = PensionPaymentFrequency.Other
    claim.EmploymentChildcareExpensesHowOftenOther_1 = "other text"
    claim.EmploymentChildcareExpensesHowOftenOther_1 = "why is my train 80 minutes late"
    claim.EmploymentNameOfthePersonWhoLooksAfterYourChild_1 = "Mr Grandfather Senior"
    claim.EmploymentChildcareExpensesWhatRelationIsthePersontoYou_1 = "Father"
    claim.EmploymentChildcareExpensesWhatRelationIsthePersontoYourPartner_1 = "fatherInLaw"
    claim.EmploymentChildcareExpensesWhatRelationIsthePersonToThePersonYouCareFor_1 = "Grandfather"
    claim.EmploymentAddressChildcareProvider_1 = "12 Banbury Close&St Annes&Lancashire"
    claim.EmploymentPostcodeChildcareProvider_1 = "FY8 7TH"
    claim.EmploymentCareExpensesHowMuchYouPayfor_1 = "150.55"
    claim.EmploymentCareExpensesHowOftenYouPayfor_1 = PensionPaymentFrequency.Other
    claim.EmploymentCareExpensesHowOftenYouPayforOther_1 = "other text"
    claim.EmploymentNameOfPersonYouPayForCaring_1 = "Carers UK Ltd"
    claim.EmploymentCareExpensesWhatRelationIsToYou_1 = "Uncle"
    claim.EmploymentCareExpensesWhatRelationIsToPersonYouCareFor_1 = "Other"
    claim.EmploymentAddressCareProvider_1 = "1 London Road&Preston&Lancashire"
    claim.EmploymentPostcodeCareProvider_1 = "PR4 5TH"

    claim
  }

  def s9otherMoney = {
    val claim = s7Employment()
    // G1 About other money
    claim.OtherMoneyHaveYouClaimedOtherBenefits = "yes"
    claim.OtherMoneyAnyPaymentsSinceClaimDate = "yes"
    claim.OtherMoneyWhoPaysYou = "The Man"
    claim.OtherMoneyHowMuch = "Not much"
    claim.OtherMoneyHowOften = "weekly"
    // G5 Statutory Sick Pay
    claim.OtherMoneyHaveYouSSPSinceClaim = "yes"
    claim.OtherMoneySSPHowMuch = "123"
    claim.OtherMoneySSPHowOften = "weekly"
    claim.OtherMoneySSPEmployerName = "Burger King"
    // G6 Other Statutory Pay
    claim.OtherMoneyHaveYouSMPSinceClaim = "yes"
    claim.OtherMoneySMPEmployerName = "Employers Name"
    claim.OtherMOneySMPHowOften = "weekly"

    claim
  }

  def s9SelfEmployment = {
    val claim = s9otherMoney
    // About self employment
    claim.SelfEmployedAreYouSelfEmployedNow = "no"
    claim.SelfEmployedWhenDidYouStartThisJob = "11/09/2001"
    claim.SelfEmployedWhenDidTheJobFinish = "07/07/2005"

    // G6 Childcare provider's contact Details
    claim.SelfEmployedChildcareProviderAddress = "Care Provider Address"
    claim.SelfEmployedChildcareProviderPostcode = "SE1 6EH"
    // G7 Expenses while at work
    claim.SelfEmployedCareExpensesNameOfPerson = "Expenses Name Of Person"
    // G8 Care provider's contact Details
    claim.SelfEmployedCareProviderAddress = "Care Provider Address"
    claim.SelfEmployedCareProviderPostcode = "SE1 6EH"
    // G9 Completion
    //   None

    claim
  }

  def s9SelfEmploymentYourAccounts = {
    val claim = s9SelfEmployment
    //About self employment
    claim.SelfEmployedAreTheseAccountsPreparedonaCashFlowBasis = "yes"
    claim.SelfEmployedAretheIncomeOutgoingSimilartoYourCurrent = "no"
    claim.SelfEmployedTellUsWhyandWhentheChangeHappened = "A Year back"
    claim.SelfEmployedDoYouHaveAnAccountant = "yes"
    claim.SelfEmployedCanWeContactYourAccountant = "yes"

    claim
  }

  def s9SelfEmploymentPensionsAndExpenses = {
    val claim = s9SelfEmploymentYourAccounts

    claim.SelfEmployedDoYouPayTowardsPensionScheme = "yes"
    claim.SelfEmployedHowMuchYouPayTowardsPensionScheme = "11.2"
    claim.SelfEmployedHowoftenYouPayTowardsPensionScheme = app.PensionPaymentFrequency.Weekly
    claim.SelfEmployedDoYouPayAnyonetoLookAfterYourChild = "yes"
    claim.SelfEmployedDoYouPayAnyonetoLookAfterPersonYouCareFor = "yes"

    claim
  }

  def s9SelfEmploymentChildCareExpenses = {
    val claim = s9SelfEmploymentPensionsAndExpenses

    claim.SelfEmployedChildcareExpensesHowMuchYouPay = "123456"
    claim.SelfEmployedChildcareExpensesHowOften = app.PensionPaymentFrequency.Weekly
    claim.SelfEmployedChildcareProviderNameOfPerson = "hello123"
    claim.SelfEmployedChildcareProviderWhatRelationIsToYou = "son"
    claim.SelfEmployedChildcareProviderWhatRelationIsTothePersonYouCareFor = "son"
    claim
  }

  def s9SelfEmploymentExpensesRelatedToPersonYouCareFor = {
    val claim = s9SelfEmploymentChildCareExpenses

    claim.SelfEmployedCareExpensesHowMuchYouPay = "900.9"
    claim.SelfEmployedCareExpensesHowOften = app.PensionPaymentFrequency.Weekly
    claim.SelfEmployedCareExpensesNameOfPerson = "John"
    claim.SelfEmployedCareExpensesWhatRelationIsToYou = "grandSon"
    claim.SelfEmployedCareExpensesWhatRelationIsTothePersonYouCareFor = "son"

    claim
  }

}
