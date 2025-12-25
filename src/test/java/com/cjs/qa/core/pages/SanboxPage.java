package com.cjs.qa.core.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class SanboxPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SanboxPage.class));

  public SanboxPage(WebDriver webDriver) {
    super(webDriver);
  }

  // TABLE DEF
  // DataTable table = Convert.toDataTable(Arrays.asList(
  // Arrays.asList("Checkbox", LABEL_OPTION_CHECKED),
  // Arrays.asList("Dropdown", "Dropdown"),
  // Arrays.asList("Edit", "Edit"),
  // Arrays.asList("Option", "Option")));
  //
  // INPUTS
  // |Button|Button|
  // |Checkbox|Checkbox|
  // |Dropdown|Dropdown|
  // |Edit|Edit|
  // |Label|Label|
  // |Link|Link|
  // |Option|Option|
  //
  // DOCUMENTATION
  // CreateCucumberCodePage Allows you to click the [Button] Button.
  // CreateCucumberCodePage clickButtonButton Allows you to set the value of
  // the [Checkbox] Field.
  // CreateCucumberCodePage toggleCheckboxCheckbox Allows you to toggle the
  // value of the [Checkbox] Field regardless of what the current value is.
  // CreateCucumberCodePage getCheckboxCheckbox Allows you to get the value of
  // the [Checkbox] Field.
  // CreateCucumberCodePage setCheckboxCheckbox Allows you to set the value of
  // the [Dropdown] Field.
  // CreateCucumberCodePage getDropdownDropdown Allows you to get the value of
  // the [Dropdown] Field.
  // CreateCucumberCodePage selectDropdownDropdown Allows you to set the value
  // of the [Edit] Field.
  // CreateCucumberCodePage getEditEdit Allows you to get the value of the
  // [Edit] Field.
  // CreateCucumberCodePage getLabelLabel Allows you to get the value of the
  // [Label] Field.
  // CreateCucumberCodePage Allows you to click the [Link] Link.
  // CreateCucumberCodePage clickLinkLink Allows you to set the value of the
  // [Option] Field.
  // CreateCucumberCodePage getOptionOption Allows you to get the value of the
  // [Option] Field.
  // CreateCucumberCodePage populatePage Allows you to set the value of the
  // included fields on the page.
  // CreateCucumberCodePage validatePage Allows you to validate the value of
  // the included fields on the page.
  //
  // DECLARATIONS
  private static final By buttonButton = By.id("Button");
  private static final By checkboxCheckbox = By.id("Checkbox");
  private static final By dropdownDropdown = By.id("Dropdown");
  private static final By editEdit = By.id("Edit");
  private static final By labelLabel = By.id("Label");
  private static final By linkLink = By.id("Link");
  private static final By optionOption = By.id("Option");
  private static final String pageTitle = "Core - CreateCucumberCodePage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getCheckboxCheckbox() {
    return getCheckbox(checkboxCheckbox);
  }

  public String getDropdownDropdown() {
    return getDropdown(dropdownDropdown);
  }

  public String getEditEdit() {
    return getEdit(editEdit);
  }

  public String getLabelLabel() {
    return getLabel(labelLabel);
  }

  public String getOptionOption() {
    return getOption(optionOption);
  }

  // METHODS SET
  public void clickButtonButton() {
    clickObject(buttonButton);
  }

  public void toggleCheckboxCheckbox() {
    toggleCheckbox(checkboxCheckbox);
  }

  public void setCheckboxCheckbox(String value) {
    setCheckbox(checkboxCheckbox, value);
  }

  public void selectDropdownDropdown(String value) {
    selectDropdown(dropdownDropdown, value);
  }

  public void setEditEdit(String value) {
    setEdit(editEdit, value);
  }

  public void clickLinkLink() {
    clickObject(linkLink);
  }

  public void selectOptionOption(String value) {
    selectOption(optionOption, value);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "Checkbox":
            setCheckboxCheckbox(value);
            break;
          case "Dropdown":
            selectDropdownDropdown(value);
            break;
          case "edit":
            setEditEdit(value);
            break;
          case "option":
            selectOptionOption(value);
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
          case "Checkbox":
            value = getCheckboxCheckbox();
            break;
          case "Dropdown":
            value = getDropdownDropdown();
            break;
          case "edit":
            value = getEditEdit();
            break;
          case "label":
            value = getLabelLabel();
            break;
          case "option":
            value = getOptionOption();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("{}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    // Assert.assertSame(getPageTitle() + " validatePage", expected, actual);
    // hardAssert.assertSame(actual, expected, getPageTitle() + "
    // validatePage");
    Environment.getSoftAssert().assertEquals(actual, expected, getPageTitle() + "validatePage");
  }
}
