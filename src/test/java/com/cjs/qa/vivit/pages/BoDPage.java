package com.cjs.qa.vivit.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class BoDPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(BoDPage.class));

  public BoDPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DOCUMENTATION
  // BoDPage getLabelName Allows you to get the value of the [Name] Field.
  // BoDPage getLabelTitle Allows you to get the value of the [Title] Field.
  // BoDPage Allows you to click the [eMail] Link.
  // BoDPage getLabelCompany Allows you to get the value of the [Company]
  // Field.
  // BoDPage getLabelAddress Allows you to get the value of the [Address]
  // Field.
  // BoDPage getLabelCountry Allows you to get the value of the [Country]
  // Field.
  // BoDPage getLabelDuties Allows you to get the value of the [Duties] Field.
  // BoDPage populatePage Allows you to set the value of the included fields
  // on the page.
  // BoDPage validatePage Allows you to validate the value of the included
  // fields on the page.
  //
  // DECLARATIONS
  public static final String PAGE_TITLE = "Borad of Directors - Vivit Worldwide";
  public static final String URL =
      "https://www.vivit-worldwide.org/general/custom.asp?page=2018board";

  public void verifyPage() {
    verifyTitle(PAGE_TITLE);
  }

  // METHODS GET
  public String getLabelName(String value) {
    final By labelName =
        By.xpath(".//*[@id='CustomPageBody']/h3/a[text()[contains(.,'" + value + "')]]");
    return getLabel(labelName);
  }

  public String getLabelTitle(String value) {
    final By labelTitle =
        By.xpath(".//*[@id='CustomPageBody']/p[text()[contains(.,'" + value + "')]]");
    return getLabel(labelTitle);
  }

  public String getLabelEmail(String value) {
    final By labelEmail =
        By.xpath(".//*[@id='CustomPageBody']/p/a[text()[contains(.,'" + value + "')]]");
    return getLabel(labelEmail);
  }

  public String getLabelCompany(String value) {
    final By labelCompany =
        By.xpath(".//*[@id='CustomPageBody']/p[text()[contains(.,'" + value + "')]]");
    return getLabel(labelCompany);
  }

  public String getLabelAddress(String value) {
    final By labelAddress =
        By.xpath(".//*[@id='CustomPageBody']/p[text()[contains(.,'" + value + "')]]");
    return getLabel(labelAddress);
  }

  public String getLabelCountry(String value) {
    final By labelCountry =
        By.xpath(".//*[@id='CustomPageBody']/p[text()[contains(.,'" + value + "')]]");
    return getLabel(labelCountry);
  }

  public String getLabelDuties(String value) {
    final By labelDuties =
        By.xpath(".//*[@id='CustomPageBody']/p/span[text()[contains(.,'" + value + "')]]");
    return getLabel(labelDuties);
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable table) {
    final Map<String, String> mapExpected = new HashMap<>();
    final Map<String, String> mapActual = new HashMap<>();
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("{Field}{}, {Value}{}", field, value);
        }
        mapExpected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "name":
            value = getLabelName(value);
            break;
          case "title":
            value = getLabelTitle(value);
            break;
          case "email":
            value = getLabelEmail(value);
            break;
          case "company":
            value = getLabelCompany(value);
            break;
          case "address":
            value = getLabelAddress(value);
            break;
          case "country":
            value = getLabelCountry(value);
            break;
          case "duties":
            value = getLabelDuties(value);
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        mapActual.put(field, value);
      }
    }
    Assertions.assertSame(
        mapExpected.toString(), mapActual.toString(), PAGE_TITLE + " validatePage");
    // hardAssert.assertSame(actual, expected, PAGE_TITLE + "
    // validatePage");
    // softAssert.assertSame(actual, expected, PAGE_TITLE + "validatePage");
  }

  public void getData() throws QAException {
    getWebDriver().get(URL);
    // final List<String> webSiteMemberIDList = new ArrayList<>();
    // .//*[@id='CustomPageBody']/h3
    // .//*[@id='CustomPageBody']//a[contains(@href,'/members/?id=')]
  }
}
