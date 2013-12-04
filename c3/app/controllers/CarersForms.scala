package controllers

import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.Constraints
import controllers.Mappings._

object CarersForms  {

  val carersText: Mapping[String] =
    text verifying restrictedStringText

  def carersText(minLength: Int = 0, maxLength: Int = Int.MaxValue): Mapping[String] =
    text(minLength, maxLength) verifying restrictedStringText

  val carersNonEmptyText: Mapping[String] =
    text verifying Constraints.nonEmpty verifying restrictedStringText

  def carersNonEmptyText(minLength: Int = 0, maxLength: Int = Int.MaxValue): Mapping[String] =
    nonEmptyText(minLength, maxLength) verifying restrictedStringText

}
