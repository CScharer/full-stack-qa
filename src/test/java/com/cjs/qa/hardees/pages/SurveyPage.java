package com.cjs.qa.hardees.pages;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.hardees.HardeesEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class SurveyPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SurveyPage.class));

  public SurveyPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String nodeRoot = ".//*[@id='surveyQuestions']";
  private static final By editCN1 = By.xpath(".//*[@id='CN1']");
  private static final By editCN2 = By.xpath(".//*[@id='CN2']");
  private static final By editCN3 = By.xpath(".//*[@id='CN3']");
  private static final By editCN4 = By.xpath(".//*[@id='CN4']");
  private static final By editCN5 = By.xpath(".//*[@id='CN5']");
  private static final By editCN6 = By.xpath(".//*[@id='CN6']");
  private static final By buttonStart = By.xpath(".//*[@id='NextButton']");
  // Same as buttonStart
  private static final By buttonNext = By.xpath(".//*[@id='NextButton']");
  private static final By checkboxAgree = By.name("checkboxAgree");
  private static final By labelThankYou = By.name("labelThankYou");

  private By getCheckboxAgree() {
    return checkboxAgree;
  }

  public By getLabelThankYou() {
    return labelThankYou;
  }

  private By getEditCN1() {
    return editCN1;
  }

  private By getEditCN2() {
    return editCN2;
  }

  private By getEditCN3() {
    return editCN3;
  }

  private By getEditCN4() {
    return editCN4;
  }

  private By getEditCN5() {
    return editCN5;
  }

  private By getEditCN6() {
    return editCN6;
  }

  private By getButtonStart() {
    return buttonStart;
  }

  private By getButtonNext() {
    return buttonNext;
  }

  public void editCN1Set(String value) throws QAException {
    setEdit(getEditCN1(), value);
  }

  public void editCN2Set(String value) throws QAException {
    setEdit(getEditCN2(), value);
  }

  public void editCN3Set(String value) throws QAException {
    setEdit(getEditCN3(), value);
  }

  public void editCN4Set(String value) throws QAException {
    setEdit(getEditCN4(), value);
  }

  public void editCN5Set(String value) throws QAException {
    setEdit(getEditCN5(), value);
  }

  public void editCN6Set(String value) throws QAException {
    setEdit(getEditCN6(), value);
  }

  public void optionDidYouVisistTheMcDonaldsLocatedAtSelect(String value) throws QAException {
    String xPath = nodeRoot;
    // By optionDidYouVisit = By.xpath(nodeRoot);
    switch (value) {
      case "No":
        xPath +=
            "//td[contains(text(),"
                + Constants.QUOTE_DOUBLE
                + "Did you visit the McDonald's located at"
                + Constants.QUOTE_DOUBLE
                + ")]/../td[@class='Opt2 inputtyperbloption']/span";
        break;
      case "Yes":
      default:
        xPath +=
            "//td[contains(text(),"
                + Constants.QUOTE_DOUBLE
                + "Did you visit the McDonald's located at"
                + Constants.QUOTE_DOUBLE
                + ")]/../td[@class='Opt1 inputtyperbloption']/span";
        break;
    }
    final By optionDidYouVisit = By.xpath(xPath);
    clickObject(optionDidYouVisit);
  }

  public void optionOrderTypeSelect(String value) throws QAException {
    String xPath = nodeRoot;
    // By optionOrderType = By.xpath(nodeRoot);
    switch (value) {
      case "Carry out:":
      case "Dine-in":
      case "Drive-thru":
      default:
        // xPath += "//td[contains(text()," + Constants.QUOTE_DOUBLE +
        // "Did you
        // visit the McDonald's
        // located at" + Constants.QUOTE_DOUBLE)]/../td[@class='Opt1
        // inputtyperbloption']/span";
        xPath += "//label[.='" + value + "']";
        break;
    }
    final By optionOrderType = By.xpath(xPath);
    clickObject(optionOrderType);
  }

  public void optionOverallSatisfactionSelect(String value) throws QAException {
    String xPath = nodeRoot;
    // By optionOverallSatisfaction = By.xpath(nodeRoot);
    switch (value) {
      case "Highly Satisfied":
        xPath +=
            ".//td[contains(text(),'Please rate your overall satisfaction with your"
                + " experience at this')]/../td[@class='Opt5 inputtyperbloption']/span";
        break;
      case "Satisfied":
        xPath +=
            ".//td[contains(text(),'Please rate your overall satisfaction with your"
                + " experience at this')]/../td[@class='Opt4 inputtyperbloption']/span";
        break;
      case "Neither Satisfied nor Dissatisfied":
        xPath +=
            ".//td[contains(text(),'Please rate your overall satisfaction with your"
                + " experience at this')]/../td[@class='Opt3 inputtyperbloption']/span";
        break;
      case "Dissatisfied":
        xPath +=
            ".//td[contains(text(),'Please rate your overall satisfaction with your"
                + " experience at this')]/../td[@class='Opt2 inputtyperbloption']/span";
        break;
      case "Highly Dissatisfied":
        xPath +=
            ".//td[contains(text(),'Please rate your overall satisfaction with your"
                + " experience at this')]/../td[@class='Opt1 inputtyperbloption']/span";
        break;
      default:
        xPath +=
            ".//td[contains(text(),'Please rate your overall satisfaction with your"
                + " experience at this')]/../td[@class='Opt3 inputtyperbloption']/span";
        break;
    }
    final By optionOverallSatisfaction = By.xpath(xPath);
    clickObject(optionOverallSatisfaction);
  }

  public void optionsSatisfactionSelect() throws QAException {
    final String xPath = "//td[@class='Opt4 inputtyperbloption']";
    final By optionsSatisfaction = By.xpath(xPath);
    final List<WebElement> webElements = getWebDriver().findElements(optionsSatisfaction);
    for (final WebElement webElement : webElements) {
      clickObject(webElement);
    }
  }

  public void optionDidYouExerienceAProblemDuringYourVisitSelect(String value) throws QAException {
    String xPath = nodeRoot;
    // By optionDidYouExerienceAProblemDuringYourVisit =
    // By.xpath(nodeRoot);
    switch (value) {
      case "No":
        xPath +=
            "//td[contains(text(),'Did you experience a problem during your"
                + " visit')]/../td[@class='Opt2 inputtyperbloption']/span";
        break;
      case "Yes":
      default:
        xPath +=
            "//td[contains(text(),'Did you experience a problem during your"
                + " visit')]/../td[@class='Opt1 inputtyperbloption']/span";
        break;
    }
    final By optionDidYouExerienceAProblemDuringYourVisit = By.xpath(xPath);
    clickObject(optionDidYouExerienceAProblemDuringYourVisit);
  }

  public void optionWereYouAskedToPullForwardSelect(String value) throws QAException {
    String xPath = nodeRoot;
    // By optionWereYouAskedToPullForward =
    // By.xpath(nodeRoot);
    switch (value) {
      case "No":
        xPath +=
            "//td[contains(text(),'Did you experience a problem during your"
                + " visit')]/../td[@class='Opt2 inputtyperbloption']/span";
        break;
      case "Yes":
      default:
        xPath +=
            "//td[contains(text(),'Did you experience a problem during your"
                + " visit')]/../td[@class='Opt1 inputtyperbloption']/span";
        break;
    }
    final By optionWereYouAskedToPullForward = By.xpath(xPath);
    clickObject(optionWereYouAskedToPullForward);
  }

  public void setCheckboxAgree(String value) throws QAException {
    setCheckbox(getCheckboxAgree(), value);
  }

  public void buttonStartClick() throws QAException {
    clickObject(getButtonStart());
  }

  public void buttonNextClick() throws QAException {
    clickObject(getButtonNext());
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[{}]", HardeesEnvironment.URL_LOGIN);
    getWebDriver().get(HardeesEnvironment.URL_LOGIN);
  }

  public void populate() throws QAException {
    load();
    editCN1Set("00398");
    editCN2Set("13430");
    editCN3Set("40217");
    editCN4Set("12099");
    editCN5Set("00153");
    editCN6Set("4");
    buttonStartClick();
    optionDidYouVisistTheMcDonaldsLocatedAtSelect("Yes");
    buttonNextClick();
    optionOrderTypeSelect("Drive-thru");
    buttonNextClick();
    // optionOverallSatisfactionSelect("Satisfied");
    optionsSatisfactionSelect();
    buttonNextClick();
    optionsSatisfactionSelect();
    buttonNextClick();
    optionsSatisfactionSelect();
    buttonNextClick();
    optionDidYouExerienceAProblemDuringYourVisitSelect("No");
    buttonNextClick();
    optionsSatisfactionSelect();
    buttonNextClick();
    buttonNextClick();
    optionWereYouAskedToPullForwardSelect("No");
    buttonNextClick();
  }
}
