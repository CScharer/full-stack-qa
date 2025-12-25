package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiAdvantagePage extends Page {

  public BiAdvantagePage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editYearBusinessStarted = By.id("YR_BUS_STRT");
  private static final By editYearAgencyObtainClient = By.id("YR_CLT_OBTND");
  private static final By checkboxCurrentAutoCoverage = By.id("INSD_AUTO_CVG");
  private static final By checkboxCurrentWorkCompCoverage = By.id("INSD_WC_CVG");
  private static final By checkboxCurrentUmbrellaCoverage = By.id("INSD_UMB_CVG");
  private static final By checkboxCurrentOtherCoverage = By.id("INSD_OTH_CVG");
  private static final By editDescribeOtherCoverage = By.id("DESC_OTH_CVG");
  private static final By dropdownApplicantPartOfFranchise = By.id("APL_FRN_IND");
  private static final By dropdownFormalSafetyProgramInPlace = By.id("FRML_SFTY_PGM_IND");
  private static final By editNumberOfLossesGL = By.id("NUM_LOSE_GL");
  private static final By editNumberOfLossesProperty = By.id("NUM_LOSE_PROP");
  private static final By editNumberOfEmployees = By.id("NUM_EMP");
  private static final By buttonFrame = By.id("advantage-title");
  private static final String pageTitle = "BI_AdvantagePage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void setEditYearBusinessStarted(String value) {
    setEdit(editYearBusinessStarted, value);
  }

  public void setEditYearAgencyObtainClient(String value) {
    setEdit(editYearAgencyObtainClient, value);
  }

  public void toggleCheckboxCurrentAutoCoverage() {
    clickObject(checkboxCurrentAutoCoverage);
  }

  public void toggleCheckboxCurrentWorkCompCoverage() {
    clickObject(checkboxCurrentWorkCompCoverage);
  }

  public void toggleCheckboxCurrentUmbrellaCoverage() {
    clickObject(checkboxCurrentUmbrellaCoverage);
  }

  public void toggleCheckboxCurrentOtherCoverage() {
    clickObject(checkboxCurrentOtherCoverage);
  }

  public void setEditDescribeOtherCoverage(String value) {
    setEdit(editDescribeOtherCoverage, value);
  }

  public void selectDropdownApplicantPartOfFranchise(String value) {
    selectDropdown(dropdownApplicantPartOfFranchise, value);
  }

  public void selectDropdownFormalSafetyProgramInPlace(String value) {
    selectDropdown(dropdownFormalSafetyProgramInPlace, value);
  }

  public void setEditNumberOfLossesGL(String value) {
    setEdit(editNumberOfLossesGL, value);
  }

  public void setEditNumberOfLossesProperty(String value) {
    setEdit(editNumberOfLossesProperty, value);
  }

  public void setEditNumberOfEmployees(String value) {
    setEdit(editNumberOfEmployees, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }
}
