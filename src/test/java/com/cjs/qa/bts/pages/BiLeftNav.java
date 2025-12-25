package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiLeftNav extends Page {

  public BiLeftNav(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By buttonBasicInformation = By.id("basicinfonavitem");
  private static final By buttonNamedInsureds = By.id("NamedInsuredsNamedInsureds");
  private static final By buttonAddresses = By.id("viewAddressId");
  private static final By buttonScheduleOfLocationsAndVehicles =
      By.id("ScheduleofLocationsandVehiclesScheduleofLocationsandVehicles");
  private static final By buttonInsurableInterestSummary =
      By.id("InsurableInterestSummaryInsurableInterestSummary");
  // ***UPDATE***
  private static final By buttonManageProducts =
      By.xpath(".//*[@id='left-sidebar']/div/div[2]/div/ul/li[6]");
  // .id("manageProductsId");
  // private By CWG() { }
  private static final By buttonBusinessowners = By.id("Businessowners500914");
  private static final By buttonForms = By.id("FormsForms");
  private static final By buttonCommissions = By.id("defaultCommissionsId");
  private static final By buttonPremiumModifiers = By.id("PremiumModifiersPremiumModifiers");
  private static final By buttonPremiumSummary = By.id("premiumSummaryId");
  private static final By buttonPolicyActivity = By.id("policyActivityId");
  private static final By buttonNotes = By.id("notesId");
  private static final By buttonPolicyDocuments = By.id("policyDocumentsId");
  private static final By buttonExportExcel = By.xpath(".//*[@id='viewport']/div[2]/div/a/span");
  private static final String pageTitle = "BI_LeftNav";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void clickButtonBasicInformation() {
    clickObject(buttonBasicInformation);
  }

  public void clickButtonNamedInsureds() {
    clickObject(buttonNamedInsureds);
  }

  public void clickButtonAddresses() {
    clickObject(buttonAddresses);
  }

  public void clickButtonExportExcel() {
    clickObject(buttonExportExcel);
  }

  public void clickButtonScheduleOfLocationsAndVehicles() {
    clickObject(buttonScheduleOfLocationsAndVehicles);
  }

  public void clickButtonInsurableInterestSummary() {
    clickObject(buttonInsurableInterestSummary);
  }

  public void clickButtonManageProducts() {
    refresh();
    clickObject(buttonManageProducts);
  }

  public void clickButtonBusinessowners() {
    clickObject(buttonBusinessowners);
  }

  public void clickButtonForms() {
    clickObject(buttonForms);
  }

  public void clickButtonCommissions() {
    clickObject(buttonCommissions);
  }

  public void clickButtonPremiumModifiers() {
    clickObject(buttonPremiumModifiers);
  }

  public void clickButtonPremiumSummary() {
    clickObject(buttonPremiumSummary);
  }

  public void clickButtonPolicyActivity() {
    clickObject(buttonPolicyActivity);
  }

  public void clickButtonNotes() {
    clickObject(buttonNotes);
  }

  public void clickButtonPolicyDocuments() {
    clickObject(buttonPolicyDocuments);
  }
}
