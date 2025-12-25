package com.cjs.qa.vivit.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.vivit.VivitEnvironment;

import io.cucumber.datatable.DataTable;

public class MyMemberProfilePage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(MyMemberProfilePage.class));

  public MyMemberProfilePage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By editEmail = By.name("txt_email");
  private static final By editEmailConfirm = By.name("txt_emailConfirm");
  private static final By editGender = By.name("txt_gender");
  private static final By editTitle = By.name("txt_title");
  private static final By editFirstName = By.name("txt_firstName");
  private static final By editMiddleName = By.name("txt_middleName");
  private static final By editLastName = By.name("txt_lastName");
  private static final By editSuffixName = By.name("txt_suffix");
  private static final By editNickname = By.name("txt_nickName");
  private static final By editMaidenName = By.name("txt_maidenName");
  private static final By editBirthday = By.name("dat_birthday");
  private static final By editPersonalURL = By.name("txt_personalURL");
  private static final By editPhoneHomeFaxAreaCode = By.name("txt_homeFaxAreaCode");
  private static final By editPhoneHomeFax = By.name("txt_homeFax");
  private static final By editEmailAlternate = By.name("txt_emailAlternate");
  private static final By editAlternateIMUsername = By.name("txt_alternateIMUsername");
  private static final By editAlternateIMType = By.name("txt_alternateIMType");
  private static final By editEmployeeName = By.name("txt_employName");
  private static final By editEmployeeOwn = By.name("bln_employOwn");
  private static final By editEmployeeTitle = By.name("txt_employTitle");
  private static final By editEmployeeURL = By.name("txt_employURL");
  private static final By editEmployeeAddress1 = By.name("txt_employAddress1");
  private static final By editEmployeeAddress2 = By.name("txt_employAddress2");
  private static final By editEmployeeCity = By.name("txt_employCity");
  private static final By editEmployeeCountry = By.name("txt_employCountry");
  private static final By editEmployeeState = By.name("txt_employState");
  private static final By editEmployeeZip = By.name("txt_employZip");
  private static final By editEmployeePhoneAreaCode = By.name("txt_employPhoneAreaCode");
  private static final By editEmployeePhone = By.name("txt_employPhone");
  private static final By editEmployeeFaxAreaCode = By.name("txt_employFaxAreaCode");
  private static final By editEmployeeFax = By.name("txt_employFax");
  private static final By editEmployeeContinent = By.name("cdlCustomFieldValueIDcontinent");
  private static final By editUsedHPYears = By.name("cdlCustomFieldValueIDUsedHPYears");

  public static final String PAGE_TITLE = "Genesys - EditMyMemberProfilePage";

  public void verifyPage() {
    verifyTitle(PAGE_TITLE);
  }

  // METHODS GET
  public String getEditEmail() {
    return getEdit(editEmail);
  }

  public String getEditEmailConfirm() {
    return getEdit(editEmailConfirm);
  }

  public String getEditGender() {
    return getEdit(editGender);
  }

  public String getEditTitle() {
    return getEdit(editTitle);
  }

  public String getEditFirstName() {
    return getEdit(editFirstName);
  }

  public String getEditMiddleName() {
    return getEdit(editMiddleName);
  }

  public String getEditLastName() {
    return getEdit(editLastName);
  }

  public String getEditSuffixName() {
    return getEdit(editSuffixName);
  }

  public String getEditNickname() {
    return getEdit(editNickname);
  }

  public String getEditMaidenName() {
    return getEdit(editMaidenName);
  }

  public String getEditBirthday() {
    return getEdit(editBirthday);
  }

  public String getEditPersonalURL() {
    return getEdit(editPersonalURL);
  }

  public String getEditPhoneHomeFaxAreaCode() {
    return getEdit(editPhoneHomeFaxAreaCode);
  }

  public String getEditPhoneHomeFax() {
    return getEdit(editPhoneHomeFax);
  }

  public String getEditEmailAlternate() {
    return getEdit(editEmailAlternate);
  }

  public String getEditAlternateIMUsername() {
    return getEdit(editAlternateIMUsername);
  }

  public String getEditAlternateIMType() {
    return getEdit(editAlternateIMType);
  }

  public String getEditEmployeeName() {
    return getEdit(editEmployeeName);
  }

  public String getEditEmployeeOwn() {
    return getEdit(editEmployeeOwn);
  }

  public String getEditEmployeeTitle() {
    return getEdit(editEmployeeTitle);
  }

  public String getEditEmployeeURL() {
    return getEdit(editEmployeeURL);
  }

  public String getEditEmployeeAddress1() {
    return getEdit(editEmployeeAddress1);
  }

  public String getEditEmployeeAddress2() {
    return getEdit(editEmployeeAddress2);
  }

  public String getEditEmployeeCity() {
    return getEdit(editEmployeeCity);
  }

  public String getEditEmployeeCountry() {
    return getEdit(editEmployeeCountry);
  }

  public String getEditEmployeeState() {
    return getEdit(editEmployeeState);
  }

  public String getEditEmployeeZip() {
    return getEdit(editEmployeeZip);
  }

  public String getEditEmployeePhoneAreaCode() {
    return getEdit(editEmployeePhoneAreaCode);
  }

  public String getEditEmployeePhone() {
    return getEdit(editEmployeePhone);
  }

  public String getEditEmployeeFaxAreaCode() {
    return getEdit(editEmployeeFaxAreaCode);
  }

  public String getEditEmployeeFax() {
    return getEdit(editEmployeeFax);
  }

  public String getEditEmployeeContinent() {
    return getEdit(editEmployeeContinent);
  }

  public String getEditUsedHPYears() {
    return getEdit(editUsedHPYears);
  }

  // METHODS SET
  public void setEditEmail(String value) {
    setEdit(editEmail, value);
  }

  public void setEditEmailConfirm(String value) {
    setEdit(editEmailConfirm, value);
  }

  public void setEditGender(String value) {
    setEdit(editGender, value);
  }

  public void setEditTitle(String value) {
    setEdit(editTitle, value);
  }

  public void setEditFirstName(String value) {
    setEdit(editFirstName, value);
  }

  public void setEditMiddleName(String value) {
    setEdit(editMiddleName, value);
  }

  public void setEditLastName(String value) {
    setEdit(editLastName, value);
  }

  public void setEditSuffixName(String value) {
    setEdit(editSuffixName, value);
  }

  public void setEditNickname(String value) {
    setEdit(editNickname, value);
  }

  public void setEditMaidenName(String value) {
    setEdit(editMaidenName, value);
  }

  public void setEditBirthday(String value) {
    setEdit(editBirthday, value);
  }

  public void setEditPersonalURL(String value) {
    setEdit(editPersonalURL, value);
  }

  public void setEditPhoneHomeFaxAreaCode(String value) {
    setEdit(editPhoneHomeFaxAreaCode, value);
  }

  public void setEditPhoneHomeFax(String value) {
    setEdit(editPhoneHomeFax, value);
  }

  public void setEditEmailAlternate(String value) {
    setEdit(editEmailAlternate, value);
  }

  public void setEditAlternateIMUsername(String value) {
    setEdit(editAlternateIMUsername, value);
  }

  public void setEditAlternateIMType(String value) {
    setEdit(editAlternateIMType, value);
  }

  public void setEditEmployeeName(String value) {
    setEdit(editEmployeeName, value);
  }

  public void setEditEmployeeOwn(String value) {
    setEdit(editEmployeeOwn, value);
  }

  public void setEditEmployeeTitle(String value) {
    setEdit(editEmployeeTitle, value);
  }

  public void setEditEmployeeURL(String value) {
    setEdit(editEmployeeURL, value);
  }

  public void setEditEmployeeAddress1(String value) {
    setEdit(editEmployeeAddress1, value);
  }

  public void setEditEmployeeAddress2(String value) {
    setEdit(editEmployeeAddress2, value);
  }

  public void setEditEmployeeCity(String value) {
    setEdit(editEmployeeCity, value);
  }

  public void setEditEmployeeCountry(String value) {
    setEdit(editEmployeeCountry, value);
  }

  public void setEditEmployeeState(String value) {
    setEdit(editEmployeeState, value);
  }

  public void setEditEmployeeZip(String value) {
    setEdit(editEmployeeZip, value);
  }

  public void setEditEmployeePhoneAreaCode(String value) {
    setEdit(editEmployeePhoneAreaCode, value);
  }

  public void setEditEmployeePhone(String value) {
    setEdit(editEmployeePhone, value);
  }

  public void setEditEmployeeFaxAreaCode(String value) {
    setEdit(editEmployeeFaxAreaCode, value);
  }

  public void setEditEmployeeFax(String value) {
    setEdit(editEmployeeFax, value);
  }

  public void setEditEmployeeContinent(String value) {
    setEdit(editEmployeeContinent, value);
  }

  public void setEditUsedHPYears(String value) {
    setEdit(editUsedHPYears, value);
  }

  public void populatePageAndValidate(DataTable dataTable) {
    populatePage(dataTable);
    validatePage(dataTable);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "email":
            setEditEmail(value);
            break;
          case "email confirm":
            setEditEmailConfirm(value);
            break;
          case "gender":
            setEditGender(value);
            break;
          case "title":
            setEditTitle(value);
            break;
          case "first name":
            setEditFirstName(value);
            break;
          case "middle name":
            setEditMiddleName(value);
            break;
          case "last name":
            setEditLastName(value);
            break;
          case "suffix name":
            setEditSuffixName(value);
            break;
          case "nickname":
            setEditNickname(value);
            break;
          case "maiden name":
            setEditMaidenName(value);
            break;
          case "birthday":
            setEditBirthday(value);
            break;
          case "personal url":
            setEditPersonalURL(value);
            break;
          case "phone home fax area code":
            setEditPhoneHomeFaxAreaCode(value);
            break;
          case "phone home fax":
            setEditPhoneHomeFax(value);
            break;
          case "email alternate":
            setEditEmailAlternate(value);
            break;
          case "alternate im username":
            setEditAlternateIMUsername(value);
            break;
          case "alternate im type":
            setEditAlternateIMType(value);
            break;
          case "employee name":
            setEditEmployeeName(value);
            break;
          case "employee own":
            setEditEmployeeOwn(value);
            break;
          case "employee title":
            setEditEmployeeTitle(value);
            break;
          case "employee url":
            setEditEmployeeURL(value);
            break;
          case "employee address1":
            setEditEmployeeAddress1(value);
            break;
          case "employee address2":
            setEditEmployeeAddress2(value);
            break;
          case "employee city":
            setEditEmployeeCity(value);
            break;
          case "employee country":
            setEditEmployeeCountry(value);
            break;
          case "employee state":
            setEditEmployeeState(value);
            break;
          case "employee zip":
            setEditEmployeeZip(value);
            break;
          case "employee phone area code":
            setEditEmployeePhoneAreaCode(value);
            break;
          case "employee phone":
            setEditEmployeePhone(value);
            break;
          case "employee fax area code":
            setEditEmployeeFaxAreaCode(value);
            break;
          case "employee fax":
            setEditEmployeeFax(value);
            break;
          case "employee continent":
            setEditEmployeeContinent(value);
            break;
          case "used hp years":
            setEditUsedHPYears(value);
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
    final Map<String, String> mapExpected = new HashMap<>();
    final Map<String, String> mapActual = new HashMap<>();
    final List<List<String>> listData = table.asLists();
    for (final List<?> item : listData) {
      final String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (VivitEnvironment.isLogAll()) {
          LOG.debug("{Field}{}, {Value}{}", field, value);
        }
        mapExpected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "email":
            value = getEditEmail();
            break;
          case "email confirm":
            value = getEditEmailConfirm();
            break;
          case "gender":
            value = getEditGender();
            break;
          case "title":
            value = getEditTitle();
            break;
          case "first name":
            value = getEditFirstName();
            break;
          case "middle name":
            value = getEditMiddleName();
            break;
          case "last name":
            value = getEditLastName();
            break;
          case "suffix name":
            value = getEditSuffixName();
            break;
          case "nickname":
            value = getEditNickname();
            break;
          case "maiden name":
            value = getEditMaidenName();
            break;
          case "birthday":
            value = getEditBirthday();
            break;
          case "personal url":
            value = getEditPersonalURL();
            break;
          case "phone home fax area code":
            value = getEditPhoneHomeFaxAreaCode();
            break;
          case "phone home fax":
            value = getEditPhoneHomeFax();
            break;
          case "email alternate":
            value = getEditEmailAlternate();
            break;
          case "alternate im username":
            value = getEditAlternateIMUsername();
            break;
          case "alternate im type":
            value = getEditAlternateIMType();
            break;
          case "employee name":
            value = getEditEmployeeName();
            break;
          case "employee own":
            value = getEditEmployeeOwn();
            break;
          case "employee title":
            value = getEditEmployeeTitle();
            break;
          case "employee url":
            value = getEditEmployeeURL();
            break;
          case "employee address1":
            value = getEditEmployeeAddress1();
            break;
          case "employee address2":
            value = getEditEmployeeAddress2();
            break;
          case "employee city":
            value = getEditEmployeeCity();
            break;
          case "employee country":
            value = getEditEmployeeCountry();
            break;
          case "employee state":
            value = getEditEmployeeState();
            break;
          case "employee zip":
            value = getEditEmployeeZip();
            break;
          case "employee phone area code":
            value = getEditEmployeePhoneAreaCode();
            break;
          case "employee phone":
            value = getEditEmployeePhone();
            break;
          case "employee fax area code":
            value = getEditEmployeeFaxAreaCode();
            break;
          case "employee fax":
            value = getEditEmployeeFax();
            break;
          case "employee continent":
            value = getEditEmployeeContinent();
            break;
          case "used hp years":
            value = getEditUsedHPYears();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        mapActual.put(field, value);
      }
    }
    // Assert.assertSame(PAGE_TITLE + " validatePage", expected, actual)
    // hardAssert.assertSame(actual, expected, PAGE_TITLE + "
    // validatePage")
    // softAssert.assertSame(actual, expected, PAGE_TITLE + "
    // validatePage")
  }
}
