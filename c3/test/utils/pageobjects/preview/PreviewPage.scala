package utils.pageobjects.preview

import utils.pageobjects.xml_validation.XMLBusinessValidation
import utils.pageobjects.{TestData, PageContext, ClaimPage, PageObjectsContext}
import utils.WithBrowser


final class PreviewPage(ctx:PageObjectsContext) extends ClaimPage(ctx, PreviewPage.url) {

  def validateXmlWith(claim: TestData, validator: XMLBusinessValidation) = {

    Tuple2(
      validator.validateXMLClaim(claim, source, throwException = false),
      validator.warnings
    )
  }
}

object PreviewPage {
  val url = "/preview"

  def apply(ctx:PageObjectsContext) = new PreviewPage(ctx)
}

trait PreviewPageContext extends PageContext {
  this: WithBrowser[_] =>

  val page = PreviewPage (PageObjectsContext(browser))
}
