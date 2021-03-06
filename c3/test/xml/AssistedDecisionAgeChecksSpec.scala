package xml

import controllers.mappings.Mappings
import models.{SortCode, DayMonthYear}
import models.domain._
import models.view.CachedClaim
import models.yesNo._
import org.joda.time.DateTime
import org.specs2.mutable._
import utils.WithApplication
import xml.claim.AssistedDecision
import org.specs2.mock.Mockito

class AssistedDecisionAgeChecksSpec extends Specification with Mockito {
  lazy val moreAboutTheCare = MoreAboutTheCare(Some(Mappings.yes))
  lazy val paymentsFromAbroad = PaymentsFromAbroad(guardQuestion = YesNoWith2MandatoryFieldsOnYes(answer = Mappings.no, field1 = Some(YesNoWith1MandatoryFieldOnYes(answer = Mappings.no)), field2 = Some(YesNoWith1MandatoryFieldOnYes(answer = Mappings.no))))
  lazy val benefits = Benefits(benefitsAnswer = Benefits.pip)
  lazy val yourCourseDetails = YourCourseDetails(beenInEducationSinceClaimDate = Mappings.no)
  lazy val someBankDetails=HowWePayYou(likeToBePaid = Mappings.yes,
    bankDetails=Some(BankBuildingSocietyDetails(accountHolderName="Smith", bankFullName="HSBC", sortCode=SortCode("20","56","57"), accountNumber="12341234")),
    paymentFrequency="Weekly")
  lazy val noBankDetails=HowWePayYou(likeToBePaid = Mappings.no)
  lazy val happyClaim = Claim(CachedClaim.key).update(moreAboutTheCare).update(paymentsFromAbroad).update(benefits).update(yourCourseDetails).update(someBankDetails)

  lazy val submitDate = DateTime.now()
  lazy val fifteenAndNineInMonths = 15 * 12 + 9
  lazy val claimDateDetails = ClaimDate(DateTime.now)

  // Do not reformat this xml it breaks the tests ... !!!
  lazy val showTableDecisionNode = <AssistedDecision><Reason>None</Reason><RecommendedDecision>None,show table</RecommendedDecision></AssistedDecision>
  lazy val defaultCheckCISDecisionNode = <AssistedDecision><Reason>Check CIS for benefits. Send Pro517 if relevant.</Reason><RecommendedDecision>Potential award,show table</RecommendedDecision></AssistedDecision>
  lazy val pensionerCheckCISDecisionNode = <AssistedDecision><Reason>Check CIS for benefits. Send Pro517 if relevant.</Reason><RecommendedDecision>Potential underlying entitlement,show table</RecommendedDecision></AssistedDecision>

  section("unit")
  "Assisted section" should {
    "Default AD if customer EQUALS 15 and 9 year old today" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusMonths(fifteenAndNineInMonths))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(defaultCheckCISDecisionNode)
    }

    "Create 'disallowance' AD if customer LESS THAN 15 and 9 year old today i.e. born 1 day later" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusMonths(fifteenAndNineInMonths).plusDays(1))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text mustEqual("Customer does not turn 16 in next 3 months. Send Proforma 491 to customer.")
      (xml \\ "RecommendedDecision").text mustEqual("Potential disallowance decision,no table")
    }

    "Default AD if customer OLDER THAN 15 and 9 year old today i.e. i.e. born 1 day earlier" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusMonths(fifteenAndNineInMonths).minusDays(1))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(defaultCheckCISDecisionNode)
    }

    "Create 'show table' AD if no bank details provided" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(30))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails).update(noBankDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "Reason").text mustEqual("None")
      (xml \\ "RecommendedDecision").text mustEqual("None,show table")
    }

    "Create 'underlying entitlement' AD if customer EQUALS 65 claim date=today" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(65))
      val howWePayYou=HowWePayYou(likeToBePaid = Mappings.no)
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails).update(noBankDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(pensionerCheckCISDecisionNode)
    }

    "Default AD if customer 65 tomorrow and claim date=today with bank details" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(65).plusDays(1))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails).update(someBankDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(defaultCheckCISDecisionNode)
    }

    "Create 'show table' AD if customer 65 tomorrow and claim date=today without bank details" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(65).plusDays(1))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails).update(noBankDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(showTableDecisionNode)
    }

    "Create 'underlying entitlement' AD if customer 65 yesterday claim date=today" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(65).minusDays(1))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails).update(noBankDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(pensionerCheckCISDecisionNode)
    }

    "Create 'underlying entitlement' AD if customer is 65 tomorrow but OLDER when claim date is next week" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(65).plusDays(1))
      val claimDateDetails = ClaimDate(DateTime.now.plusDays(7))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(pensionerCheckCISDecisionNode)
    }

    "Happy path Default AD if customer is in the middle say 30 years old" in new WithApplication {
      val yourDetails = YourDetails(dateOfBirth = submitDate.minusYears(30))
      val claim = AssistedDecision.createAssistedDecisionDetails(happyClaim.update(claimDateDetails).update(yourDetails));
      val xml = AssistedDecision.xml(claim)
      (xml \\ "AssistedDecision") (0) mustEqual(defaultCheckCISDecisionNode)
    }
  }
  section ("unit")
}
