package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class RateRightNav extends Page {

  public RateRightNav(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  //   private static final String nodeForm1Admin = "form1:adminFunctions:";
  //   private static final String nodePolicyIssuance = nodeForm1Admin + "policyIssuanceFunctions:";
  //   private static final String nodeNavigationMenu = ":navigationMenuItem:";
  //   private static final String nodeMenuCsubmit = "menuCommandSubmittable";
  private static final By sidebarRight = By.xpath(".//*[@id='outer-east-open-btn']");
  private static final By linkExitPolicySearch = By.xpath(".//*[@id='requestLinkExitPolicy']");
  private static final By linkChangeDates = By.xpath(".//*[@id='requestLinkChangeDates']");
  private static final By linkPolicyErrors = By.xpath(".//*[@id='policyErrorsLink']");
  private static final By linkPolicyViews =
      By.xpath(".//*[@id='" + "form1:adminFunctions:panelGroupButtonForPolicyViews']/a");
  private static final By linkEdit =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:0:navigationMenuItem:menuCommandSubmittable']");
  private static final By linkValidate =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:1:navigationMenuItem:menuCommandSubmittable']");
  private static final By linkRate =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:2:navigationMenuItem:menuCommandSubmittable']");
  private static final By linkBind =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:3:navigationMenuItem:menuCommandSubmittable']");
  private static final By linkIssue =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:4:navigationMenuItem:menuCommandSubmittable']");
  private static final By linkRequestPrint =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:5:navigationMenuItem:menuLinkSubmittable']");
  private static final By linkTerritoryInformation =
      By.xpath(".//*[@id='" + "form1:adminFunctions:terrInfoLink']");
  private static final By linkClientInformation =
      By.xpath(".//*[@id='" + "form1:adminFunctions:clientViewLink']");
  private static final By linkPayorDetails =
      By.xpath(".//*[@id='" + "form1:adminFunctions:payorDetailsLink']");

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
