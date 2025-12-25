package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiBillingPage extends Page {

  public BiBillingPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By numberOfGroupMembers = By.id("NUM_GRP_MBR");
  private static final By buttonFrame = By.id("billing-title");
  private static final String pageTitle = "BI_BillingPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void enterNumberOfGroupMembers(String value) {
    setEdit(numberOfGroupMembers, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }
}
