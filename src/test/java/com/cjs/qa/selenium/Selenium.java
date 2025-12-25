package com.cjs.qa.selenium;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.safari.SafariDriver;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class Selenium extends Page implements ISelenium {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Selenium.class));
  private double screenshotCounter = 0;
  private Capabilities capabilities = null;
  private SessionId sessionId = null;

  /**
   * @param webDriver
   */
  public Selenium(WebDriver webDriver) {
    super(webDriver);
  }

  public void getSessionInformation() {
    // Java 17: Pattern matching for instanceof (simplifies type checking and casting)
    if (getWebDriver() instanceof RemoteWebDriver remoteDriver) {
      setCapabilities(remoteDriver.getCapabilities());
      setSessionId(remoteDriver.getSessionId());
    } else if (getWebDriver() instanceof ChromeDriver chromeDriver) {
      setCapabilities(chromeDriver.getCapabilities());
      setSessionId(chromeDriver.getSessionId());
    } else if (getWebDriver() instanceof EdgeDriver edgeDriver) {
      setCapabilities(edgeDriver.getCapabilities());
      setSessionId(edgeDriver.getSessionId());
    } else if (getWebDriver() instanceof FirefoxDriver firefoxDriver) {
      setCapabilities(firefoxDriver.getCapabilities());
      setSessionId(firefoxDriver.getSessionId());
    } else if (getWebDriver() instanceof InternetExplorerDriver ieDriver) {
      setCapabilities(ieDriver.getCapabilities());
      setSessionId(ieDriver.getSessionId());
      // PhantomJS support removed - use Chrome/Firefox headless instead
    } else if (getWebDriver() instanceof HtmlUnitDriver htmlUnitDriver) {
      setCapabilities(htmlUnitDriver.getCapabilities());
      // HtmlUnitDriver doesn't have getSessionId(), using RemoteWebDriver cast as fallback
      if (getWebDriver() instanceof RemoteWebDriver remoteDriver) {
        setSessionId(remoteDriver.getSessionId());
      }
    } else if (getWebDriver() instanceof RemoteWebDriver remoteDriver) {
      setCapabilities(remoteDriver.getCapabilities());
      setSessionId(remoteDriver.getSessionId());
    } else if (getWebDriver() instanceof SafariDriver safariDriver) {
      setCapabilities(safariDriver.getCapabilities());
      setSessionId(safariDriver.getSessionId());
    }
    // Note: OperaDriver support commented out
    // else if (getWebDriver() instanceof OperaDriver) {
    //   setCapabilities(((OperaDriver) webDriver).getCapabilities());
    //   setSessionId(((OperaDriver) webDriver).getSessionId());
    // }
    LOG.debug("{}", toString());
  }

  public double getScreenshotCounter() {
    return screenshotCounter;
  }

  /**
   * @param screenshotCounter
   */
  public void setScreenshotCounter(double screenshotCounter) {
    this.screenshotCounter = screenshotCounter;
  }

  public Capabilities getCapabilities() {
    return capabilities;
  }

  /**
   * @param capabilities
   */
  public void setCapabilities(Capabilities capabilities) {
    this.capabilities = capabilities;
  }

  public SessionId getSessionId() {
    return sessionId;
  }

  /**
   * @param driver
   */
  public void setDriver(WebDriver driver) {
    setWebDriver(driver);
  }

  /**
   * @param sessionId
   */
  public void setSessionId(SessionId sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Selenium WebDriver Information:");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Session ID:[" + getSessionId() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Capabilities:");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Browser:[" + getCapabilities().getBrowserName() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Version:[" + getCapabilities().getBrowserVersion() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Platform:[" + getCapabilities().getPlatformName() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("toString:[" + getCapabilities().toString() + "]");
    return stringBuilder.toString();
  }
}
