package controllers.circs.s2_report_changes

import org.specs2.mutable.{Tags, Specification}
import app.ReportChange._

class G1ReportChangesFormSpec extends Specification with Tags {
   "Report a change in your circumstances - Change in circumstances" should {

     "map data into case class" in {
       G1ReportChanges.form.bind(
         Map(
           "reportChanges" -> AdditionalInfo.name
         )
       ).fold(
         formWithErrors => "This mapping should not happen." must equalTo("Error"),
         f => {
           f.reportChanges must equalTo(AdditionalInfo.name)
         }
       )
     }

     "mandatory fields must be populated" in {
       G1ReportChanges.form.bind(
         Map("reportChanges" -> "")
       ).fold(
           formWithErrors => {
             formWithErrors.errors(0).message must equalTo("error.required")
           },
           f => "This mapping should not happen." must equalTo("Valid")
         )
     }

   } section("unit", models.domain.CircumstancesSelfEmployment.id)
 }
