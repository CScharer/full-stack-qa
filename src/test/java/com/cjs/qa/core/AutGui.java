package com.cjs.qa.core;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.utilities.SoftAssert;

public class AutGui {

  private SoftAssert softAssert;

  public SoftAssert getSoftAssert() {
    return softAssert;
  }

  public AutGui(WebDriver webDriver) {
    softAssert = new SoftAssert();
    webDriver
        .manage()
        .timeouts()
        .pageLoadTimeout(java.time.Duration.ofSeconds(Environment.getTimeOutPage()));
  }

  public static String updateSQL(String sql) {
    return sql;
  }
}
