package xml

import models.domain._
import models.view.CachedClaim
import org.specs2.mutable._
import models.DayMonthYear
import org.joda.time.DateTime
import models.PaymentFrequency
import utils.WithApplication
import models.yesNo.{YesNoWith1MandatoryFieldOnYes, YesNoWith2MandatoryFieldsOnYes, YesNoWithText}
import xml.claim.AssistedDecision

class AssistedDecisionSpec extends Specification {
  section("unit")
  "Assisted section" should {
    "Create an assisted decision section if care less than 35 hours" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain ("Not caring 35 hours a week.")
      (xml \\ "RecommendedDecision").text must contain ("Potential disallowance decision")
    }

    "Not create an assisted decision section if care more than 35 hours" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "no", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.pip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecisions")(0) mustEqual AssistedDecision.emptyAssistedDecision
    }

    "Create an assisted decision section if date of claim > 3 months and 1 day" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "no", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.pip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val now = DateTime.now()
      val details = ClaimDate(DayMonthYear(now.plusMonths(3).plusDays(2)))
      val claim = Claim(CachedClaim.key).update(details).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain ("Claim date over 3 months into future.")
      (xml \\ "RecommendedDecision").text must contain("Potential disallowance decision")
    }

    "Not create an assisted decision section if date of claim <= 3 month and 1 day" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "no", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.pip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val now = DateTime.now()
      val details = ClaimDate(DayMonthYear(now.plusMonths(3)))
      val claim = Claim(CachedClaim.key).update(details).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecisions")(0) mustEqual AssistedDecision.emptyAssistedDecision
    }

    "Create an assisted decision section if no EEA and no benefits" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "no", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.noneOfTheBenefits)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain("DP on No QB. Check CIS.")
      (xml \\ "RecommendedDecision").text must contain ("Potential disallowance decision")
    }

    "Create an assisted decision section if no EEA and AFIP" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "no", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.afip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain("Assign to AFIP officer on CAMLite workflow.")
      (xml \\ "RecommendedDecision").text must contain ("None")
    }

    "Create an assisted decision section if EEA insurance or working 1" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "yes", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="yes")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.afip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain("Assign to Exportability in CAMLite workflow.")
      (xml \\ "RecommendedDecision").text must contain ("None")
    }

    "Create an assisted decision section if EEA insurance or working 2" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "yes", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="yes"))))
      val benefits = Benefits(benefitsAnswer = Benefits.afip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain("Assign to Exportability in CAMLite workflow.")
      (xml \\ "RecommendedDecision").text must contain ("None")
    }

    "Create an assisted decision section if EEA insurance or working 3" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "yes", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="yes")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="yes"))))
      val benefits = Benefits(benefitsAnswer = Benefits.afip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain("Assign to Exportability in CAMLite workflow.")
      (xml \\ "RecommendedDecision").text must contain ("None")
    }

    "Not create an assisted decision section if no EEA insurance or working" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "yes", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.afip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "no")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecisions")(0) mustEqual AssistedDecision.emptyAssistedDecision
    }

    "Create an assisted decision section if no EEA and in education" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "no", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="yes")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="yes"))))
      val benefits = Benefits(benefitsAnswer = Benefits.caa)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "yes")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text must contain("Send DS790/790B COMB to customer.")
      (xml \\ "RecommendedDecision").text must contain ("None")
    }

    "Not create an assisted decision section if no EEA and in education but afip claim" in new WithApplication {
      val moreAboutTheCare = MoreAboutTheCare("yes")
      val otherEEAStateOrSwitzerland = OtherEEAStateOrSwitzerland(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = "yes", field1=Some(YesNoWith1MandatoryFieldOnYes(answer="no")), field2=Some(YesNoWith1MandatoryFieldOnYes(answer="no"))))
      val benefits = Benefits(benefitsAnswer = Benefits.afip)
      val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = "yes")
      val claim = Claim(CachedClaim.key).update(moreAboutTheCare).update(otherEEAStateOrSwitzerland).update(benefits).update(yourCourseDetails)
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecisions")(0) mustEqual AssistedDecision.emptyAssistedDecision
    }
  }
  section("unit")
}
