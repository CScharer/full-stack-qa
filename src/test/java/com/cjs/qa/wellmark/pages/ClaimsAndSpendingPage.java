package com.cjs.qa.wellmark.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class ClaimsAndSpendingPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ClaimsAndSpendingPage.class));

  public ClaimsAndSpendingPage(WebDriver webDriver) {
    super(webDriver);
  }

  private List<String> listHeadings = null;
  private List<String> listTypes = null;
  private static final By linkMedicalAndPharmacy =
      By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_tabAll']/a");
  private static final By linkMedical =
      By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_tabMedical']/a");
  private static final By linkPharmacy =
      By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_tabPharm']/a");
  private static final By gridMedicalAndPharmacy =
      By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_AllClaimsGrid']/tbody");
  private static final By gridMedical =
      By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_MedClaimsGrid']/tbody");
  private static final By gridPharmacy =
      By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_PharmClaimsGrid']/tbody");

  // ClaimsAndSpendingPage
  // .//*[@id='ctl00_ContentLeft_DisplayGrid_AllClaimsGrid']
  // Medical
  // .//*[@id='ctl00_ContentLeft_DisplayGrid_MedClaimsGrid']
  // Pharmacy
  // .//*[@id='ctl00_ContentLeft_DisplayGrid_PharmClaimsGrid']
  // ./tbody/tr/td/node()
  // ./tbody/tr/td/span
  // ./tbody/tr/td/div/span
  // ./tbody/tr/td/b/span
  // ./tbody/tr/td/select/option[@selected]

  private void getHeadingInfo() throws QAException {
    final String labelSpan = "//span";
    listHeadings = new ArrayList<>();
    listTypes = new ArrayList<>();
    listHeadings.add("Patient Name");
    listTypes.add(labelSpan);
    listHeadings.add("Service Date");
    listTypes.add(labelSpan);
    listHeadings.add("Provider");
    listTypes.add(labelSpan);
    listHeadings.add("Amount Charged");
    listTypes.add(labelSpan);
    listHeadings.add("Your Share");
    listTypes.add(labelSpan);
    listHeadings.add("Status");
    listTypes.add(labelSpan);
    listHeadings.add("Issue Date");
    listTypes.add("/div");
    listHeadings.add("Details");
    listTypes.add(labelSpan);
    listHeadings.add("Category");
    listTypes.add("//select/option[@selected]");
    listHeadings.add("Note");
    listTypes.add("/span/a");
  }

  public void linkClaimsPerPageClick(int linksPerPage) throws QAException {
    // By linkClaimsPerPage =
    // By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_aa100']")
    By linkClaimsPerPage =
        By.xpath(".//*[@id='ctl00_ContentLeft_DisplayGrid_aa" + linksPerPage + "']");
    clickObject(linkClaimsPerPage);
  }

  public void linkMedicalAndPharmacyClick() throws QAException {
    clickObject(linkMedicalAndPharmacy);
  }

  public void linkMedicalClick() throws QAException {
    clickObject(linkMedical);
  }

  public void linkPharmacyClick() throws QAException {
    clickObject(linkPharmacy);
  }

  public StringBuilder getRecords(String type) throws QAException {
    final String byXpath = "By.xpath: ";
    final StringBuilder stringBuilder = new StringBuilder();
    By byType = null;
    waitPageLoaded();
    switch (type) {
      case "Medical and Pharmacy":
        byType = gridMedicalAndPharmacy;
        linkMedicalAndPharmacyClick();
        break;
      case "Medical":
        byType = gridMedical;
        linkMedicalClick();
        break;
      case "Pharmacy":
        byType = gridPharmacy;
        linkPharmacyClick();
        break;
      default:
        break;
    }
    waitPageLoaded();
    linkClaimsPerPageClick(100);
    waitPageLoaded();
    sleep(1);
    final List<String> headings = new ArrayList<>();
    StringBuilder sqlStringBuilder = null;
    if (byType != null) {
      getWebDriver().findElements(byType);
      final List<WebElement> headingElements =
          getWebDriver()
              .findElements(
                  By.xpath(byType.toString().replace(byXpath, "") + "/tr[" + 1 + "]/th/a"));
      LOG.debug("headingElements: {}", headingElements.toString());
      for (int indexHeading = 0; indexHeading < headingElements.size(); indexHeading++) {
        final WebElement element = headingElements.get(indexHeading);
        LOG.debug("element: {}", element.toString());
        String value = element.getText();
        value = value.replaceAll("Not sorted", "");
        value = value.replaceAll(Constants.NL, "");
        value = value.trim();
        headings.add(value);
        if (indexHeading != 0) {
          stringBuilder.append(",");
        }
        stringBuilder.append(value);
      }
      stringBuilder.append(Constants.NEWLINE);
      LOG.debug("listHeadings: {}", listHeadings);
      LOG.debug("headings: {}", headings);
      Assert.assertEquals("Headings " + type, listHeadings, headings);
      String xPath = byType.toString().replace(byXpath, "") + "/tr";
      LOG.debug("xPath:{}", xPath);
      final List<WebElement> rowElements = getWebDriver().findElements(By.xpath(xPath));
      sqlStringBuilder = new StringBuilder();
      for (int indexRecord = 2; indexRecord <= rowElements.size(); indexRecord++) {
        final Map<String, String> recordMap = getHeadingMap();
        for (int index = 0; index < listHeadings.size(); index++) {
          final int indexField = index + 1;
          // LOG.debug("xPath:[{}]", xPath);
          // LOG.debug("{} xpath:[{}]", HEADINGS.get((index)).toString(), xPath);
          xPath =
              byType.toString().replace(byXpath, "")
                  + "/tr["
                  + indexRecord
                  + "]/td["
                  + indexField
                  + "]"
                  + listTypes.get(index);
          // LOG.debug("xPath:[{}]", xPath);
          final WebElement cell = getWebDriver().findElement(By.xpath(xPath));
          // LOG.debug("{}", cell.toString());
          final String value = cell.getText().trim();
          LOG.debug("{}:[{}]", listHeadings.get(index), value);
          if (index != 0) {
            stringBuilder.append(",");
          }
          final String record = formatValue(value);
          if (record.contains(",") || record.contains(Constants.QUOTE_DOUBLE + "")) {
            stringBuilder.append(Constants.QUOTE_DOUBLE + record + Constants.QUOTE_DOUBLE + "");
          } else {
            stringBuilder.append(record);
          }
          recordMap.put(listHeadings.get(index), record);
        }
        stringBuilder.append(Constants.NEWLINE);
        LOG.debug("recordMap:[{}]", recordMap.toString());
        sqlStringBuilder =
            SQL.appendStringBuilderSQLInsertRecord("t_Wellmark", sqlStringBuilder, recordMap, true);
      }
    }
    JDBC jdbc = new JDBC("", "QAAuto");
    try {
      jdbc.executeUpdate(JDBCConstants.DELETE_FROM + "[t_Wellmark];", false);
      if (sqlStringBuilder != null) {
        jdbc.executeUpdate(sqlStringBuilder.toString(), false);
      }
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
    return stringBuilder;
  }

  public void getRecords() throws QAException {
    getHeadingInfo();
    StringBuilder stringBuilder = getRecords("Medical and Pharmacy");
    // stringBuilder = getRecords("Medical")
    // stringBuilder = getRecords("Pharmacy")
    // if (FSOTests.fileExists(Environment.FILE_CSV))
    // {
    // stringBuilder = sortRecords(stringBuilder)
    final String fileName = Environment.getFolderData() + "Wellmark" + IExtension.CSV;
    FSOTests.fileWrite(fileName, stringBuilder.toString(), false);
    // }
  }

  private Map<String, String> getHeadingMap() throws QAException {
    final Map<String, String> headingMap = new HashMap<>();
    for (final String heading : listHeadings) {
      headingMap.put(heading, "");
    }
    return headingMap;
  }

  private String formatValue(String value) throws QAException {
    final List<String> replacees =
        Arrays.asList(Constants.NEWLINE, Constants.CR, Constants.NL, Constants.TAB, "EOB");
    String record = value;
    for (final String replacee : replacees) {
      final String temp = record.replaceAll(replacee, "");
      record = temp.trim();
    }
    return record;
  }

  public StringBuilder sortRecords(StringBuilder stringBuilder) throws QAException {
    // This sorts on the string value to the dates will not sort properly.
    final String[] records = stringBuilder.toString().split(Constants.NEWLINE);
    final String headings = records[0];
    stringBuilder = new StringBuilder();
    stringBuilder.append(headings);
    List<String> list = new ArrayList<>();
    for (int index = 1; index < records.length; index++) {
      list.add(records[index]);
    }
    Collections.sort(list);
    for (String item : list) {
      stringBuilder.append(Constants.NEWLINE);
      stringBuilder.append(item);
      // if (index < (arrayList.size() - 1))
      // {
      // stringBuilder.append(Constants.NEWLINE)
      // }
    }
    return stringBuilder;
  }
}
