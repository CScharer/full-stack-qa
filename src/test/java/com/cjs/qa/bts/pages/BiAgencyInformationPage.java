package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiAgencyInformationPage extends Page {

  public BiAgencyInformationPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By dropdownBranch = By.id("branch");
  private static final By dropdownUnderwriter = By.id("underwriter");
  private static final By editAgencyCode = By.id("agencyCode");
  private static final By editAgencyName = By.id("agencyName");
  private static final By buttonSearchAgencyName = By.id("agencyName-lookup");
  private static final By editProducerCode = By.id("producerCode");
  private static final By dropdownProducerName = By.id("producerName");
  private static final By buttonFrame = By.id("agencyInformation-title");
  private static final String pageTitle = "BI_AgencyInformationPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void selectDropdownBranch(String value) {
    selectDropdown(dropdownBranch, value);
  }

  public void selectDropdownUnderwriter(String value) {
    selectDropdown(dropdownUnderwriter, value);
  }

  public void setEditAgencyCode(String value) {
    setEdit(editAgencyCode, value);
  }

  public void setEditAgencyName(String value) {
    setEdit(editAgencyName, value);
  }

  public void selectDropdownProducerName(String value) {
    selectDropdown(dropdownProducerName, value);
  }

  public void clickButtonSearchAgencyNameButton() {
    clickObject(buttonSearchAgencyName);
  }

  public void setEditProducerCode(String value) {
    // N/A Read-Only
    setEdit(editProducerCode, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }
}
