package com.cjs.qa.vivit.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.vivit.objects.StaffDetails;

import io.cucumber.datatable.DataTable;

public class StaffPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(StaffPage.class));

  public StaffPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DOCUMENTATION
  // StaffPage getLabelNameTitle Allows you to get the value of the
  // [Name-Title] Field.
  // StaffPage Allows you to click the [eMail] Link.
  // StaffPage getLabelPhone Allows you to get the value of the [Phone] Field.
  // StaffPage populatePage Allows you to set the value of the included fields
  // on the page.
  // StaffPage validatePage Allows you to validate the value of the included
  // fields on the page.
  //
  // DECLARATIONS
  public static final String PAGE_TITLE = "Staff List - Vivit Worldwide";
  public static final String URL = "https://www.vivit-worldwide.org/staff/";
  private static final By labelNameTitle = By.id("Name-Title");
  private static final By linkeMail = By.id("eMail");
  private static final By labelPhone = By.id("Phone");

  public void verifyPage() {
    verifyTitle(PAGE_TITLE);
  }

  // METHODS GET
  public String getLabelNameTitle() {
    return getLabel(labelNameTitle);
  }

  public String getLabelPhone() {
    return getLabel(labelPhone);
  }

  // METHODS SET
  public void clickLinkeMail() {
    clickObject(linkeMail);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        switch (field.toLowerCase(Locale.ENGLISH)) {
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable table) {
    final Map<String, String> expected = new HashMap<>();
    final Map<String, String> actual = new HashMap<>();
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("{Field}{}, {Value}{}", field, value);
        }
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "name-title":
            value = getLabelNameTitle();
            break;
          case "phone":
            value = getLabelPhone();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    Assertions.assertSame(expected.toString(), actual.toString(), PAGE_TITLE + " validatePage");
    // hardAssert.assertSame(actual.toString(), expected.toString(),
    // PAGE_TITLE + " validatePage")
    // softAssert.assertSame(actual.toString(), expected.toString(),
    // PAGE_TITLE + " validatePage")
  }

  public void getData() throws QAException {
    getWebDriver().get(URL);
    final List<StaffDetails> staffDetailsList = new ArrayList<>();
    final List<WebElement> webElementList =
        getWebDriver()
            .findElements(
                By.xpath(
                    ".//*[@id='SpContent_Container']/table/tbody/tr/td/b/a[contains(@href,'id=')]"));
    for (WebElement webElement : webElementList) {
      final String url = webElement.getAttribute("href");
      final StaffDetails staffDetails = new StaffDetails(url);
      staffDetailsList.add(staffDetails);
    }
    for (final StaffDetails staffDetails : staffDetailsList) {
      getWebDriver().get(staffDetails.getUrl());
      WebElement webElement =
          getWebDriver().findElement(By.xpath(".//*[@id='SpContent_Container']"));
      final String pageText = webElement.getText();
      final String[] pageTextArray = pageText.split(Constants.NL);
      webElement = getWebDriver().findElement(By.xpath(".//*[@id='SpContent_Container']/h2"));
      staffDetails.setNameTitle(webElement.getText());
      webElement = getWebDriver().findElement(By.xpath(".//*[@id='SpContent_Container']/a"));
      staffDetails.seteMail(webElement.getText());
      final String labelPhone = "Phone: ";
      for (final String text : pageTextArray) {
        if (text.contains(labelPhone)) {
          staffDetails.setPhone(text.substring(text.indexOf(labelPhone) + labelPhone.length()));
          break;
        }
      }
      staffDetails.setProfile(pageTextArray[pageTextArray.length - 1]);
      LOG.debug("Staff Details: {}", staffDetails.toString());
    }
  }
}
