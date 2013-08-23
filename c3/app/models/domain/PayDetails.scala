package models.domain

import app.XMLValues
import models.SortCode

object PayDetails extends Section.Identifier {
  val id = "s10"
}

case class HowWePayYou(likeToBePaid: String = XMLValues.NotAsked, paymentFrequency: String = XMLValues.NotAsked) extends QuestionGroup(HowWePayYou)

object HowWePayYou extends QuestionGroup.Identifier {
  val id = s"${PayDetails.id}.g1"
}

case class BankBuildingSocietyDetails(accountHolderName: String = "",
                                      bankFullName: String = "",
                                      sortCode: SortCode = SortCode("","",""),
                                      accountNumber: String = "",
                                      rollOrReferenceNumber: String = "") extends QuestionGroup(BankBuildingSocietyDetails)

object BankBuildingSocietyDetails extends QuestionGroup.Identifier {
  val id = s"${PayDetails.id}.g2"
}