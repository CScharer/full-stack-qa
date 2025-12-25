package com.cjs.qa.linkedin.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class HomePage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(HomePage.class));

  public HomePage(WebDriver webDriver) {
    super(webDriver);
  }

  // DOCUMENTATION
  // clickButtonHome:
  // Clicks the [Home] Button.
  // clickButtonJobs:
  // Clicks the [Jobs] Button.
  // clickButtonMe:
  // Clicks the [Me] Button.
  // clickButtonMessaging:
  // Clicks the [Messaging] Button.
  // clickButtonMyNetwork:
  // Clicks the [My Network] Button.
  // clickButtonNotifications:
  // Clicks the [Notifications] Button.
  // populatePage:
  // Populates the value of the all of the fields.
  // validatePage:
  // Validates the value of the all of the fields.

  // INPUTS

  // TABLE DEF
  // DataTable dataTable = Convert.toDataTable(Arrays.asList));

  public By getByButtonHome() {
    return By.xpath(".//*[@id='feed-tab-icon']");
  }

  public By getByButtonMyNetwork() {
    return By.xpath(".//*[@id='mynetwork-tab-icon']");
  }

  public By getByButtonJobs() {
    return By.xpath(".//*[@id='jobs-tab-icon']");
  }

  public By getByButtonMessaging() {
    return By.xpath(".//*[@id='messaging-tab-icon']");
  }

  public By getByButtonNotifications() {
    return By.xpath(".//*[@id='notifications-tab-icon']");
  }

  public By getByButtonMe() {
    return By.xpath(".//*[@id='nav-settings__dropdown-trigger']");
  }

  public void clickButtonHome() {
    clickObject(getByButtonHome());
  }

  public boolean isButtonHomeDisplayed() {
    return isDisplayed(getByButtonHome());
  }

  public boolean isButtonHomeEnabled() {
    return isEnabled(getByButtonHome());
  }

  public void clickButtonMyNetwork() {
    clickObject(getByButtonMyNetwork());
  }

  public boolean isButtonMyNetworkDisplayed() {
    return isDisplayed(getByButtonMyNetwork());
  }

  public boolean isButtonMyNetworkEnabled() {
    return isEnabled(getByButtonMyNetwork());
  }

  public void clickButtonJobs() {
    clickObject(getByButtonJobs());
  }

  public boolean isButtonJobsDisplayed() {
    return isDisplayed(getByButtonJobs());
  }

  public boolean isButtonJobsEnabled() {
    return isEnabled(getByButtonJobs());
  }

  public void clickButtonMessaging() {
    clickObject(getByButtonMessaging());
  }

  public boolean isButtonMessagingDisplayed() {
    return isDisplayed(getByButtonMessaging());
  }

  public boolean isButtonMessagingEnabled() {
    return isEnabled(getByButtonMessaging());
  }

  public void clickButtonNotifications() {
    clickObject(getByButtonNotifications());
  }

  public boolean isButtonNotificationsDisplayed() {
    return isDisplayed(getByButtonNotifications());
  }

  public boolean isButtonNotificationsEnabled() {
    return isEnabled(getByButtonNotifications());
  }

  public void clickButtonMe() {
    clickObject(getByButtonMe());
  }

  public boolean isButtonMeDisplayed() {
    return isDisplayed(getByButtonMe());
  }

  public boolean isButtonMeEnabled() {
    return isEnabled(getByButtonMe());
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable dataTable) {
    List<List<String>> list = dataTable.asLists();
    for (List<?> item : list) {
      String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        LOG.debug("{Field}{}, {Value}{}", field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable dataTable) {
    Map<String, String> expected = new HashMap<>();
    Map<String, String> actual = new HashMap<>();
    List<List<String>> list = dataTable.asLists();
    for (List<?> item : list) {
      String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        LOG.debug("{Field}{}, {Value}{}", field, value);
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
        actual.put(field, value);
      }
    }
    Assert.assertSame(
        this.getClass().getName() + "validatePage", expected.toString(), actual.toString());
  }
}
