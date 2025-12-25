package com.cjs.qa.marlboro.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;

public class OffersAndActivityPage extends Page {

  public OffersAndActivityPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By button3DollarsOffACarton =
      By.xpath(".//span[contains(text(),'$3 Off a Carton')]");
  // final By buttonClaimYours =
  // By.xpath(".//*[@id='container']//span[.='CLAIM YOURS']");
  private static final By buttonGetYoursByMail =
      By.xpath(
          ".//div[@class='image-cta-link']//div[@class='inner-content']/"
              + "button/i[contains(@class,'btn-ctaIcon')]/../..");
  private static final By buttonCOUPONS =
      By.xpath(".//li[contains(@class,'menu-list-desk')]/a[@data-dtmtext='COUPONS']");
  private static final By buttonSubmit = By.xpath(".//*[@id='cpn-submit']");
  private static final By buttonLogOut = By.xpath(".//*[@id='logout']/span");
  private static final By labelMessage = By.xpath(".//div[@class='coupon-heading']/h6");
  private static final By labelThanks =
      By.xpath(".//div[@class='coupon-heading']/h6[.='THANK YOU FOR YOUR REQUEST.']");
  private static final By labelCouponInTheMail =
      By.xpath(
          ".//div[@class='coupon-heading']/h6[.='LOOKS LIKE YOUR MONTHLY COUPON IS ALREADY IN THE MAIL.']");

  private By getButton3DollarsOffACarton() {
    return button3DollarsOffACarton;
  }

  private By getButtonGetYoursByMail() {
    return buttonGetYoursByMail;
  }

  private By getButtonCOUPONS() {
    return buttonCOUPONS;
  }

  private By getButtonSubmit() {
    return buttonSubmit;
  }

  private By getButtonLogOut() {
    return buttonLogOut;
  }

  private By getLabelMessage() {
    return labelMessage;
  }

  private By getLabelThanks() {
    return labelThanks;
  }

  private By getLabelCouponInTheMail() {
    return labelCouponInTheMail;
  }

  public void button3DollarsOffACartonClick() throws QAException {
    scrollToTop();
    scrollElementIntoView(getWebElement(getButton3DollarsOffACarton()));
    scrollUp(250);
    Environment.setScrollToObject(false);
    clickObject(getButton3DollarsOffACarton());
    Environment.setScrollToObject(true);
  }

  public void buttonGetYoursByMailClick() throws QAException {
    clickObject(getButtonGetYoursByMail());
    boolean waitingVideo = true;
    do {
      if (objectExists(getButton3DollarsOffACarton())) {
        waitingVideo = false;
      }
    } while (waitingVideo);
  }

  public void buttonCOUPONSClick() throws QAException {
    clickObject(getButtonCOUPONS());
  }

  public void buttonSubmitClick() throws QAException {
    scrollToTop();
    scrollElementIntoView(getWebElement(getButton3DollarsOffACarton()));
    scrollUp(250);
    Environment.setScrollToObject(false);
    clickObject(getButtonSubmit());
    Environment.setScrollToObject(true);
  }

  public void buttonLogOutClick() throws QAException {
    clickObject(getButtonLogOut());
  }

  public void collectCoupons() throws QAException {
    // buttonSeeAvailableCouponsClick();
    // buttonCOUPONSClick();
    //
    // buttonClaimYoursClick();
    // buttonGetYoursByMailClick();
    button3DollarsOffACartonClick();
    buttonSubmitClick();
  }

  public String getlabelMessage() throws QAException {
    String message = "";
    WebElement webElement = null;
    if (objectExists(getLabelMessage(), 3)) {
      webElement = getWebElement(getLabelMessage());
      message = webElement.getText();
    }
    final By labelMessage = By.xpath(".//div[@class='error-page']/p[2]/span");
    if (objectExists(labelMessage, 3)) {
      webElement = getWebElement(labelMessage);
      message = webElement.getText();
    }
    return message;
  }

  public boolean labelThanksValidate() throws QAException {
    return objectExists(getLabelThanks(), 3);
  }

  public boolean labelCouponInTheMailValidate() throws QAException {
    boolean collected = false;
    if (objectExists(getLabelCouponInTheMail(), 3)) {
      final WebElement webElement = getWebElement(getLabelCouponInTheMail());
      collected = webElement.isDisplayed();
    }
    return collected;
  }
}
