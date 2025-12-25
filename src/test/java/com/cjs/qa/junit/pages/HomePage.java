package com.cjs.qa.junit.pages;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.SelenideElement;

import io.qameta.allure.Step;

/** Page Object Model for the Home Page */
public class HomePage {

  @Step("Navigate to home page")
  public void navigate() {
    open("/");
  }

  @Step("Get navigation title")
  public SelenideElement getNavigationTitle() {
    // Use the sidebar-title data-qa attribute (h2 element)
    return $("[data-qa='sidebar-title']");
  }

  @Step("Get sidebar")
  public SelenideElement getSidebar() {
    return $("[data-qa='sidebar']");
  }

  @Step("Get sidebar navigation")
  public SelenideElement getSidebarNavigation() {
    return $("[data-qa='sidebar-navigation']");
  }
}
