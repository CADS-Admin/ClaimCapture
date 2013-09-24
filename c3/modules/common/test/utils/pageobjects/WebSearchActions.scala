package utils.pageobjects

import play.api.test.TestBrowser
import scala.collection.convert.Wrappers.JListWrapper
import org.openqa.selenium.By
import scala.collection.mutable
import utils.helpers.StringPadding._
import org.fluentlenium.core.filter.FilterConstructor._


/**
 * Search operations on the web elements composing a page.
 * To be used by assertions in tests.
 * @author Jorge Migueis
 *         Date: 11/07/2013
 */
trait WebSearchActions {
  this: {val browser: TestBrowser} =>

  // Read operations
  def readAddress(elementCssSelector: String): Option[String] = {
    val extensions = Array("_street_lineOne", "_town_lineTwo", "_town_lineThree")
    val addressLines = mutable.ArrayBuffer.empty[String]
    extensions.foreach(s => addressLines += readInput(elementCssSelector + s).orNull)
    if (addressLines.nonEmpty) Some(addressLines.mkString("&").dropRight(1)) else None
  }

  def readCheck(elementCssSelector: String): Option[String] = {
    this checkElement elementCssSelector
    if (browser.find(elementCssSelector, 0).isSelected) Some("yes")
    else None
  }

  def readDate(elementCssSelector: String): Option[String] = {
    val day = readSelect(elementCssSelector + "_day")
    val month = readSelect(elementCssSelector + "_month")
    val year = readInput(elementCssSelector + "_year")
    if (day.isDefined && month.isDefined && year.isDefined) Some(leftPadWithZero(2, day.get) + "/" + leftPadWithZero(2, month.get) + "/" + year.get)
    else None
  }

  def readInput(elementCssSelector: String): Option[String] = {
    this checkElement elementCssSelector
    Some(browser.value(elementCssSelector).get(0))
  }

  def readNino(elementCssSelector: String): Option[String] = {
    val n1 = readInput(elementCssSelector + "_ni1")
    val n2 = readInput(elementCssSelector + "_ni2")
    val n3 = readInput(elementCssSelector + "_ni3")
    val n4 = readInput(elementCssSelector + "_ni4")
    val n5 = readInput(elementCssSelector + "_ni5")
    if (n1.isDefined) Some(n1.get + n2.get + n3.get + n4.get + n5.get)
    else None
  }

  def readSelect(elementCssSelector: String): Option[String] = {
    this checkElement elementCssSelector
    val select = browser.find(elementCssSelector, 0).getElement
    val allOptions = new JListWrapper(select.findElements(By.tagName("option"))) // Java list
    var value = ""
    for (option <- allOptions; if option.isSelected) {
      value = option.getAttribute("value")
    }
    if (value.nonEmpty) Some(value) else None
  }

  def readSortCode(elementCssSelector: String): Option[String] = {
    val n1 = readInput(elementCssSelector + "_sort1")
    val n2 = readInput(elementCssSelector + "_sort2")
    val n3 = readInput(elementCssSelector + "_sort3")
    if (n1.isDefined && n2.isDefined && n3.isDefined) Some(n1.get + n2.get + n3.get)
    else None
  }

  def readTime(elementCssSelector: String): Option[String] = {
    val cssSelectorHours = elementCssSelector + "_hour"
    val cssSelectorMinutes = elementCssSelector + "_minutes"
    val hours = this.readSelect(cssSelectorHours)
    val minutes = this.readSelect(cssSelectorMinutes)
    if (hours.isDefined && minutes.isDefined) Some(hours.get + ":" + minutes.get)
    else None
  }

  def readYesNo(location: String, sep: String = "_"): Option[String] = {
    def isCheckSelected(value: String): Boolean = {
      val valCssSelector = location + sep + value
      this checkElement valCssSelector
      browser.find(valCssSelector, 0).isSelected
    }

    if (isCheckSelected("yes")) Some("yes")
    else if (isCheckSelected("no")) Some("no")
    else None
  }

  //====================================================================================================================
  // Other search operations
  //====================================================================================================================

  def titleOfSubmitButton = browser.find("#submit").getText

  // Completed Sections
  def isSpecifiedSectionCompleted(index: Integer, name: String, value: String, location: String = "div[class=completed] ul li") = {
    this checkElement location
    val completed = browser.find(location).get(index).getText
    completed.contains(name) && completed.contains(value)
  }

  def hasSectionsBeenCompleted(location: String = "div[class=completed] ul li") = this hasListOfElements location

  def numberSectionsCompleted(location: String = "div[class=completed] ul li") = this sizeLitOfElements location

  /**
   * Provides the list of completed sections displayed in a page. If there is no completed section then returns an empty list.
   * @return The list of completed sections.
   */
  def listCompletedForms = findTarget("div[class=completed] ul li")

  // Table
  def hasTableEntriesChangeable(location: String = "input[value='Change']") = this hasListOfElements location

  def numberTableEntriesChangeable(location: String = "input[value='Change']") = this sizeLitOfElements location

  def hasTable = !browser.find("table").isEmpty

  def readTableCell(row: Int, column: Int, elementCssSelector: String = "tbody"): Option[String] = {
    this checkElement elementCssSelector
    val rowWebElement = browser.find("tbody", 0) find("tr", row)
    if (rowWebElement != null) {
      val columnWebElement = rowWebElement find("td", column)
      if (columnWebElement != null) Some(columnWebElement.getElement.getText)
      else None
    }
    else None
  }

  // Errors
  /**
   * Provides the list of errors displayed in a page. If there is no error then returns an empty list.
   * @return The list of errors displayed by a page.
   */
  def listErrors: List[String] = findTarget("div[class=validation-summary] ol li")


  // Prompt messages
  /**
   * Provides the list of prompt messages displayed in a page. If there is no prompt message then returns an empty list.
   * @return The list of prompt messages displayed by a page.
   */
  def listDisplayedPromptMessages: List[String] = try {
    val target = "div.breaks-prompt"
    this checkElement target
    val rawTarget = browser.find(target, `with`("style").notContains("display: none"))
    if (!rawTarget.isEmpty) new JListWrapper(rawTarget.getTexts).toList
    else List()
  } catch {
    case e: Exception => List[String]()
  }

  //====================================================================================================================
  // Protected & private functions
  //====================================================================================================================

  protected def findTarget(target: String): List[String] = {
    try {
      this checkElement target
      val rawTarget = browser.find(target)
      if (!rawTarget.isEmpty) new JListWrapper(rawTarget.getTexts).toList
      else List()
    }
    catch {
      case e: Exception => List[String]()
    }
  }

  protected def hasListOfElements(location: String) = {
    this checkElement location
    !browser.find(location).isEmpty
  }

  protected def sizeLitOfElements(location: String) = {
    this checkElement location
    browser.find(location).size()
  }

  protected def checkElement(elementCssSelector: String) = {
    if (browser.find(elementCssSelector).isEmpty) handleUnknownElement(elementCssSelector)
  }

  private def handleUnknownElement(elementCssSelector: String) = {
    throw new PageObjectException("Unknown element with CSS selector " + elementCssSelector + " in html:\n" + browser.pageSource())
  }

}
