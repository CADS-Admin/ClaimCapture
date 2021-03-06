package utils

import app.ConfigProperties._
import models.domain._
import utils.C3Encryption._

object ClaimEncryption {
  def saveForLaterKey = getStringProperty("saveForLater.uuid.secret.key")
  def encryptUuid(uuid:String):String=XorEncryption.encryptUuid(uuid, saveForLaterKey)

  def decryptUuid(encuuid:String):String=XorEncryption.decryptUuid(encuuid, saveForLaterKey)

  def encrypt(claim: Claim): Claim = {
    val claimWithYourDetails = encryptYourDetails(claim)
    val claimWithContactDetails = encryptContactDetails(claimWithYourDetails)
    val claimWithTheirPersonalDetails = encryptTheirPersonalDetails(claimWithContactDetails)
    val claimWithCircumstancesReportChange = encryptCircumstancesReportChange(claimWithTheirPersonalDetails)
    val claimWithHowWePayYou = encryptHowWePayYou(claimWithCircumstancesReportChange)
    val claimWithYourPartnerPersonalDetails = encryptYourPartnerPersonalDetails(claimWithHowWePayYou)
    val claimWithCircumstancesAddressChange = encryptCircumstancesAddressChange(claimWithYourPartnerPersonalDetails)
    val claimWithCircumstancesPaymentChange = encryptCircumstancesPaymentChange(claimWithCircumstancesAddressChange)
    val claimWithSaveClaimMap = encryptSaveClaimMap(claimWithCircumstancesPaymentChange)

    claimWithSaveClaimMap
  }

  def decrypt(claim: Claim): Claim = {
    val claimWithYourDetails = decryptYourDetails(claim)
    val claimWithContactDetails = decryptContactDetails(claimWithYourDetails)
    val claimWithTheirPersonalDetails = decryptTheirPersonalDetails(claimWithContactDetails)
    val claimWithCircumstancesReportChange = decryptCircumstancesReportChange(claimWithTheirPersonalDetails)
    val claimWithHowWePayYou = decryptHowWePayYou(claimWithCircumstancesReportChange)
    val claimWithYourPartnerPersonalDetails = decryptYourPartnerPersonalDetails(claimWithHowWePayYou)
    val claimWithCircumstancesAddressChange = decryptCircumstancesAddressChange(claimWithYourPartnerPersonalDetails)
    val claimWithCircumstancesPaymentChange = decryptCircumstancesPaymentChange(claimWithCircumstancesAddressChange)
    val claimWithSaveClaim = decryptSaveClaimMap(claimWithCircumstancesPaymentChange)

    claimWithSaveClaim
  }

  def encryptYourDetails(claim: Claim): Claim = {
    claim.questionGroup[YourDetails] match {
      case Some(yourDetails) =>
        claim.update(yourDetails.copy(
          encryptString(yourDetails.title),
          encryptString(yourDetails.firstName),
          encryptOptionalString(yourDetails.middleName),
          encryptString(yourDetails.surname),
          encryptNationalInsuranceNumber(yourDetails.nationalInsuranceNumber),
          encryptDayMonthYear(yourDetails.dateOfBirth)
        ))
      case _ => claim
    }
  }

  def encryptContactDetails(claim: Claim): Claim = {
    claim.questionGroup[ContactDetails] match {
      case Some(contactDetails) =>
        claim.update(contactDetails.copy(
          encryptMultiLineAddress(contactDetails.address),
          encryptOptionalString(contactDetails.postcode),
          encryptOptionalString(contactDetails.howWeContactYou),
          contactDetails.contactYouByTextphone,
          contactDetails.wantsContactEmail,
          encryptOptionalString(contactDetails.email),
          encryptOptionalString(contactDetails.emailConfirmation)
        ))
      case _ => claim
    }

  }

  def encryptTheirPersonalDetails(claim: Claim): Claim = {
    claim.questionGroup[TheirPersonalDetails] match {
      case Some(theirPersonalDetails) =>
        claim.update(theirPersonalDetails.copy(
          encryptString(theirPersonalDetails.title),
          encryptString(theirPersonalDetails.firstName),
          encryptOptionalString(theirPersonalDetails.middleName),
          encryptString(theirPersonalDetails.surname),
          encryptOptionalNationalInsuranceNumber(theirPersonalDetails.nationalInsuranceNumber),
          encryptDayMonthYear(theirPersonalDetails.dateOfBirth),
          encryptString(theirPersonalDetails.relationship),
          encryptYesNoMandWithAddress(theirPersonalDetails.theirAddress)
        ))
      case _ => claim
    }
  }

  def encryptCircumstancesReportChange(claim: Claim): Claim = {
    claim.questionGroup[CircumstancesYourDetails] match {
      case Some(circumstancesReportChange) =>
        claim.update(circumstancesReportChange.copy(
          encryptString(circumstancesReportChange.firstName),
          encryptString(circumstancesReportChange.surname),
          encryptNationalInsuranceNumber(circumstancesReportChange.nationalInsuranceNumber),
          encryptDayMonthYear(circumstancesReportChange.dateOfBirth),
          encryptString(circumstancesReportChange.wantsContactEmail),
          encryptOptionalString(circumstancesReportChange.email),
          encryptOptionalString(circumstancesReportChange.emailConfirmation),
          encryptString(circumstancesReportChange.theirFirstName),
          encryptString(circumstancesReportChange.theirSurname),
          encryptString(circumstancesReportChange.theirRelationshipToYou),
          encryptOptionalString(circumstancesReportChange.furtherInfoContact)
        ))
      case _ => claim
    }
  }

  def encryptHowWePayYou(claim: Claim): Claim = {
    claim.questionGroup[HowWePayYou] match {
      case Some(howWePayYou) =>
        claim.update(howWePayYou.copy(
          encryptString(howWePayYou.likeToBePaid),
          encryptOptionalBankBuildingSoceityDetails(howWePayYou.bankDetails),
          encryptString(howWePayYou.paymentFrequency)
        ))
      case _ => claim
    }
  }

  def encryptYourPartnerPersonalDetails(claim: Claim): Claim = {
    claim.questionGroup[YourPartnerPersonalDetails] match {
      case Some(yourPartnerPersonalDetails) =>
        claim.update(yourPartnerPersonalDetails.copy(
          encryptOptionalString(yourPartnerPersonalDetails.title),
          encryptOptionalString(yourPartnerPersonalDetails.firstName),
          encryptOptionalString(yourPartnerPersonalDetails.middleName),
          encryptOptionalString(yourPartnerPersonalDetails.surname),
          encryptOptionalString(yourPartnerPersonalDetails.otherSurnames),
          encryptOptionalNationalInsuranceNumber(yourPartnerPersonalDetails.nationalInsuranceNumber),
          encryptOptionalDayMonthYear(yourPartnerPersonalDetails.dateOfBirth),
          encryptOptionalString(yourPartnerPersonalDetails.nationality),
          encryptOptionalString(yourPartnerPersonalDetails.separatedFromPartner),
          encryptOptionalString(yourPartnerPersonalDetails.isPartnerPersonYouCareFor),
          encryptString(yourPartnerPersonalDetails.hadPartnerSinceClaimDate)
        ))
      case _ => claim
    }
  }

  def encryptCircumstancesAddressChange(claim: Claim): Claim = {
    claim.questionGroup[CircumstancesAddressChange] match {
      case Some(circumstancesAddressChange) =>
        claim.update(circumstancesAddressChange.copy(
          encryptMultiLineAddress(circumstancesAddressChange.previousAddress),
          encryptOptionalString(circumstancesAddressChange.previousPostcode),
          circumstancesAddressChange.stillCaring,
          encryptMultiLineAddress(circumstancesAddressChange.newAddress),
          encryptOptionalString(circumstancesAddressChange.newPostcode),
          circumstancesAddressChange.caredForChangedAddress,
          encryptYesNoWithAddress(circumstancesAddressChange.sameAddress),
          encryptOptionalString(circumstancesAddressChange.moreAboutChanges)
        ))
      case _ => claim
    }
  }

  def encryptCircumstancesPaymentChange(claim: Claim): Claim = {
    claim.questionGroup[CircumstancesPaymentChange] match {
      case Some(circumstancesPaymentChange) =>
        claim.update(circumstancesPaymentChange.copy(
          encryptString(circumstancesPaymentChange.currentlyPaidIntoBankAnswer),
          encryptOptionalString(circumstancesPaymentChange.currentlyPaidIntoBankText1),
          encryptOptionalString(circumstancesPaymentChange.currentlyPaidIntoBankText2),
          encryptString(circumstancesPaymentChange.accountHolderName),
          encryptString(circumstancesPaymentChange.bankFullName),
          encryptSortCode(circumstancesPaymentChange.sortCode),
          encryptString(circumstancesPaymentChange.accountNumber),
          encryptString(circumstancesPaymentChange.rollOrReferenceNumber),
          encryptString(circumstancesPaymentChange.paymentFrequency),
          encryptOptionalString(circumstancesPaymentChange.moreAboutChanges)
        ))
      case _ => claim
    }
  }

  def encryptSaveClaimMap(claim: Claim): Claim = {
    claim.update(saveForLaterPageData = claim.saveForLaterCurrentPageData.map { case (k,v) => k -> encryptString(v) })
  }

  def decryptSaveClaimMap(claim: Claim): Claim = {
    claim.update(saveForLaterPageData = claim.saveForLaterCurrentPageData.map { case (k,v) => k -> decryptString(v)})
  }

  def decryptYourDetails(claim: Claim): Claim = {
    claim.questionGroup[YourDetails] match {
      case Some(yourDetails) =>
        claim.update(yourDetails.copy(
          decryptString(yourDetails.title),
          decryptString(yourDetails.firstName),
          decryptOptionalString(yourDetails.middleName),
          decryptString(yourDetails.surname),
          decryptNationalInsuranceNumber(yourDetails.nationalInsuranceNumber),
          decryptDayMonthYear(yourDetails.dateOfBirth)
        ))
      case _ => claim
    }
  }

  def decryptContactDetails(claim: Claim): Claim = {
    claim.questionGroup[ContactDetails] match {
      case Some(contactDetails) =>
        claim.update(contactDetails.copy(
          decryptMultiLineAddress(contactDetails.address),
          decryptOptionalString(contactDetails.postcode),
          decryptOptionalString(contactDetails.howWeContactYou),
          contactDetails.contactYouByTextphone,
          contactDetails.wantsContactEmail,
          decryptOptionalString(contactDetails.email),
          decryptOptionalString(contactDetails.emailConfirmation)
        ))
      case _ => claim
    }

  }

  def decryptTheirPersonalDetails(claim: Claim): Claim = {
    claim.questionGroup[TheirPersonalDetails] match {
      case Some(theirPersonalDetails) =>
        claim.update(theirPersonalDetails.copy(
          decryptString(theirPersonalDetails.title),
          decryptString(theirPersonalDetails.firstName),
          decryptOptionalString(theirPersonalDetails.middleName),
          decryptString(theirPersonalDetails.surname),
          decryptOptionalNationalInsuranceNumber(theirPersonalDetails.nationalInsuranceNumber),
          decryptDayMonthYear(theirPersonalDetails.dateOfBirth),
          decryptString(theirPersonalDetails.relationship),
          decryptYesNoMandWithAddress(theirPersonalDetails.theirAddress)
        ))
      case _ => claim
    }
  }

  def decryptCircumstancesReportChange(claim: Claim): Claim = {
    claim.questionGroup[CircumstancesYourDetails] match {
      case Some(circumstancesReportChange) =>
        claim.update(circumstancesReportChange.copy(
          decryptString(circumstancesReportChange.firstName),
          decryptString(circumstancesReportChange.surname),
          decryptNationalInsuranceNumber(circumstancesReportChange.nationalInsuranceNumber),
          decryptDayMonthYear(circumstancesReportChange.dateOfBirth),
          decryptString(circumstancesReportChange.wantsContactEmail),
          decryptOptionalString(circumstancesReportChange.email),
          decryptOptionalString(circumstancesReportChange.emailConfirmation),
          decryptString(circumstancesReportChange.theirFirstName),
          decryptString(circumstancesReportChange.theirSurname),
          decryptString(circumstancesReportChange.theirRelationshipToYou),
          decryptOptionalString(circumstancesReportChange.furtherInfoContact)
        ))
      case _ => claim
    }
  }

  def decryptHowWePayYou(claim: Claim): Claim = {
    claim.questionGroup[HowWePayYou] match {
      case Some(howWePayYou) =>
        claim.update(howWePayYou.copy(
          decryptString(howWePayYou.likeToBePaid),
          decryptOptionalBankBuildingSoceityDetails(howWePayYou.bankDetails),
          decryptString(howWePayYou.paymentFrequency)
        ))
      case _ => claim
    }
  }

  def decryptYourPartnerPersonalDetails(claim: Claim): Claim = {
    claim.questionGroup[YourPartnerPersonalDetails] match {
      case Some(yourPartnerPersonalDetails) =>
        claim.update(yourPartnerPersonalDetails.copy(
          decryptOptionalString(yourPartnerPersonalDetails.title),
          decryptOptionalString(yourPartnerPersonalDetails.firstName),
          decryptOptionalString(yourPartnerPersonalDetails.middleName),
          decryptOptionalString(yourPartnerPersonalDetails.surname),
          decryptOptionalString(yourPartnerPersonalDetails.otherSurnames),
          decryptOptionalNationalInsuranceNumber(yourPartnerPersonalDetails.nationalInsuranceNumber),
          decryptOptionalDayMonthYear(yourPartnerPersonalDetails.dateOfBirth),
          decryptOptionalString(yourPartnerPersonalDetails.nationality),
          decryptOptionalString(yourPartnerPersonalDetails.separatedFromPartner),
          decryptOptionalString(yourPartnerPersonalDetails.isPartnerPersonYouCareFor),
          decryptString(yourPartnerPersonalDetails.hadPartnerSinceClaimDate)
        ))
      case _ => claim
    }
  }

  def decryptCircumstancesAddressChange(claim: Claim): Claim = {
    claim.questionGroup[CircumstancesAddressChange] match {
      case Some(circumstancesAddressChange) =>
        claim.update(circumstancesAddressChange.copy(
          decryptMultiLineAddress(circumstancesAddressChange.previousAddress),
          decryptOptionalString(circumstancesAddressChange.previousPostcode),
          circumstancesAddressChange.stillCaring,
          decryptMultiLineAddress(circumstancesAddressChange.newAddress),
          decryptOptionalString(circumstancesAddressChange.newPostcode),
          circumstancesAddressChange.caredForChangedAddress,
          decryptYesNoWithAddress(circumstancesAddressChange.sameAddress),
          decryptOptionalString(circumstancesAddressChange.moreAboutChanges)
        ))
      case _ => claim
    }
  }

  def decryptCircumstancesPaymentChange(claim: Claim): Claim = {
    claim.questionGroup[CircumstancesPaymentChange] match {
      case Some(circumstancesPaymentChange) =>
        claim.update(circumstancesPaymentChange.copy(
          decryptString(circumstancesPaymentChange.currentlyPaidIntoBankAnswer),
          decryptOptionalString(circumstancesPaymentChange.currentlyPaidIntoBankText1),
          decryptOptionalString(circumstancesPaymentChange.currentlyPaidIntoBankText2),
          decryptString(circumstancesPaymentChange.accountHolderName),
          decryptString(circumstancesPaymentChange.bankFullName),
          decryptSortCode(circumstancesPaymentChange.sortCode),
          decryptString(circumstancesPaymentChange.accountNumber),
          decryptString(circumstancesPaymentChange.rollOrReferenceNumber),
          decryptString(circumstancesPaymentChange.paymentFrequency),
          decryptOptionalString(circumstancesPaymentChange.moreAboutChanges)
        ))
      case _ => claim
    }
  }

}
