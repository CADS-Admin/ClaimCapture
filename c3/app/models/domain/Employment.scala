package models.domain

import models._
import play.api.i18n.{MMessages => Messages}
import scala.reflect.ClassTag
import controllers.mappings.Mappings._
import models.PaymentFrequency
import models.MultiLineAddress
import models.yesNo.YesNoWithText

object Employed extends Section.Identifier {
  val id = "s7"
}

case class Employment(beenSelfEmployedSince1WeekBeforeClaim: String = "", beenEmployedSince6MonthsBeforeClaim: String = "") extends QuestionGroup(Employment)

object Employment extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g0"
}

case class BeenEmployed(beenEmployed: String) extends QuestionGroup(BeenEmployed)

object BeenEmployed extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g1"
}

case class Jobs(jobs: List[Iteration] = Nil) extends QuestionGroup(Jobs) with Iterable[Iteration] {
  def update(job: Iteration): Jobs = {
    val updated = jobs map { j => if (j.iterationID == job.iterationID) job else j }

    if (updated.contains(job)) Jobs(updated) else Jobs(jobs :+ job)
  }

  def update(questionGroup: QuestionGroup with Iteration.Identifier): Jobs = jobs.find(_.iterationID == questionGroup.iterationID) match {
    case Some(job: Iteration) => update(job.update(questionGroup))
    case _ => update(Iteration(questionGroup.iterationID, questionGroup :: Nil))
  }

  def completeJob(iterationID: String): Jobs = {
    job(iterationID) match {
        case Some(j:Iteration) => update(j.copy( completed = j.questionGroups.size > 2 ))
        case _ => copy()
      }
  }

  def delete(iterationID: String): Jobs = Jobs(jobs.filterNot(_.iterationID == iterationID))

  def delete(iterationID: String, questionGroup: QuestionGroup.Identifier): Jobs = jobs.find(_.iterationID == iterationID) match {
    case Some(job: Iteration) => update(Iteration(iterationID,job.questionGroups.filterNot(_.identifier.id == questionGroup.id)))
    case _ => this
  }

  def job(iterationID: String): Option[Iteration] = jobs.find(_.iterationID == iterationID)

  def questionGroup(questionGroup: QuestionGroup with Iteration.Identifier): Option[QuestionGroup] = job(questionGroup.iterationID) match {
    case Some(j: Iteration) => j(questionGroup)
    case _ => None
  }

  def questionGroup(iterationID: String, questionGroup: QuestionGroup.Identifier): Option[QuestionGroup] = job(iterationID) match {
    case Some(j: Iteration) => j(questionGroup)
    case _ => None
  }

  override def iterator: Iterator[Iteration] = jobs.iterator
}

object Jobs extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g99"
}

case class Iteration(iterationID: String="", questionGroups: List[QuestionGroup with Iteration.Identifier] = Nil, completed:Boolean=false) extends Iteration.Identifier with Iterable[QuestionGroup with Iteration.Identifier] {
  def employerName = jobDetails(_.employerName)

  def jobStartDate = jobDetailsDate(_.jobStartDate.getOrElse(DayMonthYear(None,None,None)))

  def finishedThisJob = jobDetails(_.finishedThisJob)

  def update(questionGroup: QuestionGroup with Iteration.Identifier): Iteration = {
    val updated = questionGroups map { qg => if (qg.identifier == questionGroup.identifier) questionGroup else qg }
    if (updated.contains(questionGroup)) copy(questionGroups = updated) else copy(questionGroups = questionGroups :+ questionGroup)
  }

  private def jobDetails(f: JobDetails => String) = questionGroups.find(_.isInstanceOf[JobDetails]) match {
    case Some(j: JobDetails) => f(j)
    case _ => ""
  }

  private def jobDetailsDate(f:JobDetails => DayMonthYear) = questionGroups.find(_.isInstanceOf[JobDetails]) match {
    case Some(j: JobDetails) => f(j)
    case _ => DayMonthYear(None,None,None)
  }

  def questionGroup[Q <: QuestionGroup](implicit classTag: ClassTag[Q]): Option[Q] = {
    def needQ(qg: QuestionGroup): Boolean = {
      qg.getClass == classTag.runtimeClass
    }

    questionGroups.find(needQ) match {
      case Some(q: Q) => Some(q)
      case _ => None
    }
  }

  def apply(questionGroup: QuestionGroup): Option[QuestionGroup] = questionGroups.find(_.identifier == questionGroup.identifier)

  def apply(questionGroup: QuestionGroup.Identifier): Option[QuestionGroup] = questionGroups.find(_.identifier.id == questionGroup.id)

  override def iterator: Iterator[QuestionGroup with Iteration.Identifier] = questionGroups.iterator
}

object Iteration {
  trait Identifier {
    val iterationID: String
  }
}

case class JobDetails(iterationID: String = "",
                      employerName: String = "",
                      phoneNumber: String = "",
                      address: MultiLineAddress = MultiLineAddress(),
                      postcode: Option[String] = None,
                      startJobBeforeClaimDate:String = "",
                      jobStartDate: Option[DayMonthYear] = None,
                      finishedThisJob: String = "",
                      lastWorkDate:Option[DayMonthYear] = None,
                      p45LeavingDate:Option[DayMonthYear] = None,
                      hoursPerWeek: Option[String] = None) extends QuestionGroup(JobDetails) with Iteration.Identifier {
  override val definition = Messages(identifier.id, employerName)
}

object JobDetails extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g2"

  def validateLastWorkDate(input: JobDetails):Boolean = input.finishedThisJob match {
    case `yes` => input.lastWorkDate.isDefined
    case `no` => true
  }

  def validateHoursPerWeek(input: JobDetails):Boolean = input.finishedThisJob match {
    case `yes` => input.hoursPerWeek.isDefined
    case `no` => true
  }

  def validateJobStartDate(input: JobDetails):Boolean = input.startJobBeforeClaimDate match {
    case `yes` => true
    case `no` => input.jobStartDate.isDefined
  }


}

case class LastWage(iterationID: String = "",
                    oftenGetPaid: PaymentFrequency = PaymentFrequency(),
                    whenGetPaid: String = "",
                    lastPaidDate: DayMonthYear,
                    grossPay: String = "",
                    payInclusions: Option[String] = None,
                    sameAmountEachTime: String = "",
                    employerOwesYouMoney: Option[String] = None) extends QuestionGroup(LastWage) with Iteration.Identifier


object LastWage extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g3"

  def validateOftenGetPaid(input: LastWage): Boolean = input.oftenGetPaid match {
    case payment if payment.frequency == "other" => payment.other.isDefined
    case _ => true
  }
}

case class PensionAndExpenses(iterationID: String = "",
                         payPensionScheme: YesNoWithText = YesNoWithText("", None),
                         haveExpensesForJob: YesNoWithText = YesNoWithText("", None)
                        ) extends QuestionGroup(PensionAndExpenses) with Iteration.Identifier

object PensionAndExpenses extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g4"
}

object JobCompletion extends QuestionGroup.Identifier {
  val id = s"${Employed.id}.g14"
}