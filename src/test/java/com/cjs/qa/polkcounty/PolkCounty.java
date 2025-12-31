package com.cjs.qa.polkcounty;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.polkcounty.pages.Details;
import com.cjs.qa.polkcounty.pages.Main;

public class PolkCounty {

  private Main main;
  private Details details;

  public PolkCounty(WebDriver webDriver) {
    main = new Main(webDriver);
    details = new Details(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }

  public Main getMain() {
    return main;
  }

  public Details getDetails() {
    return details;
  }
}
