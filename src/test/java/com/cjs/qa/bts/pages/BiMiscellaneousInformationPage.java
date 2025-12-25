package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiMiscellaneousInformationPage extends Page {

  public BiMiscellaneousInformationPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By checkboxPackageModification = By.id("packageModification");
  private static final By checkboxAuditable = By.id("auditable");
  private static final By dropdownLegalEntity = By.id("legalEntity");
  private static final By dropdownAuditBasis = By.id("auditBasis");
  private static final By dropdownTypeOfPolicy = By.id("typeOfPolicy");
  private static final By dropdownExternalAuditIndicator = By.id("externalAuditIndicator");
  private static final By dropdownPolicySymbol = By.id("policySymbol");
  private static final By editNaicsCode = By.id("naicsCode");
  private static final By editBusinessDescription = By.id("businessDescription");
  private static final By editOwnersLegalName = By.id("ownersLegalName");
  private static final By editFinancialReliabilityCode = By.id("financialReliabilityCode");
  private static final By editNationalAccountIndicator = By.id("nationalAccountIndicator");
  private static final By dropdownReportingCompany = By.id("reportingCompany");
  private static final By buttonFrame = By.id("miscInformation-title");
  private static final String pageTitle = "BI_MiscellaneousInformationPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void toggleCheckboxPackageModification() {
    if (!getWebElement(checkboxPackageModification).isSelected()) {
      clickObject(checkboxPackageModification);
    }
  }

  public void toggleCheckboxAuditable() {
    if (!getWebElement(checkboxAuditable).isSelected()) {
      clickObject(checkboxAuditable);
    }
  }

  public void selectDropdownLegalEntity(String value) {
    selectDropdown(dropdownLegalEntity, value);
  }

  public void selectDropdownAuditBasis(String value) {
    selectDropdown(dropdownAuditBasis, value);
  }

  public void selectDropdownTypeOfPolicy(String value) {
    selectDropdown(dropdownTypeOfPolicy, value);
  }

  public void selectDropdownExternalAuditIndicator(String value) {
    selectDropdown(dropdownExternalAuditIndicator, value);
  }

  public void selectDropdownPolicySymbol(String value) {
    selectDropdown(dropdownPolicySymbol, value);
  }

  public void enterEditNaicsCode(String value) {
    setEdit(editNaicsCode, value);
  }

  public void enterEditBusinessDescription(String value) {
    setEdit(editBusinessDescription, value);
  }

  public void enterEditOwnersLegalName(String value) {
    setEdit(editOwnersLegalName, value);
  }

  public void enterEditFinancialReliabilityCode(String value) {
    setEdit(editFinancialReliabilityCode, value);
  }

  public void enterEditNationalAccountIndicator(String value) {
    setEdit(editNationalAccountIndicator, value);
  }

  public void selectDropdownReportingCompanyl(String value) {
    selectDropdown(dropdownReportingCompany, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }
}
