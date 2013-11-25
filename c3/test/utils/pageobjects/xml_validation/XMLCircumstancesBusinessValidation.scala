package utils.pageobjects.xml_validation

import scala.xml.Elem
import utils.pageobjects.{TestDatumValue, PageObjectException, TestData}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Validates that an XML contains all the relevant data that was provided in a Claim.
 * The mapping claim's attributes to XML xPath is defined in a configuration file.
 * @author Jorge Migueis
 *         Date: 23/07/2013
 */
class XMLCircumstancesBusinessValidation extends XMLBusinessValidation  {
  val mappingFilename = "/CircumstancesXmlMapping.csv"

  def createXMLValidationNode = (xml: Elem, nodes: Array[String]) => new XmlNode(xml,nodes)

  /**
   * Performs the validation of a claim XML against the data used to populate the claim forms.
   * @param claim Original claim used to go through the screens and now used to validate XML.
   * @param xml  XML that needs to be validated against the provided claim.
   * @param throwException Specify whether the validation should throw an exception if mismatches are found.
   * @return List of errors found. The list is empty if no errors were found.
   */
  def validateXMLClaim(claim: TestData, xml: Elem, throwException: Boolean): List[String] = {
    super.validateXMLClaim(claim, xml, throwException, mappingFilename, createXMLValidationNode)
  }
}
