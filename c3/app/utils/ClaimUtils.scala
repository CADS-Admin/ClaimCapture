package utils

import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

object ClaimUtils {

  def sectionId(formId: String) = {
    formId.split('.')(0)
  }
  val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("'DayMonthYear'('Some'(dd),'Some'(M),'Some'(yyyy))")
  val dateFormatPrint: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")
}