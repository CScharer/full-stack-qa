package com.cjs.qa.wellmark.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;

public class PopUpPage extends Page {

  public PopUpPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By labelInvitationPrompt = By.xpath(".//*[@id='oo_invitation_prompt']");
  private static final By buttonNoThanks = By.xpath(".//*[@id='oo_no_thanks']");

  public void linkNoThanksClick() throws QAException {
    waitPageLoaded();
    if (objectExists(labelInvitationPrompt)) {
      clickObject(buttonNoThanks);
    }
  }
}
