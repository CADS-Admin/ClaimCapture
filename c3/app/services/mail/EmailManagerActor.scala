package services.mail

import akka.actor.SupervisorStrategy._
import akka.actor.{OneForOneStrategy, _}
import org.postgresql.util.PSQLException
import play.api.Logger
import play.modules.mailer._
import services.ClaimTransactionComponent

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Try, Failure, Success}
import app.ConfigProperties._
case class EmailWrapper(transactionId:String,email:Email)
object EmailActorsCreators {

  val emailSenderProps = Props(classOf[EmailSenderActor],getProperty("mail.rescheduleTime",3600))

  lazy val emailManagerProps = Props(classOf[EmailManagerActor],emailSenderProps)

  val SUCCESS = 0
  val ERROR = 1
}

class EmailManagerActor(emailSendingCreator:Props) extends Actor {

  override def supervisorStrategy = OneForOneStrategy(){
    case e:PSQLException =>
      Logger.error("DB Error while updating email status.",e)
      Stop
    case e:Exception =>
      Logger.error("Could not send email.", e)
      Stop
  }

  override def receive: Actor.Receive = {
    case email:EmailWrapper =>
      this.context.actorOf(emailSendingCreator,"email-sender-actor") ! email
  }
}

class EmailSenderActor(rescheduleTime:Int) extends Actor with ClaimTransactionComponent{


  override def receive: Actor.Receive = {
    case e:EmailWrapper =>
      sendEmail(e)
      self ! PoisonPill
  }

  private def rescheduleMail(mail:EmailWrapper) = {
    Logger.info(s"Rescheduling mail for transactionId [${mail.transactionId}] actorId [${context.self.path}]")
    context.system.scheduler.scheduleOnce(rescheduleTime seconds,context.parent,mail)(context.system.dispatcher,context.parent)

  }
  protected def sendEmail(mail:EmailWrapper) = {

    Try(processResponse(mail,mailerPluginApi.sendEmail(mail.email)))match {
      case Failure(e) =>
        Logger.error(s"Unexpected error for transactionId [${mail.transactionId}]. ${e.getMessage}",e)
        rescheduleMail(mail)
        throw e
      case _ =>
    }
  }

  private def processResponse(mail:EmailWrapper, response:Try[_]) ={
    response match {
      case Success(s) =>
        claimTransaction.updateEmailStatus(mail.transactionId,EmailActorsCreators.SUCCESS)
        Logger.info(s"Email sent successfully for transactionId [${mail.transactionId}]")
      case Failure(SendEmailTransportCloseException(Some(Success(_)),e)) =>
        claimTransaction.updateEmailStatus(mail.transactionId,EmailActorsCreators.SUCCESS)
        Logger.warn(s"Email sent successfully for transactionId [${mail.transactionId}] but couldn't close connection",e)
      case Failure(e) =>
        claimTransaction.updateEmailStatus(mail.transactionId,EmailActorsCreators.ERROR)
        Logger.error(s"Could not send email for transactionId [${mail.transactionId}]. ${e.getMessage}",e)
        rescheduleMail(mail)
        throw e
    }
  }

  protected def mailerPluginApi = {
    Mailer
  }

  val claimTransaction = new ClaimTransaction
}
