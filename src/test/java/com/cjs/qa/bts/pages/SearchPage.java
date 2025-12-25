package com.cjs.qa.bts.pages;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class SearchPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SearchPage.class));

  public SearchPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editPolicyNumber = By.id("policyNumber");
  private static final By editSequenceNumber = By.id("sequenceNumber");
  private static final By checkboxIncludeAllPolicies = By.id("includeAllPolicies");
  private static final By editFirstName = By.id("firstName");
  private static final By editLastName = By.id("lastName");
  private static final By editBusinessName = By.id("businessName");
  private static final By editClientID = By.id("clientId");
  private static final By editFEIN = By.id("fein");
  private static final By buttonAddClientAndPolicy = By.id("addClientAndPolicy");
  private static final By buttonAddClientAndQuote = By.id("addClientAndQuote");
  private static final By buttonNavLeft = By.xpath("//button[contains(@data-sidebar,'left')]");
  private static final By buttonNavRight = By.xpath("//button[contains(@data-sidebar,'right')]");
  private static final By linkAddNewPolicy = By.linkText("Add New Policy");
  private static final By linkAddNewQuote = By.linkText("Add New Quote");
  private static final By linkClientInformation = By.linkText("Client Information");
  private static final By linkTransferOwnership = By.linkText("Transfer Ownership");
  private static final By imagePrimaryLogo = By.xpath("//div[@class='primary-logo']");
  private static final By buttonSearch = By.id("searchButton");
  private static final By buttonClear = By.id("clearButton");
  private static final By dropdownLogin =
      By.xpath("//button[contains(@class,'btn-primary')][@data-toggle='Dropdown']");
  private static final String pageTitle = "SearchPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void setEditPolicyNumber(String value) {
    setEdit(editPolicyNumber, value);
  }

  public void setEditSequenceNumber(String value) {
    setEdit(editSequenceNumber, value);
  }

  public void setEditFirstName(String value) {
    setEdit(editFirstName, value);
  }

  public void setEditLastName(String value) {
    setEdit(editLastName, value);
  }

  public void setEditBusinessName(String value) {
    setEdit(editBusinessName, value);
  }

  public void setEditFEIN(String value) {
    setEdit(editFEIN, value);
  }

  public void setEditClientID(String value) {
    setEdit(editClientID, value);
  }

  public void toggleCheckboxIncludeAllPolicies() {
    clickObject(checkboxIncludeAllPolicies);
  }

  public void clickButtonAddClientAndPolicy() {
    clickObject(buttonAddClientAndPolicy);
  }

  public void clickButtonAddClientQuote() {
    clickObject(buttonAddClientAndQuote);
  }

  public void clickbuttonNavLeft() {
    clickObject(buttonNavLeft);
  }

  public void clickbuttonNavRight() {
    clickObject(buttonNavRight);
  }

  public void clickImagePrimaryLogo() {
    clickObject(imagePrimaryLogo);
  }

  public void clickButtonSearch() {
    clickObject(buttonSearch);
  }

  public void clickButtonClear() {
    clickObject(buttonClear);
  }

  public void clickDropdownLogin() {
    clickObject(dropdownLogin);
  }

  public void selectSearchResults(String value) {
    clickObject(By.partialLinkText(value));
  }

  public void clickLinkAddNewPolicy() {
    clickObject(linkAddNewPolicy);
  }

  public void clickLinkAddNewQuote() {
    clickObject(linkAddNewQuote);
  }

  public void clickLinkClientInformation() {
    clickObject(linkClientInformation);
  }

  public void clickLinkTransferOwnership() {
    clickObject(linkTransferOwnership);
  }

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
          case "policy number":
            setEditPolicyNumber(value);
            break;
          case "sequence number":
            setEditSequenceNumber(value);
            break;
          case "include all policies":
            toggleCheckboxIncludeAllPolicies();
            break;
          case "first name":
            setEditFirstName(value);
            break;
          case "last name":
            setEditLastName(value);
            break;
          case "client id":
            setEditClientID(value);
            break;
          case "fein":
            setEditFEIN(value);
            break;
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }
}
