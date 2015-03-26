package services.mail

import akka.actor._
import org.specs2.mock.Mockito
import org.specs2.mutable.{Specification, Tags}
import play.api.Logger
import play.modules.mailer._
import specs2.akka.AkkaTestkitSpecs2Support

import scala.util.{Failure, Success}


class EmailActorsSpec extends Specification with Tags with Mockito{

  "Email Manager" should {

    "start an email sending actor and receive email message" in new AkkaTestkitSpecs2Support {

      synchronized{SimpleSenderTestable.preStartCalls = 0}
      synchronized{SimpleSenderTestable.sendEmailsReceived = 0}

      val emailManager = system.actorOf(Props(classOf[EmailManagerTestable],Props[SimpleSenderTestable],5,60))

      emailManager ! mock[EmailWrapper]

      Thread.sleep(1000)

      SimpleSenderTestable.preStartCalls mustEqual 1
      SimpleSenderTestable.sendEmailsReceived mustEqual 1
    }

    "start an email sending actor" in new AkkaTestkitSpecs2Support() {

      val mailerMock = mock[Mailer]
      mailerMock.sendEmail(any[Email]) returns Success(Unit)

      val emailManager = system.actorOf(Props(classOf[EmailManagerTestable],Props(classOf[EmailSenderTestable],mailerMock),5,60))

      val b = "Body content"
      val s = "my subject"
      val r = Seq("recipient1","recipient2")
      val email = Email(s,EmailAddress(null,""),"",b,recipients = r.map(r => Recipient(RecipientType.TO,EmailAddress(null,r))),replyTo = None)
      emailManager ! EmailWrapper("",email)

      Thread.sleep(1000)

      there was one(mailerMock).sendEmail(email)

    }

    "restart the email actor because of failures" in new AkkaTestkitSpecs2Support() {
      synchronized{EmailSenderTestable.preStartCalls = 0}
      synchronized{EmailSenderTestable.preRestartCalls = 0}

      val mail = mock[EmailWrapper]
      mail.transactionId returns ""
      mail.email returns mock[Email]
      val mailerMock = mock[Mailer]
      val retries = 5
      val time = 2

      mailerMock.sendEmail(any[Email]).returns(Failure(new Exception("Test exception")))

      val emailManager = system.actorOf(Props(classOf[EmailManagerTestable],Props(classOf[EmailSenderTestable],mailerMock),retries,time))

      emailManager ! mail

      Thread.sleep(4000)

      EmailSenderTestable.preStartCalls must beGreaterThan(retries)
      EmailSenderTestable.preRestartCalls must beGreaterThan(retries-1)

      there was atMost(retries+1)(mailerMock).sendEmail(any[Email])

    }
  } section "unit"

}

class SimpleSenderTestable extends Actor {
  override def receive: Actor.Receive = {
    case email:EmailWrapper => SimpleSenderTestable.sendEmailsReceived += 1
  }


  override def preStart() = {
    synchronized{ SimpleSenderTestable.preStartCalls += 1 }
  }
}

object SimpleSenderTestable {
  var preStartCalls = 0
  var sendEmailsReceived = 0
}

class EmailSenderTestable(mailPlugin:Mailer) extends EmailSenderActor{

  override def mailerPluginApi: Mailer = mailPlugin

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    Logger.info(s"Restarting because of $reason with $message")
    synchronized{ EmailSenderTestable.preRestartCalls += 1 }
  }


  override def preStart() = {
    synchronized{ EmailSenderTestable.preStartCalls += 1 }
  }

  override val claimTransaction = new StubClaimTransaction

}

object EmailSenderTestable{
  var preStartCalls = 0
  var preRestartCalls = 0
}

class EmailManagerTestable(emailSendingCreator:Props,retries:Int = 5, retriesTimeSpan:Int = 60) extends EmailManagerActor(emailSendingCreator,retries,retriesTimeSpan) {


}

object EmailManagerTestable {
  val preStartCalls = 0
  val exceptionRaised = 0

}
