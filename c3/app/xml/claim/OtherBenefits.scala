package xml.claim

import app.{StatutoryPaymentFrequency, XMLValues}
import app.XMLValues._
import models.domain._
import scala.xml.NodeSeq
import xml.XMLHelper._
import xml.XMLComponent
import models.domain.Claim
import scala.Some
import play.api.i18n.Messages
import models.domain.Claim
import scala.Some


object OtherBenefits extends XMLComponent {

  def xml(claim: Claim) = {
    val moreAboutYou = claim.questionGroup[MoreAboutYou]
    val statutorySickPay = claim.questionGroup[StatutorySickPay].getOrElse(StatutorySickPay(haveYouHadAnyStatutorySickPay = no))
    val otherStatutoryPayOption = claim.questionGroup[OtherStatutoryPay].getOrElse(OtherStatutoryPay(otherPay = no))
    val aboutOtherMoney = claim.questionGroup[AboutOtherMoney].getOrElse(AboutOtherMoney())
    val otherEEAState = claim.questionGroup[OtherEEAStateOrSwitzerland].getOrElse(OtherEEAStateOrSwitzerland())


    <OtherBenefits>
      <ClaimantBenefits>
          {
          moreAboutYou match {
            case Some(n) => question(<StatePension></StatePension>,"receiveStatePension",n.receiveStatePension)
            case _ => NodeSeq.Empty
          }
        }
      </ClaimantBenefits>
      {question(<OtherMoneySSP/>,"haveYouHadAnyStatutorySickPay.label", statutorySickPay.haveYouHadAnyStatutorySickPay,claim.dateOfClaim.fold("{NO CLAIM DATE}")(_.`dd/MM/yyyy`))}
      {otherMoneySPPXml(statutorySickPay)}
      {question(<OtherMoneySP/>,"otherPay.label",otherStatutoryPayOption.otherPay,claim.dateOfClaim.fold("")(_.`dd/MM/yyyy`))}
      {otherMoneySPDetails(otherStatutoryPayOption)}
      {question(<OtherMoney/>,"othermoney.label", aboutOtherMoney.yourBenefits.answer)}
      {question(<OtherMoneyPayments/>,"anyPaymentsSinceClaimDate.answer.label",aboutOtherMoney.anyPaymentsSinceClaimDate.answer,claim.dateOfClaim.fold("{NO CLAIM DATE}")(_.`dd/MM/yyyy`))}

      {aboutOtherMoney.anyPaymentsSinceClaimDate.answer match {
          case "yes" =>{
                <OtherMoneyDetails>
                  <Payment>
                    {questionCurrency(<Payment/>,"howMuch.label", aboutOtherMoney.howMuch)}
                    {questionOther(<Frequency/>,"howOftenPension", aboutOtherMoney.howOften.get.frequency, aboutOtherMoney.howOften.get.other)}
                  </Payment>
                  {question(<Name/>,"whoPaysYou.label", aboutOtherMoney.whoPaysYou)}
                  </OtherMoneyDetails>
                }
                case "no" => NodeSeq.Empty
                case n => throw new RuntimeException("AnyPaymentsSinceClaimDate is either Yes Or No")
        }}
      <EEA>
        {question(<EEAReceivePensionsBenefits/>,"benefitsFromOtherEEAStateOrSwitzerland", otherEEAState.benefitsFromOtherEEAStateOrSwitzerland)}
        {question(<EEAWorkingInsurance/>,"workingForOtherEEAStateOrSwitzerland", otherEEAState.workingForOtherEEAStateOrSwitzerland)}
      </EEA>
    </OtherBenefits>
  }

  def otherMoneySPPXml(statutorySickPay: StatutorySickPay) = {
    if (statutorySickPay.haveYouHadAnyStatutorySickPay == yes) {
      <OtherMoneySSPDetails>
          <Payment>
            {questionCurrency(<Payment/>,"howMuch",statutorySickPay.howMuch)}
            {questionOther(<Frequency/>,"howOften_frequency", statutorySickPay.howOften.get.frequency, statutorySickPay.howOften.get.other)}
        </Payment>
        <Name>{statutorySickPay.employersName.orNull}</Name>
        {postalAddressStructure(statutorySickPay.employersAddress, statutorySickPay.employersPostcode)}
      </OtherMoneySSPDetails>
    }
    else NodeSeq.Empty
  }

  def otherMoneySPDetails(otherStatutoryPay: OtherStatutoryPay) = {
    if (otherStatutoryPay.otherPay == yes) {
      <OtherMoneySPDetails>
          <Payment>
           {questionCurrency(<Payment/>, "howMuch", otherStatutoryPay.howMuch)}
           {questionOther(<Frequency/>,"howOften_frequency", otherStatutoryPay.howOften.get.frequency, otherStatutoryPay.howOften.get.other)}
        </Payment>
        <Name>{otherStatutoryPay.employersName.getOrElse("empty")}</Name>
        {postalAddressStructure(otherStatutoryPay.employersAddress, otherStatutoryPay.employersPostcode)}
      </OtherMoneySPDetails>
    }
    else NodeSeq.Empty
  }
}