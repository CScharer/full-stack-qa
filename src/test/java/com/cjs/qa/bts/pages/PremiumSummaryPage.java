package com.cjs.qa.bts.pages;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class PremiumSummaryPage extends Page {

  // State abbreviations for validation
  private static final List<String> STATE_ABBREVIATIONS =
      List.of(
          "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA",
          "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "ND", "NY",
          "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI",
          "WY");

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(PremiumSummaryPage.class));

  public PremiumSummaryPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLERATIONS
  //   private static final String nodeViewPort = ".//*[@id='viewport']";
  //   private final String nodePolicyInfoDiv =
  //   "/div[1]/div[1]/div[2]/div[1]/div/";
  //   private static final By textPremium =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[2]/span");
  //   private static final By textMinimumAdjustment =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[5]/span");
  //   private static final By textTaxes =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[8]/span");
  //   private static final By textFees =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[11]/span");
  //   private static final By textSurcharge =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[14]/span");
  //   private static final By textPolicyTotal =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[17]/span");
  //   private static final By textTotalManualPremium =
  //       By.xpath(".//*[@id='viewport']/div[1]/div[1]/div[2]/div[1]/div/div[20]/span");

  private static final By buttonExportExcel = By.xpath(".//*[@id='viewport']/div[2]/div/a/span");
  private static final By subHeaders =
      By.xpath(".//*[@id='productsPanel']/div[5]/div/div[@class='table']/div/..");
  private static final String pageTitle = "PremiumSummaryPage";

  public String getPageTitle() {
    return pageTitle;
  }

  // METHODS GET TEXT
  public List<String> getPolicyInformation() {
    return getElementsText(By.xpath(".//*[@id='viewport']//span[@class='data-label-r']"));
  }

  public List<String> getProductInformation() {
    return getElementsText(By.xpath(".//*[@id='productsPanel']//div"));
  }

  public String getTextPremium() {
    // final String premiumText = getLabel(textPremium);
    // return premiumText;
    return getPolicyInformation().get(1);
  }

  public String getTextMinimumAdjustment() {
    // final String minimumAdjustmentText = getLabel(textMinimumAdjustment);
    // return minimumAdjustmentText;
    return getPolicyInformation().get(3);
  }

  public String getTextTaxes() {
    // final String taxesText = getLabel(textTaxes);
    // return taxesText;
    return getPolicyInformation().get(5);
  }

  public String getTextFees() {
    // final String feesText = getLabel(textFees);
    // return feesText;
    return getPolicyInformation().get(7);
  }

  public String getTextSurcharge() {
    // final String surchargeText = getLabel(textSurcharge);
    // return surchargeText;
    return getPolicyInformation().get(9);
  }

  public String getTextPolicyTotal() {
    // final String policyTotalText = getLabel(textPolicyTotal);
    // return policyTotalText;
    return getPolicyInformation().get(11);
  }

  public String getTextTotalManualPremium() {
    // final String totalManualPremium = getLabel(textTotalManualPremium);
    // return totalManualPremium;
    return getPolicyInformation().get(13);
  }

  public String getSubHeaders() {
    String subHeaderText = "";
    final List<WebElement> elements = getWebDriver().findElements(subHeaders);
    for (final WebElement element : elements) {
      if (element.getText().contains("$")) {
        subHeaderText +=
            element.getText().substring(0, element.getText().indexOf("$")).trim()
                + Constants.DELIMETER_LIST;
      } else {
        subHeaderText += element.getText() + Constants.DELIMETER_LIST;
      }
    }
    return subHeaderText;
  }

  // After this there needs to be something to work with the excel file that
  // is getting exported
  // This is the CSV that needs to be exported and then compared to the input
  // file to make sure things are working as they should and all values are
  // correct
  // This needs to be converted before any checking can be done
  // The exporting needs to be done on the microsoft side, this is not
  // something you can do with just selenium
  public void clickButtonExportExcel() {
    clickObject(buttonExportExcel);
  }

  public void clickAllButtons() {
    final List<WebElement> allButtons =
        getWebDriver().findElements(By.xpath(".//*[@id='productsPanel']/*/div/div[1]/button"));
    for (final WebElement we : allButtons) {
      clickObject(we);
    }
  }

  public void verifyTable(String coverageDeductiblePremium) {
    final String state =
        coverageDeductiblePremium.substring(0, coverageDeductiblePremium.indexOf(":"));
    final String coverage =
        coverageDeductiblePremium.substring(
            coverageDeductiblePremium.indexOf(":"), coverageDeductiblePremium.indexOf(":"));
    final String limitExp =
        coverageDeductiblePremium.substring(
            coverageDeductiblePremium.indexOf(":"), coverageDeductiblePremium.indexOf(":"));
    final String deductible =
        coverageDeductiblePremium.substring(
            coverageDeductiblePremium.indexOf(":"), coverageDeductiblePremium.indexOf(":"));
    final String classCode =
        coverageDeductiblePremium.substring(
            coverageDeductiblePremium.indexOf(":"), coverageDeductiblePremium.indexOf(":"));
    final String premium =
        coverageDeductiblePremium.substring(
            coverageDeductiblePremium.lastIndexOf(':') + 1, coverageDeductiblePremium.length());
    checkProductInfo(state, coverage, limitExp, deductible, classCode, premium);
  }

  public void checkProductInfo(
      String state,
      String coverage,
      String limitExp,
      String deductible,
      String classCode,
      String premium) {
    String coverageFromTable = "";
    if (" ".equals(state)) {
      if (" ".equals(coverage) && " ".equals(deductible) && " ".equals(premium)) {
        LOG.debug("ALL BLANK");
      } else if ("".equals(deductible)) {
        coverageFromTable =
            getWebElement(
                    By.xpath(
                        " .//*/div/div/div/div/div/div/div/div/div/div/div/div/div[contains(.,'"
                            + coverage
                            + "')]"))
                .getText();
        assert coverageFromTable
            .toString()
            .equals(coverage + " " + limitExp + " " + deductible + " " + classCode + " " + premium);
      } else {
        coverageFromTable =
            getWebElement(
                    By.xpath(
                        ".//div/div/div/div/div/div/div/div/div/div/div[contains(.,'"
                            + coverage
                            + "')]/div[contains(.,'"
                            + limitExp
                            + "')]/div[contains(.,'"
                            + premium
                            + "')]"))
                .getText();
        assert coverageFromTable
            .toString()
            .equals(coverage + " " + limitExp + " " + deductible + " " + classCode + " " + premium);
      }
    } else if (STATE_ABBREVIATIONS.contains(state)) {
      if (" ".equals(coverage) && " ".equals(deductible) && " ".equals(premium)) {
        LOG.debug("ALL BLANK");
      } else if ("".equals(deductible)) {
        coverageFromTable =
            getWebElement(By.xpath(" .//*[@id='stateCloser']/div[contains(.,'" + coverage + "')]"))
                .getText();
        assert coverageFromTable
            .toString()
            .equals(coverage + " " + limitExp + " " + deductible + " " + classCode + " " + premium);
      } else {
        coverageFromTable =
            getWebElement(
                    By.xpath(
                        ".//*[@id='stateCloser']/div[contains(.,'"
                            + coverage
                            + "')]/div[contains(.,'"
                            + limitExp
                            + "')]/div[contains(.,'"
                            + premium
                            + "')]"))
                .getText();
        assert coverageFromTable
            .toString()
            .equals(coverage + " " + limitExp + " " + deductible + " " + classCode + " " + premium);
      }
    } else {
      if (" ".equals(coverage) && " ".equals(deductible) && " ".equals(premium)) {
        LOG.debug("ALL BLANK");
      } else {
        coverageFromTable =
            getWebElement(
                    By.xpath(
                        ".//*[@id='productsPanel']/div/div/div/div[contains(.,'"
                            + coverage
                            + "')]"))
                .getText();
        assert coverageFromTable
            .toString()
            .equals(coverage + " " + limitExp + " " + deductible + " " + classCode + " " + premium);
        LOG.debug("LAST ONE");
      }
      // else
      // {
      // coverageFromTable = getWebElement(By.xpath(
      // ".//*[@id='stateCloser']/div[contains(.,'" + coverage +
      // "')]/div[contains(.,'" + limitExp + "')]/div[contains(.,'" +
      // premium + "')]"))
      // .getText();
      // assert (coverageFromTable.toString().equals(coverage + " " +
      // limitExp + " " + deductible + " " + classCode + " " + premium));
      // }
      // coverageFromTable =
      // getWebElement(By.xpath(".//div/div/div/div/div/div/div/div/div/div/div[contains(.,'"
      // + coverage + "')]/div[contains(.,'"
      // + limitExp + " ')]/div[contains(.,'" + premium +
      // "')]")).getText();
      // assert (coverageFromTable.toString().equals(coverage + " " +
      // limitExp + " " + deductible + " " + classCode + " " + premium));
    }
  }
}
