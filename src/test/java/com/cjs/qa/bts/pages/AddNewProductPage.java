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

public class AddNewProductPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(AddNewProductPage.class));

  public AddNewProductPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By dropdownProduct = By.id("productId");
  private static final By dropdownProgram = By.id("programId");
  private static final By dropdownRatingCompany =
      By.id(".//*[@id='addProductForm']/div[3]/div[1]/div[2]/div[1]/div[4]/div[1]/div/select");
  private static final By dropdownState = By.id("stateCode");
  private static final By buttonAdd = By.id("add");
  private static final By buttonAddProductCoverages = By.id("addProductCoverages");
  private static final String pageTitle = "AddNewProductPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getDropdownProduct() {
    return getDropdown(dropdownProduct);
  }

  public String getDropdownProgram() {
    return getDropdown(dropdownProgram);
  }

  public String getDropdownRatingCompany() {
    return getDropdown(dropdownRatingCompany);
  }

  public String getDropdownState() {
    return getDropdown(dropdownState);
  }

  // METHODS SET
  public void selectDropdownProduct(String value) {
    selectDropdown(dropdownProduct, value);
  }

  public void selectDropdownProgram(String value) {
    selectDropdown(dropdownProgram, value);
  }

  public void selectDropdownRatingCompany(String value) {
    selectDropdown(dropdownRatingCompany, value);
  }

  public void selectDropdownState(String value) {
    selectDropdown(dropdownState, value);
  }

  public void clickButtonAdd(String value) {
    clickObject(buttonAdd);
  }

  public void clickButtonAddProductCoverages(String value) {
    clickObject(buttonAddProductCoverages);
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
          case "Product":
            selectDropdownProduct(value);
            break;
          case "Program":
            selectDropdownProgram(value);
            break;
          case "Rating Company":
            selectDropdownRatingCompany(value);
            break;
          case "State":
            selectDropdownState(value);
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
          case "Product":
            value = getDropdownProduct();
            break;
          case "Program":
            value = getDropdownProgram();
            break;
          case "Rating Company":
            value = getDropdownRatingCompany();
            break;
          case "State":
            value = getDropdownState();
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
