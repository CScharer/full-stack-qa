package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class SidbarReferenceMaterialsPage extends Page {

  public SidbarReferenceMaterialsPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By linkTerritoryInformation = By.id("terrInfoLink");
  private static final By linkClientInformation = By.id("clientViewLink");
  private static final By linkPayorDetails = By.id("payorViewLink");

  // METHODS SET
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
