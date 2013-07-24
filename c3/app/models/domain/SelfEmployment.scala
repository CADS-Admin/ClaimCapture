package models.domain

import models.DayMonthYear
import play.api.mvc.Call


case object SelfEmployment extends Section.Identifier {
  val id = "s9"
}

case object AboutSelfEmployment extends QuestionGroup.Identifier {
  val id = s"${SelfEmployment.id}.g1"
}

case class AboutSelfEmployment(call: Call,
                               areYouSelfEmployedNow: String,
                               whenDidYouStartThisJob: Option[DayMonthYear],
                               whenDidTheJobFinish: Option[DayMonthYear],
                               haveYouCeasedTrading: Option[String],
                               natureOfYourBusiness: Option[String]
                                ) extends QuestionGroup(AboutSelfEmployment)


