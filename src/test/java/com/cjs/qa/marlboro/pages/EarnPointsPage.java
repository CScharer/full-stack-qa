package com.cjs.qa.marlboro.pages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.marlboro.Marlboro;
import com.cjs.qa.marlboro.MarlboroEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class EarnPointsPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(EarnPointsPage.class));

  public static final String URL_REWARDS_ENROLLED =
      MarlboroEnvironment.URL_BASE + "/pages/rewards-enrolled-view" + IExtension.HTML;
  public static final String CHARACTERS =
      "0;1;2;3;4;5;6;7;8;9;A;B;C;D;E;F;G;H;I;J;K;L;M;N;O;P;Q;R;S;T;U;V;W;X;Y;Z";
  public static final List<String> CHARACTERS_LIST =
      Arrays.asList(CHARACTERS.split(Constants.DELIMETER_LIST));
  public static final String STATUS_FAILED = "FAILED";
  public static final String STATUS_PASSED = "PASSED";
  private static final By labelPointsEarned = By.xpath(".//div[@class='points-container']");
  private static final By labelCodesEntered = By.xpath(".//div[@class='added-code-text']/p");
  private static final By labelErrorMessage = By.xpath(".//div[@class='err-msg']/p");
  private static final By editPackCode = By.xpath(".//input[@name='snumber']");
  private static final By buttonSubmit = By.xpath(".//button[@type='button'][.='Submit']");
  private final Map<Integer, String> characterMap = new HashMap<>();
  private final Map<Integer, Integer> indexMap = new HashMap<>();

  private By getLabelPointsEarned() {
    return labelPointsEarned;
  }

  private By getLabelCodesEntered() {
    return labelCodesEntered;
  }

  private By getLabelErrorMessage() {
    return labelErrorMessage;
  }

  private By getEditPackCode() {
    return editPackCode;
  }

  private By getButtonSubmit() {
    return buttonSubmit;
  }

  private Map<Integer, Integer> indexStartMap =
      getHashMapFromFile(MarlboroEnvironment.FILE_INDEXES);
  private Marlboro marlboro = null;
  private Map<String, String> mapUser = null;
  private Integer searches = 0;

  public EarnPointsPage(WebDriver webDriver) {
    super(webDriver);
  }

  public Marlboro getMarlboro() {
    return marlboro;
  }

  public void setMarlboro(Marlboro marlboro) {
    this.marlboro = marlboro;
  }

  public Map<String, String> getMapUser() {
    return mapUser;
  }

  public Integer getSearches() {
    return searches;
  }

  public void setMapUser(Map<String, String> mapUser) {
    this.mapUser = mapUser;
  }

  public void setSearches(Integer searches) {
    this.searches = searches;
  }

  public void buttonSubmitClick() throws QAException {
    clickObject(getButtonSubmit());
  }

  private Map<Integer, Integer> getHashMapFromFile(String filePathName) {
    String indexes = FSOTests.fileReadAll(filePathName);
    Map<Integer, Integer> map = new HashMap<>();
    // List<String> indexList =
    // Arrays.asList(indexes.split(Constants.DELIMETER_LIST));
    // for (Integer index = 0; index < indexList.size(); index++)
    // {
    // Integer indexMap = Integer.valueOf(indexList.get(index));
    // map.put((index + 1), indexMap);
    // }
    // indexes = indexes.replaceAll("{", "").replaceAll("}", "");
    indexes = indexes.substring(1, indexes.length() - 1);
    String[] indexArray = indexes.split(", ");
    for (String index : indexArray) {
      String[] indexRecord = index.split("=");
      Integer key = Integer.valueOf(indexRecord[0]);
      Integer value = Integer.valueOf(indexRecord[1]);
      map.put(key, value);
    }
    return map;
  }

  public void checkCodesEnteredThisMonth() throws QAException {
    final int codesPerMonthMax = 30;
    String codesEnteredThisMonthLabel = getLabelCodesEnteredText();
    LOG.debug("Codes Entered:{}", codesEnteredThisMonthLabel);
    String[] codesEnteredThisMonthArray = codesEnteredThisMonthLabel.split(" ");
    int codesEnteredThisMonth = Integer.valueOf(codesEnteredThisMonthArray[2]);
    Assert.assertTrue(
        "Codes Entered:" + codesEnteredThisMonth + "<" + codesPerMonthMax,
        codesEnteredThisMonth < codesPerMonthMax);
  }

  public void earnPoints(Marlboro marlboro, Map<String, String> mapUser) throws QAException {
    setMarlboro(marlboro);
    setMapUser(mapUser);
    double fileLogCount = FSOTests.filesGetCount(MarlboroEnvironment.FOLDER_DATA, IExtension.LOG);
    String filePathNameSource = MarlboroEnvironment.FILE_LOG;
    String extentionNew = formatNumber(fileLogCount, "000");
    String filePathNameDestination =
        MarlboroEnvironment.FILE_LOG.replace(IExtension.LOG, "_" + extentionNew + IExtension.LOG);
    if (FSOTests.fileExists(filePathNameSource)) {
      FSOTests.fileCopy(filePathNameSource, filePathNameDestination);
      FSOTests.fileDelete(filePathNameSource);
    }
    Environment.setScrollToObject(false);
    getWebDriver().get(URL_REWARDS_ENROLLED);
    LOG.debug("Points Earned:" + getLabelPointsEarned());
    checkCodesEnteredThisMonth();
    Integer indexMax = CHARACTERS_LIST.size();
    for (int index01 = indexStartMap.get(1); index01 < indexMax; index01++) {
      indexMap.put(1, index01);
      for (int index02 = indexStartMap.get(2); index02 < indexMax; index02++) {
        indexMap.put(2, index02);
        for (int index03 = indexStartMap.get(3); index03 < indexMax; index03++) {
          indexMap.put(3, index03);
          for (int index04 = indexStartMap.get(4); index04 < indexMax; index04++) {
            indexMap.put(4, index04);
            for (int index05 = indexStartMap.get(5); index05 < indexMax; index05++) {
              indexMap.put(5, index05);
              for (int index06 = indexStartMap.get(6); index06 < indexMax; index06++) {
                indexMap.put(6, index06);
                for (int index07 = indexStartMap.get(7); index07 < indexMax; index07++) {
                  indexMap.put(7, index07);
                  for (int index08 = indexStartMap.get(8); index08 < indexMax; index08++) {
                    indexMap.put(8, index08);
                    for (int index09 = indexStartMap.get(9); index09 < indexMax; index09++) {
                      indexMap.put(9, index09);
                      for (int index10 = indexStartMap.get(10); index10 < indexMax; index10++) {
                        indexMap.put(10, index10);
                        for (int index11 = indexStartMap.get(11); index11 < indexMax; index11++) {
                          indexMap.put(11, index11);
                          for (int index12 = indexStartMap.get(12); index12 < indexMax; index12++) {
                            indexMap.put(12, index12);
                            submitPackCode();
                          }
                          indexStartMap.put(12, 0);
                        }
                        indexStartMap.put(11, 0);
                      }
                      indexStartMap.put(10, 0);
                    }
                    indexStartMap.put(9, 0);
                  }
                  indexStartMap.put(8, 0);
                }
                indexStartMap.put(7, 0);
              }
              indexStartMap.put(6, 0);
            }
            indexStartMap.put(5, 0);
          }
          indexStartMap.put(4, 0);
        }
        indexStartMap.put(3, 0);
      }
      indexStartMap.put(2, 0);
    }
    indexStartMap.put(1, 0);
  }

  public void editPackCodeSet(String value) {
    setEdit(getEditPackCode(), value);
  }

  public String getLabelCodesEnteredText() throws QAException {
    WebElement webElement = getWebElement(getLabelCodesEntered());
    return webElement.getText();
  }

  public String getLabelPointsEarnedText() throws QAException {
    WebElement webElement = getWebElement(getLabelPointsEarned());
    return webElement.getText();
  }

  public String getLabelErrorMessageText() throws QAException {
    WebElement webElement = getWebElement(getLabelErrorMessage());
    return webElement.getText();
  }

  private String getPackCode() {
    String packCode = "";
    for (int index = 1; index <= 12; index++) {
      characterMap.put(index, CHARACTERS_LIST.get(indexMap.get(index)));
      packCode += characterMap.get(index);
      if (index == 3 || index == 6 || index == 9) {
        packCode += "-";
      }
    }
    return packCode;
  }

  private void submitPackCode() throws QAException {
    boolean failed = true;
    String packCode = getPackCode();
    String messageData =
        "packCode:["
            + packCode
            + "], indexMap:["
            + indexMap.toString()
            + "], characterMap:["
            + characterMap.toString()
            + "]";
    LOG.debug("{}", messageData);
    FSOTests.fileWrite(MarlboroEnvironment.FILE_INDEXES, indexMap.toString(), false);
    String messageSys = "";
    String messageFile = "";
    int attempts = 0;
    do {
      attempts++;
      if (attempts > 1) {
        // getMarlboro().SignInPage.wrapUp();
        // getMarlboro().SignInPage.populate(mapUser);
        // getWebDriver().get(URL_REWARDS_ENROLLED);
        refresh();
      }
      editPackCodeSet(packCode);
      String packCodeActual = getWebDriver().findElement(getEditPackCode()).getAttribute("value");
      if (!packCode.equals(packCodeActual)) {
        throw new QAException(
            "The Pack Code [" + packCode + "] does NOT match [" + packCodeActual + "]");
      }
      buttonSubmitClick();
      sleep(5);
      objectExists(getEditPackCode(), 1);
      if (objectExists(getLabelErrorMessage())) {
        messageSys = getLabelErrorMessageText();
        messageFile = STATUS_FAILED;
      } else {
        messageSys = STATUS_PASSED;
        messageFile = messageSys;
        failed = false;
      }
      LOG.debug("{}, {}", messageSys, messageData);
    } while (STATUS_FAILED.equals(messageFile)
        && !"The code you entered is not valid.".equals(messageSys));
    setSearches(getSearches() + 1);
    LOG.debug("Searches Made:[{}]", getSearches());
    messageData = messageData.replaceAll("], ", "]\t");
    FSOTests.fileWrite(
        MarlboroEnvironment.FILE_LOG, messageFile + "\t" + messageData + Constants.NEWLINE, true);
    if (!failed) {
      LOG.debug("Points Earned:" + getLabelPointsEarned());
      checkCodesEnteredThisMonth();
    }
  }
}
