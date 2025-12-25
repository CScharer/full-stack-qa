package com.cjs.qa.bts.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class AddNewPolicyPage extends Page {

  public AddNewPolicyPage(WebDriver webDriver) {
    super(webDriver);
  }

  //   private static final String nodePolicyDetails = "form1:policy:";
  // @FindBy(how = How.ID, using = "usernameT") private WebElement
  // editUsername;
  // @FindBy(id = nodePolicyDetails + "renewalPolicyBI") private By
  // checkboxRenewalPolicy;
  private static final By checkboxRenewalPolicy = By.id("form1:policy:renewalPolicyBI");
  private static final By editEffectiveDate = By.id("form1:policy:effectiveDtBI");
  private static final By dropdownTerm = By.id("form1:policy:termBI");
  private static final By dropdownRenewalTerm = By.id("form1:policy:renewalTermBI");
  private static final By editExpirationDate = By.id("form1:policy:expirationDtBI");
  private static final By dropdownRatingCompany = By.id("form1:policy:policyCompanyBI");
  private static final By dropdownIssuingCompany = By.id("form1:policy:policyProductBI");
  private static final By dropdownProgram = By.id("form1:policy:policyProgramBI");
  private static final By dropdownPrimaryState = By.id("form1:policy:policyStateBI");
  private static final By editReleaseDate = By.id("form1:policy:releaseDtBI");
  private static final By checkboxUserEnteredPolicyNum = By.id("form1:policy:userEnteredPolicyBI");
  private static final By editPolicyNum = By.id("form1:policy:policyNbrBI");
  private static final By buttonSave = By.id("form1:policy:button1");
  private static final By buttonCancel = By.id("form1:policy:button2");
  private static final By clickOffDate = By.id("form1:policy:buildPolicyGrp");
  private static final By editPolicyNumSeq = By.id("form1:policy:policyNbrSeqBI");
  private static final String pageTitle = "AddNewPolicyPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void toggleCheckboxRenewalPolicy() {
    clickObject(checkboxRenewalPolicy);
    clickObject(clickOffDate);
  }

  public void setEditEffectiveDate(String value) {
    setEdit(editEffectiveDate, value);
    clickObject(clickOffDate);
  }

  public void selectTerm(String value) {
    selectDropdown(dropdownTerm, value);
    clickObject(clickOffDate);
  }

  public void selectRenewalTerm(String value) {
    selectDropdown(dropdownRenewalTerm, value);
    clickObject(clickOffDate);
  }

  public void setEditExpirationDate(String value) {
    setEdit(editExpirationDate, value);
    clickObject(clickOffDate);
  }

  public void selectRatingCompany(String value) {
    selectDropdown(dropdownRatingCompany, value);
    clickObject(clickOffDate);
  }

  public void selectIssuingCompany(String value) {
    selectDropdown(dropdownIssuingCompany, value);
    clickObject(clickOffDate);
  }

  public void selectProgram(String value) {
    selectDropdown(dropdownProgram, value);
    clickObject(clickOffDate);
  }

  public void selectPrimaryState(String value) {
    selectDropdown(dropdownPrimaryState, value);
    clickObject(clickOffDate);
  }

  public void setReleaseDate(String value) {
    setEdit(editReleaseDate, value);
    clickObject(clickOffDate);
  }

  public void toggleCheckboxUserEnteredPolicyNum() {
    clickObject(checkboxUserEnteredPolicyNum);
    clickObject(clickOffDate);
  }

  public void enterPolicyNum(String policyNum, String policyNumSeq) {
    if (LABEL_OPTION_CHECKED.equals(getCheckbox(checkboxUserEnteredPolicyNum))) {
      setEdit(editPolicyNum, policyNum);
      clickObject(clickOffDate);
      setEdit(editPolicyNumSeq, policyNumSeq);
      clickObject(clickOffDate);
    }
  }

  public void verifyPolicyNum(String policyNum, String policyNumSeq) {
    final String shownPolicyNum = getEdit(editPolicyNum);
    final String shownPolicyNumSeq = getEdit(editPolicyNumSeq);
    Assert.assertEquals(shownPolicyNumSeq, policyNumSeq);
    Assert.assertEquals(shownPolicyNum, policyNum);
    clickObject(clickOffDate);
  }

  public void clickButtonSave() {
    clickObject(buttonSave);
  }

  public void clickButtonCancel() {
    clickObject(buttonCancel);
  }
}
