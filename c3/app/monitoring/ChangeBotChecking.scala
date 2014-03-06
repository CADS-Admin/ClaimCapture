package monitoring

import app.ConfigProperties._
import models.domain.{CircumstancesDeclaration, Claimable, Claim}
import scala.Some
import play.api.Logger

trait ChangeBotChecking extends BotChecking {

  def checkTimeToCompleteAllSections(circs: Claim with Claimable, currentTime: Long = System.currentTimeMillis()) = {
    val sectionExpectedTimes = Map[String, Long](
      "c1" -> getProperty("speed.c1",5000L),
      "c2" -> getProperty("speed.c2",5000L),
      "c3" -> getProperty("speed.c3",5000L)
    )
    evaluateTimeToCompleteAllSections(circs, currentTime, sectionExpectedTimes)
  }

  def honeyPot(circs: Claim): Boolean = {

    def checkDeclaration: Boolean = {
      circs.questionGroup(CircumstancesDeclaration) match {
        case Some(q) =>
          val h = q.asInstanceOf[CircumstancesDeclaration]
          if (h.obtainInfoAgreement == "yes") {
            h.obtainInfoWhy match {
              case Some(f) => true // Bot given field howOftenPersonal was not visible.
              case _ => false
            }
          }
          else false

        case None =>
          false
      }
    }

    val declaration = checkDeclaration

    if (declaration) Logger.warn("Honeypot triggered coc : declaration")

    declaration
  }

}
