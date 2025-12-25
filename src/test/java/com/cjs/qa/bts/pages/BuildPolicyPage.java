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

public class BuildPolicyPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BuildPolicyPage.class));

  public BuildPolicyPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By checkboxRenewalPolicy = By.id("renewalPolicyBI");
  private static final By editEffectiveDate = By.id("policyBuildTitleTxtId");
  private static final By dropdownTerm = By.id("termBI");
  private static final By dropdownRenewalTerm = By.id("renewalTermBI");
  private static final By editExpirationDate = By.id("expirationDtBI");
  private static final By dropdownRatingCompany = By.id("policyCompanyBI");
  private static final By dropdownIssuingCompany = By.id("policyProductBI");
  private static final By dropdownProgram = By.id("policyProgramBI");
  private static final By dropdownPrimaryState = By.id("policyStateBI");
  private static final By editReleaseDate = By.id("releaseDtBI");
  private static final By checkboxUserEnteredPolicyNumber = By.id("userEnteredPolicyBI");
  private static final By editPolicyNumber = By.id("policyNbrBI");
  private static final By editSequenceNumber = By.id("policyNbrSeqBI");
  private static final By buttonSave = By.id("button1");
  private static final By buttonCancel = By.id("button2");
  private static final String pageTitle = "BuildPolicyPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getCheckboxRenewalPolicy() {
    return getCheckbox(checkboxRenewalPolicy);
  }

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

  public String getDropdownRatingCompany() {
    return getDropdown(dropdownRatingCompany);
  }

  public String getDropdownIssuingCompany() {
    return getDropdown(dropdownIssuingCompany);
  }

  public String getDropdownProgram() {
    return getDropdown(dropdownProgram);
  }

  public String getDropdownPrimaryState() {
    return getDropdown(dropdownPrimaryState);
  }

  public String getEditReleaseDate() {
    return getEdit(editReleaseDate);
  }

  public String getCheckboxUserEnteredPolicyNumber() {
    return getCheckbox(checkboxUserEnteredPolicyNumber);
  }

  public String getEditPolicyNumber() {
    return getEdit(editPolicyNumber);
  }

  public String getEditSequenceNumber() {
    return getEdit(editSequenceNumber);
  }

  // METHODS SET
  public void toggleCheckboxRenewalPolicy() {
    toggleCheckbox(checkboxRenewalPolicy);
  }

  public void setCheckboxRenewalPolicy(String value) {
    setCheckbox(checkboxRenewalPolicy, value);
  }

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

  public void selectDropdownRatingCompany(String value) {
    selectDropdown(dropdownRatingCompany, value);
  }

  public void selectDropdownIssuingCompany(String value) {
    selectDropdown(dropdownIssuingCompany, value);
  }

  public void selectDropdownProgram(String value) {
    selectDropdown(dropdownProgram, value);
  }

  public void selectDropdownPrimaryState(String value) {
    selectDropdown(dropdownPrimaryState, value);
  }

  public void setEditReleaseDate(String value) {
    setEdit(editReleaseDate, value);
  }

  public void toggleCheckboxUserEnteredPolicyNumber() {
    toggleCheckbox(checkboxUserEnteredPolicyNumber);
  }

  public void setCheckboxUserEnteredPolicyNumber(String value) {
    setCheckbox(checkboxUserEnteredPolicyNumber, value);
  }

  public void setEditPolicyNumber(String value) {
    setEdit(editPolicyNumber, value);
  }

  public void setEditSequenceNumber(String value) {
    setEdit(editSequenceNumber, value);
  }

  public void clickButtonSave() {
    clickObject(buttonSave);
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
          case "renewal policy":
            setCheckboxRenewalPolicy(value);
            break;
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
          case "rating company":
            selectDropdownRatingCompany(value);
            break;
          case "issuing company":
            selectDropdownIssuingCompany(value);
            break;
          case "program":
            selectDropdownProgram(value);
            break;
          case "primary state":
            selectDropdownPrimaryState(value);
            break;
          case "release date":
            setEditReleaseDate(value);
            break;
          case "user entered policy #":
            setCheckboxUserEnteredPolicyNumber(value);
            break;
          case "policy #":
            setEditPolicyNumber(value);
            break;
          case "sequence #":
            setEditSequenceNumber(value);
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
          case "renewal policy":
            value = getCheckboxRenewalPolicy();
            break;
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
          case "rating company":
            value = getDropdownRatingCompany();
            break;
          case "issuing company":
            value = getDropdownIssuingCompany();
            break;
          case "program":
            value = getDropdownProgram();
            break;
          case "primary state":
            value = getDropdownPrimaryState();
            break;
          case "release date":
            value = getEditReleaseDate();
            break;
          case "user entered policy #":
            value = getCheckboxUserEnteredPolicyNumber();
            break;
          case "policy #":
            value = getEditPolicyNumber();
            break;
          case "sequence #":
            value = getEditSequenceNumber();
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
