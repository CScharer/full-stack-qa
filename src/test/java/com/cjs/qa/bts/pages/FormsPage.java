package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;

public class FormsPage extends Page {

  public FormsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String formsListBoxBegin = ".//*/tr[contains(.," + Constants.QUOTE_SINGLE;
  private static final String formsListBoxEnd = Constants.QUOTE_SINGLE + ")]/td/input";
  private static final String nodeForms1 = ".//*[@id='form1:forms:";
  private static final By buttonOptionalForms = By.xpath(nodeForms1 + "manualFormsBtn']");
  private static final By buttonUserDefinedForms = By.xpath(nodeForms1 + "userDefinedFormsBtn']");
  private static final By buttonViewExcludedForms =
      By.xpath(nodeForms1 + "forms:excludedFormsBtn']");
  private static final By buttonQuickScroll =
      By.xpath(nodeForms1 + "forms:variableDataBtnGroup']/div[6]/input");
  private static final By buttonDelete = By.xpath(nodeForms1 + "forms:deleteBtn']");
  private static final By buttonUpdateReprint = By.xpath(".//*[@id='form1:forms:printBtn']");
  private static final String pageTitle = "FormsPage";

  public String getPageTitle() {
    return pageTitle;
  }

  // METHODS
  public void clickButtonOptionalForms() {
    clickObject(buttonOptionalForms);
  }

  public void clickButtonUpdateReprint() {
    clickObject(buttonUpdateReprint);
  }

  public void clickButtonUserDefinedForms() {
    clickObject(buttonUserDefinedForms);
  }

  public void clickButtonViewExcludedForms() {
    clickObject(buttonViewExcludedForms);
  }

  public void clickButtonQuickScroll() {
    clickObject(buttonQuickScroll);
  }

  public void clickButtonDelete() {
    clickObject(buttonDelete);
  }

  public void clickFormsBox(String value) {
    final String form = value.substring(0, value.indexOf(';'));
    final String edition = value.substring(value.indexOf(';') + 1, value.length());
    final By formToClick =
        By.xpath(
            formsListBoxBegin
                + form
                + Constants.QUOTE_SINGLE
                + ")][contains(.,"
                + Constants.QUOTE_SINGLE
                + edition
                + formsListBoxEnd);
    clickObject(formToClick);
  }
  // 4211585-10
  // NEED TO STILL FIGURE OUT HOW TO GET DATA FROM FORMS LIST
  // TO MATCH ONLY WHAT WE HAVE ON THE INPUT SHEET
}
