package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class PolicyIssuancePage extends Page {

  public PolicyIssuancePage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By buttonEdit = By.id("EditEdit");
  private static final By buttonValidate = By.id("ValidateValidate");
  private static final By buttonRate = By.id("RateRate");
  private static final By buttonBind = By.id("BindBind");
  private static final By buttonIssue = By.id("form1:cplProcessedResults:buttonRate");
  private static final By buttonRequestPrint = By.id("requestPrintLink");
  private static final String pageTitle = "PolicyIssuancePage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void clickButtonEdit() {
    clickObject(buttonEdit);
  }

  public void clickButtonValidate() {
    clickObject(buttonValidate);
  }

  public void clickButtonRate() {
    clickObject(buttonRate);
  }

  public void clickButtonBind() {
    clickObject(buttonBind);
  }

  public void clickButtonIssue() {
    clickObject(buttonIssue);
  }

  public void clickButtonRequestPrint() {
    clickObject(buttonRequestPrint);
  }
}
