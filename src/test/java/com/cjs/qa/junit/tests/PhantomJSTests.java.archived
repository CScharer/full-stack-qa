package com.cjs.qa.junit.tests;

import com.cjs.qa.utilities.CommandLineTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PhantomJSTests {
  public static final String DRIVER_PATH = "C:\\Selenium\\Grid2\\Drivers\\";
  public static final String DRIVER_EXE_PHANTOMJS = "phantomjs.exe";
  public static final String DRIVER_PATH_PHANTOMJS = DRIVER_PATH + DRIVER_EXE_PHANTOMJS;
  private WebDriver webDriver;
  private WebElement webElement;
  private By byObject;

  private WebDriver getWebDriver() {
    return webDriver;
  }

  private void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  private WebElement getWebElement() {
    return webElement;
  }

  private void setWebElement(WebElement webElement) {
    this.webElement = webElement;
  }

  private By getByObject() {
    return byObject;
  }

  private void setByObject(By byObject) {
    this.byObject = byObject;
  }

  @Before
  public void beforeTest() throws Exception {
    if (!CommandLineTests.isProcessRunning(DRIVER_EXE_PHANTOMJS)) {
      String command = "cmd \"PhantomJS\" cmd /C \"" + DRIVER_PATH_PHANTOMJS + "\"";
      CommandLineTests.runProcessNoWait(command);
    }
  }

  @After
  public void afterTest() throws Exception {
    if (CommandLineTests.isProcessRunning(DRIVER_EXE_PHANTOMJS)) {
      CommandLineTests.killProcess(DRIVER_EXE_PHANTOMJS);
    }
  }

  @Test
  public void phantomJS() throws Exception {
    System.setProperty("phantomjs.binary.path", DRIVER_PATH_PHANTOMJS);
    // WebDriver webDriver = new PhantomJSDriver(BrowserVersion.CHROME)
    setWebDriver(new PhantomJSDriver());
    getWebDriver().manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
    getWebDriver().get("https://www.google.com");
    for (int counter = 1; counter <= 10; counter++) {
      final String searchString = "Searching for " + counter;
      setByObject(By.name("q"));
      setWebElement(waitExists(getByObject()));
      getWebElement().clear();
      getWebElement().sendKeys(searchString);
      System.out.println("Search Text:[" + getWebElement().getAttribute("value") + "]");
      // setByObject(By.xpath(".//input[@name='btnK']"));
      // setByObject(By.name("btnK"));
      // setWebElement(waitExists(getByObject()));
      // // getWebElement().submit()
      // getWebElement().click();
      getWebElement().sendKeys(Keys.ENTER);
      getWebDriver().navigate().refresh();
    }
    setByObject(By.id("gb_70"));
    setByObject(By.xpath(".//a[.='Sign in']"));
    setWebElement(waitExists(getByObject()));
    getWebElement().click();
    String title = getWebDriver().getTitle();
    System.out.println("title:[" + title + "]");
  }

  protected WebElement waitExists(By by) {
    // May need to update as new webDriverWait returns boolean.
    final WebDriverWait webDriverWait =
        new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(15));
    return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }
}
