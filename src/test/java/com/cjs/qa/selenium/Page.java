package com.cjs.qa.selenium;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import javax.swing.JEditorPane;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

import io.cucumber.java.Scenario;

/**
 * @author CHRIS
 */
public class Page extends JavaHelpers {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Page.class));
  // May need to update as new webDriverWait returns boolean.
  private String currentUser;
  public static final String PATH_DELIMETER = "//";
  public static final String LABEL_THE_VALUE = "The value [";
  public static final String LABEL_OPTION_CHECKED = "checked";
  public static final String LABEL_OPTION_UNCHECKED = "unchecked";
  // red;deeppink;fuchsia
  public static final String BORDER_COLOR = "deeppink";
  private static final String scriptGetElementBorder =
      "var element = arguments[0];if (element.currentStyle) {var style ="
          + " element.currentStyle;var border = style['borderTopWidth'] + ' ' +"
          + " style['borderTopStyle'] + ' ' + style['borderTopColor'] + ';' +"
          + " style['borderRightWidth'] + ' ' + style['borderRightStyle'] + ' ' +"
          + " style['borderRightColor'] + ';' + style['borderBottomWidth'] + ' ' +"
          + " style['borderBottomStyle'] + ' ' + style['borderBottomColor'] + ';' +"
          + " style['borderLeftWidth'] + ' ' + style['borderLeftStyle'] + ' ' +"
          + " style['borderLeftColor'];} else if (window.getComputedStyle) {var style ="
          + " document.defaultView.getComputedStyle(element);var border ="
          + " style.getPropertyValue('border-top-width') + ' ' +"
          + " style.getPropertyValue('border-top-style') + ' ' +"
          + " style.getPropertyValue('border-top-color') + ';' +"
          + " style.getPropertyValue('border-right-width') + ' ' +"
          + " style.getPropertyValue('border-right-style') + ' ' +"
          + " style.getPropertyValue('border-right-color') + ';' +"
          + " style.getPropertyValue('border-bottom-width') + ' ' +"
          + " style.getPropertyValue('border-bottom-style') + ' ' +"
          + " style.getPropertyValue('border-bottom-color') + ';' +"
          + " style.getPropertyValue('border-left-width') + ' ' +"
          + " style.getPropertyValue('border-left-style') + ' ' +"
          + " style.getPropertyValue('border-left-color');}element.style.border = '4px solid "
          + BORDER_COLOR
          + "';"
          + "return border;";
  private static final String scriptUnhighlightElement =
      "var element = arguments[0];"
          + "var borders = arguments[1].split(';');"
          + "element.style.borderTop = borders[0];"
          + "element.style.borderRight = borders[1];"
          + "element.style.borderBottom = borders[2];"
          + "element.style.borderLeft = borders[3];";
  // public final boolean SCROLL_TO_OBJECT = false
  private static final String FORMAT_SCREENSHOT_NUMBER = "000000";
  private static final int flashMilliseconds = 25;
  private WebElement previousWebElement = null;
  private String previousBorder = null;
  private int timeoutElement = Environment.getTimeOutElement();
  private boolean logAll = Environment.isLogAll();
  private WebDriver webDriver;
  private int screenshotCounter = 1;
  private SeleniumWebDriverEventListener seleniumWebDriverEventListener =
      new SeleniumWebDriverEventListener();

  /**
   * @param webDriver
   */
  public Page(WebDriver webDriver) {
    setWebDriver(webDriver);
    EventFiringDecorator<WebDriver> eventFiringWebDriver =
        new EventFiringDecorator<>(seleniumWebDriverEventListener);
    setWebDriver(eventFiringWebDriver.decorate(getWebDriver()));
  }

  private String getScriptGetElementBorder() {
    return scriptGetElementBorder;
  }

  private String getScriptUnhighlightElement() {
    return scriptUnhighlightElement;
  }

  private String getFormatScreenshotNumber() {
    return FORMAT_SCREENSHOT_NUMBER;
  }

  public int getFlashMilliseconds() {
    return flashMilliseconds;
  }

  public int getTimeoutElement() {
    return timeoutElement;
  }

  private boolean isLogAll() {
    return logAll;
  }

  public WebElement getPreviousWebElement() {
    return previousWebElement;
  }

  public void setPreviousWebElement(WebElement previousWebElement) {
    this.previousWebElement = previousWebElement;
  }

  public String getPreviousBorder() {
    return previousBorder;
  }

  public void setPreviousBorder(String previousBorder) {
    this.previousBorder = previousBorder;
  }

  /**
   * @param flashMilliseconds
   * @return
   */
  public boolean flashCurrentElement(WebElement webElement, int flashMilliseconds) {
    if (!Environment.isFlashObjects()) {
      return true;
    }
    boolean success = highlightCurrentElement(webElement);
    if (!success) {
      return success;
    }
    sleep(0, flashMilliseconds);
    success = unhighlightPreviousElement();
    sleep(0, flashMilliseconds);
    return success;
  }

  /**
   * @param webElement
   * @param flashMilliseconds
   * @return
   */
  public boolean flashCurrentElement(String xPath, int flashMilliseconds) {
    WebElement webElement = waitExists(By.xpath(xPath));
    return flashCurrentElement(webElement, flashMilliseconds);
  }

  /**
   * @return
   */
  public String getStackInfo() {
    final List<String> lClassExclusions =
        Arrays.asList(
            "java.lang.Thread",
            "com.cjs.framework.Core.Page",
            "com.cjs.framework.Core.AutGui",
            "com.cjs.framework.Core.AppCoreObject");
    String stack = null;
    final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    // final StackTraceElement stackTraceElement = stackTraceElements[4]
    for (final StackTraceElement stackTraceElement : stackTraceElements) {
      // stack = stackTraceElement.getClassName() + Constants.TAB +
      // stackTraceElement.getFileName() + Constants.TAB +
      // stackTraceElement.getMethodName() + Constants.TAB +
      // "Line (" + stackTraceElement.getLineNumber()+ ")"
      final String className = stackTraceElement.getClassName();
      if (!lClassExclusions.contains(className)) {
        stack =
            stackTraceElement.getFileName()
                + Constants.TAB
                + stackTraceElement.getMethodName()
                + Constants.TAB
                + "Line ("
                + stackTraceElement.getLineNumber()
                + ")";
        break;
      }
    }
    return stack;
  }

  public String getUserName() {
    return currentUser;
  }

  /**
   * @param webDriver
   * @param webElement
   * @return
   */
  public boolean highlightCurrentElement(WebElement webElement) {
    if (Environment.isFlashObjects()) {
      unhighlightPreviousElement();
    }
    try {
      // remember the new webElement
      setPreviousWebElement(webElement);
      // setPreviousBorder((String)
      // (executeJavaScript(getScriptGetElementBorder(), webElement)))
      // Java 17: Pattern matching for instanceof
      if (getWebDriver() instanceof JavascriptExecutor) {
        setPreviousBorder((String) executeJavaScript(getScriptGetElementBorder(), webElement));
      }
      return true;
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * @param webElement
   * @return
   */
  public boolean highlightCurrentElement(String xPath) {
    WebElement webElement = waitExists(By.xpath(xPath));
    return highlightCurrentElement(webElement);
  }

  public void highlightCurrentElementBriefly(WebElement webElement, int milliseconds) {
    highlightCurrentElement(webElement);
    sleep(0, milliseconds);
    unhighlightPreviousElement();
  }

  /**
   * @param user
   */
  public void setCurrentUserName(String user) {
    currentUser = user;
  }

  /**
   * @param webDriver
   * @return
   */
  public boolean unhighlightPreviousElement() {
    boolean success = false;
    if (getPreviousWebElement() != null) {
      try {
        // if there already is a highlighted element, un-highlight it
        // executeJavaScript(getScriptUnhighlightElement(),
        // getPreviousWebElement(), getPreviousBorder())
        // Java 17: Pattern matching for instanceof
        if (webDriver instanceof JavascriptExecutor) {
          executeJavaScript(
              getScriptUnhighlightElement(), getPreviousWebElement(), getPreviousBorder());
          success = true;
        }
      } catch (final StaleElementReferenceException ignored) {
        // the page got reloaded, the element isn't there
      } finally {
        // element either restored or wasn't valid, nullify in both
        // cases
        setPreviousWebElement(null);
      }
    }
    return success;
  }

  public void alertButtonPopupClickAccept() {
    final Alert alert = getWebDriver().switchTo().alert();
    alert.accept();
  }

  public void alertButtonPopupClickDismiss() {
    final Alert alert = getWebDriver().switchTo().alert();
    alert.dismiss();
  }

  /**
   * @return
   */
  public boolean alertIsPresent() {
    try {
      getWebDriver().switchTo().alert();
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  public void back() {
    seleniumWebDriverEventListener.beforeNavigateBack(getWebDriver());
    getWebDriver().navigate().back();
    seleniumWebDriverEventListener.afterNavigateBack(getWebDriver());
  }

  /**
   * Uses Selenium to create a screenshot. Captures the entire webpage even if it is not entirely
   * visible.
   *
   * @param pathToSaveScreenShot
   */
  public void captureScreenshot() {
    try {
      FileUtils.forceMkdir(new File(Constants.PATH_SCREENSHOTS));
    } catch (final IOException e) {
      LOG.error("Failed to create screenshots directory", e);
    }
    StringBuilder stringBuilder = new StringBuilder(Constants.PATH_SCREENSHOTS);
    setScreenshotCounter(getScreenshotCounter() + 1);
    String dateTimeStamp = DateHelpersTests.getCurrentDateTimeStamp();
    stringBuilder.append(dateTimeStamp);
    stringBuilder.append("_");
    String title = getTitle();
    title = FSOTests.fileValidateName(title);
    stringBuilder.append(title);
    stringBuilder.append("_");
    stringBuilder.append(formatNumber(getScreenshotCounter(), getFormatScreenshotNumber()));
    stringBuilder.append(".png");
    String filePathName = stringBuilder.toString();
    LOG.debug("Screenshot file name: [{}]", filePathName);
    final File screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(screenshot, new File(filePathName));
    } catch (final IOException e) {
      LOG.error("Failed to copy screenshot to file", e);
    }
  }

  /**
   * Uses Selenium to create a screenshot. Captures the entire webpage even if it is not entirely
   * visible.
   *
   * @param pathFileName
   */
  public void captureScreenshot(String pathFileName) {
    final File screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(screenshot, new File(pathFileName));
    } catch (final IOException e) {
      LOG.error("Failed to copy screenshot to path: {}", pathFileName, e);
    }
  }

  public void clearCookies() {
    // Set<Cookie> cookies = getWebDriver().manage().getCookies()
    // LOG.debug("cookies = [{}]", cookies);
    // int cookiesSize = cookies.size()
    // LOG.debug("Number of cookies in this site [{}]", cookiesSize);
    // for (Cookie cookie : cookies) {
    // LOG.debug("Cookie: {} = {}", cookie.getName(), cookie.getValue());
    // This will delete cookie By Name
    // getWebDriver().manage().deleteCookieNamed(cookie.getName())
    // This will delete the cookie
    // getWebDriver().manage().deleteCookie(cookie)
    // }
    // cookiesSize = cookies.size()
    // LOG.debug("Number of cookies in this site [{}]", cookiesSize);
    getWebDriver().manage().deleteCookieNamed("Local Storage");
    getWebDriver().manage().deleteAllCookies();
  }

  public void clickAlertCancel() {
    waitForAlert();
    final Alert alert = getWebDriver().switchTo().alert();
    alert.dismiss();
  }

  public void clickAlertCancelIfPresent() {
    if (alertIsPresent()) {
      clickAlertCancel();
    }
  }

  public void clickAlertOK() {
    waitForAlert();
    final Alert alert = getWebDriver().switchTo().alert();
    alert.accept();
  }

  public void clickAlertOKIfPresent() {
    if (alertIsPresent()) {
      clickAlertOK();
    }
  }

  /**
   * @param linkText
   */
  public void clickLink(String linkText) {
    clickObject(By.linkText(linkText));
  }

  /**
   * @param linkText
   */
  public void clickLinkPartial(String linkText) {
    clickObject(By.partialLinkText(linkText));
  }

  /**
   * @param by
   */
  protected void clickMethodsOther(By by) {
    // WebElement webElement = waitExists(by)
    // Method Primary
    // webElement.click()
    // // Method Secondary
    final Actions actions = new Actions(getWebDriver());
    WebElement webElement =
        getWebDriver()
            .findElement(By.xpath(".//div[@class='ng-scope']//button[@data-sidebar='right']/i"));
    actions.click(webElement).build().perform();
    //
    // String script = ""
    // // Method Secondary
    // script = "arguments[0].click();"
    // executeJavaScript(script, webElement)
    // // Method Secondary
    // script = "$('#elementID').click();"
    // executeJavaScript(script)
    // // Method Secondary
    // script = "document.getElementById('elementID').click();"
    // executeJavaScript(script)
  }

  /**
   * @param by
   */
  protected void clickObject(By by) {
    // clickMethodsOther(By)
    final WebElement webElement = getWebElement(by);
    clickObject(webElement);
  }

  /**
   * @param webElement
   */
  protected void clickObject(WebElement webElement) {
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), "click");
    }
    seleniumWebDriverEventListener.beforeClickOn(webElement, getWebDriver());
    final Actions actions = new Actions(getWebDriver());
    actions.moveToElement(webElement).click().perform();
    // LOG.debug("Clicked the [{}]", webElement.toString() +
    // "]Object."))
    seleniumWebDriverEventListener.afterClickOn(webElement, getWebDriver());
  }

  /**
   * @param by
   */
  protected void clickObjectPopup(By by) {
    WebElement webElement = null;
    boolean buttonExists = false;
    final long startTime = System.currentTimeMillis();
    long elapsedTime = System.currentTimeMillis() - startTime;
    do {
      try {
        elapsedTime = System.currentTimeMillis() - startTime;
        webElement = getWebElement(by);
        buttonExists = true;
      } catch (final Exception e) {
        // Intentionally empty - retry loop continues until timeout
        if (Environment.isLogAll()) {
          LOG.debug("Element not found yet, retrying...");
        }
      }
    } while (elapsedTime <= Environment.noPMDUselessParentheses(getTimeoutElement() / 6) * 1000
        && !buttonExists);
    // Only wait five seconds as these are supposed to be pop-ups
    if (buttonExists) {
      clickObject(webElement);
    }
  }

  public void closeDriver() {
    // getWebDriver().close()
    getWebDriver().quit();
  }

  public void closeFirstTab() throws QAException {
    final List<String> listWindows = new ArrayList<>(getOpenWindows());
    if (listWindows.isEmpty()) {
      throw new QAException("Popup window did not open");
    }
    switchToWindow(listWindows.get(0));
    getWebDriver().close();
    switchToWindow(listWindows.get(1));
  }

  public void closePDFPopUp() {
    getWebDriver()
        .findElement(
            By.xpath("//div[@class = 'popupContent']//div[@class =" + " 'v-window-closebox']"))
        .click();
  }

  /**
   * Closes a specified popup by switching the driver to the pop up and closing it
   *
   * @param windowTitle
   */
  public void closePopup(String windowTitle) {
    final String parent = getWebDriver().getWindowHandle();
    switchToPopup(windowTitle);
    if (!getWebDriver().getWindowHandle().equals(parent)) {
      getWebDriver().findElement(By.id("close")).click();
      sleep(1);
    }
    getWebDriver().switchTo().window(parent);
  }

  public void closeWindow() {
    getWebDriver().close();
  }

  /**
   * @param webElementSource
   * @param webElementTarget
   */
  protected void dragAndDrop(WebElement webElementSource, WebElement webElementTarget) {
    new Actions(getWebDriver()).dragAndDrop(webElementSource, webElementTarget).perform();
  }

  /**
   * @param code
   * @return
   */
  public Object executeJavaScript(String script) {
    return executeJavaScript(script, "");
  }

  /**
   * @param code
   * @param arg
   * @return
   */
  public Object executeJavaScript(String script, Object arguments) {
    // LOG.debug("Executing JavaScript script:[{}], arguments:[{}]", script, arguments.toString());
    seleniumWebDriverEventListener.beforeScript(script, getWebDriver());
    Object object = getJavascriptExecutor().executeScript(script, arguments);
    seleniumWebDriverEventListener.afterScript(script, getWebDriver());
    return object;
  }

  /**
   * @param script
   * @param arguments
   * @param value
   * @return
   */
  public Object executeJavaScript(String script, Object arguments, String value) {
    // LOG.debug("Executing JavaScript script:[{}], arguments:[{}], value:[{}]", script,
    // arguments.toString(), value);
    return getJavascriptExecutor().executeScript(script, arguments, value);
  }

  /**
   * @param by
   * @param timeOut
   * @return
   */
  public WebElement findDynamicElement(By by, long timeOut) {
    final WebDriverWait webDriverWait =
        new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(timeOut));
    return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  /**
   * @param webElement
   */
  protected void focusOnElement(WebElement webElement) {
    if ("input".equals(webElement.getTagName())) {
      webElement.sendKeys("");
    } else {
      new Actions(getWebDriver()).moveToElement(webElement).perform();
    }
  }

  public void focusWindow() {
    executeJavaScript("window.focus();");
  }

  public WebElement getActiveWebElement() {
    return getWebDriver().switchTo().activeElement();
  }

  public String getAlertText() {
    waitForAlert();
    final Alert alert = getWebDriver().switchTo().alert();
    sleep(1);
    return alert.getText();
  }

  public List<String> getAllFields() {
    final String[] sObjectList = "a,input:text;password;Checkbox,button,select".split(",");
    final List<String> listFields = new ArrayList<>();
    for (final String sObject : sObjectList) {
      String xpath = null;
      if (sObject.indexOf(':') == 0) {
        xpath = PATH_DELIMETER + sObject;
        listFields.add("XPATH:" + xpath);
        listFields.addAll(getAllFieldsType(xpath));
      } else {
        final String objectSubType = sObject.substring(0, sObject.indexOf(':'));
        final String objectSubTypes = sObject.substring(sObject.indexOf(':') + 1);
        final String[] objectTypes = objectSubTypes.split(Constants.DELIMETER_LIST);
        for (String objectType : objectTypes) {
          xpath = PATH_DELIMETER + objectSubType + "[@type='" + objectType + "']";
          listFields.add("XPATH:" + xpath);
          listFields.addAll(getAllFieldsType(xpath));
        }
      }
    }
    return listFields;
  }

  /**
   * @param xpath
   * @return
   */
  public List<String> getAllFieldsType(String xpath) {
    final List<String> fieldList = new ArrayList<>();
    final List<WebElement> webElementList = getWebDriver().findElements(By.xpath(xpath));
    switch (xpath) {
      case "//a":
      case "//button":
        fieldList.addAll(toString(webElementList));
        break;
      default:
        for (final WebElement webElement : webElementList) {
          fieldList.add(webElement.getAttribute("name"));
        }
        break;
    }
    fieldList.add(Constants.NL);
    return fieldList;
  }

  /**
   * @param webElement
   * @return
   */
  public List<String> getBoundedRectangleOfElement(WebElement webElement) {
    final String script =
        "var rect = arguments[0].getBoundingClientRect();return [ '' + parseInt(rect.left),"
            + " '' + parseInt(rect.top), '' + parseInt(rect.width), '' +"
            + " parseInt(rect.height) ]";
    // JavaScript returns Object, must cast to List<String> - type safety verified at runtime
    @SuppressWarnings("unchecked")
    final List<String> bounds = (List<String>) executeJavaScript(script, webElement);
    if (isLogAll()) {
      logFieldNameTagNameValue(webElement.toString(), webElement.getTagName(), bounds.toString());
    }
    return bounds;
  }

  /**
   * @param by
   * @return
   */
  protected String getCheckbox(By by) {
    String value;
    if (getWebElement(by).getAttribute(LABEL_OPTION_CHECKED) == null) {
      value = LABEL_OPTION_UNCHECKED;
    } else {
      value = LABEL_OPTION_CHECKED;
    }
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), value);
    }
    return value;
  }

  /**
   * @param webElement
   * @return
   */
  protected String getCheckbox(WebElement webElement) {
    String value;
    if (webElement.getAttribute(LABEL_OPTION_CHECKED) == null) {
      value = LABEL_OPTION_UNCHECKED;
    } else {
      value = LABEL_OPTION_CHECKED;
    }
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), value);
    }
    return value;
  }

  /**
   * @param webElement
   * @return
   */
  protected String getComboBoxSelectedText(WebElement webElement) {
    final Select select = new Select(webElement);
    final List<WebElement> webElements = select.getAllSelectedOptions();
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = 0; index < webElements.size(); index++) {
      stringBuilder.append(webElements.get(index).getText());
      if (index < (webElements.size() - 1)) {
        stringBuilder.append(Constants.NL);
      }
    }
    if ("".equals(stringBuilder.toString())) {
      stringBuilder = new StringBuilder(CJSConstants.EMPTY);
    }
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), stringBuilder.toString());
    }
    return stringBuilder.toString();
  }

  public String getCookie() {
    return (String) executeJavaScript("return document.cookie");
  }

  /**
   * @param cookie
   * @return
   */
  public String getCookie(String cookie) {
    String cookieValue = null;
    // return
    // getWebDriver().manage().getCookieNamed("authenticationToken").getValue()
    try {
      cookieValue = getWebDriver().manage().getCookieNamed(cookie).getValue();
    } catch (final Exception e) {
      LOG.error("Failed to get cookie: {}", cookie, e);
    }
    return cookieValue;
  }

  /**
   * @param by
   * @return
   */
  protected String getDropdown(By by) {
    final WebElement webElement = waitExists(by);
    final Select select = new Select(webElement);
    final String value = select.getFirstSelectedOption().getText();
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), value);
    }
    return value;
  }

  /**
   * @param wildCard
   * @return
   */
  public String getDynamicID(String wildCard) {
    // This needs to be rewritten to use jsoup HTML parser, see
    // getSourceLink()
    final String source = getWebDriver().getPageSource();
    final String[] arIDs = source.split("id=" + Constants.QUOTE_DOUBLE);
    String idString = "";
    String id = "";
    String returnID = "";
    for (String currentIdString : arIDs) {
      idString = currentIdString;
      id = idString.substring(0, idString.indexOf(Constants.QUOTE_DOUBLE));
      // LOG.debug("{}", sID);
      if (id.contains(wildCard)) {
        // LOG.debug("The element ID you're looking for is: [{}]", sID);
        returnID = id;
        return returnID;
      }
    }
    return returnID;
  }

  /**
   * @param by
   * @return
   */
  protected String getEdit(By by) {
    final WebElement webElement = getWebElement(by);
    String value;
    value = webElement.getAttribute("value");
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), value);
    }
    return value;
  }

  /**
   * @param by
   * @return
   */
  public List<WebElement> getElements(By by) {
    return getWebDriver().findElements(by);
  }

  /**
   * @param by
   * @return
   */
  public List<String> getElementsText(By by) {
    final List<String> textList = new ArrayList<>();
    final List<WebElement> webElementList = getWebDriver().findElements(by);
    for (final WebElement webElement : webElementList) {
      textList.add(webElement.getText());
    }
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), textList.toString());
    }
    return textList;
  }

  public int getHeight() {
    return getWebDriver().manage().window().getSize().getHeight();
  }

  /**
   * @param webElement
   * @return
   */
  protected String getInputFieldText(WebElement webElement) {
    String value = webElement.getText();
    if ("".equals(value)) {
      value = CJSConstants.EMPTY;
    }
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), value);
    }
    return value;
  }

  public JavascriptExecutor getJavascriptExecutor() {
    return (JavascriptExecutor) getWebDriver();
  }

  /**
   * @param by
   * @return
   */
  protected String getLabel(By by) {
    final WebElement webElement = waitExists(by);
    final String value = webElement.getText();
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), value);
    }
    return value;
  }

  public Set<String> getOpenWindows() {
    return getWebDriver().getWindowHandles();
  }

  /**
   * @param by
   * @return
   */
  protected String getOption(By by) {
    final WebElement grandParent = waitExists(by);
    final List<WebElement> webElementParentList = grandParent.findElements(By.xpath(".//input"));
    for (final WebElement webElementParent : webElementParentList) {
      if (webElementParent.isSelected()) {
        final WebElement webElementChild = webElementParent.findElement(By.xpath("./../../label"));
        return webElementChild.getText();
      }
    }
    return null;
  }

  public String getPageText() {
    return getWebDriver().findElement(By.tagName("body")).getText();
  }

  private int getScreenshotCounter() {
    return screenshotCounter;
  }

  public int getScrollHeight() {
    // String script = "return document.body.scrollHeight"
    String script = "return document.documentElement.scrollHeight";
    final String height = executeJavaScript(script).toString();
    return Integer.valueOf(height);
  }

  /**
   * @param linkName
   * @return
   */
  public String getSourceLink(String linkName) {
    final String source = getWebDriver().getPageSource();
    final Document document = Jsoup.parse(source);
    // Convert JSoup Elements to List<Element> to use interface instead of implementation
    final List<Element> elementList = new ArrayList<>(document.select("a[href]"));
    String linkURL = null;
    for (final Element element : elementList) {
      if (linkName.equals(element.text())) {
        linkURL = element.attr("href");
        break;
      }
    }
    return linkURL;
  }

  /**
   * @param start
   * @param finish
   * @return
   */
  public String getTabOrderFrom(String start, String finish) {
    final StringBuilder stringBuilderFields = new StringBuilder();
    String name = "";
    getWebDriver().findElement(By.xpath("//*[@id='" + start + "']")).sendKeys("");
    while (!name.equals(finish)) {
      final WebElement currentElement = getActiveWebElement();
      name = currentElement.getAttribute("id");
      if ("".equals(name)) {
        name = currentElement.getAttribute("name");
      }
      if (!name.equals(finish)) {
        stringBuilderFields.append(name + Constants.NL);
        currentElement.sendKeys(Constants.TAB);
      }
    }
    String tabOrder = stringBuilderFields.toString();
    LOG.debug("Tab Order: [{}]", tabOrder);
    if (tabOrder.indexOf(Constants.NL) > 0) {
      tabOrder = tabOrder.substring(0, tabOrder.length() - Constants.NL.length());
    }
    return tabOrder;
  }

  /**
   * @param id
   * @return
   */
  public String getTextAlignByID(String id) {
    return getWebDriver().findElement(By.id(id)).getCssValue("text-align");
  }

  /**
   * @param text
   * @return
   */
  public String getTextAlignByText(String text) {
    return getWebDriver()
        .findElement(By.xpath("//*[text()='" + text + "']"))
        .getCssValue("text-align");
  }

  /**
   * @param text
   * @return
   */
  public String getTextColor(String text) {
    try {
      return getWebDriver()
          .findElement(By.xpath("//*[text()='" + text + "']"))
          .getCssValue("color");
    } catch (final Exception e) {
      return "Could not find text: '" + text + "'.";
    }
  }

  public String getTitle() {
    return getWebDriver().getTitle();
  }

  public String getURL() {
    return getWebDriver().getCurrentUrl();
  }

  /**
   * @return
   */
  public int getVerticalScrollBarPosition() {
    final String script = "return window.pageYOffset;";
    return (int) (long) executeJavaScript(script);
  }

  /**
   * @param webDriver
   * @return
   */
  private double getViewPortHeight() {
    final JEditorPane jEditorPane = new JEditorPane();
    return jEditorPane.getPreferredScrollableViewportSize().getHeight();
  }

  public WebDriver getWebDriver() {
    return webDriver;
  }

  /**
   * @param by
   * @return
   */
  protected WebElement getWebElement(By by) {
    // System.out.print("Object:[" + By.toString() + "]" + Constants.TAB)
    final WebElement webElement = waitExists(by);
    if (Environment.isScrollToObjects()) {
      scrollElementIntoView(webElement);
    }
    if (Environment.isHighlightObjects()) {
      highlightCurrentElement(webElement);
    }
    // WebElement webElement = waitClickable(getWebDriver().findElement(By))
    // WebDriverWait webDriverWait = new WebDriverWait(getWebDriver(),
    // getTimeOutElement())
    // webDriverWait.until(ExpectedConditions.elementToBeClickable(webElement))
    // LOG.debug("Object:[{}]{}{}{}", By.toString(), Constants.TAB,
    //     "Displayed:[" + webElement.isDisplayed() + "]" + Constants.TAB +
    //     "Enabled:[" + webElement.isEnabled() + "]");
    return webElement;
  }

  /**
   * @param by
   * @return
   */
  protected List<WebElement> getWebElements(By by) {
    return getWebDriver().findElements(by);
  }

  public int getWidth() {
    return getWebDriver().manage().window().getSize().getWidth();
  }

  public String getWindowHandle() {
    return getWebDriver().getWindowHandle();
  }

  public Dimension getWindowSize() {
    return getWebDriver().manage().window().getSize();
  }

  /**
   * @param by
   */
  protected void hoverObject(By by) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), "hover");
    }
    final WebElement webElement = getWebElement(by);
    final Actions actions = new Actions(getWebDriver());
    actions.moveToElement(webElement).build().perform();
  }

  /**
   * @param webElement
   */
  protected void hoverObject(WebElement webElement) {
    Actions actions = new Actions(getWebDriver());
    final Action action = actions.moveToElement(webElement).build();
    action.perform();
  }

  /**
   * @param webElement
   */
  protected void hoverOverWebElement(WebElement webElement) {
    new Actions(getWebDriver()).moveToElement(webElement).perform();
  }

  /**
   * @param by
   * @return
   */
  public boolean isDisplayed(By by) {
    final WebElement webElement = waitExists(by);
    return webElement.isDisplayed();
  }

  /**
   * @param by
   * @return
   */
  public boolean isEnabled(By by) {
    final WebElement webElement = waitExists(by);
    return webElement.isEnabled();
  }

  public boolean isHorizontalScrollbarPresent() {
    final String script =
        "return document.documentElement.scrollWidth>document.documentElement.clientWidth;";
    return (Boolean) executeJavaScript(script);
  }

  /**
   * @param stringList1
   * @param stringList2
   * @return
   */
  public boolean isListsEqual(List<String> stringList1, List<String> stringList2) {
    if (stringList1.containsAll(stringList2) && stringList2.containsAll(stringList1)) {
      return true;
    } else {
      LOG.warn(
          "The following items are not in the list: {}",
          CollectionUtils.disjunction(stringList1, stringList2));
      return false;
    }
  }

  /**
   * @param text
   * @return
   */
  public boolean isTextPresent(String text) {
    try {
      new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(5))
          .until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text));
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  public boolean isVerticalScrollbarPresent() {
    final String script =
        "return" + " document.documentElement.scrollHeight>document.documentElement.clientHeight;";
    return (Boolean) executeJavaScript(script);
  }

  /**
   * @param windowTitle
   * @return
   */
  public boolean isWindowPresent(String windowTitle) {
    final String oldHandle = getWebDriver().getWindowHandle();
    for (final String handle : getWebDriver().getWindowHandles()) {
      getWebDriver().switchTo().window(handle);
      if (getWebDriver().getTitle().equals(windowTitle)) {
        getWebDriver().switchTo().window(oldHandle);
        return true;
      }
    }
    getWebDriver().switchTo().window(oldHandle);
    return false;
  }

  private void logFieldName(String fieldName, String value) {
    LOG.debug("Field: [{}], Value: [{}]", fieldName, value);
  }

  private void logFieldNameTagNameValue(String fieldName, String tagName, String bounds) {
    LOG.debug("Field: [{}], TagName: [{}], Bounds: [{}]", fieldName, tagName, bounds);
  }

  public void maximizeWindow() {
    getWebDriver().manage().window().maximize();
  }

  public void minimizeWindow() {
    getWebDriver().manage().window().setPosition(new Point(-2000, 0));
  }

  /**
   * @param by
   * @return
   */
  protected boolean objectExists(By by) {
    return objectExists(by, getTimeoutElement());
  }

  /**
   * @param by
   * @param seconds
   * @return
   */
  protected boolean objectExists(By by, int seconds) {
    try {
      final WebDriverWait webDriverWait =
          new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(seconds));
      webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
    } catch (final Exception e) {
      return false;
    }
    return true;
  }

  protected void objectExistsRefresh(By by, int attemptsMax, int seconds) {
    boolean objectExists = false;
    int attempt = 0;
    do {
      attempt++;
      objectExists = objectExists(by, seconds);
      if (!objectExists && attempt < attemptsMax) {
        getWebDriver().navigate().refresh();
      }
    } while (!objectExists && attempt < attemptsMax);
  }

  /**
   * @param url
   */
  public void openURL(String url) {
    getWebDriver().get(url);
  }

  public void pressCtrlInsert() {
    try {
      final Robot robot = new Robot();
      robot.keyPress(KeyEvent.VK_CONTROL);
      robot.keyPress(KeyEvent.VK_INSERT);
      sleep(1);
      robot.keyRelease(KeyEvent.VK_INSERT);
      sleep(1);
      robot.keyRelease(KeyEvent.VK_CONTROL);
      sleep(1);
    } catch (final Exception e) {
      LOG.error("Failed to perform Robot paste operation", e);
    }
  }

  public void pressEnter() {
    try {
      final Robot robot = new Robot();
      robot.keyPress(KeyEvent.VK_ENTER);
      robot.keyRelease(KeyEvent.VK_ENTER);
    } catch (final Exception e) {
      // Intentionally empty - Robot may not be available in headless mode
      if (Environment.isLogAll()) {
        LOG.debug("Robot Enter not available (expected in headless mode)");
      }
    }
  }

  public void pressESC() {
    try {
      final Robot robot = new Robot();
      robot.keyPress(KeyEvent.VK_ESCAPE);
      robot.keyRelease(KeyEvent.VK_ESCAPE);
    } catch (final Exception e) {
      // Intentionally empty - Robot may not be available in headless mode
      if (Environment.isLogAll()) {
        LOG.debug("Robot ESC not available (expected in headless mode)");
      }
    }
  }

  /**
   * @param message
   */
  public void printFormattedMessage(String message) {
    LOG.info(message);
  }

  public void quitDriver() {
    getWebDriver().quit();
  }

  public void refresh() {
    seleniumWebDriverEventListener.beforeNavigateRefresh(getWebDriver());
    getWebDriver().navigate().refresh();
    // String currentUrl = getWebDriver().getCurrentUrl()
    // getWebDriver().get(currentUrl)
    seleniumWebDriverEventListener.afterNavigateRefresh(getWebDriver());
  }

  /**
   * @param dimension
   */
  public void resizeWindow(Dimension dimension) {
    getWebDriver().manage().window().setSize(dimension);
  }

  /**
   * @param webElement
   */
  public void rightClick(WebElement webElement) {
    new Actions(getWebDriver()).contextClick(webElement).perform();
  }

  /**
   * @param length
   */
  public void scrollDown(int length) {
    if (isVerticalScrollbarPresent()) {
      // String script = "window.scrollBy(" +
      // getVerticalScrollBarPosition() + "," + length + ")"
      String script =
          "scroll("
              + getVerticalScrollBarPosition()
              + ","
              + (getVerticalScrollBarPosition() + length)
              + ")";
      executeJavaScript(script);
    }
  }

  public void scrollDownPage() {
    if (isVerticalScrollbarPresent()) {
      final int height = getWebDriver().manage().window().getSize().getHeight();
      scrollDown(height);
    }
  }

  /**
   * @param webElement
   */
  public void scrollElementIntoView(WebElement webElement) {
    scrollToElement(webElement);
    moveToElement(webElement);
  }

  public void scrollToBottom() {
    if (isVerticalScrollbarPresent()) {
      // final int height =
      // getWebDriver().manage().window().getSize().getHeight()
      // String script = "window.scrollBy(0," + height + ")"
      String script = "window.scrollTo(0, document.body.scrollHeight);";
      executeJavaScript(script);
    }
  }

  /**
   * @param by
   */
  public void moveToElement(By by) {
    final WebElement webElement = waitExists(by);
    moveToElement(webElement);
  }

  /**
   * @param webElement
   */
  public void moveToElement(WebElement webElement) {
    // executeJavaScript("arguments[0].moveToElement(true);", webElement)
    final Actions actions = new Actions(webDriver);
    actions.moveToElement(webElement).perform();
  }

  /**
   * @param xPath
   */
  public void moveToElement(String xPath) {
    moveToElement(By.xpath(xPath));
  }

  /**
   * @param by
   */
  public void scrollToElement(By by) {
    final WebElement webElement = waitExists(by);
    scrollToElement(webElement);
  }

  /**
   * @param webElement
   */
  public void scrollToElement(WebElement webElement) {
    executeJavaScript("arguments[0].scrollIntoView(true);", webElement);
  }

  /**
   * @param xPath
   */
  public void scrollToElement(String xPath) {
    scrollToElement(By.xpath(xPath));
  }

  /**
   * @param webElement
   */
  public void scrollToElementAndCenterVertically(WebElement webElement) {
    try {
      final List<String> bounds = getBoundedRectangleOfElement(webElement);
      final double totalInnerPageHeight = getViewPortHeight();
      final double centerLocation = Integer.parseInt(bounds.get(1)) - totalInnerPageHeight / 2;
      final String script = "window.scrollTo(0, " + centerLocation + ");";
      executeJavaScript(script);
      final Actions actions = new Actions(getWebDriver());
      actions.moveToElement(webElement);
    } catch (final Exception e) {
      LOG.error("Failed to move to element", e);
    }
    if (Environment.isHighlightObjects()) {
      for (int iFlash = 1; iFlash <= 3; iFlash++) {
        flashCurrentElement(webElement, getFlashMilliseconds());
      }
    } else {
      highlightCurrentElement(webElement);
    }
  }

  public void scrollToTop() {
    if (isVerticalScrollbarPresent()) {
      // final int height =
      // getWebDriver().manage().window().getSize().getHeight()
      // String script = "window.scrollBy(0,-" + height + ")"
      String script = "window.scrollTo(0, -document.body.scrollHeight);";
      executeJavaScript(script);
    }
  }

  /**
   * @param length
   */
  public void scrollUp(int length) {
    if (isVerticalScrollbarPresent()) {
      // String script = "window.scrollBy(" +
      // getVerticalScrollBarPosition() + ",-" + length + ")"
      String script =
          "scroll("
              + getVerticalScrollBarPosition()
              + ",-"
              + (getVerticalScrollBarPosition() + length)
              + ")";
      executeJavaScript(script);
    }
  }

  public void scrollUpPage() {
    if (isVerticalScrollbarPresent()) {
      final int height = getWebDriver().manage().window().getSize().getHeight();
      scrollUp(height);
    }
  }

  /**
   * @param by
   * @param value
   */
  protected void selectDropdown(By by, String value) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), value);
    }
    final WebElement webElement = waitExists(by);
    final Select select = new Select(webElement);
    select.selectByVisibleText(value);
  }

  /**
   * @param by
   * @param index
   */
  protected void selectDropdownByIndex(By by, int index) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), String.valueOf(index));
    }
    final WebElement webElement = waitExists(by);
    final Select select = new Select(webElement);
    select.selectByIndex(index);
  }

  /**
   * @param by
   * @param value
   */
  protected void selectDropdownWithPartialText(By by, String value) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), value);
    }
    final WebElement webElement = waitExists(by);
    final Select select = new Select(webElement);
    final List<WebElement> list = getWebDriver().findElements(by);
    final Iterator<WebElement> iterator = list.iterator();
    while (iterator.hasNext()) {
      final WebElement wel = iterator.next();
      if (wel.getText().contains(value)) {
        select.selectByValue(wel.getText());
        break;
      }
    }
  }

  /**
   * @param by
   * @param value
   */
  protected void selectOption(By by, String value) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), value);
    }
    final WebElement parent = waitExists(by);
    final WebElement child =
        parent.findElement(By.xpath(".//label[contains(text(),'" + value + "')]"));
    child.click();
  }

  /**
   * @param by
   * @param value
   */
  protected void setCheckbox(By by, String value) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), value);
    }
    switch (value.toLowerCase(Locale.ENGLISH)) {
      case LABEL_OPTION_CHECKED:
        if (!getCheckbox(by).equals(value)) {
          toggleCheckbox(by);
        }
        break;
      case LABEL_OPTION_UNCHECKED:
        if (!getCheckbox(by).equals(value)) {
          toggleCheckbox(by);
        }
        break;
      default:
        LOG.warn("{}] is not supported for a Checkbox.", LABEL_THE_VALUE + value);
        break;
    }
    if (!getCheckbox(by).equals(value)) {
      LOG.warn("{}] was not selected for Checkbox [{}].", LABEL_THE_VALUE + value, by.toString());
    }
  }

  /**
   * @param webElement
   * @param value
   */
  protected void setCheckbox(WebElement webElement, String value) {
    if (!"".equals(value)) {
      if (Environment.isLogAll()) {
        logFieldName(webElement.toString(), value);
      }
      final Select select = new Select(webElement);
      select.selectByVisibleText(value);
      // logFieldName(webElement.getAttribute("id").toString(), value)
    }
  }

  /**
   * @param webElements
   * @param value
   */
  protected void setCheckboxes(List<WebElement> webElements, String value) {
    if (Environment.isLogAll()) {
      logFieldName(webElements.toString(), value);
    }
    for (final WebElement webElement : webElements) {
      switch (value.toLowerCase(Locale.ENGLISH)) {
        case LABEL_OPTION_CHECKED:
          if (!getCheckbox(webElement).equals(value)) {
            toggleCheckbox(webElement);
          }
          break;
        case LABEL_OPTION_UNCHECKED:
          if (!getCheckbox(webElement).equals(value)) {
            toggleCheckbox(webElement);
          }
          break;
        default:
          LOG.warn("{}] is not supported for a Checkbox.", LABEL_THE_VALUE + value);
          break;
      }
      if (!getCheckbox(webElement).equals(value)) {
        LOG.warn(
            "{}] was not selected for Checkbox [{}].",
            LABEL_THE_VALUE + value,
            webElement.toString());
      }
    }
  }

  /**
   * @param string
   */
  public void setClipboardData(String string) {
    final StringSelection stringSelection = new StringSelection(string);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
  }

  /**
   * @param webElement
   * @param value
   */
  protected void setComboBox(WebElement webElement, String value) {
    setComboBox(webElement, value, true);
  }

  /**
   * @param webElement
   * @param value
   * @param bCaseSensitive
   */
  protected void setComboBox(WebElement webElement, String value, boolean bCaseSensitive) {
    if (Environment.isLogAll()) {
      logFieldName(webElement.toString(), value);
    }
    if (!"".equals(value)) {
      final Select select = new Select(webElement);
      if (value.charAt(0) == '#') {
        int index = Integer.parseInt(value.substring(1));
        index--;
        select.selectByIndex(index);
      } else {
        boolean itemFound = false;
        if (CJSConstants.EMPTY.equalsIgnoreCase(value)) {
          value = "";
        }
        if (bCaseSensitive) {
          select.selectByVisibleText(value);
        } else {
          final String[] itemArray = webElement.getText().split(Constants.NL);
          for (final String item : itemArray) {
            if (item.equalsIgnoreCase(value.toUpperCase(Locale.ENGLISH))) {
              select.selectByVisibleText(item);
              itemFound = true;
              break;
            }
          }
          Assertions.assertTrue(
              itemFound, "Could not find an item with the value of [" + value + "]");
        }
      }
      // logFieldName(webElement.getAttribute("id").toString(), value)
    }
  }

  /**
   * @param by
   * @param value
   */
  protected void setEdit(By by, String value) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), value);
    }
    final WebElement webElement = getWebElement(by);
    LOG.debug("{}", webElement.toString());
    CharSequence[] keysToSend = new CharSequence[] {value};
    seleniumWebDriverEventListener.beforeChangeValueOf(webElement, getWebDriver(), keysToSend);
    webElement.clear();
    webElement.sendKeys(value);
    seleniumWebDriverEventListener.afterChangeValueOf(webElement, getWebDriver(), keysToSend);
  }

  /**
   * @param by
   * @param value
   */
  protected void setEditPassword(By by, String value) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), "********");
    }
    final WebElement webElement = getWebElement(by);
    LOG.debug("{}", webElement.toString());
    CharSequence[] keysToSend = new CharSequence[] {value};
    seleniumWebDriverEventListener.beforeChangeValueOf(webElement, getWebDriver(), keysToSend);
    webElement.clear();
    webElement.sendKeys(value);
    seleniumWebDriverEventListener.afterChangeValueOf(webElement, getWebDriver(), keysToSend);
  }

  /**
   * @param webElement
   * @param value
   */
  protected void setInputField(WebElement webElement, String value) {
    if (!value.isEmpty()) {
      if (Environment.isLogAll()) {
        logFieldName(webElement.toString(), value);
      }
      webElement.clear();
      if (!CJSConstants.EMPTY.equalsIgnoreCase(value)) {
        webElement.sendKeys(value);
      }
      // logFieldName(webElement.toString(), value)
    }
  }

  /**
   * @param webElement
   * @param value
   */
  protected void setRadio(WebElement webElement, String value) {
    if (!value.isEmpty()) {
      if (Environment.isLogAll()) {
        logFieldName(webElement.toString(), value);
      }
      final Select select = new Select(webElement);
      select.selectByVisibleText(value);
      // logFieldName(webElement.getAttribute("id").toString(), value)
    }
  }

  private void setScreenshotCounter(int screenshotCounter) {
    this.screenshotCounter = screenshotCounter;
  }

  /**
   * @param webDriver
   */
  public void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  /**
   * @param x
   * @param y
   */
  public void setWindowPosition(int x, int y) {
    getWebDriver().manage().window().setPosition(new Point(x, y));
  }

  /**
   * @param width
   * @param height
   */
  public void setWindowSize(int width, int height) {
    final Dimension dimensionScreenResolution = new Dimension(width, height);
    getWebDriver().manage().window().setSize(dimensionScreenResolution);
  }

  /**
   * @param scenario
   * @param actual
   * @param expected
   * @param message
   * @return
   */
  public boolean softAssert(
      Scenario scenario, List<String> actual, List<String> expected, String message) {
    if (!actual.equals(expected)) {
      scenario.log(message);
      scenario.log("Expected:[" + expected + "]");
      scenario.log("Actual:[" + actual + "]");
      return false;
    } else {
      scenario.log(message + " - Match:[" + expected + "]");
    }
    return true;
  }

  /**
   * @param tab
   * @throws QAException
   */
  public void switchTab(int tab) throws QAException {
    final List<String> listWindows = new ArrayList<>(getOpenWindows());
    if (listWindows.isEmpty()) {
      throw new QAException("Popup window did not open");
    }
    switchToWindow(listWindows.get(tab));
  }

  /**
   * Switches to an iframe
   *
   * @param frameid
   */
  public void switchToFrame(String frameid) {
    getWebDriver().switchTo().frame(frameid);
    getWebDriver().switchTo().activeElement();
  }

  /**
   * @param by
   */
  public void switchToIFrame(By by) {
    final WebElement webElement = getWebElement(by);
    getWebDriver().switchTo().frame(webElement);
  }

  public void switchToMainFrame() {
    getWebDriver().switchTo().defaultContent();
    sleep(1);
  }

  /**
   * Switches driver to a specified popup by searching through open windows
   *
   * @param windowTitle
   */
  public void switchToPopup(String windowTitle) {
    for (int attempt = 0; attempt < 3; attempt++) {
      final Set<String> openWindows = getWebDriver().getWindowHandles();
      final String initialPage = getWebDriver().getWindowHandle();
      for (final String s : openWindows) {
        getWebDriver().switchTo().window(s);
        if (getWebDriver().getTitle().contains(windowTitle)) {
          return;
        }
      }
      LOG.warn("Could not find popup: {}", windowTitle);
      // switch back
      getWebDriver().switchTo().window(initialPage);
      sleep(4);
    }
  }

  /**
   * @param windowID
   */
  public void switchToWindow(String windowID) {
    getWebDriver().switchTo().window(windowID);
  }

  /**
   * @param windowIndex
   * @throws QAException
   */
  public void switchToWindowByIndex(int windowIndex) throws QAException {
    final List<String> listWindows = new ArrayList<>(getOpenWindows());
    if (listWindows.isEmpty() || listWindows.size() < windowIndex) {
      throw new QAException("Window did not open");
    }
    switchToWindow(listWindows.get(windowIndex));
  }

  /**
   * @param windowTitle
   */
  public void switchToWindowByTitle(String windowTitle) {
    for (final String handle : getWebDriver().getWindowHandles()) {
      getWebDriver().switchTo().window(handle);
      if (getWebDriver().getTitle().equals(windowTitle)) {
        return;
      }
    }
  }

  public void tabCloseExtras() {
    // final List<String> tabs = new
    // List<>(getWebDriver().getWindowHandles())
    // for (int tabIndex = tabs.size(); tabIndex > 1; tabIndex--)
    final List<String> listWindows = new ArrayList<>(getOpenWindows());
    for (int tabIndex = listWindows.size(); tabIndex > 1; tabIndex--) {
      getWebDriver().switchTo().window(listWindows.get(tabIndex - 1));
      getWebDriver().close();
    }
    getWebDriver().switchTo().window(listWindows.get(0));
  }

  public void tabOpenNew() {
    getWebDriver().findElement(By.xpath(".//body")).sendKeys(Keys.CONTROL + Constants.TAB);
  }

  /**
   * @param by
   */
  protected void toggleCheckbox(By by) {
    if (Environment.isLogAll()) {
      logFieldName(by.toString(), "toggle");
    }
    final WebElement webElement = getWebElement(by);
    webElement.click();
  }

  /**
   * @param webElement
   */
  protected void toggleCheckbox(WebElement webElement) {
    webElement.click();
  }

  /**
   * @param webElements
   * @return
   */
  public List<String> toString(List<WebElement> webElements) {
    final List<String> listResult = new ArrayList<>();
    for (final WebElement webElement : webElements) {
      listResult.add(webElement.getText());
    }
    return listResult;
  }

  /**
   * @param expected
   * @param by
   */
  protected void verifyResultData(String expected, By by) {
    final WebElement webElement = getWebElement(by);
    final String actual = webElement.getText();
    Assertions.assertEquals(expected, actual);
  }

  /**
   * @param expected
   */
  protected void verifyTitle(String expected) {
    waitForPageToLoad();
    final String actual = getWebDriver().getTitle();
    LOG.info("Expected title: [{}], Actual title: [{}]", expected, actual);
    Assertions.assertEquals(expected, actual);
  }

  /**
   * @param by
   * @return
   */
  protected WebElement waitClickable(By by) {
    WebElement webElement = waitExists(by);
    webElement = waitClickable(webElement);
    return webElement;
  }

  /**
   * @param webElement
   * @return
   */
  protected WebElement waitClickable(WebElement webElement) {
    final WebDriverWait webDriverWait =
        new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(getTimeoutElement()));
    webElement = webDriverWait.until(ExpectedConditions.elementToBeClickable(webElement));
    return webElement;
  }

  /**
   * @param by
   * @return
   */
  protected WebElement waitExists(By by) {
    // May need to update as new webDriverWait returns boolean.
    final WebDriverWait webDriverWait =
        new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(getTimeoutElement()));
    return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public void waitForAlert() {
    final Wait<WebDriver> webDriverWait =
        new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(getTimeoutElement()));
    webDriverWait.until(ExpectedConditions.alertIsPresent());
  }

  /**
   * @param selector
   * @param timeout
   * @return
   */
  protected WebElement waitForElementExists(final String selector, long timeout) {
    final Wait<WebDriver> webDriverWait =
        new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(timeout));
    return webDriverWait.until(
        new Function<WebDriver, WebElement>() {
          @Override
          public WebElement apply(WebDriver driver) {
            return driver.findElement(By.cssSelector(selector));
          }
        });
  }

  /**
   * @param headingTitle
   * @param timeoutSec
   */
  public void waitForPageHeading(String headingTitle, int timeoutSec) {
    final long initialTime = System.currentTimeMillis() / 1000;
    long currentTime = System.currentTimeMillis() / 1000;
    String heading = "";
    while (!heading.equals(headingTitle) && timeoutSec > currentTime - initialTime) {
      sleep(0, 500);
      currentTime = System.currentTimeMillis() / 1000;
      heading = getWebDriver().findElement(By.className("headingtitle")).getText().trim();
      // LOG.debug("Waiting for page heading: '{}'", headingTitle);
    }
  }

  protected void waitForPageToLoad() {
    // getWebDriver().manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(getTimeOutPage()),
    // TimeUnit.SECONDS)
    sleep(3);
  }

  /**
   * @param webElement
   * @param expectedText
   */
  protected void waitForTextInWebElement(WebElement webElement, String expectedText) {
    new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(getTimeoutElement()))
        .until(ExpectedConditions.textToBePresentInElement(webElement, expectedText));
  }

  /**
   * For the Validating and Saving dialog, use waitForValidatingBox() instead
   *
   * @param initialURL
   * @param timeoutSec
   * @return
   */
  public boolean waitForURLChange(String initialURL, int timeoutSec) {
    try {
      String changedURL = initialURL;
      final long initialTime = System.currentTimeMillis() / 1000;
      long currentTime = System.currentTimeMillis() / 1000;
      while (initialURL.equals(changedURL) && timeoutSec > currentTime - initialTime) {
        sleep(0, 500);
        changedURL = getWebDriver().getCurrentUrl();
        currentTime = System.currentTimeMillis() / 1000;
      }
      return timeoutSec > currentTime - initialTime;
    } catch (final Exception e) {
      LOG.error("Error in waitForPageHeading", e);
      return false;
    }
  }

  /**
   * @param xpath
   * @return
   */
  public boolean waitForXpath(String xpath) {
    try {
      final WebDriverWait webDriverWait =
          new WebDriverWait(getWebDriver(), java.time.Duration.ofSeconds(getTimeoutElement()));
      webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
      return true;
    } catch (final Exception e) {
      LOG.warn("Could not find xpath: [{}]", xpath);
      return false;
    }
  }

  /**
   * @param value
   */
  public void waitHard(int value) {
    getWebDriver().manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(value));
  }

  public void waitPageLoaded() {
    boolean loading = true;
    // final By imageLoading = By.xpath(".//img[@alt='Loading']")
    final By imageSpinnerBy = By.xpath(".//*[@id='spinner']/img");
    do {
      // List<WebElement> imagesLoading = getElements(imageLoading)
      // if ((imagesLoading.size() == 0) && !objectExists(imageSpinner,
      // 0))
      if (!objectExists(imageSpinnerBy, 0)) {
        loading = false;
      }
    } while (loading);
  }

  /**
   * Matches strings ignoring '*' wildcards
   *
   * @param text
   * @param pattern
   * @return
   */
  public boolean wildCardMatch(String text, String pattern) {
    if (text.length() < 1 && pattern.length() < 1) {
      return true;
    }
    if (text.length() < 1 || pattern.length() < 1) {
      return false;
    }
    // Create the cards by splitting using a RegEx. If more speed
    // is desired, a simpler character based splitting can be done.
    final String[] cards = pattern.split(Constants.DELIMETER_PATH + "*");
    // Iterate over the cards.
    for (final String card : cards) {
      final int idx = text.indexOf(card);
      // Card not detected in the text.
      if (idx == -1) {
        LOG.warn("Card not found: [{}], Diff: [{}]", card, StringUtils.difference(pattern, text));
        return false;
      }
      // Move ahead, towards the right of the text.
      text = text.substring(idx + card.length());
    }
    return true;
  }

  /**
   * Matches strings ignoring '*' wildcards
   *
   * @param text
   * @param pattern
   * @return
   */
  public boolean wildCardMatchNoPrint(String text, String pattern) {
    if (text.length() < 1 && pattern.length() < 1) {
      return true;
    }
    if (text.length() < 1 || pattern.length() < 1) {
      return false;
    }
    // Create the cards by splitting using a RegEx. If more speed
    // is desired, a simpler character based splitting can be done.
    final String[] cards = pattern.split(Constants.DELIMETER_PATH + "*");
    // Iterate over the cards.
    for (final String card : cards) {
      final int idx = text.indexOf(card);
      // Card not detected in the text.
      if (idx == -1) {
        return false;
      }
      // Move ahead, towards the right of the text.
      text = text.substring(idx + card.length());
    }
    return true;
  }
}
