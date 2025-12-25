package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class AmendPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(AmendPage.class));

  public AmendPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  // private final String nodeAmendForm = ".//*[@id='amendForm:amendPanel']";
  private static final By editPolicyEffectiveDate = By.id("amendForm:policyOriginalEfftDt");
  private static final By editPolicyExpirationDate = By.id("amendForm:policyExpDtDisp");
  private static final By optionAmendmentType = By.id("amendForm:amendOpt");
  private static final By dropdownExistingAmendment = By.id("amendForm:openAmendment");
  private static final By editAmendDescription = By.id("amendForm:amendmentDesc");
  private static final By editAmendEffectiveDate = By.id("amendForm:effectiveDt");
  private static final By editAmendExpirationDate = By.id("amendForm:expirationDt");
  private static final By dropdownAvailable = By.id("amendForm:avlAmendReasons");
  private static final By dropdownSelected = By.id("amendForm:amendReasons");
  private static final By editAmendReasonText = By.id("amendForm:amendReasonText");
  private static final By dropdownPrintCopies = By.id("amendForm:printCopies");
  private static final By checkboxApplyToRenewal = By.id("amendForm:applyToRenewal']/..");
  private static final By checkboxAutoDeleteUnissuedAmendment =
      By.id("amendForm:autoDeleteUnissuedAmend']/..");
  private static final By optionApplyType = By.id("amendForm:applyWaiveRule");
  private static final By buttonAdd = By.id("amendForm:btnRight");
  private static final By buttonRemove = By.id("amendForm:btnLeft");
  private static final By buttonOK = By.id("amendForm:buttonOk");
  private static final By buttonCancel = By.id("amendForm:cancelBtn");
  private static final String pageTitle = "AmendPage";

  private String getPageTitle() {
    return pageTitle;
  }

  private static final By iFrame = By.xpath(".//*/iframe");

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void switchIFrame() {
    switchToIFrame(iFrame);
  }

  // METHODS GET
  public String getEditPolicyEffectiveDate() {
    return getEdit(editPolicyEffectiveDate);
  }

  public String getEditPolicyExpirationDate() {
    return getEdit(editPolicyExpirationDate);
  }

  public String getOptionAmendmentType() {
    return getOption(optionAmendmentType);
  }

  public String getEditAmendDescription() {
    return getEdit(editAmendDescription);
  }

  public String getDropdownExistingAmendment() {
    return getDropdown(dropdownExistingAmendment);
  }

  public String getEditAmendEffectiveDate() {
    return getEdit(editAmendEffectiveDate);
  }

  public String getEditAmendExpirationDate() {
    return getEdit(editAmendExpirationDate);
  }

  public String getDropdownAvailable() {
    return getDropdown(dropdownAvailable);
  }

  public String getDropdownSelected() {
    return getDropdown(dropdownSelected);
  }

  public String getEditAmendReasonText() {
    return getEdit(editAmendReasonText);
  }

  public String getDropdownPrintCopies() {
    return getDropdown(dropdownPrintCopies);
  }

  public String getCheckboxApplyToRenewal() {
    return getCheckbox(checkboxApplyToRenewal);
  }

  public String getCheckboxAutoDeleteUnissuedAmendment() {
    return getCheckbox(checkboxAutoDeleteUnissuedAmendment);
  }

  public String getOptionApplyType() {
    return getOption(optionApplyType);
  }

  // METHODS SET
  public void setEditPolicyEffectiveDate(String value) {
    setEdit(editPolicyEffectiveDate, value);
  }

  public void setEditPolicyExpirationDate(String value) {
    setEdit(editPolicyExpirationDate, value);
  }

  public void selectOptionAmendmentType(String value) {
    selectOption(optionAmendmentType, value);
  }

  public void selectDropdownExistingAmendment(String value) {
    selectDropdownWithPartialText(dropdownExistingAmendment, value);
  }

  public void setEditAmendDescription(String value) {
    setEdit(editAmendDescription, value);
  }

  public void setEditAmendEffectiveDate(String value) {
    setEdit(editAmendEffectiveDate, value);
  }

  public void setEditAmendExpirationDate(String value) {
    setEdit(editAmendExpirationDate, value);
  }

  public void selectDropdownAvailable(String value) {
    selectDropdown(dropdownAvailable, value);
  }

  public void selectDropdownSelected(String value) {
    selectDropdown(dropdownSelected, value);
  }

  public void setEditAmendReasonText(String value) {
    setEdit(editAmendReasonText, value);
  }

  public void selectDropdownPrintCopies(String value) {
    selectDropdown(dropdownPrintCopies, value);
  }

  public void toggleCheckboxApplyToRenewal() {
    toggleCheckbox(checkboxApplyToRenewal);
  }

  public void setCheckboxApplyToRenewal(String value) {
    setCheckbox(checkboxApplyToRenewal, value);
  }

  public void toggleCheckboxAutoDeleteUnissuedAmendment() {
    toggleCheckbox(checkboxAutoDeleteUnissuedAmendment);
  }

  public void setCheckboxAutoDeleteUnissuedAmendment(String value) {
    setCheckbox(checkboxAutoDeleteUnissuedAmendment, value);
  }

  public void selectOptionApplyType(String value) {
    String xPath = ".//*[@id='amendForm:applyWaiveRule']//label//input[@value='";
    switch (value) {
      case "Apply Waived Rules":
        xPath += "WAIVED_RULES";
        break;
      case "Do not Charge or Return Premium":
        xPath += "DONT_RETURN_P";
        break;
      case "Charge or Return Premium":
        xPath += "RETURN_P";
        break;
      default:
        LOG.warn("Unknown apply type: {}. xPath may be invalid.", value);
        xPath += "UNKNOWN";
        break;
    }
    xPath += "']/../..";
    final By by = By.xpath(xPath);
    selectOption(by, value);
  }

  public void clickButtonAdd() {
    clickObject(buttonAdd);
  }

  public void clickButtonRemove() {
    clickObject(buttonRemove);
  }

  public void clickButtonOK() {
    clickObject(buttonOK);
  }

  public void clickButtonCancel() {
    clickObject(buttonCancel);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "policy effective date":
            setEditPolicyEffectiveDate(value);
            break;
          case "policy expiration date":
            setEditPolicyExpirationDate(value);
            break;
          case "amendment type":
            selectOptionAmendmentType(value);
            break;
          case "existing amendment":
            selectDropdownExistingAmendment(value);
            break;
          case "amend description":
            setEditAmendDescription(value);
            break;
          case "amend effective date":
            setEditAmendEffectiveDate(value);
            break;
          case "amend expiration date":
            setEditAmendExpirationDate(value);
            break;
          case "available":
            selectDropdownAvailable(value);
            break;
          case "selected":
            selectDropdownSelected(value);
            break;
          case "amend reason text":
            setEditAmendReasonText(value);
            break;
          case "print copies":
            selectDropdownPrintCopies(value);
            break;
          case "apply to renewal":
            setCheckboxApplyToRenewal(value);
            break;
          case "auto delete un-issued amendment":
            setCheckboxAutoDeleteUnissuedAmendment(value);
            break;
          case "apply type":
            selectOptionApplyType(value);
            break;
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable table) {
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
          case "policy effective date":
            value = getEditPolicyEffectiveDate();
            break;
          case "policy expiration date":
            value = getEditPolicyExpirationDate();
            break;
          case "amendment type":
            value = getOptionAmendmentType();
            break;
          case "existing amendment":
            value = getDropdownExistingAmendment();
            break;
          case "amend description":
            value = getEditAmendDescription();
            break;
          case "amend effective date":
            value = getEditAmendEffectiveDate();
            break;
          case "amend expiration date":
            value = getEditAmendExpirationDate();
            break;
          case "available":
            value = getDropdownAvailable();
            break;
          case "selected":
            value = getDropdownSelected();
            break;
          case "amend reason text":
            value = getEditAmendReasonText();
            break;
          case "print copies":
            value = getDropdownPrintCopies();
            break;
          case "apply to renewal":
            value = getCheckboxApplyToRenewal();
            break;
          case "auto delete un-issued amendment":
            value = getCheckboxAutoDeleteUnissuedAmendment();
            break;
          case "apply type":
            value = getOptionApplyType();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    Assert.assertSame(getPageTitle() + " validatePage", expected, actual);
  }
}
