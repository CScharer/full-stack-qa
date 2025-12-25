package com.cjs.qa.selenium;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

public class SeleniumWebDriverEventListener implements WebDriverListener {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SeleniumWebDriverEventListener.class));

  public void beforeAlertAccept(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void afterAlertAccept(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void afterAlertDismiss(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void beforeAlertDismiss(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void beforeNavigateTo(String url, WebDriver webDriver) {
    // LOG.debug("{} - url:[{}]", JavaHelpers.getCurrentMethodName(), url);
  }

  public void afterNavigateTo(String url, WebDriver webDriver) {
    // LOG.debug("{} - url:[{}]", JavaHelpers.getCurrentMethodName(), url);
  }

  public void beforeNavigateBack(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void afterNavigateBack(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void beforeNavigateForward(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void afterNavigateForward(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void beforeNavigateRefresh(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void afterNavigateRefresh(WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }

  public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
    // LOG.debug("{} - by:[{}], webElement:[{}]", JavaHelpers.getCurrentMethodName(), by.toString(),
    // webElement.toString());
  }

  public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
    // LOG.debug("{} - by:[{}], webElement:[{}]", JavaHelpers.getCurrentMethodName(), by.toString(),
    // webElement.toString());
  }

  public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
    // LOG.debug("{} - webElement:[{}]", JavaHelpers.getCurrentMethodName(), webElement.toString());
  }

  public void afterClickOn(WebElement webElement, WebDriver webDriver) {
    // LOG.debug("{} - webElement:[{}]", JavaHelpers.getCurrentMethodName(), webElement.toString());
  }

  public void beforeChangeValueOf(
      WebElement webElement, WebDriver webDriver, CharSequence[] keysToSend) {
    // LOG.debug("{} - webElement:[{}], keysToSend:[{}]", JavaHelpers.getCurrentMethodName(),
    // webElement.toString(), keysToSend[0].toString());
  }

  public void afterChangeValueOf(
      WebElement webElement, WebDriver webDriver, CharSequence[] keysToSend) {
    // LOG.debug("{} - webElement:[{}], keysToSend:[{}]", JavaHelpers.getCurrentMethodName(),
    // webElement.toString(), keysToSend[0].toString());
  }

  public void beforeScript(String script, WebDriver webDriver) {
    // LOG.debug("{} - script:[{}]", JavaHelpers.getCurrentMethodName(), script);
  }

  public void afterScript(String script, WebDriver webDriver) {
    // LOG.debug("{} - script:[{}]", JavaHelpers.getCurrentMethodName(), script);
  }

  public void onException(Throwable throwable, WebDriver webDriver) {
    LOG.debug("{}", JavaHelpers.getCurrentMethodName());
  }
}
