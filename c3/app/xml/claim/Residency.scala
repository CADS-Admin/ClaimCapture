package xml.claim

import models.domain._
import scala.xml.NodeSeq
import xml.XMLHelper._
import xml.XMLComponent
import models.domain.Claim
import controllers.mappings.Mappings
import play.api.i18n.{Lang, MMessages => Messages}

object Residency extends XMLComponent{

  def xml(claim: Claim) = {

    val nationalityAndResidency = claim.questionGroup[NationalityAndResidency].getOrElse(NationalityAndResidency(""))
    val timeOutsideGBLastThreeYears = claim.questionGroup[AbroadForMoreThan52Weeks].getOrElse(AbroadForMoreThan52Weeks())

    val nationality = nationalityAndResidency.nationality match{
      case NationalityAndResidency.anothercountry=> Messages("label.anothercountry")
      case _ => Messages("label.british")
    }

    <Residency>
      {question(<Nationality/>, "nationality", nationality)}

      {nationalityAndResidency.nationality match {
        case NationalityAndResidency.anothercountry => question(<ActualNationality/>, "actualnationality.text.label", nationalityAndResidency.actualnationality)
        case _ => NodeSeq.Empty
      }}

      {question(<NormallyLiveInGB/>, "resideInUK.answer", nationalityAndResidency.resideInUK.answer)}

      {nationalityAndResidency.resideInUK.answer match {
        case Mappings.no => question(<CountryNormallyLive/>, "resideInUK.text.label", nationalityAndResidency.resideInUK.text)
        case _ => NodeSeq.Empty
      }}

      <PeriodAbroad>
        {question(<TimeOutsideGBLast3Years/>, "52Weeks.label", timeOutsideGBLastThreeYears.anyTrips)}
        {question(<TripsDetails/>, "tripDetails", timeOutsideGBLastThreeYears.tripDetails)}
      </PeriodAbroad>

    </Residency>
  }
}