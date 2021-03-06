package utils.helpers

import app.ConfigProperties._
import gov.dwp.carers.xml.schemavalidations.SchemaValidation

/**
  * Created by peterwhitehead on 11/03/2016.
  */
object TextLengthHelper {
  def textMaxLength(path: String): Integer = {
    val schemaVersion=getStringProperty("xml.schema.version")
    Option(new SchemaValidation(schemaVersion).getRestriction(path)) match {
      case Some(restriction) if(restriction.getMaxlength != null) => restriction.getMaxlength
      case _ => -1
    }
  }
}
