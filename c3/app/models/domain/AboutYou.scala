package models.domain

import models.{ReasonForBeingThere, NationalInsuranceNumber, MultiLineAddress, DayMonthYear}
import models.yesNo.YesNoWithText

object AboutYou extends Section.Identifier {
  val id = "s2"
}

case class YourDetails(title: String = "",
                       firstName: String = "",
                       middleName: Option[String] = None,
                       surname: String = "",
                       otherSurnames: Option[String] = None,
                       nationalInsuranceNumber: NationalInsuranceNumber = NationalInsuranceNumber(None,None,None,None,None),
                       dateOfBirth: DayMonthYear = DayMonthYear(None, None, None)) extends QuestionGroup(YourDetails) {

  def otherNames = firstName + middleName.map(" " + _).getOrElse("")
}

object YourDetails extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g1"
}

case class ContactDetails(address: MultiLineAddress = new MultiLineAddress(),
                          postcode: Option[String] = None,
                          phoneNumber: Option[String] = None,
                          contactYouByTextphone: Option[String] = None,
                          mobileNumber: Option[String] = None) extends QuestionGroup(ContactDetails)

object ContactDetails extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g2"
}

case class ClaimDate(dateOfClaim: DayMonthYear = DayMonthYear()) extends QuestionGroup(ClaimDate)

object ClaimDate extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g3"
}

case class NationalityAndResidency(nationality: String = "",
                                   resideInUK: YesNoWithText = YesNoWithText("", None)) extends QuestionGroup(NationalityAndResidency)

object NationalityAndResidency extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g4"
}

case class AbroadForMoreThan52Weeks(anyTrips: String = "") extends QuestionGroup(AbroadForMoreThan52Weeks)

object AbroadForMoreThan52Weeks extends QuestionGroup.Identifier  {
  val id = s"${AboutYou.id}.g5"
}

case class Trips(fiftyTwoWeeksTrips: List[FiftyTwoWeeksTrip] = Nil) extends QuestionGroup(Trips) {
  def update(trip: FiftyTwoWeeksTrip): Trips = {
    val updated = fiftyTwoWeeksTrips map { t => if (t.id == trip.id) trip else t }
    if (updated.contains(trip)) Trips(updated) else Trips(fiftyTwoWeeksTrips :+ trip)
  }

  def +(trip: FiftyTwoWeeksTrip): Trips = update(trip)

  def delete(tripID: String): Trips = Trips(fiftyTwoWeeksTrips.filterNot(_.id == tripID))

  def -(tripID: String): Trips = delete(tripID)
}

object Trips extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g6"
}

case class Trip(id: String, where: String, start: Option[DayMonthYear] = None, end: Option[DayMonthYear] = None, why: Option[ReasonForBeingThere], personWithYou: String) extends FiftyTwoWeeksTrip {
  def as[T >: Trip]: T = asInstanceOf[T]
}

sealed trait TripPeriod {
  val id: String

  val where: String

  val start: Option[DayMonthYear]

  val end: Option[DayMonthYear]

  val why: Option[ReasonForBeingThere]

  val personWithYou: String
}

trait FiftyTwoWeeksTrip extends TripPeriod {
  this: Trip =>
}

case class OtherEEAStateOrSwitzerland(benefitsFromEEA: String = "", claimedForBenefitsFromEEA: String = "", workingForEEA: String = "") extends QuestionGroup(OtherEEAStateOrSwitzerland)

object OtherEEAStateOrSwitzerland extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g7"
}

case class MoreAboutYou(maritalStatus: String = "",
                        hadPartnerSinceClaimDate: Option[String] = None,
                        beenInEducationSinceClaimDate: String = "",
                        receiveStatePension: String = "") extends QuestionGroup(MoreAboutYou)

object MoreAboutYou extends QuestionGroup.Identifier {
  val id = s"${AboutYou.id}.g8"

  def validateHadPartner(moreAboutYou: MoreAboutYou) = (moreAboutYou.maritalStatus == "p" || moreAboutYou.maritalStatus == "s") || moreAboutYou.hadPartnerSinceClaimDate.isDefined

}
