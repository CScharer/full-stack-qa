package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class FormsRightNav extends Page {

  public FormsRightNav(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  //   private static final String nodeForm1Admin = "form1:adminFunctions:";
  //   private static final String nodeNavigationMenu = ":navigationMenuItem:";
  private static final By sidebarRightOpen = By.xpath(".//*[@id='outer-east-open-btn']");
  private static final By sidebarRightClose = By.xpath(".//*[@'outer-east-close-btn']");
  private static final By linkExitPolicySearch = By.xpath(".//*[@id='requestLinkExitPolicy']");
  private static final By linkPolicyErrors = By.xpath(".//*[@id='policyErrorsLink']");
  private static final By linkPolicyViews =
      By.xpath(".//*[@id='form1:adminFunctions:panelGroupButtonForPolicyViews']/a");
  private static final By linkRequestPrint =
      By.xpath(".//*[@id='form1:adminFunctions:0:navigationMenuItem:menuLinkSubmittable']");
  private static final By linkTerritoryInformation =
      By.xpath(".//*[@id='form1:adminFunctions:terrInfoLink']");
  private static final By linkClientInformation =
      By.xpath(".//*[@id='form1:adminFunctions:clientViewLink']");
  private static final By linkPayorDetails =
      By.xpath(".//*[@id='form1:adminFunctions:payorDetailsLink']");
  private static final By linkRate =
      By.xpath(
          ".//*[@id='form1:adminFunctions:policyIssuanceFunctions:2:navigationMenuItem:menuCommandSubmittable']");

  // METHODS SET
  public boolean isSidebarOpen() {
    return getWebElement(sidebarRightClose) != null;
  }

  public void toggleSidebarRight() {
    // sidebarIsOpen = isSidebarOpen();
    // if (sidebarIsOpen)
    // {
    // clickObject(sidebarRightClose);
    // } else
    // {
    clickObject(sidebarRightOpen);
    // }
  }

  public void clickLinkExitPolicySearch() {
    clickObject(linkExitPolicySearch);
  }

  public void clickLinkPolicyErrors() {
    clickObject(linkPolicyErrors);
  }

  public void clickLinkPolicyViews() {
    clickObject(linkPolicyViews);
  }

  public void clickLinkRequestPrint() {
    clickObject(linkRequestPrint);
  }

  public void clickLinkRate() {
    clickObject(linkRate);
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
