package com.cjs.qa.wellmark.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;

public class DetailsPage extends Page {

  public DetailsPage(WebDriver webDriver) {
    super(webDriver);
  }

  // List<String> sectionList = new Arrays.asList("PatientInfo",
  // "FinancialSum","ClaimSum","YourResp");
  private List<String> listSection = new ArrayList<>();

  private List<String> getListSection() {
    return listSection;
  }

  public void getData() {
    final StringBuilder stringBuilder = new StringBuilder();
    getListSection().add("PatientInfo");
    getListSection().add("FinancialSum");
    getListSection().add("ClaimSum");
    getListSection().add("YourResp");
    for (final String section : getListSection()) {
      final By labels = By.xpath(".//*[@id='" + section + "']/..//tr/th");
      final List<WebElement> elements = getWebDriver().findElements(labels);
      for (WebElement elementLabel : elements) {
        stringBuilder.append(elementLabel.getText());
        stringBuilder.append(Constants.DELIMETER_LIST);
        final WebElement elementValue = elementLabel.findElement(By.xpath("./../td/span"));
        stringBuilder.append(elementValue.getText());
      }
    }
  }
}
