package models.yesNo

import models.DayMonthYear
import controllers.Mappings._

/**
 * Created by neddakaltcheva on 3/20/14.
 */

case class YesNoWithDateTimeAndText(answer: String = "", date: Option[DayMonthYear] = None, time: Option[String] = None,
                                    expectStartCaring: Option[String] = None)

object YesNoWithDateTimeAndText {

  def validateOnYes (input: YesNoWithDateTimeAndText) : Boolean = input.answer match {
    case `yes` => {
      input.date.isDefined
      input.time.isDefined
    }
    case `no` => true
  }

  def validateOnNo (input: YesNoWithDateTimeAndText) : Boolean = input.answer match {
    case `yes` => true
    case `no` => {
      input.expectStartCaring.isDefined
    }
  }
}
