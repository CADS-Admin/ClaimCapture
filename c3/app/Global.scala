import app.ConfigProperties._
import java.net.InetAddress
import monitoring.ApplicationMonitor
import org.slf4j.MDC
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import jmx.JMXActors
import play.api.mvc.SimpleResult
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import services.mail.EmailActors
import utils.helpers.CarersLanguageHelper

object Global extends GlobalSettings with Injector with CarersLanguageHelper {

  override def onStart(app: Application) {
    MDC.put("httpPort", getProperty("http.port", "Value not set"))
    MDC.put("hostName", Option(InetAddress.getLocalHost.getHostName).getOrElse("Value not set"))
    MDC.put("envName", getProperty("env.name", "Value not set"))
    MDC.put("appName", getProperty("app.name", "Value not set"))
    super.onStart(app)

    val secret: String = getProperty("application.secret", "secret")
    val secretDefault: String = getProperty("secret.default", "don't Match")

    if (secret.equals(secretDefault)) {
      Logger.warn("application.secret is using default value")
    }

    actorSystems

    Logger.info("c3 Started") // used for operations, do not remove
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("c3 Stopped") // used for operations, do not remove
  }

  // 404 - page not found error http://alvinalexander.com/scala/handling-scala-play-framework-2-404-500-errors
  override def onHandlerNotFound(requestHeader: RequestHeader): Future[SimpleResult] = {
    implicit val request = Request(requestHeader,AnyContentAsEmpty)
    Future(NotFound(views.html.common.onHandlerNotFound()))
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = resolve(controllerClass)

  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger.error(ex.getMessage)
    val startUrl: String = getProperty("claim.start.page", "/allowance/benefits")
    Future(Ok(views.html.common.error(startUrl)(lang(request), Request(request, AnyContentAsEmpty))))
  }

  def actorSystems = {
    JMXActors
    EmailActors
    ApplicationMonitor.begin

  }

}

// Add WithFilters(LoggingFilter) to enable good debug
object LoggingFilter extends Filter {
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] = {
    val startTime = System.currentTimeMillis
    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      Logger.info(s"${requestHeader.method} ${requestHeader.uri} " +
        s"took ${requestTime}ms and returned ${result.header.status}")
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

class JMXFilter extends Filter {
  def apply(f: (RequestHeader) => Future[SimpleResult])(rh: RequestHeader): Future[SimpleResult] = f(rh)

//  def apply(f: (RequestHeader) => Result)(rh: RequestHeader): Result = f(rh)

  override def apply(f: EssentialAction): EssentialAction = {
    if (play.Configuration.root().getBoolean("jmxEnabled", false)) super.apply(f)
    else f
  }
}