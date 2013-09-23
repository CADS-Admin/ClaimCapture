package utils.pageobjects.s8_self_employment

import play.api.test.{WithBrowser, TestBrowser}
import utils.pageobjects.{ClaimPage,Page, PageContext}

final class G7ExpensesWhileAtWorkPage(browser: TestBrowser, previousPage: Option[Page] = None) extends ClaimPage(browser, G7ExpensesWhileAtWorkPage.url, G7ExpensesWhileAtWorkPage.title, previousPage) {
  declareInput("#nameOfPerson", "SelfEmployedCareExpensesNameOfPerson")
  declareInput("#howMuchYouPay", "SelfEmployedCareExpensesHowMuchYouPay")
  declareSelect("#howOftenPayExpenses_frequency","SelfEmployedCareExpensesHowOften")
  declareInput("#howOftenPayExpenses_frequency_other","SelfEmployedCareExpensesHowOftenOther")
  declareSelect("#whatRelationIsToYou", "SelfEmployedCareExpensesWhatRelationIsToYou")
  declareSelect("#relationToPartner", "SelfEmployedCareExpensesWhatRelationToPartner")
  declareSelect("#whatRelationIsTothePersonYouCareFor", "SelfEmployedCareExpensesWhatRelationIsTothePersonYouCareFor")
}

object G7ExpensesWhileAtWorkPage {
  val title = "Expenses related to the person you care for while at work - About self-employment".toLowerCase
  val url = "/self-employment/expenses-while-at-work"

  def apply(browser: TestBrowser, previousPage: Option[Page] = None) = new G7ExpensesWhileAtWorkPage(browser, previousPage)
}

trait G7ExpensesWhileAtWorkPageContext extends PageContext {
  this: WithBrowser[_] =>

  val page = G7ExpensesWhileAtWorkPage (browser)
}