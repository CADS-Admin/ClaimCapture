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
            case Some(n) => {question(<StatePension/>,"receiveStatePension", n.receiveStatePension) }
            case None => NodeSeq.Empty
          }
       }
      </ClaimantBenefits>
      {question(<OtherMoneySSP/>,"haveYouHadAnyStatutorySickPay.label",statutorySickPay.haveYouHadAnyStatutorySickPay,claim.dateOfClaim.fold("{NO CLAIM DATE}")(_.`dd/MM/yyyy`))}
      {otherMoneySPPXml(statutorySickPay)}
      <OtherMoneySP>
        <QuestionLabel>{Messages("otherPay.label", claim.dateOfClaim.fold("")(_.`dd/MM/yyyy`))}</QuestionLabel>
        <Answer>{otherStatutoryPayOption.otherPay match {
          case "yes" => XMLValues.Yes
          case "no" => XMLValues.No
          case n => n
        }}</Answer>
      </OtherMoneySP>
      {otherMoneySPDetails(otherStatutoryPayOption)}
      <OtherMoney>
        <QuestionLabel>{Messages("othermoney.label")}</QuestionLabel>
        <Answer>{aboutOtherMoney.yourBenefits.answer match {
          case "yes" => XMLValues.Yes
          case "no" => XMLValues.No
          case n => n
        }}</Answer>
      </OtherMoney>

      {
      <OtherMoneyPayments>
        <QuestionLabel>{Messages("anyPaymentsSinceClaimDate.answer.label", claim.dateOfClaim.fold("{NO CLAIM DATE}")(_.`dd/MM/yyyy`))}</QuestionLabel>
        <Answer>{aboutOtherMoney.anyPaymentsSinceClaimDate.answer match {
          case "yes" => XMLValues.Yes
          case "no" => XMLValues.No
          case n => n
        }}</Answer>
      </OtherMoneyPayments>
      }

      {aboutOtherMoney.anyPaymentsSinceClaimDate.answer match {
          case "yes" =>{
              <OtherMoneyDetails>
                <Payment>
                  {aboutOtherMoney.howMuch match {
                  case Some(n) => {
                    <Payment>
                      <QuestionLabel>{Messages("howMuch.label")}</QuestionLabel>
                      <Answer>
                        <Currency>{GBP}</Currency>
                        <Amount>{aboutOtherMoney.howMuch.orNull}</Amount>
                      </Answer>
                    </Payment>

                  }
                  case None => NodeSeq.Empty
                }}

                {aboutOtherMoney.howOften match {
                  case Some(howOften) => {
                    <Frequency>
                      <QuestionLabel>{Messages("howOftenPension")}</QuestionLabel>
                      {howOften.frequency match {
                        case "Other" => <Other>{howOften.other.orNull}</Other>
                        case _ => NodeSeq.Empty
                      }}
                      <Answer>{howOften.frequency}</Answer>
                    </Frequency>
                  }
                  case None => NodeSeq.Empty
                }}
                </Payment>

                {aboutOtherMoney.whoPaysYou match {
                  case Some(n) => {<Name>
                    <QuestionLabel>{Messages("whoPaysYou.label")}</QuestionLabel>
                    <Answer>{aboutOtherMoney.whoPaysYou.orNull}</Answer>
                  </Name>}
                  case None => NodeSeq.Empty
                }}

              </OtherMoneyDetails>
          }
          case "no" => NodeSeq.Empty
          case n => throw new RuntimeException("AnyPaymentsSinceClaimDate is either Yes Or No")
        }}
      <EEA>
        <EEAReceivePensionsBenefits>
          <QuestionLabel>{Messages("benefitsFromOtherEEAStateOrSwitzerland")}</QuestionLabel>
          <Answer>{otherEEAState.benefitsFromOtherEEAStateOrSwitzerland match {
            case "yes" => XMLValues.Yes
            case "no" => XMLValues.No
            case n => n
          }}</Answer>
        </EEAReceivePensionsBenefits>
        <EEAWorkingInsurance>
          <QuestionLabel>{Messages("workingForOtherEEAStateOrSwitzerland")}</QuestionLabel>
          <Answer>{otherEEAState.workingForOtherEEAStateOrSwitzerland match {
            case "yes" => XMLValues.Yes
            case "no" => XMLValues.No
            case n => n
          }}</Answer>
        </EEAWorkingInsurance>
      </EEA>
    </OtherBenefits>
  }

  def otherMoneySPPXml(statutorySickPay: StatutorySickPay) = {
    if (statutorySickPay.haveYouHadAnyStatutorySickPay == yes) {
      <OtherMoneySSPDetails>
          <Payment>
          {questionCurrency(<Payment/>,"howMuch",statutorySickPay.howMuch) }
          {statutorySickPay.howOften match {
          case Some(howOften) => {
            {questionOther(<Frequency/>,"howOften_frequency",howOften.frequency,howOften.other)}
//
//            <Frequency>
//              <QuestionLabel>{Messages("howOften_frequency")}</QuestionLabel>
//              {howOften.frequency match {
//              case "Other" => <Other>{howOften.other.getOrElse("")}</Other>
//              case _ => NodeSeq.Empty
//            }}
//              <Answer>{howOften.frequency}</Answer>
//            </Frequency>
          }
          case None => NodeSeq.Empty
        }}
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
            {otherStatutoryPay.howMuch match {
            case Some(n) => {
              <Payment>
                <QuestionLabel>{Messages("howMuch")}</QuestionLabel>
                <Answer>
                  <Currency>{GBP}</Currency>
                  <Amount>{otherStatutoryPay.howMuch.orNull}</Amount>
                </Answer>
              </Payment>

            }
            case None => NodeSeq.Empty
          }}

          {otherStatutoryPay.howOften match {
          case Some(howOften) => {
            <Frequency>
              <QuestionLabel>{Messages("howOften_frequency")}</QuestionLabel>
              {howOften.frequency match {
              case "Other" => <Other>{howOften.other.getOrElse("")}</Other>
              case _ => NodeSeq.Empty
            }}
              <Answer>{howOften.frequency}</Answer>
            </Frequency>
          }
          case None => NodeSeq.Empty
        }}
        </Payment>
        <Name>{otherStatutoryPay.employersName.getOrElse("empty")}</Name>
        {postalAddressStructure(otherStatutoryPay.employersAddress, otherStatutoryPay.employersPostcode)}
      </OtherMoneySPDetails>
    }
    else NodeSeq.Empty
  }
}