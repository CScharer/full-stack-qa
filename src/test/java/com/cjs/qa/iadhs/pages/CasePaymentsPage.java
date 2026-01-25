package com.cjs.qa.iadhs.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.iadhs.objects.Payment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class CasePaymentsPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(CasePaymentsPage.class));

  public CasePaymentsPage(WebDriver webDriver) {
    super(webDriver);
  }

  public static final int PAYMENT_AMOUNT_INDEX = 3;
  // Headings
  // .//*[@id='content']/div/section/div/div[2]/div[2]/div/b
  // .//*[@id='content']//div/div[2]/div[2]//b
  private static final By headingsBy = By.xpath(".//*[@id='content']//div/div[2]/div[2]//b");
  // Payments
  // .//*[@id='content']/div/section/div/div[2]/section/div[2]/div/a
  // .//*[@id='content']//div[2]/section/div[2]//a[1]
  private static final By previousPaymentRecordsBy =
      By.xpath(".//*[@id='content']//div[2]/section/div");
  private static final By lastPaymentBy =
      By.xpath(".//*[@id='content']//div[2]/section/div[2]//a[2]");
  private List<Payment> paymentList = new ArrayList<>();
  private Payment payment = new Payment();

  private By getHeadingsBy() {
    return headingsBy;
  }

  private By getPreviousPaymentRecordsBy() {
    return previousPaymentRecordsBy;
  }

  private By getLastPaymentBy() {
    return lastPaymentBy;
  }

  protected List<Payment> getPaymentList() {
    return paymentList;
  }

  protected void setPaymentList(List<Payment> paymentList) {
    this.paymentList = paymentList;
  }

  protected Payment getPayment() {
    return payment;
  }

  protected void setPayment(Payment payment) {
    this.payment = payment;
  }

  public void getLastPayment() throws Throwable {
    List<String> headingList = validateHeadingList();
    // Only get the most recent payment.
    List<WebElement> lastPaymentWebElementList = getWebDriver().findElements(getLastPaymentBy());
    getRecordWebElementsPayment(headingList, lastPaymentWebElementList);
    getPayment().append(getPaymentList());
  }

  public void getPreviousPayments() throws Throwable {
    final int recordsCheckMax = 3;
    List<String> headingList = validateHeadingList();
    List<WebElement> previousPaymentRecordsWebElementList =
        getWebDriver().findElements(getPreviousPaymentRecordsBy());
    int recordsCheck = previousPaymentRecordsWebElementList.size() - 1;
    if (recordsCheck > recordsCheckMax) {
      recordsCheck = recordsCheckMax;
    }
    for (int previousPaymentRecordsWebElementListIndex = recordsCheck;
        previousPaymentRecordsWebElementListIndex > 0;
        previousPaymentRecordsWebElementListIndex--) {
      WebElement previousPaymentRecordsWebElement =
          previousPaymentRecordsWebElementList.get(previousPaymentRecordsWebElementListIndex);
      highlightCurrentElementBriefly(previousPaymentRecordsWebElement, getFlashMilliseconds());
      List<WebElement> previousPaymentWebElementList =
          previousPaymentRecordsWebElement.findElements(By.xpath(".//a[2]"));
      getRecordWebElementsPayment(headingList, previousPaymentWebElementList);
    }
    getPayment().append(getPaymentList());
  }

  private void getRecordWebElementsPayment(
      List<String> headingList, List<WebElement> paymentWebElementList) throws Throwable {
    if (paymentWebElementList.isEmpty()) {
      return;
    }
    Map<String, String> paymentMap = new HashMap<>();
    for (int webElementListIndex = 0;
        webElementListIndex < paymentWebElementList.size();
        webElementListIndex++) {
      WebElement lastPaymentWebElement = paymentWebElementList.get(webElementListIndex);
      highlightCurrentElementBriefly(lastPaymentWebElement, 5);
      String value = lastPaymentWebElement.getText().trim();
      if (webElementListIndex == PAYMENT_AMOUNT_INDEX) {
        value = value.replaceAll("\\$", "");
      }
      String fieldName = headingList.get(webElementListIndex);
      paymentMap.put(fieldName, value);
    }
    Payment payment = new Payment(paymentMap);
    if (!payment.exists(paymentMap)) {
      getPaymentList().add(payment);
    }
  }

  public List<String> validateHeadingList() {
    int headingCount = 0;
    List<WebElement> headingWebElementList;
    do {
      sleep(3);
      headingWebElementList = getWebDriver().findElements(getHeadingsBy());
      headingCount = headingWebElementList.size();
      LOG.debug("Heading Count:[{}]", headingCount);
    } while (headingCount == 0);
    List<String> headingListActual = new ArrayList<>();
    for (int headingWebElementIndex = 0;
        headingWebElementIndex < headingWebElementList.size();
        headingWebElementIndex++) {
      WebElement headingWebElement = headingWebElementList.get(headingWebElementIndex);
      if (headingWebElementIndex == 0) {
        scrollToElement(headingWebElement);
        highlightCurrentElementBriefly(headingWebElement, getFlashMilliseconds());
      }
      String heading = headingWebElement.getText();
      LOG.debug("heading:[{}]", heading);
      headingListActual.add(heading);
    }
    List<String> headingListExpected = Payment.HEADING_LIST;
    String message =
        "The headings do not match!"
            + Constants.NEWLINE
            + difference(headingListActual.toString(), headingListExpected.toString());
    if (!headingListActual.toString().equals(headingListExpected.toString())) {
      LOG.warn("{}", message);
    }
    Assertions.assertEquals(headingListExpected.toString(), headingListActual.toString(), message);
    return headingListActual;
  }
}
