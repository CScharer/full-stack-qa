package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class ManageProductsPage extends Page {

  public ManageProductsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By buttonDeleteProductList = By.id("deleteButton");
  private static final By buttonAddNewProductAdd = By.id("addProductBtn");
  private static final By buttonAddNewProductAddProductCoveragegs = By.id("addProductCvgBtn");
  private static final By dropdownProduct = By.id("productId");
  private static final By dropdownProgram = By.id("programId");
  private static final By dropdownRatingCompany = By.id("addProductForm");
  private static final By dropdownState = By.id("stateCode");
  private static final String pageTitle = "ManageProductsPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void clickButtonDeleteProductList() {
    clickObject(buttonDeleteProductList);
  }

  public void clickButtonAddNewProductAdd() {
    clickObject(buttonAddNewProductAdd);
  }

  public void clickButtonAddNewProductAddProductCoveragegs() {
    clickObject(buttonAddNewProductAddProductCoveragegs);
  }

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
}
