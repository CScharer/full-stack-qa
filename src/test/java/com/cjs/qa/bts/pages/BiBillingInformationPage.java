package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiBillingInformationPage extends Page {

  public BiBillingInformationPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editBillingAccountNumber = By.id("billingAccountNumber");
  private static final By dropdownBillType = By.id("billType");
  private static final By dropdownPayPlan = By.id("payPlan");
  private static final By dropdownAmendmentDistribution = By.id("amendmentDistribution");
  private static final By editDepositAmount = By.id("depositAmount");
  private static final By dropdownDepositType = By.id("depositType");
  private static final By editDepositPercent = By.id("depositPercent");
  private static final By buttonFrame = By.id("billingInformation-title");
  private static final String pageTitle = "BI_BillingInformationPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void setEditBillingAccountNumber(String value) {
    setEdit(editBillingAccountNumber, value);
  }

  public void selectDropdownBillType(String value) {
    selectDropdown(dropdownBillType, value);
  }

  public void selectDropdownPayPlan(String value) {
    selectDropdown(dropdownPayPlan, value);
  }

  public void selectDropdownAmendmentDistribution(String value) {
    selectDropdown(dropdownAmendmentDistribution, value);
  }

  public void setEditDepositAmount(String value) {
    setEdit(editDepositAmount, value);
  }

  public void selectDropdownDepositType(String value) {
    selectDropdown(dropdownDepositType, value);
  }

  public void setEditDepositPercent(String value) {
    setEdit(editDepositPercent, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }
}
