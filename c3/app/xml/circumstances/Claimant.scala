package xml.circumstances

import models.domain.{CircumstancesDeclaration, CircumstancesReportChange, Claim}
import scala.xml.NodeSeq
import xml.XMLHelper._
import models.domain.Claim
import scala.Some

/**
 * Created by neddakaltcheva on 3/13/14.
 */
object Claimant {
  def xml(circs :Claim): NodeSeq = {
    val reportChange = circs.questionGroup[CircumstancesReportChange].getOrElse(CircumstancesReportChange())
    val contactPreference = circs.questionGroup[CircumstancesDeclaration].getOrElse(CircumstancesDeclaration())

    <ClaimantDetails>
      <FullName>{reportChange.fullName}</FullName>
      {statement(<DateOfBirth/>,reportChange.dateOfBirth.`dd-MM-yyyy`)}
      <NationalInsuranceNumber>{stringify(Some(reportChange.nationalInsuranceNumber))}</NationalInsuranceNumber>
      {question(<ContactPreference/>,"furtherInfoContact", contactPreference.furtherInfoContact)}
    </ClaimantDetails>
  }
}
