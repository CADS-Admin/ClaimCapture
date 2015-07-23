package utils.pageobjects.s8_employment

import utils.pageobjects.{PageContext, ClaimPage, PageObjectsContext}
import utils.WithBrowser


final class G9EmploymentAdditionalInfoPage (ctx:PageObjectsContext) extends ClaimPage(ctx, G9EmploymentAdditionalInfoPage.url) {
  declareYesNo("#empAdditionalInfo_answer", "EmploymentDoYouWantToAddAnythingAboutYourWork")
  declareInput("#empAdditionalInfo_text", "EmploymentAdditionalInfo")
}

object G9EmploymentAdditionalInfoPage {
  val url  = "/employment/additional-info"

  def apply(ctx:PageObjectsContext) = new G9EmploymentAdditionalInfoPage(ctx)
}

trait G9EmploymentAdditionalInfoPageContext extends PageContext {
  this: WithBrowser[_] =>

  val page = G9EmploymentAdditionalInfoPage (PageObjectsContext(browser))
}