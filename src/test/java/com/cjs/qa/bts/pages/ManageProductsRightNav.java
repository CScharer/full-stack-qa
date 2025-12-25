package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class ManageProductsRightNav extends Page {

  public ManageProductsRightNav(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By sidebarRight = By.xpath(".//*[@id='outer-east-open-btn']");
  private static final By linkExitPolicySearch = By.xpath(".//*[@id='requestLinkSearch1']");
  private static final By linkChangeDates = By.xpath(".//*[@id='changeDateLink']");
  private static final By linkPolicyErrors = By.xpath(".//*[@id='policyErrorsLink']");
  private static final By linkPolicyViews = By.xpath(".//*[@id='policyViewsLink']");
  private static final By linkEdit = By.xpath(".//*[@id='EditEdit']");
  private static final By linkValidate = By.xpath(".//*[@id='ValidateValidate']");
  private static final By linkRate = By.xpath(".//*[@id='RateRate']");
  private static final By linkBind = By.xpath(".//*[@id='BindBind']");
  private static final By linkIssue = By.xpath(".//*[@id='IssueIssue']");
  private static final By linkRequestPrint = By.xpath(".//*[@id='requestPrintLink']");
  private static final By linkTerritoryInformation = By.xpath(".//*[@id='terrInfoLink']");
  private static final By linkClientInformation = By.xpath(".//*[@id='clientViewLink']");
  private static final By linkPayorDetails = By.xpath(".//*[@id='payorViewLink']");

  // METHODS SET
  public void toggleSidebarRight() {
    clickObject(sidebarRight);
  }

  public void clickLinkExitPolicySearch() {
    clickObject(linkExitPolicySearch);
  }

  public void clickLinkChangeDates() {
    clickObject(linkChangeDates);
  }

  public void clickLinkPolicyErrors() {
    clickObject(linkPolicyErrors);
  }

  public void clickLinkPolicyViews() {
    clickObject(linkPolicyViews);
  }

  public void clickLinkEdit() {
    clickObject(linkEdit);
  }

  public void clickLinkValidate() {
    clickObject(linkValidate);
  }

  public void clickLinkRate() {
    clickObject(linkRate);
  }

  public void clickLinkBind() {
    clickObject(linkBind);
  }

  public void clickLinkIssue() {
    clickObject(linkIssue);
  }

  public void clickLinkRequestPrint() {
    clickObject(linkRequestPrint);
  }

  public void clickLinkTerritoryInformation() {
    clickObject(linkTerritoryInformation);
  }

  public void clickLinkClientInformation() {
    clickObject(linkClientInformation);
  }

  public void clickLinkPayorDetails() {
    clickObject(linkPayorDetails);
  }
}
