package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class IssuePageLeftNav extends Page {

  public IssuePageLeftNav(WebDriver webDriver) {
    super(webDriver);
  }

  // private final String nodeProcessedErrs =
  // ".//*[@id='form1:viewNavigationMenu:navigationMenuLevel1:"
  private static final By linkPremiumSummary = By.linkText("Premium Summary");
  private static final By linkPolicyActivity = By.linkText("Policy Activity");

  // .xpath(nodeProcessedErrs +
  // "11:navigationMenuItem:menuLinkSubmittable']/span");
  public void clickLinkPremiumSummary() {
    while (!isDisplayed(linkPremiumSummary)) {
      Thread.yield(); // Intentional busy wait loop
    }
    clickObject(linkPremiumSummary);
  }

  public void clickLinkPolicyActivity() {
    while (!isDisplayed(linkPolicyActivity)) {
      Thread.yield(); // Intentional busy wait loop
    }
    clickObject(linkPolicyActivity);
  }
}
