package com.cjs.qa.microsoft.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.objects.Answers;
import com.cjs.qa.microsoft.objects.PointsCard;
import com.cjs.qa.utilities.GuardedLogger;

public class DailyPollQuizPages extends RewardsPage {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(DailyPollQuizPages.class));
  private List<Answers> answersList;
  private static final By bySearchEdit = By.xpath(".//input[@name='q']");
  private static final By byPointsAvailableLabel =
      By.xpath(".//*[@id='quizWelcomeContainer']//span[@class='rqWcCredits']");
  private static final By byPointsEarnedLabel =
      By.xpath(".//*[@id='btoHeadPanel']//span[@class='rqECredits']");
  private static final By byWelcomeLabel =
      By.xpath(".//*[@id='quizWelcomeContainer']/div[@class='rqText']");
  private static final By byStartPlayingButton = By.xpath(".//*[@id='rqStartQuiz']");
  private static final By byQuestionID = By.xpath(".//*[@id='questionId']");
  private static final By byQuizComplete = By.xpath(".//*[@id='quizCompleteContainer']/div");
  private static final By byAnswerCorrectMessageLabel = By.xpath(".//*[@id='rqcorrectAns']");
  private static final By byAnswerWrongMessageLabel = By.xpath(".//*[@id='wrongAnswerMessage']");

  private String pointsAvailable;

  // Reserved for future use - currently unused but kept for API compatibility
  @SuppressWarnings("unused")
  private int answersNeeded;

  /**
   * @param webDriver
   */
  public DailyPollQuizPages(WebDriver webDriver) {
    super(webDriver);
  }

  /**
   * @throws QAException
   */
  private void clickStartPlayingButton() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    WebElement webElement;
    getQuizWelcomeValue();
    if (objectExists(byStartPlayingButton, 3)) {
      webElement = scrollToElementAndHighlightBriefly(byStartPlayingButton);
      sleep(3);
      clickObject(webElement);
    }
    sleep(3);
    webElement = scrollToElementAndHighlightBriefly(byQuestionID);
    LOG.debug("Question: [{}]", webElement.getText());
    boolean ordering = false;
    if (webElement.getText().toLowerCase(Locale.ENGLISH).contains("order")) {
      LOG.info("Ordering Items");
      ordering = true;
    }
    final int answersNeeded = getAnswersNeeded();
    for (int answerNumber = 0; answerNumber < answersNeeded; answerNumber++) {
      getAnswers(ordering);
      if (ordering) {
        completeQuizByOrder();
      } else {
        completeQuizBySelection();
      }
      if (!objectExists(byQuizComplete, 5)) {
        // Quiz completion check - continue if not complete
        assert true; // No-op statement to satisfy PMD
      }
    }
  }

  public void completeQuizByOrder() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    for (int answerIndex = 0; answerIndex < answersList.size() - 1; answerIndex++) {
      getAnswers(true);
      final Answers answers = answersList.get(answerIndex);
      final int answerActualIndex = Integer.valueOf(answers.getDataOptionNumber());
      final int answerExpectedIndex = Integer.valueOf(answers.getDataID()) - 1;
      if (answerActualIndex != answerExpectedIndex) {
        final WebElement moveFromWebElement =
            scrollToElementAndHighlightBriefly(
                By.xpath(
                    ".//div[@class='rq_button']//*[@type='button'][@data-optionnumber='"
                        + answerActualIndex
                        + "']"));
        final WebElement moveToWebElement =
            scrollToElementAndHighlightBriefly(
                By.xpath(
                    ".//div[@class='rq_button']//*[@type='button'][@data-optionnumber='"
                        + answerExpectedIndex
                        + "']"));
        dragAndDrop(moveFromWebElement, moveToWebElement);
        sleep(2, 500);
      }
    }
  }

  public void completeQuizBySelection() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    for (final Answers answers : answersList) {
      final String xPath =
          ".//div[@class='rq_button']//*[@type='button'][@data-option='"
              + answers.getDataOption()
              + "']";
      WebElement webElement = scrollToElementAndHighlightBriefly(By.xpath(xPath));
      clickObject(webElement);
      if (objectExists(byAnswerCorrectMessageLabel, 5)) {
        webElement = scrollToElementAndHighlightBriefly(byAnswerCorrectMessageLabel);
        answers.setCorrect(true);
        break;
      }
      if (objectExists(byAnswerWrongMessageLabel, 5)) {
        webElement = scrollToElementAndHighlightBriefly(byAnswerWrongMessageLabel);
        answers.setCorrect(false);
      }
      webElement = scrollToElementAndHighlightBriefly(byPointsEarnedLabel);
    }
  }

  private void getAnswers(boolean ordering) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final By byXPath =
        By.xpath(
            ".//div[@id='currentQuestionContainer']//div[@class='rq_button']//*[@type='button']");
    answersList = new ArrayList<>();
    final List<WebElement> webElements = getWebDriver().findElements(byXPath);
    for (WebElement webElement : webElements) {
      webElement = scrollToElementAndHighlightBriefly(webElement);
      final Answers answers = new Answers();
      answersList.add(answers);
      // This is the order number that the object should be in.
      answers.setId(webElement.getAttribute("id"));
      answers.setValue(webElement.getAttribute("value"));
      answers.setDataID(webElement.getAttribute("data-id"));
      answers.setDataSerpquery(webElement.getAttribute("data-serpquery"));
      answers.setDataOptionNumber(webElement.getAttribute("data-optionnumber"));
      answers.setDataOption(webElement.getAttribute("data-option"));
      LOG.debug("answers: ({})", answers.toString());
      if (ordering) {
        // Update correct state.
        if (Integer.valueOf(answers.getDataID()) - 1
            == Integer.valueOf(answers.getDataOptionNumber())) {
          answers.setCorrect(true);
        }
      }
    }
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  public void getDailyPollPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    getQuizSearchValue();
    // Find the group of options and randomly click one.
    String xPath = ".//input[@type='radio']";
    xPath = ".//input[@type='radio']/../../div[contains(@class,'pl_optionRadio')]";
    xPath = ".//div[contains(@class,'bt_pollOptions')]//div[contains(@class,'btOption')]";
    final List<WebElement> webElementList = getWebDriver().findElements(By.xpath(xPath));
    for (final WebElement webElement : webElementList) {
      scrollToElementAndHighlightBriefly(webElement);
    }
    final WebElement webElementSelect = webElementList.get(0);
    clickObject(webElementSelect);
    sleep(5);
  }

  /**
   * @param type
   * @throws QAException
   */
  public void getDailyPollQuizPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    switch (pointsCard.getGroup().toLowerCase(Locale.ENGLISH)) {
      case "daily poll":
        getDailyPollPoints(pointsCard);
        break;
      case "lightspeed quiz":
        getLightspeedQuizPoints(pointsCard);
        break;
      case "supersonic quiz":
        getSupersonicQuizPoints(pointsCard);
        break;
      case "show what you know":
      case "test your smarts":
        getTestYourSmartsQuizPoints(pointsCard);
        break;
      case "turbocharge quiz":
        getTurbochargeQuizPoints(pointsCard);
        break;
      case "warpspeed quiz":
        getWarpspeedQuizPoints(pointsCard);
        break;
      default:
        break;
    }
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  public void getLightspeedQuizPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    getQuizSearchValue();
    clickStartPlayingButton();
  }

  /**
   * @return
   * @throws QAException
   */
  private String getQuizSearchValue() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement webElement = scrollToElementAndHighlightBriefly(bySearchEdit);
    final String value = webElement.getAttribute("value");
    LOG.info("Getting Rewards for: [{}]", value);
    return value;
  }

  /**
   * @return
   * @throws QAException
   */
  private String getQuizWelcomeValue() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    String value = "";
    if (objectExists(byPointsAvailableLabel, 5)) {
      WebElement webElement = scrollToElementAndHighlightBriefly(byPointsAvailableLabel);
      setPointsAvailable(webElement.getText());
      setAnswersNeeded(Integer.valueOf(getPointsAvailable()) / 10);
      LOG.info(
          "Points Available: [{}], Answers Needed: [{}]", getPointsAvailable(), getAnswersNeeded());
      webElement = scrollToElementAndHighlightBriefly(byWelcomeLabel);
      value = webElement.getText();
      LOG.debug("Welcome Message: [{}]", value);
    }
    return value;
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  public void getSupersonicQuizPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    getQuizSearchValue();
    clickStartPlayingButton();
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  public void getTestYourSmartsQuizPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    // (1 of x)
    WebElement webElement =
        scrollToElementAndHighlightBriefly(By.xpath(".//div[contains(@class,'b_footnote')]"));
    String questions = webElement.getText();
    questions = questions.substring(1, questions.length() - 1);
    final String[] values = questions.split(" of ");
    final int questionStart = Integer.valueOf(values[0]) - 1;
    final int answers = Integer.valueOf(values[1]) - 1;
    for (int questionNumber = questionStart; questionNumber <= answers; questionNumber++) {
      webElement =
          scrollToElementAndHighlightBriefly(By.xpath(".//*[contains(@id,'wk_question_text')]"));
      final String questionValue = webElement.getText();
      LOG.debug("question: [{}]", questionValue);
      // Options
      final List<WebElement> webElementList =
          getWebDriver()
              .findElements(
                  By.xpath(
                      ".//*[@id='QuestionPane"
                          + questionNumber
                          + "']//div[@class='wk_paddingBtm']"));
      for (int webElementListIndex = 0;
          webElementListIndex < webElementList.size();
          webElementListIndex++) {
        // .//*[@id='QuestionPane1']//div[@class='wk_paddingBtm'][1]//div[contains(@id,'ChoiceText')]/div
        webElement = webElementList.get(webElementListIndex);
        final int answerNumber = webElementListIndex;
        final WebElement webElementAnswer =
            webElement.findElement(
                By.xpath(
                    "//div[@id='ChoiceText_" + questionNumber + "_" + answerNumber + "']/div"));
        final String answerValue = webElementAnswer.getText();
        LOG.debug("answerValue: [{}]", answerValue);
      }
      final int randomSelection =
          Integer.valueOf(generateRandomInteger(0, webElementList.size() - 1));
      webElement =
          scrollToElementAndHighlightBriefly(
              By.xpath(
                  "//div[@id='ChoiceText_"
                      + questionNumber
                      + "_"
                      + randomSelection
                      + "']/../..//span[@class='wk_Circle']"));
      clickObject(webElement);
      sleep(3);
      if (questionNumber == answers) {
        // Next question
        webElement =
            scrollToElementAndHighlightBriefly(
                By.xpath(".//*[@id='check'][@value='Get your score']"));
        clickObject(webElement);
        sleep(1);
      } else {
        // Next question
        webElement =
            scrollToElementAndHighlightBriefly(
                By.xpath(".//*[@id='check'][@value='Next question']"));
        clickObject(webElement);
        sleep(1);
      }
    }
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  public void getTurbochargeQuizPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    getQuizSearchValue();
    clickStartPlayingButton();
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  public void getWarpspeedQuizPoints(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    getQuizSearchValue();
    clickStartPlayingButton();
  }

  /**
   * @param by
   * @return
   * @throws QAException
   */
  private WebElement scrollToElementAndHighlightBriefly(By by) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement webElement = getWebDriver().findElement(by);
    scrollToElement(by);
    highlightCurrentElementBriefly(webElement, HIGHLIGHT_BRIEFLY);
    scrollToTop();
    return webElement;
  }

  /**
   * @param by
   * @return
   * @throws QAException
   */
  private WebElement scrollToElementAndHighlightBriefly(WebElement webElement) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToElement(webElement);
    highlightCurrentElementBriefly(webElement, HIGHLIGHT_BRIEFLY);
    scrollToTop();
    return webElement;
  }

  public String getPointsAvailable() {
    return pointsAvailable;
  }

  public void setPointsAvailable(String pointsAvailable) {
    this.pointsAvailable = pointsAvailable;
  }

  public int getAnswersNeeded() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement webElement =
        scrollToElementAndHighlightBriefly(
            By.xpath(".//*[@id='btoHeadPanel']//span[@class='rqPoints']"));
    final String points = webElement.getText();
    final String[] pointsArray = points.split("/");
    setPointsAvailable(pointsArray[1]);
    final int pointEarned = Integer.valueOf(pointsArray[0]);
    setAnswersNeeded((Integer.valueOf(getPointsAvailable()) - pointEarned) / 10);
    LOG.info(
        "Points Available: [{}], Points Earned: [{}]",
        getPointsAvailable(),
        +pointEarned + "], Answers Needed: [" + getAnswersNeeded() + "]");
    return getAnswersNeeded();
  }

  public void setAnswersNeeded(int answersNeeded) {
    this.answersNeeded = answersNeeded;
  }
}
