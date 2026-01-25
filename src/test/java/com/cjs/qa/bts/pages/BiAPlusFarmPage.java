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

public class BiAPlusFarmPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BiAPlusFarmPage.class));

  public BiAPlusFarmPage(WebDriver webDriver) {
    super(webDriver);
  }

  //
  // DECLARATIONS
  private static final By dropdownSelectthePriorPolicyPrefix = By.id("PRI_POL_PRE");
  private static final By editEnterthePriorPolicyNumber = By.id("PRI_POL_NUM");
  private static final By editEnterthePriorPolicyMod = By.id("PRI_POL_MOD");
  private static final String pageTitle = "BI_APlusFarmPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getDropdownSelectthePriorPolicyPrefix() {
    return getDropdown(dropdownSelectthePriorPolicyPrefix);
  }

  public String getEditEnterthePriorPolicyNumber() {
    return getEdit(editEnterthePriorPolicyNumber);
  }

  public String getEditEnterthePriorPolicyMod() {
    return getEdit(editEnterthePriorPolicyMod);
  }

  // METHODS SET
  public void selectDropdownSelectthePriorPolicyPrefix(String value) {
    selectDropdown(dropdownSelectthePriorPolicyPrefix, value);
  }

  public void setEditEnterthePriorPolicyNumber(String value) {
    setEdit(editEnterthePriorPolicyNumber, value);
  }

  public void setEditEnterthePriorPolicyMod(String value) {
    setEdit(editEnterthePriorPolicyMod, value);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "select the prior policy prefix":
            selectDropdownSelectthePriorPolicyPrefix(value);
            break;
          case "enter the prior policy number":
            setEditEnterthePriorPolicyNumber(value);
            break;
          case "enter the prior policy mod":
            setEditEnterthePriorPolicyMod(value);
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
        if (Environment.isLogAll()) {
          LOG.debug("({Field}{}, {Value}{});", field, value);
        }
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "select the prior policy prefix":
            value = getDropdownSelectthePriorPolicyPrefix();
            break;
          case "enter the prior policy number":
            value = getEditEnterthePriorPolicyNumber();
            break;
          case "enter the prior policy mod":
            value = getEditEnterthePriorPolicyMod();
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
