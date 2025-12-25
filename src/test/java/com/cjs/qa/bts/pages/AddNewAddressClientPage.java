package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class AddNewAddressClientPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(AddNewAddressClientPage.class));

  public AddNewAddressClientPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By dropdownType = By.id("form1:availableList:clientBusinessTypeOneMenu");
  private static final By dropdownOwner = By.id("form1:availableList:ownerTypeMenu");
  private static final By editName1 = By.id("form1:availableList:businessNameAddCli");
  private static final By dropdownName1Type = By.id("form1:availableList:nameType1");
  private static final By editName2 = By.id("form1:availableList:businessName2AddCli");
  private static final By dropdownName2Type = By.id("form1:availableList:nameType2");
  private static final By optionPrimary = By.id("form1:availableList:radio1");
  private static final By editFirstName = By.id("form1:availableList:firstNameAddCli");
  private static final By editLastName = By.id("form1:availableList:lastNameAddCli");
  private static final By editAddrNo = By.id("form1:availableList:addressPanelId:houseNumberId");
  private static final By editAddrLine1 = By.id("form1:availableList:addressPanelId:addrLine1Id");
  private static final By editAddrLine2 = By.id("form1:availableList:addressPanelId:addrLine2Id");
  private static final By editAddrLine3 = By.id("form1:availableList:addressPanelId:addrLine3Id");
  private static final By editAddrLine4 = By.id("form1:availableList:addressPanelId:addrLine4Id");
  private static final By dropdownCountry = By.id("form1:availableList:addressPanelId:countryId");
  private static final By dropdownState = By.id("form1:availableList:addressPanelId:stateId");
  private static final By editCity = By.id("form1:availableList:addressPanelId:cityDisplay");
  private static final By editCounty = By.id("form1:availableList:addressPanelId:countyDisplay");
  private static final By editPostalCode = By.id("form1:availableList:addressPanelId:postalCodeId");
  private static final By buttonClearAddress =
      By.id("form1:availableList:addressPanelId:clearAddressBtnId");
  private static final By buttonLookup =
      By.id("form1:availableList:addressPanelId:lookupPostalCdButton");
  private static final By buttonAdd = By.id("form1:availableList:addressPanelId:btnAdd");
  private static final By buttonDelete = By.id("form1:availableList:addressPanelId:btnDelete");
  private static final By buttonSave = By.id("form1:availableList:addressPanelId:btnSave");
  private static final String pageTitle = "AddNewAddressClientPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getDropdownType() {
    return getDropdown(dropdownType);
  }

  public String getDropdownOwner() {
    return getDropdown(dropdownOwner);
  }

  public String getEditName1() {
    return getEdit(editName1);
  }

  public String getDropdownName1Type() {
    return getDropdown(dropdownName1Type);
  }

  public String getEditName2() {
    return getEdit(editName2);
  }

  public String getDropdownName2Type() {
    return getDropdown(dropdownName2Type);
  }

  public String getOptionPrimary() {
    return getOption(optionPrimary);
  }

  public String getEditFirstName() {
    return getEdit(editFirstName);
  }

  public String getEditLastName() {
    return getEdit(editLastName);
  }

  public String getEditAddrNo() {
    return getEdit(editAddrNo);
  }

  public String getEditAddrLine1() {
    return getEdit(editAddrLine1);
  }

  public String getEditAddrLine2() {
    return getEdit(editAddrLine2);
  }

  public String getEditAddrLine3() {
    return getEdit(editAddrLine3);
  }

  public String getEditAddrLine4() {
    return getEdit(editAddrLine4);
  }

  public String getDropdownCountry() {
    return getDropdown(dropdownCountry);
  }

  public String getDropdownState() {
    return getDropdown(dropdownState);
  }

  public String getEditCity() {
    return getEdit(editCity);
  }

  public String getEditCounty() {
    return getEdit(editCounty);
  }

  public String getEditPostalCode() {
    return getEdit(editPostalCode);
  }

  // METHODS SET
  public void selectDropdownType(String value) {
    selectDropdown(dropdownType, value);
  }

  public void selectDropdownOwner(String value) {
    selectDropdown(dropdownOwner, value);
  }

  public void setEditName1(String value) {
    setEdit(editName1, value);
  }

  public void selectDropdownName1Type(String value) {
    selectDropdown(dropdownName1Type, value);
  }

  public void setEditName2(String value) {
    setEdit(editName2, value);
  }

  public void selectDropdownName2Type(String value) {
    selectDropdown(dropdownName2Type, value);
  }

  public void selectOptionPrimary(String value) {
    selectOption(optionPrimary, value);
  }

  public void setEditFirstName(String value) {
    setEdit(editFirstName, value);
  }

  public void setEditLastName(String value) {
    setEdit(editLastName, value);
  }

  public void setEditAddrNo(String value) {
    setEdit(editAddrNo, value);
  }

  public void setEditAddrLine1(String value) {
    setEdit(editAddrLine1, value);
  }

  public void setEditAddrLine2(String value) {
    setEdit(editAddrLine2, value);
  }

  public void setEditAddrLine3(String value) {
    setEdit(editAddrLine3, value);
  }

  public void setEditAddrLine4(String value) {
    setEdit(editAddrLine4, value);
  }

  public void selectDropdownCountry(String value) {
    selectDropdown(dropdownCountry, value);
  }

  public void selectDropdownState(String value) {
    selectDropdown(dropdownState, value);
  }

  public void setEditCity(String value) {
    setEdit(editCity, value);
  }

  public void setEditCounty(String value) {
    setEdit(editCounty, value);
  }

  public void setEditPostalCode(String value) {
    setEdit(editPostalCode, value);
  }

  public void clickButtonClearAddress() {
    clickObject(buttonClearAddress);
  }

  public void clickButtonLookup() {
    clickObject(buttonLookup);
  }

  public void clickButtonAdd() {
    clickObject(buttonAdd);
  }

  public void clickButtonDelete() {
    clickObject(buttonDelete);
  }

  public void clickButtonSave() {
    clickObject(buttonSave);
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
          case "type":
            selectDropdownType(value);
            break;
          case "owner":
            selectDropdownOwner(value);
            break;
          case "name 1":
            setEditName1(value);
            break;
          case "name 1 type":
            selectDropdownName1Type(value);
            break;
          case "name 2":
            setEditName2(value);
            break;
          case "name2  type":
            selectDropdownName2Type(value);
            break;
          case "primary":
            selectOptionPrimary(value);
            break;
          case "first name":
            setEditFirstName(value);
            break;
          case "last name":
            setEditLastName(value);
            break;
          case "addr no":
            setEditAddrNo(value);
            break;
          case "addrline 1":
            setEditAddrLine1(value);
            break;
          case "addrline 2":
            setEditAddrLine2(value);
            break;
          case "addrline 3":
            setEditAddrLine3(value);
            break;
          case "addrline 4":
            setEditAddrLine4(value);
            break;
          case "country":
            selectDropdownCountry(value);
            break;
          case "state":
            selectDropdownState(value);
            break;
          case "city":
            setEditCity(value);
            break;
          case "county":
            setEditCounty(value);
            break;
          case "postal code":
            setEditPostalCode(value);
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
          case "type":
            value = getDropdownType();
            break;
          case "owner":
            value = getDropdownOwner();
            break;
          case "name 1":
            value = getEditName1();
            break;
          case "name 1 type":
            value = getDropdownName1Type();
            break;
          case "name 2":
            value = getEditName2();
            break;
          case "name2  type":
            value = getDropdownName2Type();
            break;
          case "primary":
            value = getOptionPrimary();
            break;
          case "first name":
            value = getEditFirstName();
            break;
          case "last name":
            value = getEditLastName();
            break;
          case "addr no":
            value = getEditAddrNo();
            break;
          case "addrline 1":
            value = getEditAddrLine1();
            break;
          case "addrline 2":
            value = getEditAddrLine2();
            break;
          case "addrline 3":
            value = getEditAddrLine3();
            break;
          case "addrline 4":
            value = getEditAddrLine4();
            break;
          case "country":
            value = getDropdownCountry();
            break;
          case "state":
            value = getDropdownState();
            break;
          case "city":
            value = getEditCity();
            break;
          case "county":
            value = getEditCounty();
            break;
          case "postal code":
            value = getEditPostalCode();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    actual.put("~", null);
    Assert.assertEquals(getPageTitle() + " validatePage", expected, actual);
  }
}
