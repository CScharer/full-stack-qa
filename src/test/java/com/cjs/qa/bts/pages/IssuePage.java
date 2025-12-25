package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class IssuePage extends Page {

  public IssuePage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLERATIONS
  private static final String nodeForm1PrintPolicy = "form1:printPolicyId:";
  private static final String nodeForm1ProcessedResults = "form1:cplProcessedResults:";
  private static final By checkboxPullPrint = By.id(nodeForm1PrintPolicy + "pullPrint");
  private static final By checkboxPrintWorksheets = By.id(nodeForm1PrintPolicy + "printWorksheet");
  private static final By dropdownPrinter = By.id(nodeForm1PrintPolicy + "printer");
  private static final By buttonIssueNow = By.xpath(".//input[@value='Issue Now']");
  // id(nodeForm1ProcessedResults + "flashScreenRateButton");
  private static final By buttonIssueLater = By.id(nodeForm1ProcessedResults + "buttonLater");
  private static final String pageTitle = "IssuePage";

  public String getPageTitle() {
    return pageTitle;
  }

  // METHODS SET
  public void toggleCheckboxPullPrint() {
    clickObject(checkboxPullPrint);
  }

  public void toggleCheckboxPrintWorksheets() {
    clickObject(checkboxPrintWorksheets);
  }

  public String getDropdownPrinter() {
    return getDropdown(dropdownPrinter);
  }

  public void clickButtonIssueNow() {
    clickObject(buttonIssueNow);
    // Wait for Processing Policy pop-up window to disappear
    while (objectExists(By.xpath("//span[contains(text(), 'Processing Policy')]"))) {
      Thread.yield(); // Intentional busy wait loop
    }
  }

  public void clickButtonIssueLater() {
    clickObject(buttonIssueLater);
  }
}
