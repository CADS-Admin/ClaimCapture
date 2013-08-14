package controllers

import play.api.test.WithBrowser
import org.fluentlenium.core.filter.Filter

trait WithBrowserHelper {
  this: WithBrowser[_] =>

  def goTo(relativeURL: String) = browser.goTo(relativeURL)

  def next = browser submit "button[value='next']"

  def back = browser click "#backButton"

  def click(cssSelector: String, filters: Filter*) = browser click(cssSelector, filters: _*)

  def fill(cssSelector: String, filters: Filter*) = browser fill(cssSelector, filters: _*)

  def $(name: String, filters: Filter*) = browser $(name, filters: _*)

  def findFirst(name: String, filters: Filter*) = browser findFirst(name, filters: _*)
}