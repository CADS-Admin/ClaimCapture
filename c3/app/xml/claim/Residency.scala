package xml.claim

import models.domain._
import scala.xml.NodeSeq
import xml.XMLHelper._
import xml.XMLComponent
import models.domain.Claim
import controllers.Mappings

object Residency extends XMLComponent{

  def xml(claim: Claim) = {

    val tripsOption = claim.questionGroup[Trips]
    val nationalityAndResidency = claim.questionGroup[NationalityAndResidency].getOrElse(NationalityAndResidency())

    <Residency>
      {question(<NormallyLiveInGB/>, "resideInUK.answer", nationalityAndResidency.resideInUK.answer)}

      {nationalityAndResidency.resideInUK.answer match {
        case Mappings.no => question(<CountryNormallyLive/>, "resideInUK.text.label", nationalityAndResidency.resideInUK.text)
        case _ => NodeSeq.Empty
      }}

      <Nationality>{nationalityAndResidency.nationality}</Nationality>

      {periodAbroadLastYear(tripsOption, claim)}

    </Residency>
  }

  def periodAbroadLastYear(tripsOption: Option[Trips], claim: Claim) = {
    val trips = tripsOption.getOrElse(Trips())

    def fiftyWeeksLabel (label:String) = {
      question(<TimeOutsideGBLast3Years/>, label, trips.fiftyTwoWeeksTrips.size > 0, claim.dateOfClaim.get.`dd/MM/yyyy`)
    }

    val xmlNoTrip = {
      <PeriodAbroad>
        {fiftyWeeksLabel("52Weeks.label")}
      </PeriodAbroad>
    }

    def xml(trip: TripPeriod, index:Int) = {
      <PeriodAbroad>
        {index > 0 match {
          case true =>  fiftyWeeksLabel("52Weeks.more.label")
          case false => fiftyWeeksLabel("52Weeks.label")
        }}
        <Period>
          {question(<DateFrom/>, "start.trip", trip.start)}
          {question(<DateTo/>, "end.trip", trip.end)}
         </Period>
        {questionOther(<Reason/>, "why", trip.why.get.reason, trip.why.get.other)}
        {question(<Country/>, "where", trip.where)}
        {question(<CareePresent/>, "personWithYou", trip.personWithYou)}
      </PeriodAbroad>
    }

    trips.fiftyTwoWeeksTrips.size == 0 match {
      case true => xmlNoTrip
      case false => {for ((fiftyTwoWeeksTrip, index) <- trips.fiftyTwoWeeksTrips.zipWithIndex) yield xml(fiftyTwoWeeksTrip, index)}
    }
  }
}