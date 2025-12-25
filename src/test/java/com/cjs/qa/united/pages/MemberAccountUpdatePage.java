package com.cjs.qa.united.pages;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

public class MemberAccountUpdatePage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(MemberAccountUpdatePage.class));

  public MemberAccountUpdatePage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String nodeLabel = "/*[@id='divContactInfo']/section";
  private static final By labelAddress = By.xpath("./" + nodeLabel + "/h3[.='Address']/../div");
  private static final By labelEmailAddress =
      By.xpath("./" + nodeLabel + "/h3[.='Email address']/../div");
  private static final By labelPhoneNumber =
      By.xpath("./" + nodeLabel + "/h3[.='Phone number']/../div");
  private static final By buttonAcceptAndSubmit = By.xpath(".//*[@id='btnAccept']");
  private static final By buttonAcceptAndUpdateLater = By.xpath(".//*[@id='btnUpdateLater']");

  public String getLabelAddress() throws QAException {
    return getLabel(labelAddress);
  }

  public String getLabelEmailAddress() throws QAException {
    return getLabel(labelEmailAddress);
  }

  public String getLabelPhoneNumber() throws QAException {
    return getLabel(labelPhoneNumber);
  }

  public boolean validateMemberAccountInformation(
      String address, String eMailAddress, String phoneNumber) throws QAException {
    sleep(3);
    if (objectExists(By.xpath(".//h1[@class='visuallyHidden']"), 5)) {
      return true;
    }
    int matches = 3;
    String actual = getLabelAddress();
    if (!address.equalsIgnoreCase(actual)) {
      matches--;
      LOG.warn("Address Expected:[{}], Actual:[{}]", address, actual);
    }
    actual = getLabelEmailAddress();
    if (!eMailAddress.equalsIgnoreCase(actual)) {
      matches--;
      LOG.warn("Email Address Expected:[{}], Actual:[{}]", eMailAddress, actual);
    }
    actual = getLabelPhoneNumber();
    if (!phoneNumber.equalsIgnoreCase(actual)) {
      matches--;
      LOG.warn("Phone Number Expected:[{}], Actual:[{}]", phoneNumber, actual);
    }
    if (matches == 3) {
      buttonAcceptAndSubmitClick();
    } else {
      buttonAcceptAndUpdateLaterClick();
    }
    return matches == 3;
  }

  public void buttonAcceptAndSubmitClick() throws QAException {
    clickObject(buttonAcceptAndSubmit);
    sleep(1);
  }

  public void buttonAcceptAndUpdateLaterClick() throws QAException {
    clickObject(buttonAcceptAndUpdateLater);
    sleep(1);
  }
}
