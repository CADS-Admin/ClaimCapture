import java.net.InetAddress

import app.ConfigProperties._
import monitor.MonitorFilter
import monitoring._
import org.slf4j.MDC
import play.api._
import play.api.mvc.Results._
import play.api.mvc._
import services.async.AsyncActors
import services.mail.EmailActors
import utils.Injector
import utils.csrf.DwpCSRFFilter
import utils.helpers.CarersLanguageHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Global extends WithFilters(MonitorFilter, DwpCSRFFilter(createIfFound = CSRFCreationFilter.createIfFound, createIfNotFound = CSRFCreationFilter.createIfNotFound)) with Injector with CarersLanguageHelper with C3MonitorRegistration {

  override def onStart(app: Application) {
    MDC.put("httpPort", getProperty("http.port", "Value not set"))
    MDC.put("hostName", Option(InetAddress.getLocalHost.getHostName).getOrElse("Value not set"))
    MDC.put("envName", getProperty("env.name", "Value not set"))
    MDC.put("appName", getProperty("app.name", "Value not set"))
    super.onStart(app)

    val secret: String = getProperty("application.secret", "secret")
    val secretDefault: String = getProperty("secret.default", "don't Match")

    duplicateClaimCheckEnabled()

    if (secret.equals(secretDefault)) {
      Logger.warn("application.secret is using default value")
    }

    actorSystems()

    registerReporters()
    registerHealthChecks()

    Logger.info(s"c3 Started : memcachedplugin is ${getProperty("memcachedplugin", "Not defined")}") // used for operations, do not remove
    Logger.info(s"c3 property include.analytics is ${getProperty("include.analytics", "Not defined")}") // used for operations, do not remove
  }

  def actorSystems() {
    EmailActors
    AsyncActors
  }

  def duplicateClaimCheckEnabled() = {
    val checkLabel: String = "duplicate.submission.check"
    val check = getProperty(checkLabel, default = true)
    Logger.info(s"$checkLabel = $check")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("c3 Stopped") // used for operations, do not remove
  }

  // 404 - page not found error http://alvinalexander.com/scala/handling-scala-play-framework-2-404-500-errors
  override def onHandlerNotFound(requestHeader: RequestHeader): Future[Result] = {
    implicit val request = Request(requestHeader, AnyContentAsEmpty)
    Future(NotFound(views.html.common.onHandlerNotFound()))
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = resolve(controllerClass)

  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger.error(ex.getMessage)
    val csrfCookieName = getProperty("csrf.cookie.name","csrf")
    val csrfSecure = getProperty("csrf.cookie.secure",getProperty("session.secure",default=false))
    val theDomain = Play.current.configuration.getString("session.domain")
    val C3VERSION = "C3Version"
    val pattern = """.*circumstances.*""".r
    // We redirect and do not stay in same URL to update Google Analytics
    // We delete our cookies to ensure we restart anew
    request.headers.get("Referer").getOrElse("Unknown") match {
      case pattern(_*) => Future(Redirect(controllers.routes.CircsEnding.error()).discardingCookies(DiscardingCookie(csrfCookieName,secure=csrfSecure, domain=theDomain),DiscardingCookie(C3VERSION)).withNewSession)
      case _ => Future(Redirect(controllers.routes.ClaimEnding.error()).discardingCookies(DiscardingCookie(csrfCookieName,secure=csrfSecure, domain=theDomain),DiscardingCookie(C3VERSION)).withNewSession)
    }
  }
}


object CSRFCreationFilter {

  /**
   * We do not want to generate CSRF on error page and thank you, where we want to clean cookies.
   */
  def createIfNotFound(request:RequestHeader): Boolean = {
    request.method == "GET" && (request.accepts("text/html") || request.accepts("application/xml+xhtml")) &&
      (!request.toString().matches(".*assets.*") && !request.toString().matches(".*error.*") && !request.toString().matches(".*thankyou.*") && !request.toString().matches(".*timeout.*"))
  }

  /**
   * We do not want to generate CSRF on error page and thank you, where we want to clean cookies.
   */
  def createIfFound(request:RequestHeader): Boolean = {
    request.toString().matches(".*circumstances.identification.*") || request.toString().matches(".*allowance.benefits.*")
  }
}
