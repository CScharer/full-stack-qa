package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class SearchRightNav extends Page {

  public SearchRightNav(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By sidebarRight = By.xpath("html/body/div[1]/nav/div/div[1]/div[4]/button");
  private static final By linkExitPolicySearch = By.xpath(".//*[@id='requestLinkSearch1']");
  private static final By linkScheduleProcesses =
      By.xpath(".//*[@id='requestLinkScheduleProcess']");
  private static final By linkWebServices = By.xpath(".//*[@id='requestLinkForWebServices']");
  private static final By linkMassNotes = By.xpath(".//*[@id='requestLinkForMassNotes']");
  private static final By linkViewExportRequests = By.xpath(".//*[@id='requestLinkExportRequest']");
  private static final By linkQueriesTools = By.xpath(".//*[@id='requestLinkQueriesTools']");
  private static final By dropdownTestStatus = By.xpath(".//*[@id='modeBI']");

  // METHODS SET
  public void toggleSidebarRight() {
    clickObject(sidebarRight);
  }

  public void clickLinkExitPolicySearch() {
    clickObject(linkExitPolicySearch);
  }

  public void clickLinkScheduleProcesses() {
    clickObject(linkScheduleProcesses);
  }

  public void clickLinkWebServices() {
    clickObject(linkWebServices);
  }

  public void clickLinkMassNotes() {
    clickObject(linkMassNotes);
  }

  public void clickLinkViewExportRequests() {
    clickObject(linkViewExportRequests);
  }

  public void clickLinkQueriesTools() {
    clickObject(linkQueriesTools);
  }

  public void selectDropdownTestStatus(String value) {
    selectDropdown(dropdownTestStatus, value);
  }
}
