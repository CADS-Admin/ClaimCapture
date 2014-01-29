package utils.pageobjects.circumstances.s3_consent_and_declaration

import play.api.test.WithBrowser
import utils.pageobjects._

final class G1DeclarationPage (ctx:PageObjectsContext) extends CircumstancesPage(ctx, G1DeclarationPage.url, G1DeclarationPage.title){
  declareYesNo("#obtainInfoAgreement","CircumstancesDeclarationInfoAgreement")
  declareInput("#obtainInfoWhy","CircumstancesDeclarationWhyNot")
  declareCheck("#confirm","CircumstancesDeclarationConfirmation")
}

object G1DeclarationPage {
  val title = "Declaration - consent and declaration".toLowerCase

  val url  = "/circumstances/consent-and-declaration/declaration"

  def apply(ctx:PageObjectsContext) = new G1DeclarationPage(ctx)
}

trait G1DeclarationPageContext extends PageContext {
  this: WithBrowser[_] =>
  val page = G1DeclarationPage(PageObjectsContext(browser))
}


