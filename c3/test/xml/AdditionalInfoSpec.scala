package xml

import org.specs2.mutable.{Tags, Specification}
import models.domain._
import xml.circumstances.AdditionalInfo

class AdditionalInfoSpec extends Specification with Tags {
  val otherInfo = "Some other info"

  "Additional Info" should {
    "generate xml" in {
      val claim = Claim().update(CircumstancesOtherInfo(otherInfo))
      val xml = AdditionalInfo.xml(claim)

      (xml \\ "OtherChanges").text shouldEqual otherInfo
    }

  } section "unit"
}