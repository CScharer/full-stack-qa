package com.cjs.qa.hardees;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.hardees.pages.SurveyPage;

public class Hardees {

  private SurveyPage surveyPage;

  public SurveyPage getSurveyPage() {
    return surveyPage;
  }

  public Hardees(WebDriver webDriver) {
    surveyPage = new SurveyPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }
}
