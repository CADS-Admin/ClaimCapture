package controllers.s8_employment

import org.specs2.mutable.{Tags, Specification}
import utils.WithBrowser
import controllers.{Formulate, BrowserMatchers}

class G1EmploymentIntegrationSpec extends Specification with Tags {
  "Employment - Integration" should {
  } section("integration", models.domain.Employed.id)
}

trait EmployedSinceClaimDate extends BrowserMatchers {
  this: WithBrowser[_] =>

  def beginClaim() = {
    Formulate.claimDate(browser)

    Formulate.employment(browser)
  }
}

trait EducatedSinceClaimDate extends BrowserMatchers {
  this: WithBrowser[_] =>

  def beginClaim() = {
    Formulate.claimDate(browser)

    Formulate.nationalityAndResidency(browser)

    Formulate.otherEEAStateOrSwitzerland(browser)

    Formulate.yourCourseDetails(browser)
  }
}

trait EducatedAndEmployedSinceClaimDate extends BrowserMatchers {
  this: WithBrowser[_] =>

  def beginClaim() = {
    Formulate.claimDate(browser)

    Formulate.nationalityAndResidency(browser)

    Formulate.otherEEAStateOrSwitzerland(browser)

    Formulate.employment(browser)
  }
}

trait NotEmployedSinceClaimDate extends BrowserMatchers {
  this: WithBrowser[_] =>

  def beginClaim() = {
    Formulate.claimDate(browser)

    Formulate.notInEmployment(browser)
  }
}