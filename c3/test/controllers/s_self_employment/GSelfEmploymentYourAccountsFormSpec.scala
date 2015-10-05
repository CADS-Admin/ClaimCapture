package controllers.s_self_employment

import org.specs2.mutable.{Tags, Specification}

class GSelfEmploymentYourAccountsFormSpec extends Specification with Tags {

  "About Self Employment - Your Accounts" should {

    "map data into case class" in {
      GSelfEmploymentYourAccounts.form.bind(
        Map("doYouKnowYourTradingYear" -> "yes",
          "whatWasOrIsYourTradingYearFrom.day" -> "11",
          "whatWasOrIsYourTradingYearFrom.month" -> "11",
          "whatWasOrIsYourTradingYearFrom.year" -> "2011",
          "whatWasOrIsYourTradingYearTo.day" -> "11",
          "whatWasOrIsYourTradingYearTo.month" -> "11",
          "whatWasOrIsYourTradingYearTo.year" -> "2018",
          "areIncomeOutgoingsProfitSimilarToTrading" -> "yes",
          "tellUsWhyAndWhenTheChangeHappened" -> "A year back")
      ).fold(
        formWithErrors => "This mapping should not happen." must equalTo("Error"),
        f => {
          f.tellUsWhyAndWhenTheChangeHappened must equalTo(Some("A year back"))
        }
      )
    }

    "map data with do you know your trading year set as no" in {
      GSelfEmploymentYourAccounts.form.bind(
        Map("doYouKnowYourTradingYear" -> "no")
      ).fold(
          formWithErrors => "This mapping should not happen." must equalTo("Error"),
          f => {
            f.doYouKnowYourTradingYear must equalTo(Some("no))
          }
        )
    }

    "reject if tellUsWhyAndWhenTheChangeHappened is not filled" in {
      GSelfEmploymentYourAccounts.form.bind(
        Map("areAccountsPreparedOnCashFlowBasis" -> "yes",
          "areIncomeOutgoingsProfitSimilarToTrading" -> "no")
      ).fold(
        formWithErrors => formWithErrors.errors.head.message must equalTo("required"),
        f => "This mapping should not happen." must equalTo("Valid")
      )
    }

    "reject if do you know your trading year is not selected" in {
      GSelfEmploymentYourAccounts.form.bind(
        Map("whatWasOrIsYourTradingYearFrom.day" -> "11",
          "whatWasOrIsYourTradingYearFrom.month" -> "11",
          "whatWasOrIsYourTradingYearFrom.year" -> "2011",
          "whatWasOrIsYourTradingYearTo.day" -> "11",
          "whatWasOrIsYourTradingYearTo.month" -> "11",
          "whatWasOrIsYourTradingYearTo.year" -> "2018",
          "areIncomeOutgoingsProfitSimilarToTrading" -> "yes",
          "tellUsWhyAndWhenTheChangeHappened" -> "A year back")
      ).fold(
          formWithErrors => formWithErrors.errors.head.message must equalTo("required"),
          f => "This mapping should not happen." must equalTo("Valid")
        )
    }
  } section("unit", models.domain.SelfEmployment.id)
}