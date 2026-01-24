package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class NamedInsuredsPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(NamedInsuredsPage.class));

  public NamedInsuredsPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final String nodeForm1 = "form1:";
  private static final By buttonAdd = By.id(nodeForm1 + "addInsIntBtn");
  private static final By buttonDelete = By.id(nodeForm1 + "deleteNamedInsBtn");
  private static final By buttonCreateNewNameSeq = By.id(nodeForm1 + "crteNameSeqButton");
  private static final By buttonAddExistNameSeq = By.id(nodeForm1 + "addExistNSeqButton");
  private static final String pageTitle = "NamedInsuredsPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS SET
  public void clickButtonAdd(String value) {
    clickObject(buttonAdd);
  }

  public void clickButtonDelete(String value) {
    clickObject(buttonDelete);
  }

  public void clickButtonCreateNewNameSeq(String value) {
    clickObject(buttonCreateNewNameSeq);
  }

  public void clickButtonAddExistNameSeq(String value) {
    clickObject(buttonAddExistNameSeq);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("({Field}{}, {Value}{});", field, value);
        }
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "Add":
            clickButtonAdd(value);
            break;
          case "Delete":
            clickButtonDelete(value);
            break;
          case "Create New Name Seq":
            clickButtonCreateNewNameSeq(value);
            break;
          case "Add Exist Name Seq":
            clickButtonAddExistNameSeq(value);
            break;
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
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "":
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    Assertions.assertSame(expected, actual, getPageTitle() + " validatePage");
  }
}
