package controllers.submission

import org.specs2.mutable.Specification
import models.domain.{ChangeOfCircs, FullClaim, Claim}
import com.dwp.carers.s2.xml.validation.{CocXmlValidatorImpl, CaXmlValidatorImpl, XmlValidatorFactory}

class SubmissionSpec extends Specification {
  "Submission" should {
    "get CA XML validator for a given full claim" in {
      xmlValidator(new Claim with FullClaim) should beAnInstanceOf[CaXmlValidatorImpl]
    }

    "get COC XML validator for a given change of circs claim" in {
      xmlValidator(new Claim with ChangeOfCircs) should beAnInstanceOf[CocXmlValidatorImpl]
    }
  }
}