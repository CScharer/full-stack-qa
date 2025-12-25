package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class AdminFunctionsPage extends Page {

  public AdminFunctionsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By dropdownTestStatus = By.id("modeBI");
  private static final By linkExitPolicy = By.id("requestLinkSearch1");
  private static final By linkScheduledProcesses = By.id("requestLinkScheduleProcess");
  private static final By linkWebServices = By.id("requestLinkForWebServices");
  private static final By linkMassNotes = By.id("requestLinkForMassNotes");
  private static final By linkViewExportRequests = By.id("requestLinkExportRequest");
  private static final By linkQueriesTools = By.id("requestLinkQueriesTools");
  private static final String pageTitle = "AdminFunctionsPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void clickLinkExitPolicy() {
    clickObject(linkExitPolicy);
  }

  public void clickLinkScheduledProcesses() {
    clickObject(linkScheduledProcesses);
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
