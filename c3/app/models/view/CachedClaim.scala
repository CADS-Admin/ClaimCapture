package models.view

import app.ConfigProperties._
import java.util.UUID._
import scala.language.implicitConversions
import scala.reflect.ClassTag
import play.api.Play.current
import play.api.mvc.{Action, AnyContent, Request, Result}
import play.api.data.Form
import play.api.cache.Cache
import play.api.{Logger, Play}
import play.api.mvc.Results._
import play.api.http.HeaderNames._
import models.domain._
import controllers.routes
import models.domain.Claim
import scala.Some

object CachedClaim {
  val missingRefererConfig = "Referer not set in config"
  val key = "claim"
}

trait CachedClaim {
  type ClaimResult = (Claim, Result)

  type JobID = String

  val cacheKey = CachedClaim.key

  val redirect = getProperty("enforceRedirect", default = false)

  val expectedReferer = getProperty("claim.referer", default = CachedClaim.missingRefererConfig)

  val timeoutPage = routes.Application.timeout()

  val errorPage = routes.Application.error()


  implicit def formFiller[Q <: QuestionGroup](form: Form[Q])(implicit classTag: ClassTag[Q]) = new {
    def fill(qi: QuestionGroup.Identifier)(implicit claim: Claim): Form[Q] = claim.questionGroup(qi) match {
      case Some(q: Q) => form.fill(q)
      case _ => form
    }
  }

  implicit def defaultResultToLeft(result: Result) = Left(result)

  implicit def claimAndResultToRight(claimingResult: ClaimResult) = Right(claimingResult)

  def newInstance: Claim = new Claim(cacheKey) with FullClaim

  def copyInstance(claim: Claim): Claim = new Claim(claim.key, claim.sections, claim.created)(claim.navigation) with FullClaim

  def keyAndExpiration(r: Request[AnyContent]): (String, Int) = {
    r.session.get(cacheKey).getOrElse(randomUUID.toString) ->  getProperty("cache.expiry", 3600)
  }

  def refererAndHost(r: Request[AnyContent]): (String, String) = {
    r.headers.get("Referer").getOrElse("No Referer in header") -> r.headers.get("Host").getOrElse("No Host in header")
  }

  def fromCache(request:Request[AnyContent]): Option[Claim] = {
    val (key, _) = keyAndExpiration(request)

    Cache.getAs[Claim](key)
  }

  def newClaim(f: (Claim) => Request[AnyContent] => Either[Result, ClaimResult]): Action[AnyContent] = Action {
    request => {
      implicit val r = request

      if (request.getQueryString("changing").getOrElse("false") == "false") {
        Logger.info(s"Starting new $cacheKey")
        originCheck(refererCheck, action(newInstance, request)(f))
      }
      else {
        Logger.info(s"Changing $cacheKey")
        val key = request.session.get(cacheKey).getOrElse(throw new RuntimeException("I expected a key in the session!"))
        val claim = Cache.getAs[Claim](key).getOrElse(throw new RuntimeException("I expected a claim in the cache!"))
        originCheck(sameHostCheck, action(claim, request)(f))
      }
    }
  }

  def claiming(f: (Claim) => Request[AnyContent] => Either[Result, ClaimResult]): Action[AnyContent] = Action {
    request => {
      implicit val r = request
      originCheck(sameHostCheck,
        fromCache(request) match {
          case Some(claim) => action(copyInstance(claim), request)(f)

          case None =>
            if (Play.isTest) {
              val (key, expiration) = keyAndExpiration(request)
              val claim = newInstance
              Cache.set(key, claim, expiration) // place an empty claim in the cache to satisfy tests
              action(claim, request)(f)
            } else {
              Logger.info(s"$cacheKey timeout")
              Redirect(timeoutPage)
                .withHeaders("X-Frame-Options" -> "SAMEORIGIN") // stop click jacking
            }
        })
    }
  }

  def ending(f: => Result): Action[AnyContent] = Action {
    request => {
      implicit val r = request
      val (key, _) = keyAndExpiration(request)
      Cache.set(key, None)
      originCheck(sameHostCheck, f).withNewSession
    }
  }

  def claimingInJob(f: (JobID) => Claim => Request[AnyContent] => Either[Result, ClaimResult]) = Action { request =>
    claiming(f(request.body.asFormUrlEncoded.getOrElse(Map("" -> Seq(""))).get("jobID").getOrElse(Seq("Missing JobID at request"))(0)))(request)
  }

  private def action(claim: Claim, request: Request[AnyContent])(f: (Claim) => Request[AnyContent] => Either[Result, ClaimResult]): Result = {

    val (key, expiration) = keyAndExpiration(request)

    f(claim)(request) match {
      case Left(r: Result) =>
        r.withSession(claim.key -> key)
          .withHeaders(CACHE_CONTROL -> "no-cache, no-store")
          .withHeaders("X-Frame-Options" -> "SAMEORIGIN") // stop click jacking

      case Right((c: Claim, r: Result)) => {
        Cache.set(key, c, expiration)

        r.withSession(claim.key -> key)
          .withHeaders(CACHE_CONTROL -> "no-cache, no-store")
          .withHeaders("X-Frame-Options" -> "SAMEORIGIN") // stop click jacking
      }
    }
  }

  private def sameHostCheck()(implicit request: Request[AnyContent]) = {
    val (referer, host) = refererAndHost(request)
    referer.contains(host)
  }

  private def refererCheck()(implicit request: Request[AnyContent]) = {
    val (referer, _) = refererAndHost(request)
    referer.startsWith(expectedReferer)
  }

  private def originCheck(doCheck: => Boolean, action: => Result)(implicit request: Request[AnyContent])  = {
    val (referer, host) = refererAndHost(request)
    if (doCheck) {
      action
    } else {
      if (redirect) {
        Logger.warn(s"HTTP Referer : $referer")
        Logger.warn(s"Conf Referer : $expectedReferer")
        Logger.warn(s"HTTP Host : $host")
        Redirect(expectedReferer)
      } else {
        action
      }
    }
  }


}