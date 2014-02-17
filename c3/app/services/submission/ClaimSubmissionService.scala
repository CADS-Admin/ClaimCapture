package services.submission

import scala.xml.Elem
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws
import play.Configuration
import play.api.{http, Logger}
import play.api.libs.ws.{Response, WS}
import services.util.CharacterStripper
import java.net.ConnectException
import ExecutionContext.Implicits.global

trait ClaimSubmissionService {

  val claimSubmission: ClaimSubmission

  class ClaimSubmission {
    def submitClaim(claimSubmission: Elem): Future[ws.Response] = {
      val submissionServerEndpoint: String =
        Configuration.root().getString("submissionServerUrl", "SubmissionServerEndpointNotSet") + "submit/claim"
      Logger.debug(s"Submission Server : $submissionServerEndpoint")
      val result = WS.url(submissionServerEndpoint)
        .withHeaders(("Content-Type", "text/xml"))
        .post(CharacterStripper.stripNonPdf(claimSubmission.buildString(stripComments = true))) recover {

        case e: ConnectException =>
          Logger.error(s"ConnectException ! ${e.getMessage}")
          // Spoof service unavailable
          new Response(null) {
            override def status: Int = http.Status.SERVICE_UNAVAILABLE
          }
      }
      result
    }
  }

}
