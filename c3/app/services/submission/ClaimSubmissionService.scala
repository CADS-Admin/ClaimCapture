package services.submission

import play.api.mvc.{AnyContent, Request}
import scala.concurrent.{ExecutionContext, Future}
import play.api.{http, Logger}
import controllers.submission._
import models.view.{CachedChangeOfCircs, CachedClaim}
import play.api.mvc.Results._
import play.api.libs.ws.Response
import models.domain.Claim
import play.api.mvc.SimpleResult
import services.ClaimTransactionComponent
import ExecutionContext.Implicits.global

trait ClaimSubmissionService {

  this : ClaimTransactionComponent with WebServiceClientComponent =>

  def submission(claim: Claim, request: Request[AnyContent]): Future[SimpleResult] = {
    val txnID = claimTransaction.generateId
    Logger.info(s"Retrieved Id : $txnID")

    webServiceClient.submitClaim(claim, txnID).map(
      response => {
        registerId(claim, txnID, SUBMITTED)
        processResponse(claim, txnID, response, request)
      }
    )
  }

  private def processResponse(claim: Claim, txnId: String, response: Response, request: Request[AnyContent]): SimpleResult = {
    response.status match {
      case http.Status.OK =>
        val responseStr = response.body
        Logger.info(s"Received response : ${claim.key} : $responseStr")
        val responseXml = scala.xml.XML.loadString(responseStr)
        val result = (responseXml \\ "result").text
        Logger.info(s"Received result : ${claim.key} : $result")
        result match {
          case "response" =>
            updateStatus(claim, txnId, SUCCESS)
            respondWithSuccess(claim, txnId, request)
          case "acknowledgement" =>
            updateStatus(claim, txnId, ACKNOWLEDGED)
            respondWithSuccess(claim, txnId, request)
          case "error" =>
            val errorCode = (responseXml \\ "errorCode").text
            errorAndCleanup(claim, txnId, errorCode, request)
          case _ =>
            Logger.error(s"Received error : $result, TxnId : $txnId, Headers : ${request.headers}")
            errorAndCleanup(claim, txnId, UNKNOWN_ERROR, request)
        }
      case http.Status.SERVICE_UNAVAILABLE =>
        Logger.error(s"SERVICE_UNAVAILABLE : ${response.status} : ${response.toString}, TxnId : $txnId, Headers : ${request.headers}")
        claim.key match {
          case CachedClaim.key =>
            Redirect(controllers.s11_consent_and_declaration.routes.G6Error.present())
          case CachedChangeOfCircs.key =>
            Redirect(controllers.circs.s3_consent_and_declaration.routes.G3Error.present())
        }
      case http.Status.BAD_REQUEST =>
        Logger.error(s"BAD_REQUEST : ${response.status} : ${response.toString}, TxnId : $txnId, Headers : ${request.headers}")
        errorAndCleanup(claim, txnId, BAD_REQUEST_ERROR, request)
      case http.Status.REQUEST_TIMEOUT =>
        Logger.error(s"REQUEST_TIMEOUT : ${response.status} : ${response.toString}, TxnId : $txnId, Headers : ${request.headers}")
        errorAndCleanup(claim, txnId, REQUEST_TIMEOUT_ERROR, request)
      case http.Status.INTERNAL_SERVER_ERROR =>
        Logger.error(s"INTERNAL_SERVER_ERROR : ${response.status} : ${response.toString}, TxnId : $txnId, Headers : ${request.headers}")
        errorAndCleanup(claim, txnId, SERVER_ERROR, request)
    }
  }

  def respondWithSuccess(claim: Claim, txnId: String, request: Request[AnyContent]): SimpleResult = {
    Logger.info(s"Successful submission : ${claim.key} : $txnId")
    claim.key match {
      case CachedClaim.key =>
        Redirect(controllers.routes.ClaimEnding.thankyou())
      case CachedChangeOfCircs.key =>
        Redirect(controllers.routes.CircsEnding.thankyou())
    }
  }

  private def errorAndCleanup(claim: Claim, txnId: String, code: String, request: Request[AnyContent]): SimpleResult = {
    Logger.error(s"errorAndCleanup : ${claim.key} : $txnId : $code")
    updateStatus(claim, txnId, code)
    claim.key match {
      case CachedClaim.key =>
        Redirect(controllers.routes.ClaimEnding.error())
      case CachedChangeOfCircs.key =>
        Redirect(controllers.routes.CircsEnding.error())
    }
  }

  private[submission] def pollXml(correlationID: String, pollEndpoint: String) = {
    <poll>
      <correlationID>{correlationID}</correlationID>
      <pollEndpoint>{pollEndpoint}</pollEndpoint>
    </poll>
  }

  private def updateStatus(claim: Claim, id: String, statusCode: String) = {
    claimTransaction.updateStatus(id, statusCode, claimType(claim))
  }

  private def registerId(claim: Claim, id: String, statusCode: String) = {
    claimTransaction.registerId(id, statusCode, claimType(claim))
  }

  val SUBMITTED = "0000"
  val ACKNOWLEDGED = "0001"
  val SUCCESS = "0002"
  val UNKNOWN_ERROR = "9001"
  val BAD_REQUEST_ERROR = "9002"
  val REQUEST_TIMEOUT_ERROR = "9003"
  val SERVER_ERROR = "9004"
  val COMMUNICATION_ERROR = "9005"

}
