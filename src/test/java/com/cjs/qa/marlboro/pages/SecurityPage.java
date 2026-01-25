package com.cjs.qa.marlboro.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class SecurityPage extends Page {

  public SecurityPage(WebDriver webDriver) {
    super(webDriver);
  }

  // private By editPin1 = By.xpath(".//*[@id='createPin_pin1']");
  // private By editPin2 = By.xpath(".//*[@id='createPin_pin2']");
  // private By editPin3 = By.xpath(".//*[@id='createPin_pin3']");
  // private By editPin4 = By.xpath(".//*[@id='createPin_pin4']");
  // private By editPin5 = By.xpath(".//*[@id='createPin_pin5']");
  // private By editVerifyPin1 = By.xpath(".//*[@id='createPin_verifypin1']");
  // private By editVerifyPin2 = By.xpath(".//*[@id='createPin_verifypin2']");
  // private By editVerifyPin3 = By.xpath(".//*[@id='createPin_verifypin3']");
  // private By editVerifyPin4 = By.xpath(".//*[@id='createPin_verifypin4']");
  // private By editVerifyPin5 = By.xpath(".//*[@id='createPin_verifypin5']");
  private static final By buttonEnterPin = By.xpath(".//*[@id='btnEnterPin']");

  public void editSetPin(String pin) {
    Assertions.assertEquals(5, pin.length(), "Pin is not 5 characters in length.");
    for (int index = 0; index < 4; index++) {
      final By editPin = By.xpath(".//*[@id='createPin_pin" + (index + 1) + "']");
      final String value = pin.substring(index, index);
      setEdit(editPin, value);
    }
    for (int index = 0; index < 4; index++) {
      final By editPin = By.xpath(".//*[@id='createPin_verifypin" + (index + 1) + "']");
      final String value = pin.substring(index, index);
      setEdit(editPin, value);
    }
  }

  public void buttonEnterPinClick() {
    clickObject(buttonEnterPin);
  }

  public void buttonNotNowClick() {
    By buttonNotNow = By.xpath(".//*[@id='btnNotNowPin']");
    if (objectExists(buttonNotNow, 500)) {
      clickObject(buttonNotNow);
      return;
    }
    buttonNotNow = By.xpath(".//*[@id='RegisterPinNotNowBtn']");
    if (objectExists(buttonNotNow, 500)) {
      clickObject(buttonNotNow);
    }
  }
}
