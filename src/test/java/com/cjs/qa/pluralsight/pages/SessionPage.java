package com.cjs.qa.pluralsight.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;

public class SessionPage extends Page {

  public SessionPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By bySessionTitle = By.xpath("//h1[@class='course-hero__title']");
  private static final By bySessionInstructor =
      By.xpath(".//*[@id='ps-main']//p[@class='course-hero__byline']");
  private static final By bySessionSynopsis =
      By.xpath(".//*[@id='ps-main']//p[@class='course-hero__excerpt']");

  public String getSessionInstructor() throws QAException {
    return getWebDriver().findElement(bySessionInstructor).getText();
  }

  public String getSessionTitle() throws QAException {
    return getWebDriver().findElement(bySessionTitle).getText();
  }

  public String getSessionSynopsis() throws QAException {
    return getWebDriver().findElement(bySessionSynopsis).getText();
  }

  public String getSessionInformation(String sessionURL) throws QAException {
    getWebDriver().get(sessionURL);
    sleep(3);
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getSessionTitle());
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("By " + getSessionInstructor());
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(getSessionSynopsis());
    stringBuilder.append(Constants.NEWLINE);
    return stringBuilder.toString();
  }
}
