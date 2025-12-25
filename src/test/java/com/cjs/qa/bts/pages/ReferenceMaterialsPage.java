package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class ReferenceMaterialsPage extends Page {

  public ReferenceMaterialsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By linkTerritoryInformation = By.id("terrInfoLink");
  private static final By linkClientInformation = By.id("clientViewLink");
  private static final By linkPayorDetails = By.id("payorViewLink");
  private static final String pageTitle = "ReferenceMaterialsPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
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
