package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class Sidebar extends Page {

  public Sidebar(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By sidebarLeft =
      By.xpath(".//div[@class='ng-scope']//button[@data-sidebar='left']/i");
  private static final By sidebarRight =
      By.xpath(".//div[@class='ng-scope']//button[@data-sidebar='right']/i");

  // private By sidebarRight = By.xpath(".//*[@id='outer-east-open-btn']");

  // METHODS SET
  public void toggleSidebarLeft() {
    clickObject(sidebarLeft);
    // getWebElement(sidebarLeft).click();
    // getWebElement(By.xpath("html/body/div[1]")).sendKeys(Keys.CONTROL,
    // Keys.ARROW_RIGHT);
  }

  public void toggleSidebarRight() {
    clickObject(sidebarRight);
    // getWebElement(sidebarRight).click();
    // getWebElement(By.xpath("html/body/div[1]")).sendKeys(Keys.CONTROL,
    // Keys.ARROW_LEFT);
  }
}
