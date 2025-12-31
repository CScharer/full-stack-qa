package com.cjs.qa.selenium;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Email;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.SoftAssert;

public class WebDriverValidation {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(WebDriverValidation.class));

  private Map<String, String> mapURL = new HashMap<>();
  private final WebDriver webDriver;

  public WebDriverValidation(WebDriver webDriver) {
    setMapURL(this.mapURL);
    this.webDriver = webDriver;
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }

  public Map<String, String> getMapURL() {
    return mapURL;
  }

  public void setMapURL(Map<String, String> mapURL) {
    mapURL.put("chrome", "https://sites.google" + IExtension.COM + "/a/chromium.org/chromedriver/");
    mapURL.put(
        "edge",
        "https://developer.microsoft" + IExtension.COM + "/en-us/microsoft-edge/tools/webdriver/");
    mapURL.put("firefox", "https://github" + IExtension.COM + "/mozilla/geckodriver/releases");
    mapURL.put("ie", "https://www.seleniumhq" + IExtension.ORG + "/download/");
    this.mapURL = mapURL;
  }

  public void validate(String browser, SoftAssert softAssert) throws Throwable {
    webDriver.get(getMapURL().get(browser));
    By versionBy;
    List<WebElement> webElements;
    WebElement webElement;
    String driverReleaseExpected = "";
    String driverVersionExpected = "";
    String driverReleaseActual = "";
    String driverVersionActual = "";
    StringBuilder bodyStringBuilder;
    switch (browser.toLowerCase(Locale.ENGLISH)) {
      case "chrome":
        String command =
            "cmd /C wmic datafile where name=\"C:\\Program Files"
                + " (x86)\\Google\\Chrome\\Application\\chrome.exe\" get Version"
                + " /value";
        Map<String, String> mapCommand = CommandLineTests.runProcess(command, true);
        LOG.debug("mapCommand:[{}]", mapCommand.toString());
        // Version 74.0.3729.108 (Official Build) (64-bit)
        // WebDriver has been updated from [ChromeDriver 74.0.3729.6][5.0.3770.90] to
        // [ChromeDriver
        // 75.0.3770.90][4.0.3729.6]
        driverVersionExpected = "ChromeDriver 75.0.3770.90";
        // Supports Chrome version 74
        versionBy =
            By.xpath(
                ".//*[@id='sites-canvas-main-content']//li[contains(text(),'Latest"
                    + " stable release:')]/a");
        webElement = webDriver.findElement(versionBy);
        driverVersionActual = webElement.getText();
        break;
      case "edge":
        // Microsoft Edge 42.17134.1.0
        // Microsoft EdgeHTML 17.17134
        driverReleaseExpected = "Release 75";
        driverVersionExpected =
            "Version: 75.0.137.0 | Microsoft Edge version supported: 75 (x86, x64) |"
                + " License terms | Notices";
        versionBy =
            By.xpath(
                ".//*[@id='downloads']//li[@class='driver-download']/a[contains(text(),'Release')]/..");
        webElements = webDriver.findElements(versionBy);
        for (int indexWebELement = 0; indexWebELement < webElements.size(); indexWebELement++) {
          webElement = webElements.get(indexWebELement);
          if (indexWebELement == 0) {
            webElement = webElements.get(0);
            new Page(webDriver).highlightCurrentElement(webElement);
            final WebElement webElementA = webElement.findElement(By.xpath("./a"));
            driverReleaseActual = webElementA.getText();
            LOG.debug("driverReleaseActual:[{}]", driverReleaseActual);
            bodyStringBuilder = new StringBuilder();
            bodyStringBuilder.append("[" + browser + "] WebDriver Release has been updated from ");
            bodyStringBuilder.append("[" + driverReleaseExpected + "]");
            bodyStringBuilder.append(
                "[" + JavaHelpers.difference(driverReleaseExpected, driverReleaseActual) + "]");
            bodyStringBuilder.append(" to ");
            bodyStringBuilder.append("[" + driverReleaseActual + "]");
            bodyStringBuilder.append(
                "[" + JavaHelpers.difference(driverReleaseActual, driverReleaseExpected) + "]");
            softAssert.assertEquals(
                driverReleaseExpected, driverReleaseActual, bodyStringBuilder.toString());
            final WebElement webElementP = webElement.findElement(By.xpath("./p"));
            driverVersionActual = webElementP.getText();
            LOG.debug("driverVersionActual:[{}]", driverVersionActual);
            bodyStringBuilder = new StringBuilder();
            bodyStringBuilder.append("[" + browser + "] WebDriver has been updated from ");
            bodyStringBuilder.append("[" + driverVersionExpected + "]");
            bodyStringBuilder.append(
                "[" + JavaHelpers.difference(driverVersionExpected, driverVersionActual) + "]");
            bodyStringBuilder.append(" to ");
            bodyStringBuilder.append("[" + driverVersionActual + "]");
            bodyStringBuilder.append(
                "[" + JavaHelpers.difference(driverVersionActual, driverVersionExpected) + "]");
            softAssert.assertEquals(
                driverVersionExpected, driverVersionActual, bodyStringBuilder.toString());
            driverVersionExpected =
                "Release 75"
                    + Constants.NL
                    + "Version: 75.0.137.0 | Microsoft Edge version supported:"
                    + " 75 (x86, x64) | License terms | Notices";
            driverVersionActual = webElement.getText();
            LOG.debug("driverVersionActual:[{}]", driverVersionActual);
            break;
          }
        }
        break;
      case "firefox":
        driverVersionExpected = "v0.24.0";
        versionBy = By.xpath(".//div[@class='release-header']//div/a");
        webElements = webDriver.findElements(versionBy);
        webElement = webElements.get(0);
        driverVersionActual = webElement.getText();
        break;
      case "ie":
        // Version: 11.48.17134.0
        // Update Versions: 11.0.65 (KB4103768)
        // Product ID: 00150-20000-00003-AA459
        driverVersionExpected =
            "Download version 3.14.0 for (recommended) 32 bit Windows IE or 64 bit"
                + " Windows IE"
                + Constants.NL
                + "CHANGELOG";
        versionBy = By.xpath(".//*[@id='mainContent']//a[.='32 bit Windows IE']/..");
        webElements = webDriver.findElements(versionBy);
        webElement = webElements.get(0);
        driverVersionActual = webElement.getText();
        break;
      default:
        break;
    }
    bodyStringBuilder = new StringBuilder();
    bodyStringBuilder.append("[" + browser + "] WebDriver has been updated from ");
    bodyStringBuilder.append("[" + driverVersionExpected + "]");
    bodyStringBuilder.append(
        "[" + JavaHelpers.difference(driverVersionExpected, driverVersionActual) + "]");
    bodyStringBuilder.append(" to ");
    bodyStringBuilder.append("[" + driverVersionActual + "]");
    bodyStringBuilder.append(
        "[" + JavaHelpers.difference(driverVersionActual, driverVersionExpected) + "]");
    if (!driverVersionExpected.equals(driverVersionActual)) {
      final String subject = "WebDriver Updated";
      LOG.debug("{}", bodyStringBuilder.toString());
      Email.sendEmail(
          CJSConstants.EMAIL_ADDRESS_GMAIL,
          EPasswords.EMAIL_GMAIL.getValue(),
          CJSConstants.EMAIL_ADDRESS_GMAIL,
          "",
          "",
          subject,
          bodyStringBuilder.toString(),
          "");
    }
    softAssert.assertEquals(
        driverVersionExpected, driverVersionActual, bodyStringBuilder.toString());
    // Assert.assertEquals("Driver Is Not Current", driverVersionExpected,
    // driverVersionActual);
  }
}
