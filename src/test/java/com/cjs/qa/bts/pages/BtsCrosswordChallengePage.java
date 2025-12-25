package com.cjs.qa.bts.pages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.joda.time.LocalDateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class BtsCrosswordChallengePage extends Page {

  private final Map<Integer, String> mapGridSquares = new HashMap<>();
  private String[] lGridSquares = null;
  private static final String ALPHABET = "A;B;C;D;E;F;G;H;I;J;K;L;M;N;O;P;Q;R;S;T;U;V;W;X;Y;Z";
  private static final int squareMin = 4;
  private static final int squareMax = 628;
  private final LocalDateTime now = LocalDateTime.now();
  private String sYear = String.valueOf(now.getYear());
  // public String sMonth =
  // now.format(DateTimeFormatter.ofPattern("MMMM",Locale.ENGLISH));
  private String sMonth = "November";
  private final String sScreenShot =
      "C:"
          + Constants.DELIMETER_PATH
          + "Temp"
          + Constants.DELIMETER_PATH
          + "Crossword"
          + Constants.DELIMETER_PATH
          + sYear
          + "_"
          + sMonth
          + ".png";
  private final String[] letters = ALPHABET.split(Constants.DELIMETER_LIST);

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BtsCrosswordChallengePage.class));

  public BtsCrosswordChallengePage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By buttonContinue = By.xpath(".//input[@value='Continue']");
  private static final By buttonCheck = By.xpath(".//*[@id='game']/div[1]/div/div/span[.='Check']");
  private static final By buttonSave = By.xpath(".//*[@id='game']/div[1]/div/div/span[.='Save']");
  private static final By buttonSubmit =
      By.xpath(".//*[@id='game']/div[1]/div/div/span[.='Submit']");
  private static final By editName =
      By.xpath("html/body/div[1]/form/div/table/tbody/tr[1]/td[2]/input");
  private static final By editEmail =
      By.xpath("html/body/div[1]/form/div/table/tbody/tr[2]/td[2]/input");
  private static final By buttonOK =
      By.xpath("html/body/div[1]/form/div/table/tbody/tr[3]/td/input");
  private String sPageTitle = "Take the Challenge";

  public void clickbuttonContinue() {
    clickObject(buttonContinue);
  }

  public void clickbuttonCheck() {
    clickObject(buttonCheck);
  }

  public void clickbuttonSave() {
    clickObject(buttonSave);
  }

  public void clickbuttonSubmit() {
    clickObject(buttonSubmit);
  }

  public void clickbuttonOK() {
    clickObject(buttonOK);
  }

  public void sovleTakeTheChallenge(String values) {
    // sYear = "2016";
    // sMonth = "September";
    // sScreenShot = "C:" + Constants.DELIMETER_PATH + "Temp" +
    // Constants.DELIMETER_PATH + "Crossword" + Constants.DELIMETER_PATH +
    // sYear + "_" + sMonth +
    // ".png";
    navigateCrosswordURL(sYear, sMonth);
    getGridSquares();
    String startLetter = "";
    if (!"".equals(values)) {
      startLetter = populateFromFailure(values);
    }
    LOG.debug("startLetter: {}", startLetter);
    for (final String letter : letters) {
      if ("".equals(startLetter)) {
        // Do normal process.
        getCrosswordLetter(letter);
      } else {
        if (startLetter.equals(letter)) {
          getCrosswordLetter(letter);
          // Clear startLetter so the normal process continues.
          startLetter = "";
        }
      }
    }
    solvePuzzle();
    clickbuttonSubmit();
    submitPagePopulate();
    LOG.info("DONE!");
  }

  public String populateFromFailure(String values) {
    values = values.replace(" ", "");
    // Z{512=S,514=N,6=E,524=L,529=S,530=E,531=L,532=F,533=S,534=E,23=H,535=R,536=V,537=I,26=C,
    // 538=C,539=E,540=P,541=A,542=S,31=X,543=S,544=W,545=O,546=R,547=D,548=R,549=E,550=S,551=E,
    // 552=T,556=E,46=C,558=E,48=A,560=T,562=N,51=I,53=F,54=A,55=L,56=C,57=O,58=R,69=P,71=O,73=R,
    // ... (crossword puzzle data continues) ...
    // 499=O,504=E,506=I,508=N,510=O}
    // Z
    final String letter = values.substring(0, 1);
    // 512=S,514=N,6=E,524=L,529=S,530=E,531=L,532=F,533=S,534=E,23=H,535=R,536=V,537=I,26=C,
    // 538=C,539=E,540=P,541=A,542=S,31=X,543=S,544=W,545=O,546=R,547=D,548=R,549=E,550=S,551=E,
    // 552=T,556=E,46=C,558=E,48=A,560=T,562=N,51=I,53=F,54=A,55=L,56=C,57=O,58=R,69=P,71=O,73=R,
    // ... (crossword puzzle data continues) ...
    // 499=O,504=E,506=I,508=N,510=O
    final String letters = values.substring(2, values.length() - 1);
    final String[] gridSquares = letters.split(",");
    for (final String gridSquare : gridSquares) {
      LOG.debug("gridSquare: {}", gridSquare);
      final String[] gridValue = gridSquare.split("=");
      final int iGridSquare = Integer.valueOf(gridValue[0]);
      String sLetter = "";
      try {
        sLetter = gridValue[1];
      } catch (final Exception e) {
        LOG.debug("gridValue: {}", Arrays.toString(gridValue));
      }
      mapGridSquares.put(iGridSquare, sLetter);
    }
    return letter;
  }

  public void submitPagePopulate() {
    String userName = System.getenv("USERNAME");
    userName = System.getProperty("user.name");
    setEdit(editName, "Christopher J. Scharer");
    setEdit(editEmail, userName + "@WRBerkley" + IExtension.COM);
    clickbuttonOK();
  }

  public void navigateCrosswordURL(String sYear, String sMonth) {
    final String sURL =
        "http://crossword.info/btsclientservices/" + sMonth + "_" + sYear + "/"; // final
    // String
    // sURL
    // =
    // "http://crossword.info/btsclientservices/June_2016_a";
    getWebDriver().get(sURL);
    final String actual = getWebDriver().getTitle();
    if ("Web Page Restricted".equals(actual)) {
      clickbuttonContinue();
    }
    verifyPage(sYear, sMonth);
  }

  public void verifyPage(String sYear, String sMonth) {
    sPageTitle += " " + sMonth + " " + sYear;
    verifyTitle(sPageTitle);
  }

  public void getGridSquares() {
    for (int iGridSquare = squareMin; iGridSquare <= squareMax; iGridSquare++) {
      final WebElement oDiv =
          getWebDriver().findElement(By.xpath(".//*[@id='game']//div[" + iGridSquare + "]"));
      final String sClass = oDiv.getAttribute("class");
      if ("GridSquare Let".equals(sClass)) {
        mapGridSquares.put(iGridSquare, "");
      }
    }
    final String sKeySet =
        mapGridSquares.keySet().toString().replace("[", "").replace("]", "").replace(" ", "");
    lGridSquares = sKeySet.split(",");
  }

  public void getCrosswordLetter(String sLetter) {
    LOG.debug("sLetter: {}", sLetter);
    solvePuzzle(sLetter);
    LOG.debug("mapGridSquares: {}", mapGridSquares.toString());
    getWebDriver().navigate().refresh();
    sleep(1);
  }

  protected boolean isElementPresent(By by) {
    try {
      getWebDriver().findElement(by);
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  public void solvePuzzle(String sLetter) {
    for (final String sGridSquare : lGridSquares) {
      final int iGridSquare = Integer.valueOf(sGridSquare);
      final WebElement oDiv =
          getWebDriver().findElement(By.xpath(".//*[@id='game']//div[" + iGridSquare + "]"));
      // final JavascriptExecutor js = (JavascriptExecutor) webDriver;
      // js.executeScript("arguments[0].innerText = '" + letter + "'",
      // oSpan);
      final WebElement oSpan = oDiv.findElement(By.xpath("./p/span"));
      ((JavascriptExecutor) getWebDriver())
          .executeScript("arguments[0].innerText = '" + sLetter + "'", oSpan);
    }
    clickbuttonCheck();
    for (final String sGridSquare : lGridSquares) {
      final int iGridSquare = Integer.valueOf(sGridSquare);
      if ("".equals(mapGridSquares.get(iGridSquare))) {
        if (isElementPresent(
            By.xpath(".//*[@id='game']//div[" + iGridSquare + "][not(*/div[.='X'])]"))) {
          mapGridSquares.put(iGridSquare, sLetter);
        }
      }
    }
  }

  public void solvePuzzle() {
    for (final String sGridSquare : lGridSquares) {
      final int iGridSquare = Integer.valueOf(sGridSquare);
      final WebElement oDiv =
          getWebDriver().findElement(By.xpath(".//*[@id='game']//div[" + iGridSquare + "]"));
      final WebElement oSpan = oDiv.findElement(By.xpath("./p/span"));
      ((JavascriptExecutor) getWebDriver())
          .executeScript(
              "arguments[0].innerText = '" + mapGridSquares.get(iGridSquare) + "'", oSpan);
    }
    clickbuttonCheck();
    captureScreenshot(sScreenShot);
  }
}
