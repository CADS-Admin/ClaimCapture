package xml.claim

import models.domain.Claim
import app.XMLValues._
import xml.XMLComponent

object PropertyRentedOut extends XMLComponent {

  def xml(claim: Claim) = {
    <PropertyRentedOut>
      <PayNationalInsuranceContributions/>
      <RentOutProperty>{NotAsked}</RentOutProperty>
      <SubletHome>{NotAsked}</SubletHome>
    </PropertyRentedOut>
  }
}