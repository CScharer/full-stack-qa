package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class RequestPrintPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RequestPrintPage.class));

  public RequestPrintPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String nodeRequestPrint = "form1:requestPrint:";
  private static final By dropdownReports = By.id(nodeRequestPrint + "reportId");
  private static final By dropdownEffectiveDate = By.id(nodeRequestPrint + "effectiveDtId");
  private static final By dropdownProcessedDate = By.id(nodeRequestPrint + "processedDt");
  private static final By checkboxPullPrint = By.id(nodeRequestPrint + "printPolicyId:pullPrint");
  private static final By checkboxPrintWorkSheets =
      By.id(nodeRequestPrint + "printPolicyId:printWorksheet");
  private static final By dropdownPrinter = By.id(nodeRequestPrint + "printPolicyId:printer");
  private static final By checkboxSPLPrint = By.id(nodeRequestPrint + "printPolicyId:splPrint");
  private static final By buttonPrint = By.id(nodeRequestPrint + "buttonOk");
  private static final By buttonCancel = By.id(nodeRequestPrint + "cancelBtn");
  private static final By buttonMinimizeTransactionInformation =
      By.id(nodeRequestPrint + "heading3form1:requestPrint:heading3");
  private static final By buttonMinimizePrintOptions =
      By.id(nodeRequestPrint + "printPolicyId:printheading");
  private static final String pageTitle = "RequestPrintPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getDropdownReports() {
    return getDropdown(dropdownReports);
  }

  public String getDropdownEffectiveDate() {
    return getDropdown(dropdownEffectiveDate);
  }

  public String getDropdownProcessedDate() {
    return getDropdown(dropdownProcessedDate);
  }

  public String getCheckboxPullPrint() {
    return getCheckbox(checkboxPullPrint);
  }

  public String getCheckboxPrintWorksheets() {
    return getCheckbox(checkboxPrintWorkSheets);
  }

  public String getDropdownPrinter() {
    return getDropdown(dropdownPrinter);
  }

  public String getCheckboxSPLPrint() {
    return getCheckbox(checkboxSPLPrint);
  }

  // METHODS SET
  public void selectDropdownReports(String value) {
    selectDropdown(dropdownReports, value);
  }

  public void selectDropdownEffectiveDate(String value) {
    sleep(200);
    selectDropdownWithPartialText(dropdownEffectiveDate, value);
  }

  public void selectDropdownProcessedDate(String value) {
    sleep(200);
    selectDropdownWithPartialText(dropdownProcessedDate, value);
  }

  public void toggleCheckboxPullPrint() {
    clickObject(checkboxPullPrint);
  }

  public void setCheckboxPullPrint(String value) {
    setCheckbox(checkboxPullPrint, value);
  }

  public void toggleCheckboxPrintWorkSheets() {
    clickObject(checkboxPrintWorkSheets);
  }

  public void setCheckboxPrintWorksheets(String value) {
    setCheckbox(checkboxPrintWorkSheets, value);
  }

  public void selectDropdownPrinter(String value) {
    selectDropdown(dropdownPrinter, value);
  }

  public void toggleCheckboxSPLPrint() {
    clickObject(checkboxSPLPrint);
  }

  public void setCheckboxSPLPrint(String value) {
    setCheckbox(checkboxSPLPrint, value);
  }

  // BUTTONS
  public void clickButtonPrint() {
    clickObject(buttonPrint);
  }

  public void clickButtonCancel() {
    clickObject(buttonCancel);
  }

  public void clickButtonMinimizeTransactionInformation() {
    clickObject(buttonMinimizeTransactionInformation);
  }

  public void clickButtonMinimizePrintOptions() {
    clickObject(buttonMinimizePrintOptions);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("({Field}{}, {Value}{});", field, value);
        }
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "reports":
            selectDropdownReports(value);
            break;
          case "effective date":
            selectDropdownEffectiveDate(value);
            break;
          case "processed date":
            selectDropdownProcessedDate(value);
            break;
          case "pull print":
            setCheckboxPullPrint(value);
            break;
          case "print worksheets":
            setCheckboxPrintWorksheets(value);
            break;
          case "printer":
            selectDropdownPrinter(value);
            break;
          case "spl print":
            setCheckboxSPLPrint(value);
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
      }
    }
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable table) {
    FormsPage formsPage = new FormsPage(getWebDriver());
    formsPage.getUserName();
    final Map<String, String> expected = new HashMap<>();
    final Map<String, String> actual = new HashMap<>();
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("({Field}{}, {Value}{});", field, value);
        }
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "reports":
            value = getDropdownReports();
            break;
          case "effective date":
            value = getDropdownEffectiveDate();
            break;
          case "processed date":
            value = getDropdownProcessedDate();
            value = expected.get(field);
            break;
          case "pull print":
            value = getCheckboxPullPrint();
            break;
          case "print worksheets":
            value = getCheckboxPrintWorksheets();
            break;
          case "printer":
            value = getDropdownPrinter();
            break;
          case "spl print":
            value = getCheckboxSPLPrint();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    Assertions.assertSame(expected, actual, getPageTitle() + " validatePage");
  }
}
