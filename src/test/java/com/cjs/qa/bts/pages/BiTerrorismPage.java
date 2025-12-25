package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiTerrorismPage extends Page {

  public BiTerrorismPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By dropdownDoesTerrorismApply = By.id("TRSM_IND");
  private static final By dropdownDoesTerrorismApplyToCrime = By.id("TRSM_CRIM_IND1");
  private static final By editCertifiedActsOfTerrorismPremium = By.id("TRSM_PREM");
  private static final By editTerrorismFireFollowingPremium = By.id("TRSM_FF_PREM");
  private static final By editOtherActsOfTerrorism = By.id("TRSM_OTH_PREM");
  private static final By buttonFrame = By.id("terrorism-title");
  private static final String pageTitle = "BI_TerrorismPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void selectDropdownDoesTerrorismApply(String value) {
    selectDropdown(dropdownDoesTerrorismApply, value);
  }

  public void selectDropdownDoesTerrorismApplyToCrime(String value) {
    selectDropdown(dropdownDoesTerrorismApplyToCrime, value);
  }

  public void setEditCertifiedActsOfTerrorismPremium(String value) {
    setEdit(editCertifiedActsOfTerrorismPremium, value);
  }

  public void setEditTerrorismFireFollowingPremium(String value) {
    setEdit(editTerrorismFireFollowingPremium, value);
  }

  public void setEditOtherActsOfTerrorism(String value) {
    setEdit(editOtherActsOfTerrorism, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }
}
