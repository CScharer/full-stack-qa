package com.cjs.qa.marlboro.pages;

import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.marlboro.MarlboroEnvironment;
import com.cjs.qa.selenium.Page;

public class VerifyInformationInterruptPage extends Page {

  public VerifyInformationInterruptPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String urlVerifyInformation =
      MarlboroEnvironment.URL_BASE + "/marlboro/Security/VerifyInformationInterrupt";
  private static final By checkboxCigarettes = By.xpath(".//span[.='Cigarettes']/../div/span");
  private static final By dropdownRegularBrand =
      By.xpath(
          "//div[@data-orgtext][contains(text(),"
              + "'What is your regular brand of cigarettes, that is, the brand you buy most often?')]"
              + "/../span/select");
  private By dropdownRegularBrandNonMenthol =
      By.xpath(
          "//div[@data-orgtext][contains(text(),'What Non-Menthol pack do you buy most often?')]/../span/select");
  private static final By buttonNext = By.xpath(".//*[@id='verifyinfoInterruptnxtBtn']");

  private By getCheckboxCigarettes() {
    return checkboxCigarettes;
  }

  private By getDropdownRegularBrand() {
    return dropdownRegularBrand;
  }

  private By getDropdownRegularBrandNonMenthol() {
    return dropdownRegularBrandNonMenthol;
  }

  private By getButtonNext() {
    return buttonNext;
  }

  private String getUrlVerifyInformation() {
    return urlVerifyInformation;
  }

  public void checkboxCigarettesSet(String value) {
    setCheckbox(getCheckboxCigarettes(), value);
  }

  public void dropdownRegularBrandSelect(String value) {
    selectDropdown(getDropdownRegularBrand(), value);
  }

  public void optionRegularBrandSelect(String value) {
    final String xPath =
        ".//div[.='Please tell us about your regular brand.']/..//label[contains(text(),'"
            + value
            + "')]/..";
    final By optionRegularBrand = By.xpath(xPath);
    clickObject(optionRegularBrand);
  }

  public void dropdownRegularBrandNonMentholSelect(String value) {
    selectDropdown(getDropdownRegularBrandNonMenthol(), value);
  }

  public void buttonNextClick() {
    clickObject(getButtonNext());
  }

  public void populatePage() {
    if (!getWebDriver()
        .getCurrentUrl()
        .toLowerCase(Locale.ENGLISH)
        .equals(getUrlVerifyInformation().toLowerCase(Locale.ENGLISH))) {
      return;
    }
    // checkboxCigarettesSet(LABEL_OPTION_CHECKED);
    dropdownRegularBrandSelect("Marlboro");
    optionRegularBrandSelect("Non-Menthol");
    dropdownRegularBrandNonMentholSelect("Marlboro Gold Pack");
    buttonNextClick();
  }
}
