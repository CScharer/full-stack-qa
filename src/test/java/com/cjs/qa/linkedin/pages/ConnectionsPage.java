package com.cjs.qa.linkedin.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class ConnectionsPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ConnectionsPage.class));

  public ConnectionsPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DOCUMENTATION
  // getEditSearchbyname:
  // Gets the value of the [Search by name] Edit field.
  // setEditSearchbyname:
  // Sets the value of the [Search by name] Edit field.
  // populatePage:
  // Populates the value of the all of the fields.
  // validatePage:
  // Validates the value of the all of the fields.

  // INPUTS
  // |Search by name|Searchbyname|

  // TABLE DEF
  // DataTable dataTable = Convert.toDataTable(Arrays.asList(
  // Arrays.asList("Search by name", "Search by name")));

  public By getByEditSearchbyname() {
    return By.xpath(".//input[@placeholder='Search by name']");
  }

  public String getEditSearchbyname() {
    return getEdit(getByEditSearchbyname());
  }

  public boolean isEditSearchbynameDisplayed() {
    return isDisplayed(getByEditSearchbyname());
  }

  public boolean isEditSearchbynameEnabled() {
    return isEnabled(getByEditSearchbyname());
  }

  public void setEditSearchbyname(String value) {
    // value = value.replaceAll("-", "");
    // value = value.replaceAll("(", "");
    // value = value.replaceAll(")", "");
    setEdit(getByEditSearchbyname(), value);
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
          case "search by name":
            setEditSearchbyname(value);
            break;
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
          case "search by name":
            value = getEditSearchbyname();
            break;
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

  public String getUserHREF() {
    if (objectExists(By.xpath(".//div[@class='mn-connections__empty-search']"), 2)) {
      return null;
    }
    List<WebElement> webElementList =
        getWebDriver().findElements(By.xpath(".//div[@class='mn-connection-card__details']"));
    if (webElementList.size() == 1) {
      WebElement webElement = webElementList.get(0);
      WebElement webElementHREF = webElement.findElement(By.xpath("./a"));
      LOG.debug("href: [{}]", webElementHREF.getAttribute("href"));
      return webElementHREF.getAttribute("href");
    } else {
      return null;
    }
  }
}
