package com.cjs.qa.utilities;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Selenium;

public final class TestSWD {

  private TestSWD() {
    // Utility class - prevent instantiation
  }

  public static void main(String[] args) {
    WebDriver webDriver = null;
    final Selenium selenium = new Selenium(webDriver);
    final List<String> lBrowsers = Arrays.asList("ie", ISelenium.BROWSER_DEFAULT, "firefox");
    for (final String sBrowser : lBrowsers) {
      webDriver = selenium.browserProfiling(webDriver, sBrowser, true);
      webDriver.get("http://www.google" + IExtension.COM + "/");
      webDriver.navigate().to("https://vivitworldwide.site-ym" + IExtension.COM + "/");
      webDriver.close();
      webDriver.quit();
    }
  }
}
