package utils.pageobjects

import play.api.test.TestBrowser
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.collection.convert.Wrappers.JListWrapper
import org.openqa.selenium.By
import utils.helpers.StringPadding._

/**
 * Fill operations on the web elements composing a page.
 * @author Jorge Migueis
 *         Date: 15/07/2013
 */
trait WebFillActions {
  this: { val browser: TestBrowser } =>

  private def click(elementCssSelector: String) = {
    if (browser.find(elementCssSelector).isEmpty) handleUnknownElement(elementCssSelector)
    browser.click(elementCssSelector)
  }

  def fillAddress(elementCssSelector: String, value: String) = if (null != value) {

    val lines =  value.split("&")
    val extensions = Array("_lineOne", "_lineTwo", "_lineThree")
    for (i <- 0 to lines.size - 1) {
      fillInput(elementCssSelector + extensions(i), lines(i))
    }
  }

  def fillCheck(elementCssSelector: String, value: String) = if (null != value) {
    if ("yes" == value.toLowerCase) click(elementCssSelector)
  }

  def fillDate(elementCssSelector: String, value: String) = if (null != value) {
    val date = DateTime.parse(value, DateTimeFormat.forPattern("dd/MM/yyyy"))
    val day = date.dayOfMonth().getAsText
    fillSelect(elementCssSelector + "_day", day)
    fillSelect(elementCssSelector + "_month", date.monthOfYear().getAsString)
    fillInput(elementCssSelector + "_year", date.year().getAsText)
  }

  def fillInput(elementCssSelector: String, value: String) = if (null != value) {
    try {
      if (browser.find(elementCssSelector).isEmpty) handleUnknownElement(elementCssSelector)
      browser.fill(elementCssSelector).`with`(value)
    }catch {
      case e: Exception => throw new PageObjectException("Could not fillInput " + elementCssSelector + " with value " + value, exception = e)
    }
  }

  def fillJSInput(elementCssSelector: String, value: String) = if (null != value) {
    try {
      browser.executeScript("$(\""+elementCssSelector+"\").val(\""+value+"\")")
    }catch {
      case e: Exception => throw new PageObjectException("Could not fillJSInput " + elementCssSelector + " with value " + value, exception = e)
    }
  }

  def fillNino(elementCssSelector: String, value: String) = if (null != value) {
    val extractor = """(.{2})(.{2})(.{2})(.{2})(.)""".r
    val extractor(n1, n2, n3, n4, n5) = value
    fillInput(elementCssSelector + "_ni1", n1)
    fillInput(elementCssSelector + "_ni2", n2)
    fillInput(elementCssSelector + "_ni3", n3)
    fillInput(elementCssSelector + "_ni4", n4)
    fillInput(elementCssSelector + "_ni5", n5)
  }

  def fillRadioList(listName: String, value: String, sep: String = "_"): Unit = if (null != value) {
    try {
      click(listName + sep + value.replace(" ","_"))
    }
    catch {
      case e: Exception => throw new PageObjectException("Could not fillRadioList " + listName + " with value " + value, exception = e)
    }

  }

  def fillSelect(elementCssSelector: String, value: String):Unit = if (null != value) {
    try {
      val webElement = browser.find(elementCssSelector)
      if (webElement.isEmpty) handleUnknownElement(elementCssSelector)
      val allOptions = new JListWrapper(webElement.first().getElement.findElements(By.tagName("option"))) // Java list
      //println("***** allOptions: " + allOptions)
      var found = false
      for (option <- allOptions; if option.getAttribute("value").toLowerCase == value.toLowerCase) {
        found = true
        option.click()
      }
      if (!found) throw new PageObjectException("Option " + value + " is invalid for combobox " + elementCssSelector)
    }
    catch {
      case e: Exception => throw new PageObjectException("Could not fillSelect " + elementCssSelector + " with value " + value + " in html:\n" + browser.pageSource(), exception = e)
    }
  }

  def fillSortCode(elementCssSelector: String, value: String) = if (null != value) {
    val extractor = """(\d{2})(\d{2})(\d{2})""".r
    val extractor(n1, n2, n3) = value
    fillInput(elementCssSelector + "_sort1", n1)
    fillInput(elementCssSelector + "_sort2", n2)
    fillInput(elementCssSelector + "_sort3", n3)
  }

  def fillTime(elementCssSelector: String, value: String) = if (null != value) {
    try {
      val extractor = """([^:]*):([^:]*)""".r
      val extractor(hour, minute) = value

      fillSelect(elementCssSelector + "_hour", leftPadWithZero(2,hour))
      fillSelect(elementCssSelector + "_minutes", leftPadWithZero(2,minute))
    }
    catch {
      case e: MatchError => throw new PageObjectException("Could not fillTime " + elementCssSelector + " with value " + value, exception = e)
    }
  }

  def fillYesNo(elementCssSelector: String, value: String, sep: String = "_") = if (null != value && value.nonEmpty) try {
    click(elementCssSelector + sep + value.toLowerCase)
  }
  catch {
    case e: Exception => throw new PageObjectException("Could not fill " + elementCssSelector + " with value " + value, exception = e)
  }

  private def handleUnknownElement(elementCssSelector: String) = {
    throw new PageObjectException("Unknown element with CSS selector " + elementCssSelector + " in html:\n" + browser.pageSource())
  }

}
