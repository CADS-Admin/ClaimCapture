package controllers.s11_consent_and_declaration

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser
import controllers.{BrowserMatchers, Formulate}
import utils.pageobjects.{TestData, PageObjects}
import utils.pageobjects.S11_consent_and_declaration.G4DeclarationPage

class G4DeclarationIntegrationSpec extends Specification with Tags {
  "Declaration" should {
    "be presented" in new WithBrowser with BrowserMatchers {
      browser.goTo("/consent-and-declaration/declaration")
      titleMustEqual("Declaration - Consent and Declaration")
    }

    "contain errors on invalid submission" in new WithBrowser with BrowserMatchers {
      browser.goTo("/consent-and-declaration/declaration")
      titleMustEqual("Declaration - Consent and Declaration")

      browser.submit("button[type='submit']")
      titleMustEqual("Declaration - Consent and Declaration")
      findMustEqualSize("div[class=validation-summary] ol li", 1)
    }

    "navigate to next page on valid submission" in new WithBrowser with BrowserMatchers {
      Formulate.declaration(browser)
      titleMustEqual("Documents you need to send us - Consent and Declaration")
    }

    "navigate back to Disclaimer" in new WithBrowser with BrowserMatchers {
      Formulate.disclaimer(browser)
      titleMustEqual("Declaration - Consent and Declaration")

      browser.click(".form-steps a")
      titleMustEqual("Disclaimer - Consent and Declaration")
    }

    "contain the completed forms" in new WithBrowser with BrowserMatchers {
      Formulate.declaration(browser)
      titleMustEqual("Documents you need to send us - Consent and Declaration")

      findMustEqualSize("div[class=completed] ul li", 1)
    }

    "not have name or organisation field with optional text" in new WithBrowser with PageObjects{
      val page =  G4DeclarationPage(context)
      val claim = new TestData
      claim.ConsentDeclarationSomeoneElseTickBox = "Yes"

      page goToThePage()
      page fillPageWith claim

      page.readLabel("nameOrOrganisation") mustEqual("Your name and/or organisation")
    }

  } section("integration", models.domain.ConsentAndDeclaration.id)
}