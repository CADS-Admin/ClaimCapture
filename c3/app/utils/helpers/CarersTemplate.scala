package utils.helpers

import views.html.helper.FieldConstructor

object CarersTemplate {
  implicit val fieldConstructor = FieldConstructor(views.html.helper.templates.carersTemplate.f)
}

object EmptyTemplate {
  implicit val fieldConstructor = FieldConstructor(views.html.helper.templates.emptyTemplate.f)
}

object DateTimeTemplate {
  implicit val fieldConstructor = FieldConstructor(views.html.helper.templates.dateTimeTemplate.f)
}