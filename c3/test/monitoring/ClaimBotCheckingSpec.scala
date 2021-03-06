package monitoring

import app.PensionPaymentFrequency
import org.specs2.mutable._
import models.domain._
import org.specs2.mock.Mockito
import models.view.CachedClaim
import models.yesNo.{YesNoWithText, YesNoWithEmployerAndMoney, YesNoWithDate, YesNo}
import models.DayMonthYear
import models.domain.Claim
import models.MultiLineAddress
import utils.WithBrowser

class ClaimBotCheckingSpec extends Specification with Mockito {
  def controller = new ClaimBotChecking {}

  def claim = new CachedClaim(){}.copyInstance(new Claim(CachedClaim.key)
    .update(Benefits(Benefits.pip))
    .update(Eligibility("no","no","no"))
  )

  private def createJob(jobId: String, questionGroup: QuestionGroup with controllers.Iteration.Identifier): Iteration = {
    val jobDetails = JobDetails(jobId)
    val job = Iteration(jobId).update(jobDetails).update(questionGroup)
    job
  }

  section("unit")
  "Claim submission" should {
    "be flagged for completing sections too quickly e.g. a bot" in new WithBrowser {
      controller.checkTimeToCompleteAllSections(claim, currentTime = 0) should beTrue
    }

    "be completed slow enough to be human" in new WithBrowser {
      controller.checkTimeToCompleteAllSections(claim, currentTime = Long.MaxValue) should beFalse
    }

    "returns false given did not answer any honeyPot question groups" in new WithBrowser {
      controller.honeyPot(Claim(CachedClaim.key)) should beFalse
    }

    "returns false given spent35HoursCaringBeforeClaim answered yes and honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key).update(ClaimDate(spent35HoursCaringBeforeClaim = YesNoWithDate(answer = "yes", date = Some(DayMonthYear()))))
      controller.honeyPot(claim) should beFalse
    }

    "returns false given spent35HoursCaringBeforeClaim answered no and honeyPot not filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key).update(ClaimDate(spent35HoursCaringBeforeClaim = YesNoWithDate(answer = "no", date = None)))
      controller.honeyPot(claim) should beFalse
    }

    "returns true given spent35HoursCaringBeforeClaim answered no and honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key).update(ClaimDate(spent35HoursCaringBeforeClaim = YesNoWithDate(answer = "no", date = Some(DayMonthYear()))))
      controller.honeyPot(claim) should beTrue
    }

    /************* start employment pension and job expenses ********************/

    "returns true given PensionAndExpenses pension expenses honeyPot not filled (pension expenses entered)" in new WithBrowser {
      val aboutExpenses = PensionAndExpenses(
        "12345",
        YesNoWithText("no",None),
        YesNoWithText("no",None),
        YesNoWithText("no",Some("Some expenses"))
       )

      val jobs = new Jobs().update(createJob("12345", aboutExpenses))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beTrue
    }

    "return true given PensionAndExpenses pay for things honey pot filled (pay for things entered)" in new WithBrowser {
      val aboutExpenses = PensionAndExpenses(
        "12345",
        YesNoWithText("no",None),
        YesNoWithText("no",Some("Some expenses")),
        YesNoWithText("no",None)
      )

      val jobs = new Jobs().update(createJob("12345", aboutExpenses))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beTrue
    }

    "return false given PensionAndExpenses pay for things honey pot not filled (pay for things not entered)" in new WithBrowser {
      val aboutExpenses = PensionAndExpenses(
        "12345",
        YesNoWithText("no",None),
        YesNoWithText("no",None),
        YesNoWithText("no",None)
      )

      val jobs = new Jobs().update(createJob("12345", aboutExpenses))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beFalse
    }

    "returns false given PensionAndExpenses pension expenses honeyPot filled (pension expenses entered)" in new WithBrowser {
      val aboutExpenses = PensionAndExpenses(
        "12345",
        YesNoWithText("no",None),
        YesNoWithText("no",None),
        YesNoWithText("yes",Some("Some expenses"))
      )

      val jobs = new Jobs().update(createJob("12345", aboutExpenses))
      val claim = Claim(CachedClaim.key).update(jobs)
      controller.honeyPot(claim) should beFalse
    }

    "returns false given PensionAndExpenses pension expenses honeyPot filled (pension expenses entered) for more than one job" in new WithBrowser {
      val aboutExpenses1 = PensionAndExpenses(
        "12345",
        YesNoWithText("no",None),
        YesNoWithText("no",None),
        YesNoWithText("yes",Some("Some expenses"))
      )

      val aboutExpenses2 = PensionAndExpenses(
        "12345",
        YesNoWithText("no",None),
        YesNoWithText("no",None),
        YesNoWithText("yes",Some("Some other expenses"))
      )

      val jobs = new Jobs().update(createJob("12345", aboutExpenses1)).update(createJob("123456", aboutExpenses1)).update(createJob("1234567", aboutExpenses2)).update(createJob("12345678", aboutExpenses1)).update(createJob("123456789", aboutExpenses2))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beFalse
    }

    "returns true given PensionAndExpenses job expenses honeyPot not filled (job expenses entered)" in new WithBrowser {
      val aboutExpenses = PensionAndExpenses(
        "12345",
        YesNoWithText("no",Some("Some expenses")),
        YesNoWithText("no",None),
        YesNoWithText("no")
      )
      val jobs = new Jobs().update(createJob("12345", aboutExpenses))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beTrue
    }

    "returns false given PensionAndExpenses job expenses honeyPot filled (job expenses entered)" in new WithBrowser {
      val aboutExpenses = PensionAndExpenses(
        "12345",
        YesNoWithText("yes",Some("Some expenses")),
        YesNoWithText("no",None),
        YesNoWithText("no")
      )
      val jobs = new Jobs().update(createJob("12345", aboutExpenses))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beFalse
    }

    "returns false given PensionAndExpenses job expenses honeyPot filled (job expenses entered) for more than one job" in new WithBrowser {
      val aboutExpenses1 = PensionAndExpenses(
        "",
        YesNoWithText("yes",Some("Some expenses")),
        YesNoWithText("no",None),
        YesNoWithText("no")
      )

      val aboutExpenses2 = PensionAndExpenses(
        "",
        YesNoWithText("yes",Some("Some other expenses")),
        YesNoWithText("no",None),
        YesNoWithText("no")
      )

      val jobs = new Jobs().update(createJob("12345", aboutExpenses1)).update(createJob("123456", aboutExpenses1)).update(createJob("1234567", aboutExpenses2)).update(createJob("12345678", aboutExpenses2)).update(createJob("123456789", aboutExpenses2))
      val claim = Claim(CachedClaim.key).update(jobs)

      controller.honeyPot(claim) should beFalse
    }

    /************* end employment about expenses related *****************/

    "returns true given SelfEmploymentPensionAndExpenses pension expenses honeyPot not filled (pension expenses entered)" in new WithBrowser {
      val aboutExpenses = SelfEmploymentPensionsAndExpenses(
        YesNoWithText("no",None),
        YesNoWithText("no",Some("Some expenses"))
      )

      val claim = Claim(CachedClaim.key).update(aboutExpenses)

      controller.honeyPot(claim) should beTrue
    }

    "returns false given SelfEmploymentPensionAndExpenses pension expenses honeyPot filled (pension expenses entered)" in new WithBrowser {
      val aboutExpenses = SelfEmploymentPensionsAndExpenses(
        YesNoWithText("no",None),
        YesNoWithText("yes",Some("Some expenses"))
      )

      val claim = Claim(CachedClaim.key).update(aboutExpenses)

      controller.honeyPot(claim) should beFalse
    }

    "returns true given SelfEmploymentPensionsAndExpenses job expenses honeyPot not filled (job expenses entered)" in new WithBrowser {
      val aboutExpenses = SelfEmploymentPensionsAndExpenses(
        YesNoWithText("no",Some("Some expenses")),
        YesNoWithText("no")
      )
      val claim = Claim(CachedClaim.key).update(aboutExpenses)

      controller.honeyPot(claim) should beTrue
    }

    "returns false given SelfEmploymentPensionsAndExpenses job expenses honeyPot filled (job expenses entered)" in new WithBrowser {
      val aboutExpenses = SelfEmploymentPensionsAndExpenses(
        YesNoWithText("yes",Some("Some expenses")),
        YesNoWithText("no")
      )
      val claim = Claim(CachedClaim.key).update(aboutExpenses)

      controller.honeyPot(claim) should beFalse
    }

    "returns true given StatutorySickPay checked, stillBeingPaidThisPay answered yes and whenDidYouLastGetPaid honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_sickpay = Some("true")))
        .update(
          StatutorySickPay(
            stillBeingPaidThisPay = "yes",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "other",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given StatutorySickPay checked and howOftenPaidThisPayOther honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_sickpay = Some("true")))
        .update(
          StatutorySickPay(
            stillBeingPaidThisPay = "no",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "monthly",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns false given StatutorySickPay not checked and not honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
      controller.honeyPot(claim) should beFalse
    }

    "returns false given StatutoryPay not checked not honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
      controller.honeyPot(claim) should beFalse
    }

    "returns false given FosteringAllowance not checked and not honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
      controller.honeyPot(claim) should beFalse
    }

    "returns false given DirectPay not checked and not honeypot filled"in new WithBrowser {
      val claim = Claim(CachedClaim.key)
      controller.honeyPot(claim) should beFalse
    }

    "returns false given OtherPayments not checked and not honeypot filled"in new WithBrowser {
      val claim = Claim(CachedClaim.key)
      controller.honeyPot(claim) should beFalse
    }

    "returns true given StatutoryPay checked, stillBeingPaidThisPay answered yes and whenDidYouLastGetPaid honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_patmatadoppay = Some("true")))
        .update(
          StatutoryMaternityPaternityAdoptionPay(
            paymentTypesForThisPay = "AdoptionPay",
            stillBeingPaidThisPay = "yes",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "other",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given StatutoryPay checked and howOftenPaidThisPayOther honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_patmatadoppay = Some("true")))
        .update(
          StatutoryMaternityPaternityAdoptionPay(
            paymentTypesForThisPay = "AdoptionPay",
            stillBeingPaidThisPay = "no",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "monthly",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given FosteringAllowance checked, stillBeingPaidThisPay answered yes and whenDidYouLastGetPaid honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_fostering = Some("true")))
        .update(
          FosteringAllowance(
            paymentTypesForThisPay = "FosteringAllowance",
            stillBeingPaidThisPay = "yes",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "other",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given FosteringAllowance checked and howOftenPaidThisPayOther honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_fostering = Some("true")))
        .update(
          FosteringAllowance(
            paymentTypesForThisPay = "FosteringAllowance",
            stillBeingPaidThisPay = "no",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "monthly",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given FosteringAllowance checked, paymentTypesForThisPay not other and paymentTypesForThisPayOther honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_fostering = Some("true")))
        .update(
          FosteringAllowance(
            paymentTypesForThisPay = "FosteringAllowance",
            paymentTypesForThisPayOther = Some("Test1234"),
            stillBeingPaidThisPay = "no",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "other",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given DirectPayment checked, stillBeingPaidThisPay answered yes and whenDidYouLastGetPaid honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_directpay = Some("true")))
        .update(
          DirectPayment(
            stillBeingPaidThisPay = "yes",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "other",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given DirectPayment checked and howOftenPaidThisPayOther honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes(yourIncome_directpay = Some("true")))
        .update(
          DirectPayment(
            stillBeingPaidThisPay = "no",
            whenDidYouLastGetPaid = Some(DayMonthYear.today),
            whoPaidYouThisPay = "ASDA",
            amountOfThisPay = "12",
            howOftenPaidThisPay = "monthly",
            howOftenPaidThisPayOther = Some("It varies")
          )
        )

      controller.honeyPot(claim) should beTrue
    }

    "returns true given OtherPayments checked and whenDidYouLastGetPaid honeyPot filled" in new WithBrowser {
      val claim = Claim(CachedClaim.key)
        .update(YourIncomes())
        .update(
          OtherPayments(
            otherPaymentsInfo = "Test1234"
          )
        )

      controller.honeyPot(claim) should beTrue
    }
  }
  section("unit")
}
