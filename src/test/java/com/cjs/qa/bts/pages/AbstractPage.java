package com.cjs.qa.bts.pages;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Selenium;
import com.cjs.qa.utilities.GuardedLogger;

public class AbstractPage implements ISelenium {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(AbstractPage.class));
  private WebDriver webDriver;
  private String browser;

  protected WebDriver getWebDriver() {
    return webDriver;
  }

  protected String getBrowser() {
    return browser;
  }

  public AbstractPage(WebDriver webDriver) {
    this.webDriver = webDriver;
    if (this.browser == null) {
      final Selenium selenium = new Selenium(webDriver);
      // Default Browser
      this.browser = BROWSER_DEFAULT;
      this.webDriver = selenium.browserProfiling(this.webDriver, this.browser, true);
      this.getWebDriver().get("http://www.Vivit-Worldwide.org");
    }
  }

  protected void selectDropdown(By findBy, String selection) {
    final Select dropdown = new Select(this.getWebDriver().findElement(findBy));
    dropdown.selectByVisibleText(selection);
  }

  protected void setEdit(By byLocator, String value) {
    LOG.debug("Object: {}", byLocator.toString());
    final WebElement element = getWebDriver().findElement(byLocator);
    final WebDriverWait wait = new WebDriverWait(this.webDriver, java.time.Duration.ofSeconds(5));
    wait.until(ExpectedConditions.elementToBeClickable(element));
    LOG.debug("Displayed: {}", element.isDisplayed());
    LOG.debug("Enabled: {}", element.isEnabled());
    element.clear();
    element.sendKeys(value);
  }

  public void waitHard(int value) {
    getWebDriver().manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(value));
  }

  protected WebElement waitClickable(WebElement element) {
    final WebDriverWait wait = new WebDriverWait(this.webDriver, java.time.Duration.ofSeconds(5));
    return wait.until(ExpectedConditions.elementToBeClickable(element));
  }

  protected void verifyTitle(String value) {
    final String title = this.getWebDriver().getTitle();
    Assertions.assertEquals(value, title);
  }
}
