package controllers.circs.consent_and_declaration

import controllers.mappings.Mappings._
import play.api.Play._
import play.api.mvc._
import models.view.{Navigable, CachedChangeOfCircs}
import play.api.data.Form
import play.api.data.Forms._
import utils.helpers.CarersForm._
import models.domain.CircumstancesDeclaration
import controllers.CarersForms._
import controllers.submission.AsyncSubmissionController
import monitoring.ChangeBotChecking
import play.api.data.FormError
import services.submission.ClaimSubmissionService
import services.ClaimTransactionComponent
import play.api.i18n._


class GCircsDeclaration extends Controller with CachedChangeOfCircs with Navigable with I18nSupport
      with AsyncSubmissionController
      with ChangeBotChecking
      with ClaimSubmissionService
      with ClaimTransactionComponent {
  override val messagesApi: MessagesApi = current.injector.instanceOf[MMessages]
  val claimTransaction = new ClaimTransaction

  val form = Form(mapping(
    "jsEnabled" -> boolean,
    "obtainInfoAgreement" -> carersNonEmptyText,
    "obtainInfoWhy" -> optional(carersNonEmptyText(maxLength = 3000)),
    "circsSomeOneElse" -> optional(carersText),
    "nameOrOrganisation" -> optional(carersNonEmptyText(maxLength = 60))
  )(CircumstancesDeclaration.apply)(CircumstancesDeclaration.unapply)
    .verifying("obtainInfoWhy", CircumstancesDeclaration.validateWhy _)
    .verifying("nameOrOrganisation", CircumstancesDeclaration.validateNameOrOrganisation _)

  )

  def present = claimingWithCheck { implicit circs => implicit request => implicit request2lang =>
      track(CircumstancesDeclaration) {
        implicit circs => Ok(views.html.circs.consent_and_declaration.declaration(form.fill(CircumstancesDeclaration)))
      }
  }

  def submit: Action[AnyContent] = claiming { implicit circs => implicit request => implicit request2lang =>
      form.bindEncrypted.fold(
        formWithErrors => {
          val formWithErrorsUpdate = formWithErrors
            .replaceError("", "obtainInfoWhy", FormError("obtainInfoWhy", errorRequired))
            .replaceError("", "nameOrOrganisation", FormError("nameOrOrganisation", errorRequired))
          BadRequest(views.html.circs.consent_and_declaration.declaration(formWithErrorsUpdate))
        },
        f => {
          val updatedCircs = copyInstance(circs.update(f))
          checkForBot(updatedCircs, request)
          submission(updatedCircs, request, f.jsEnabled)
        }
      )
  }
}
