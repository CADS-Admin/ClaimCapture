package controllers

import utils.pageobjects.TestData
import app.ReportChange._
import play.api.i18n.{Messages => Messages}
import models.SortCode
import app.{CircsBreaksWhereabouts, WhoseNameAccount, PaymentFrequency}

object CircumstancesScenarioFactory {
  val yes = "yes"
  val no = "no"

  def aboutDetails = {
    val claim = new TestData
    claim.CircumstancesAboutYouFullName = "Mr John Roger Smith"
    claim.CircumstancesAboutYouNationalInsuranceNumber = "AB123456C"
    claim.CircumstancesAboutYouDateOfBirth = "03/04/1950"
    claim.CircumstancesAboutYouTheirFullName = "Mrs Jane Smith"
    claim.CircumstancesAboutYouTheirRelationshipToYou = "Wife"
    claim
  }

  def reportChangesSelfEmployment = {
    val claim = aboutDetails
    claim.CircumstancesReportChanges = SelfEmployment.name
    claim
  }

  def reportChangesStoppedCaring = {
    val claim = aboutDetails
    claim.CircumstancesReportChanges = StoppedCaring.name
    claim
  }

  def reportChangesOtherChangeInfo = {
    val claim = aboutDetails
    claim.CircumstancesReportChanges = AdditionalInfo.name
    claim
  }

  def paymentChangesChangeInfo = {
    val claim = aboutDetails
    claim.CircumstancesReportChanges = PaymentChange.name
    claim
  }

  def addressChange = {
    val claim = aboutDetails
    claim.CircumstancesReportChanges = AddressChange.name
    claim
  }

  def otherChangeInfo = {
    val claim = reportChangesOtherChangeInfo
    claim.CircumstancesOtherChangeInfoChange = "I put in the wrong date of birth"
    claim
  }

  def reportBreakFromCaring = {
    val claim = aboutDetails
    claim.CircumstancesReportChanges = BreakFromCaring.name
    claim
  }

  def reportBreakFromCaringBreaksInCareEndedYes = {
    val claim = reportBreakFromCaring

    claim.BreaksInCareStartDate = "03/04/2002"
    claim.BreaksInCareStartTime = "10 am"
    claim.BreaksInCareWhereWasThePersonYouCareFor = CircsBreaksWhereabouts.Hospital
    claim.BreaksInCareWhereWereYou = CircsBreaksWhereabouts.Holiday
    claim.BreaksInCareEnded = Mappings.yes
    claim.BreaksInCareEndDate = "01/01/2004"
    claim.BreaksInCareEndTime = "10 am"
    claim.BreaksInCareMedicalCareDuringBreak = Mappings.yes

    claim
  }

  def reportBreakFromCaringSummaryBreaksInCareEndedYesWithNoAdditionalBreaks = {
    val claim = reportBreakFromCaringBreaksInCareEndedYes

    claim.BreaksInCareSummaryAdditionalBreaks = no
    claim.BreaksInCareSummaryAdditionalBreaksInfo = ""

    claim
  }

  def reportBreakFromCaringSummaryBreaksInCareEndedYesWithAdditionalBreaks = {
    val claim = reportBreakFromCaringBreaksInCareEndedYes

    claim.BreaksInCareSummaryAdditionalBreaks = yes
    claim.BreaksInCareSummaryAdditionalBreaksInfo = "A break I haven't told you about yet"

    claim
  }

  def reportBreakFromCaringSummaryBreaksInCareEndedYesWithAdditionalBreaksNotAnswered = {
    val claim = reportBreakFromCaringBreaksInCareEndedYes

    claim.BreaksInCareSummaryAdditionalBreaks = ""
    claim.BreaksInCareSummaryAdditionalBreaksInfo = ""

    claim
  }

  def reportBreakFromCaringSummaryBreaksInCareEndedYesWithAdditionalBreaksButNotSpecified = {
    val claim = reportBreakFromCaringBreaksInCareEndedYes

    claim.BreaksInCareSummaryAdditionalBreaks = yes
    claim.BreaksInCareSummaryAdditionalBreaksInfo = ""

    claim
  }

  def reportBreakFromCaringBreaksInCareEndedNo = {
    val claim = reportBreakFromCaring

    claim.BreaksInCareStartDate = "03/04/2002"
    claim.BreaksInCareStartTime = "10 am"
    claim.BreaksInCareWhereWasThePersonYouCareFor = CircsBreaksWhereabouts.Hospital
    claim.BreaksInCareWhereWereYou = CircsBreaksWhereabouts.Holiday
    claim.BreaksInCareEnded = Mappings.no
    claim.BreaksInCareExpectToStartCaringAgain = Mappings.yes
    claim.BreaksInCareMedicalCareDuringBreak = Mappings.yes

    claim
  }

  def reportBreakFromCaringBreaksInCareEndedNoAndExpectToStartCaringNo = {
    val claim = reportBreakFromCaring

    claim.BreaksInCareStartDate = "03/04/2002"
    claim.BreaksInCareStartTime = "10 am"
    claim.BreaksInCareWhereWasThePersonYouCareFor = CircsBreaksWhereabouts.Hospital
    claim.BreaksInCareWhereWereYou = CircsBreaksWhereabouts.Holiday
    claim.BreaksInCareEnded = Mappings.no
    claim.BreaksInCareExpectToStartCaringAgain = Mappings.no
    claim.BreaksInCareExpectToStartCaringPermanentEndDate = "01/01/2002"
    claim.BreaksInCareMedicalCareDuringBreak = Mappings.yes

    claim
  }

  def reportChangesAddressChangeYes = {
    val claim = addressChange

    claim.CircumstancesAddressChangePreviousAddress = "1 test lane"
    claim.CircumstancesAddressChangePreviousPostcode = "PR1A4JQ"
    claim.CircumstancesAddressChangeStillCaring = "yes"
    claim.CircumstancesAddressChangeNewAddress = "1 new address lane"
    claim.CircumstancesAddressChangeNewPostcode = "PR1A4JQ"
    claim.CircumstancesAddressChangeCaredForChangedAddress = "yes"
    claim.CircumstancesAddressChangeSameAddress = "no"
    claim.CircumstancesAddressChangeSameAddressTheirAddress ="1 test lane"
    claim.CircumstancesAddressChangeSameAddressTheirPostcode ="PR1A4JQ"
    claim.CircumstancesAddressChangeMoreAboutChanges ="Additional info about changes"

    claim
  }

  def reportChangesAddressChangeNo = {
    val claim = addressChange

    claim.CircumstancesAddressChangePreviousAddress = "1 test lane&2 test lane"
    claim.CircumstancesAddressChangePreviousPostcode = "PR1A4JQ"
    claim.CircumstancesAddressChangeStillCaring = "no"
    claim.CircumstancesAddressChangeFinishedStillCaringDate = "03/04/2013"
    claim.CircumstancesAddressChangeNewAddress = "1 new address lane"
    claim.CircumstancesAddressChangeNewPostcode = "PR1A4JQ"
    claim.CircumstancesAddressChangeMoreAboutChanges ="Additional info about changes"

    claim
  }

  def reportChangesPaymentChangeScenario1 = {
    val claim = paymentChangesChangeInfo

    claim.CircumstancesPaymentChangeCurrentlyPaidIntoBank = yes
    claim.CircumstancesPaymentChangeNameOfCurrentBank = "Nat West"
    claim.CircumstancesPaymentChangeAccountHolderName = "Mr John Doe"
    claim.CircumstancesPaymentChangeWhoseNameIsTheAccountIn = WhoseNameAccount.YourName.name
    claim.CircumstancesPaymentChangeBankFullName = "HSBC"
    claim.CircumstancesPaymentChangeSortCode = "112233"
    claim.CircumstancesPaymentChangeAccountNumber = "12345678"
    claim.CircumstancesPaymentChangePaymentFrequency = PaymentFrequency.EveryWeek.name

    claim
  }

  def reportChangesPaymentChangeScenario2 = {
    val claim = paymentChangesChangeInfo

    claim.CircumstancesPaymentChangeCurrentlyPaidIntoBank = no
    claim.CircumstancesPaymentCurrentPaymentMethod = "Cheque"
    claim.CircumstancesPaymentChangeAccountHolderName = "Mr John Doe"
    claim.CircumstancesPaymentChangeWhoseNameIsTheAccountIn = WhoseNameAccount.YourName.name
    claim.CircumstancesPaymentChangeBankFullName = "HSBC"
    claim.CircumstancesPaymentChangeSortCode = "112233"
    claim.CircumstancesPaymentChangeAccountNumber = "12345678"
    claim.CircumstancesPaymentChangePaymentFrequency = PaymentFrequency.EveryWeek.name

    claim
  }

  def declaration = {
    val claim = otherChangeInfo
    claim.FurtherInfoContact = "By Post"
    claim.CircumstancesDeclarationInfoAgreement = "yes"
    claim.CircumstancesDeclarationWhy = "Cause I want"
    claim.CircumstancesDeclarationConfirmation = "yes"
    claim.CircumstancesSomeOneElseConfirmation = "yes"
    claim.NameOrOrganisation = "Mr Smith"
    claim
  }

}
