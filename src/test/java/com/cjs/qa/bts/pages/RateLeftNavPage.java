package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class RateLeftNavPage extends Page {

  public RateLeftNavPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String nodeProcessedErrs =
      ".//*[@id='form1:viewNavigationMenu:navigationMenuLevel1:";
  private static final By linkManageProducts =
      By.xpath(nodeProcessedErrs + "5:navigationMenuItem:menuLinkSubmittable']/span");

  // private static final By linkInusrableInterestSummary =
  // By.xpath(nodeProcessedErrs +
  // "4:navigationMenuItem:menuCommandSubmittable']/span")

  public void clickLinkManageProducts() {
    clickObject(linkManageProducts);
  }
}
