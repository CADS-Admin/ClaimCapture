package xml.claim

import models.domain.{BankBuildingSocietyDetails, Claim, HowWePayYou}
import xml.XMLHelper._
import scala.xml.NodeSeq
import app.{PaymentFrequency, AccountStatus}
import play.api.i18n.Messages
import xml.XMLComponent

object Payment extends XMLComponent {

  def xml(claim: Claim) = {

    val howWePayYou = claim.questionGroup[HowWePayYou].getOrElse(HowWePayYou())

    val showAccount = howWePayYou.likeToBePaid == Messages(AccountStatus.BankBuildingAccount.name)

    claim.questionGroup[HowWePayYou] match {
      case Some(howWePayYou) => {
        <Payment>
          <PaymentFrequency>
            <QuestionLabel>PaymentFrequency?</QuestionLabel>
            <Answer>{howWePayYou.paymentFrequency}</Answer>
          </PaymentFrequency>

          <InitialAccountQuestion>
            <QuestionLabel>InitialAccountQuestion?</QuestionLabel>
            <Answer>{howWePayYou.likeToBePaid}</Answer>
          </InitialAccountQuestion>

          {if (showAccount) account(claim) else NodeSeq.Empty}
        </Payment>
      }
      case None => NodeSeq.Empty
    }
  }

  def account(claim:Claim) = {
    val bankBuildingSocietyDetails = claim.questionGroup[BankBuildingSocietyDetails].getOrElse(BankBuildingSocietyDetails())


    // TODO If fields below are no longer in the schema then they don't need to be on the website e.g. AccountHolder
    <Account>
      <!--<AccountHolder>{bankBuildingSocietyDetails.whoseNameIsTheAccountIn}</AccountHolder>-->
      <HolderName>{bankBuildingSocietyDetails.accountHolderName}</HolderName>
      <BuildingSocietyDetails>
        <AccountNumber>{bankBuildingSocietyDetails.accountNumber}</AccountNumber>

        {if(bankBuildingSocietyDetails.rollOrReferenceNumber.isEmpty) NodeSeq.Empty
        else <RollNumber>{bankBuildingSocietyDetails.rollOrReferenceNumber}</RollNumber>}

        <SortCode>{stringify(Some(bankBuildingSocietyDetails.sortCode))}</SortCode>
        <Name>{bankBuildingSocietyDetails.bankFullName}</Name>
      </BuildingSocietyDetails>
    </Account>
  }
}