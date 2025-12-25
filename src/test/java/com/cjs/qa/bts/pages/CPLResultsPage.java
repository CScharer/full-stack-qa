package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class CPLResultsPage extends Page {

  public CPLResultsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String pageTitle = "CPLResultsPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  private static final By buttonClose = By.xpath("html/body/div[8]/div[1]/a/span");

  private By buttonOk() {
    if (getWebElement(By.xpath("html/body/div[8]/div[11]/div/button")).isDisplayed()) {
      return By.xpath("html/body/div[8]/div[11]/div/button");
    } else {
      return By.linkText("OK");
    }
    // try {
    // return By.xpath("html/body/div[8]/div[11]/div/button");
    // } catch (Exception e) {
    // return By.linkText("OK");
    // }
  }

  public void clickButtonClose() {
    clickObject(buttonClose);
  }

  public void clickButtonOk() {
    clickObject(buttonOk());
  }
}
