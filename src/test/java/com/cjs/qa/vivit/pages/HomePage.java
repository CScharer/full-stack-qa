package com.cjs.qa.vivit.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.vivit.VivitEnvironment;

public class HomePage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(HomePage.class));
  private static final By byLogo = By.xpath(".//*[@id='logo']/img");
  private static final By checkboxRememberMe = By.xpath(".//*[@id='rememberme']");
  private static final By buttonSignIn =
      By.xpath(".//*[@id='ctl00_PageContent_MainLogin']//input[@value='Sign In']");
  private static final By buttonSignOut = By.xpath(".//*[@id='itoolbar']/a[.='Sign Out']");
  private static final By editEmail = By.xpath(".//*[@id='u']");
  private static final By editPassword = By.xpath(".//*[@id='p']");
  private static final By linkSignIn = By.xpath(".//*[@id='itoolbar']/a[.='Sign In']");

  public HomePage(WebDriver webDriver) {
    super(webDriver);
  }

  public void buttonSignInClick() {
    clickObject(buttonSignIn);
  }

  public void checkboxRememberMeSet(String value) {
    setCheckbox(checkboxRememberMe, value);
  }

  public void clickButtonSignOut() {
    clickObject(buttonSignOut);
  }

  public void clickUpdateProfileLink() {
    clickLink("Update Profile");
  }

  public void editEmailSet(String value) {
    setEdit(editEmail, value);
  }

  public void editPasswordSet(String value) {
    setEditPassword(editPassword, value);
  }

  public List<String> getLanguagesSupported() {
    // May need to click on the dropdown to see the elements.
    By byLanguages =
        By.xpath(
            ".//*[@id=':1.menuBody']/table/tbody/tr/td/a/div//span[2][not(contains(.,'Select"
                + " Language'))]");
    StringBuilder stringBuilder = new StringBuilder("Languages Supported:");
    List<WebElement> webElementList = getWebDriver().findElements(byLanguages);
    List<String> languagesSupportedList = new ArrayList<>();
    for (WebElement webElement : webElementList) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(Constants.NEWLINE);
      }
      languagesSupportedList.add(webElement.getText());
      stringBuilder.append(webElement.getText());
    }
    LOG.debug("Languages Supported: {}", stringBuilder.toString());
    return languagesSupportedList;
  }

  public int getLanguagesSupportedCount() {
    return getLanguagesSupported().size();
  }

  public String getLogoSource() throws QAException {
    maximizeWindow();
    LOG.debug("Loading: [{}]", VivitEnvironment.URL_LOGIN);
    WebElement webElement = null;
    int attempt = 0;
    final int attemptsMax = 3;
    String logoSource = null;
    do {
      try {
        attempt++;
        LOG.debug("{} attempt {}", getCurrentMethodName(), attempt);
        if (attempt > 0) {
          getWebDriver().get(VivitEnvironment.URL_LOGIN);
        }
        webElement = getWebElement(byLogo);
        logoSource = webElement.getAttribute("src");
        return logoSource;
      } catch (final Exception e) {
        LOG.error("{} {}: Error Finding Logo.", QAException.ERROR, getCurrentClassMethodName());
        if (attempt > attemptsMax) {
          throw new QAException("***ERROR***:[" + e.getMessage() + "]", e);
        }
      }
    } while (webElement == null);
    return logoSource;
  }

  public void linkSignInClick() {
    clickObject(linkSignIn);
  }

  public void signIn(String eMail, String password, String rememberMe) throws QAException {
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      attempt++;
      try {
        getWebDriver().get(VivitEnvironment.URL_LOGIN);
        sleep(2);
        LOG.debug("{} attempt {}", getCurrentMethodName(), attempt);
        linkSignInClick();
        editEmailSet(eMail);
        editPasswordSet(password);
        checkboxRememberMeSet(rememberMe);
        buttonSignInClick();
        success = true;
      } catch (Exception e) {
        LOG.error("{} {}: Error Signing In.", QAException.ERROR, getCurrentClassMethodName());
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
  }
}
