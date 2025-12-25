package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class CopyPolicyPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(CopyPolicyPage.class));

  public CopyPolicyPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String nodeCopyPolicy = "form1:copyPolicy:";
  private static final By editEffectiveDate = By.id(nodeCopyPolicy + "effectiveDtBI");
  // private By dropdownEffectiveDate = By.id(nodeCopyPolicy + "termBI");
  private static final By dropdownTerm = By.id(nodeCopyPolicy + "termBI");
  private static final By dropdownRenewalTerm = By.id(nodeCopyPolicy + "renewalTermBI");
  private static final By editExpirationDate = By.id(nodeCopyPolicy + "expirationDtBI");
  private static final By editReleaseDate = By.id(nodeCopyPolicy + "releaseDtBI");
  private static final By dropdownCorporation = By.id(nodeCopyPolicy + "corporationBI");
  private static final By dropdownCompany = By.id(nodeCopyPolicy + "policyCompanyBI");
  private static final By dropdownPolicyType = By.id(nodeCopyPolicy + "policyProductBI");
  private static final By dropdownProgram = By.id(nodeCopyPolicy + "policyProgramBI");
  private static final By dropdownPolicyState = By.id(nodeCopyPolicy + "policyStateBI");
  private static final By checkboxRenewalPolicy = By.id(nodeCopyPolicy + "targetRenewalPolicy");
  private static final By checkboxUseCurrentRateRules = By.id(nodeCopyPolicy + "currentRules");
  private static final By checkboxRetainBaseNumber = By.id(nodeCopyPolicy + "retainBaseNbrBI");
  private static final By checkboxUserEnteredPolicyNumber =
      By.id(nodeCopyPolicy + "userEnteredPolNbrBI");
  private static final By editCopyFromPolicyNumber = By.id(nodeCopyPolicy + "sourcePolicyNbrId");
  private static final By editCopyToPolicyNumber = By.id(nodeCopyPolicy + "targetPolicyNbrId");
  // private By ExistingProductsInformationCheckbox() { }
  private static final By buttonDelete = By.id(nodeCopyPolicy + "DelBtn");
  private static final By buttonCopy = By.id(nodeCopyPolicy + "copyBtn");
  private static final By buttonCancel = By.id(nodeCopyPolicy + "cancelBtn");
  private static final By buttonCopySuccess = By.xpath("*//button[@type='button']//span[.='OK']");
  private static final By buttonPendingChangesOK = By.xpath(".//*/span[.='OK']");
  private static final String pageTitle = "CopyPolicyPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getEditEffectiveDate() {
    return getEdit(editEffectiveDate);
  }

  public String getDropdownTerm() {
    return getDropdown(dropdownTerm);
  }

  public String getDropdownRenewalTerm() {
    return getDropdown(dropdownRenewalTerm);
  }

  public String getEditExpirationDate() {
    return getEdit(editExpirationDate);
  }

  public String getEditReleaseDate() {
    return getEdit(editReleaseDate);
  }

  public String getDropdownCorporation() {
    return getDropdown(dropdownCorporation);
  }

  public String getDropdownCompany() {
    return getDropdown(dropdownCompany);
  }

  public String getDropdownPolicyType() {
    return getDropdown(dropdownPolicyType);
  }

  public String getDropdownProgram() {
    return getDropdown(dropdownProgram);
  }

  public String getDropdownPolicyState() {
    return getDropdown(dropdownPolicyState);
  }

  public String getCheckboxRenewalPolicy() {
    return getCheckbox(checkboxRenewalPolicy);
  }

  public String getCheckboxUseCurrentRateRules() {
    return getCheckbox(checkboxUseCurrentRateRules);
  }

  public String getCheckboxRetainBaseNumber() {
    return getCheckbox(checkboxRetainBaseNumber);
  }

  public String getCheckboxUserEnteredPolicyNumber() {
    return getCheckbox(checkboxUserEnteredPolicyNumber);
  }

  public String getEditCopyFromPolicyNumber() {
    return getEdit(editCopyFromPolicyNumber);
  }

  public String getEditCopyToPolicyNumber() {
    return getEdit(editCopyToPolicyNumber);
  }

  // METHODS SET
  public void setEditEffectiveDate(String value) {
    setEdit(editEffectiveDate, value);
  }

  public void selectDropdownTerm(String value) {
    selectDropdown(dropdownTerm, value);
  }

  public void selectDropdownRenewalTerm(String value) {
    selectDropdown(dropdownRenewalTerm, value);
  }

  public void setEditExpirationDate(String value) {
    setEdit(editExpirationDate, value);
  }

  public void setEditReleaseDate(String value) {
    setEdit(editReleaseDate, value);
  }

  public void selectDropdownCorporation(String value) {
    selectDropdown(dropdownCorporation, value);
  }

  public void selectDropdownCompany(String value) {
    selectDropdown(dropdownCompany, value);
  }

  public void selectDropdownPolicyType(String value) {
    selectDropdown(dropdownPolicyType, value);
  }

  public void selectDropdownProgram(String value) {
    selectDropdown(dropdownProgram, value);
  }

  public void selectDropdownPolicyState(String value) {
    selectDropdown(dropdownPolicyState, value);
  }

  public void toggleCheckboxRenewalPolicy() {
    toggleCheckbox(checkboxRenewalPolicy);
  }

  public void setCheckboxRenewalPolicy(String value) {
    setCheckbox(checkboxRenewalPolicy, value);
  }

  public void toggleCheckboxUseCurrentRateRules() {
    toggleCheckbox(checkboxUseCurrentRateRules);
  }

  public void setCheckboxUseCurrentRateRules(String value) {
    setCheckbox(checkboxUseCurrentRateRules, value);
  }

  public void toggleCheckboxRetainBaseNumber() {
    toggleCheckbox(checkboxRetainBaseNumber);
  }

  public void setCheckboxRetainBaseNumber(String value) {
    setCheckbox(checkboxRetainBaseNumber, value);
  }

  public void toggleCheckboxUserEnteredPolicyNumber() {
    toggleCheckbox(checkboxUserEnteredPolicyNumber);
  }

  public void setCheckboxUserEnteredPolicyNumber(String value) {
    setCheckbox(checkboxUserEnteredPolicyNumber, value);
  }

  public void setEditCopyFromPolicyNumber(String value) {
    setEdit(editCopyFromPolicyNumber, value);
  }

  public void setEditCopyToPolicyNumber(String value) {
    setEdit(editCopyToPolicyNumber, value);
  }

  public void verifyFromPolicyAndToPolicyAreCorrect() {
    final String copyFromPolicyNum = getEdit(editCopyFromPolicyNumber);
    final String copyToPolicyNum = getEdit(editCopyToPolicyNumber);
    if (getWebElement(checkboxRetainBaseNumber).isSelected()) {
      Assert.assertEquals(copyFromPolicyNum, copyToPolicyNum);
    } else {
      Assert.assertNotSame(copyFromPolicyNum, copyToPolicyNum);
    }
  }

  public void setCheckboxesExistingProducts() {
    // final List<WebElement> elements =
    // getWebElements(By.xpath(".//input[@type='Checkbox' and
    // starts-with(@id,'form1:copyPolicy:table1')]"));
    final List<WebElement> elements =
        getWebElements(
            By.xpath(".//input[@type='Checkbox'][starts-with(@id,'form1:copyPolicy:table1')]"));
    setCheckboxes(elements, LABEL_OPTION_CHECKED);
  }

  public void clickButtonDelete() {
    clickObject(buttonDelete);
  }

  public void clickButtonCopy() {
    clickObject(buttonCopy);
  }

  public void clickButtonCopySuccess() {
    /// html/body/div[contains(@class='ui-dialog-buttons')]
    clickObjectPopup(buttonCopySuccess);
    clickObjectPopup(buttonPendingChangesOK);
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
        if (Environment.isLogAll()) {
          LOG.debug("({Field}{}, {Value}{});", field, value);
        }
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "effective date":
            setEditEffectiveDate(value);
            break;
          case "term":
            selectDropdownTerm(value);
            break;
          case "renewal term":
            selectDropdownRenewalTerm(value);
            break;
          case "expiration date":
            setEditExpirationDate(value);
            break;
          case "release date":
            setEditReleaseDate(value);
            break;
          case "corporation":
            selectDropdownCorporation(value);
            break;
          case "company":
            selectDropdownCompany(value);
            break;
          case "policy type":
            selectDropdownPolicyType(value);
            break;
          case "program":
            selectDropdownProgram(value);
            break;
          case "policy state":
            selectDropdownPolicyState(value);
            break;
          case "renewal policy":
            setCheckboxRenewalPolicy(value);
            break;
          case "use current rate/rules":
            setCheckboxUseCurrentRateRules(value);
            break;
          case "retain base number":
            setCheckboxRetainBaseNumber(value);
            break;
          case "user entered policy#":
            setCheckboxUserEnteredPolicyNumber(value);
            break;
          case "copy from policy#":
            setEditCopyFromPolicyNumber(value);
            break;
          case "copy to policy#":
            setEditCopyToPolicyNumber(value);
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
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "effective date":
            value = getEditEffectiveDate();
            break;
          case "term":
            value = getDropdownTerm();
            break;
          case "renewal term":
            value = getDropdownRenewalTerm();
            break;
          case "expiration date":
            value = getEditExpirationDate();
            break;
          case "release date":
            value = getEditReleaseDate();
            break;
          case "corporation":
            value = getDropdownCorporation();
            break;
          case "company":
            value = getDropdownCompany();
            break;
          case "policy type":
            value = getDropdownPolicyType();
            break;
          case "program":
            value = getDropdownProgram();
            break;
          case "policy state":
            value = getDropdownPolicyState();
            break;
          case "renewal policy":
            value = getCheckboxRenewalPolicy();
            break;
          case "use current rate/rules":
            value = getCheckboxUseCurrentRateRules();
            break;
          case "retain base number":
            value = getCheckboxRetainBaseNumber();
            break;
          case "user entered policy#":
            value = getCheckboxUserEnteredPolicyNumber();
            break;
          case "copy from policy#":
            value = getEditCopyFromPolicyNumber();
            break;
          case "copy to policy#":
            value = getEditCopyToPolicyNumber();
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
