package com.cjs.qa.bts.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class BiButtonsPage extends Page {

  public BiButtonsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By buttonSave = By.id("save");
  private static final By buttonSaveProducts = By.id("saveProducts");
  private static final By buttonCancel = By.id("cancel");
  private static final By buttonQuickScroll = By.id("quickScroll");
  private static final By buttonOpenCloseLeftNav =
      By.xpath("html/body/div[1]/nav/div/div[1]/div[1]/button");
  private static final String pageTitle = "BI_ButtonsPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void clickButtonSave() {
    clickObject(buttonSave);
  }

  public void clickButtonSaveProducts() {
    clickObject(buttonSaveProducts);
  }

  public void clickButtonCancel() {
    clickObject(buttonCancel);
  }

  public void clickButtonQuickScroll() {
    clickObject(buttonQuickScroll);
  }

  public void clickButtonOpenCloseLeftNav() {
    clickObject(buttonOpenCloseLeftNav);
  }
}
