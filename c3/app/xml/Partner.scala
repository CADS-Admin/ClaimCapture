package xml

import models.domain._
import XMLHelper.{stringify, postalAddressStructure}
import scala.xml.NodeSeq
import app.XMLValues._
import models.Street

object Partner {

  def xml(claim: Claim) = {
    val moreAboutYou = claim.questionGroup[MoreAboutYou].getOrElse(MoreAboutYou(hadPartnerSinceClaimDate = no))

    val yourPartnerPersonalDetails = claim.questionGroup[YourPartnerPersonalDetails].getOrElse(YourPartnerPersonalDetails())

    val hadPartner = moreAboutYou.hadPartnerSinceClaimDate == yes

    if (hadPartner) {
      <Partner>
        <NationalityPartner>{yourPartnerPersonalDetails.nationality.orNull}</NationalityPartner>
        <Surname>{yourPartnerPersonalDetails.surname}</Surname>
        <OtherNames>{yourPartnerPersonalDetails.firstName} {yourPartnerPersonalDetails.middleName.orNull}</OtherNames>
        <OtherSurnames>{yourPartnerPersonalDetails.otherSurnames.orNull}</OtherSurnames>
        <Title>{yourPartnerPersonalDetails.title}</Title>
        <DateOfBirth>{yourPartnerPersonalDetails.dateOfBirth.`dd-MM-yyyy`}</DateOfBirth>
        <NationalInsuranceNumber>{stringify(yourPartnerPersonalDetails.nationalInsuranceNumber)}</NationalInsuranceNumber>
        <Address>{postalAddressStructure(models.MultiLineAddress(Street(Some(NotAsked))), "")}</Address>
        <ConfirmAddress>{yes}</ConfirmAddress>
        <RelationshipStatus>
          <JoinedHouseholdAfterDateOfClaim>{NotAsked}</JoinedHouseholdAfterDateOfClaim>
          <JoinedHouseholdDate></JoinedHouseholdDate>
          <SeparatedFromPartner>{yourPartnerPersonalDetails.separatedFromPartner}</SeparatedFromPartner>
          <SeparationDate></SeparationDate>
        </RelationshipStatus>
      </Partner>
    } else NodeSeq.Empty
  }
}
