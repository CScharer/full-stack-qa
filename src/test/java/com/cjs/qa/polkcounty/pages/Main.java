package com.cjs.qa.polkcounty.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.polkcounty.PolkCountyEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class Main extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Main.class));

  public Main(WebDriver webDriver) {
    super(webDriver);
  }

  private JDBC jdbc = new JDBC("", "");
  private static final By byNoResults = By.xpath(".//*[@id='ctl00_ContentPlaceHolder1_noResults']");

  public void load() {
    maximizeWindow();
    LOG.debug("Loading:[{}]", PolkCountyEnvironment.URL_LOGIN);
    getWebDriver().get(PolkCountyEnvironment.URL_LOGIN);
  }

  public void getInmatesOnTheWeb(boolean captureNew) {
    StringBuilder sqlStringBuilder = new StringBuilder();
    // Delete Prisoner and Case records that do not have data captured.
    sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.DELETE_FROM + "[t_DOM_IOW_Prisoners] ");
    sqlStringBuilder.append(JDBCConstants.WHERE + "[Offender/Name ID]='MISSING';");
    sqlStringBuilder.append(JDBCConstants.DELETE_FROM + "[t_DOM_IOW_Cases] ");
    sqlStringBuilder.append(JDBCConstants.WHERE + "[URL] IN ");
    sqlStringBuilder.append("(" + JDBCConstants.SELECT + "DISTINCT [URL] ");
    sqlStringBuilder.append(JDBCConstants.FROM + "[t_DOM_IOW_Cases] ");
    sqlStringBuilder.append(JDBCConstants.WHERE + "[Case #]='MISSING' ");
    sqlStringBuilder.append(JDBCConstants.OR + "[Case #] LIKE '-%');");
    SQL.execute(sqlStringBuilder.toString());
    if (captureNew) {
      load();
      String totalInmates =
          getWebDriver()
              .findElement(By.xpath(".//*[@id='ctl00_ContentPlaceHolder1_lblTotal']"))
              .getText();
      totalInmates = totalInmates.substring(totalInmates.indexOf("=") + 1).trim();
      final int totalInmatesCount = Integer.valueOf(totalInmates);
      LOG.debug("iTotalInmates:[{}]", totalInmatesCount);
      sqlStringBuilder = new StringBuilder();
      sqlStringBuilder.append("SELECT [URL] ");
      sqlStringBuilder.append("FROM [t_DOM_IOW_Arrests];");
      final String capturedArrestsURLs = jdbc.queryResults(sqlStringBuilder.toString(), "", false);
      List<String> listCapturedArrestsURL = new ArrayList<>();
      if (!"".equals(capturedArrestsURLs)) {
        listCapturedArrestsURL = Arrays.asList(capturedArrestsURLs.split(Constants.NEWLINE));
      }
      LOG.debug("capturedArrestsURLArray:[{}]", listCapturedArrestsURL.size());
      sqlStringBuilder = new StringBuilder();
      final List<WebElement> rowElements =
          getWebDriver()
              .findElements(By.xpath(".//*[@id='ctl00_ContentPlaceHolder1_gvInmates']/tbody/tr"));
      for (int indexRow = 0; indexRow < rowElements.size(); indexRow++) {
        final WebElement rowElement = rowElements.get(indexRow);
        final List<WebElement> columnElements = rowElement.findElements(By.xpath("./td"));
        final Map<String, String> mapRecord = new HashMap<>();
        int column = 0;
        for (int indexColumn = 0; indexColumn < columnElements.size(); indexColumn++) {
          final WebElement columnElement = columnElements.get(indexColumn);
          column++;
          final String value = columnElement.getText();
          switch (column) {
            case 1:
              mapRecord.put("Last Name", value);
              break;
            case 2:
              mapRecord.put("First Name", value);
              break;
            case 3:
              mapRecord.put("Age", value);
              break;
            case 4:
              mapRecord.put("Booking Date", value);
              if (!listCapturedArrestsURL.contains(mapRecord.get("URL"))) {
                sqlStringBuilder =
                    SQL.appendStringBuilderSQLInsertRecord(
                        "t_DOM_IOW_Arrests", sqlStringBuilder, mapRecord, true);
              } else {
                indexColumn = columnElements.size();
                indexRow = rowElements.size();
              }
              break;
            default:
              LOG.warn("Unexpected column number: {}. Skipping.", column);
              break;
          }
          if (column == 1) {
            final WebElement elementA = columnElement.findElement(By.xpath("./a"));
            final String href = elementA.getAttribute("href");
            mapRecord.put("URL", href);
            if (listCapturedArrestsURL.contains(href)) {
              indexColumn = columnElements.size();
              indexRow = rowElements.size();
            }
          }
        }
        LOG.debug("mapRecord:[{}]", mapRecord.toString());
        if (!"".equals(sqlStringBuilder.toString())) {
          try {
            LOG.debug(
                "sqlStringBuilder.toString():[{}{}]",
                Constants.NEWLINE,
                sqlStringBuilder.toString());
            jdbc.executeUpdate(sqlStringBuilder.toString(), false);
          } catch (final Exception e) {
            LOG.error("Exception occurred", e);
          }
          sqlStringBuilder = new StringBuilder();
        }
      }
    }
    if (!"".equals(sqlStringBuilder.toString())) {
      try {
        LOG.debug(
            "sqlStringBuilder.toString():[{}{}]", Constants.NEWLINE, sqlStringBuilder.toString());
        jdbc.executeUpdate(sqlStringBuilder.toString(), false);
      } catch (final Exception e) {
        LOG.error("Exception occurred", e);
      }
      sqlStringBuilder = new StringBuilder();
    }
    sqlStringBuilder.append("SELECT [URL] ");
    sqlStringBuilder.append("FROM [v_IOW_MissingPrisonerURLs];");
    final String missingPrisonerURLs = jdbc.queryResults(sqlStringBuilder.toString(), "", false);
    List<String> listMissingPrisonerURL = new ArrayList<>();
    if (!"".equals(missingPrisonerURLs)) {
      listMissingPrisonerURL = Arrays.asList(missingPrisonerURLs.split(Constants.NEWLINE));
    }
    LOG.debug("missingPrisonerURLArray:[{}]", listMissingPrisonerURL.size());
    sqlStringBuilder = new StringBuilder();
    for (final String missingPrisonerURL : listMissingPrisonerURL) {
      sqlStringBuilder = getArrestRecord(missingPrisonerURL, sqlStringBuilder);
      if (!"".equals(sqlStringBuilder.toString())) {
        try {
          LOG.debug(
              "sqlStringBuilder.toString():[{}{}]", Constants.NEWLINE, sqlStringBuilder.toString());
          jdbc.executeUpdate(sqlStringBuilder.toString(), false);
        } catch (final Exception e) {
          LOG.error("Exception occurred", e);
        }
        sqlStringBuilder = new StringBuilder();
        sqlStringBuilder = getCaseRecord(missingPrisonerURL, sqlStringBuilder, false);
        if (!"".equals(sqlStringBuilder.toString())) {
          try {
            LOG.debug(
                "sqlStringBuilder.toString():[{}{}]",
                Constants.NEWLINE,
                sqlStringBuilder.toString());
            jdbc.executeUpdate(sqlStringBuilder.toString(), false);
          } catch (final Exception e) {
            LOG.error("e:[{}]", e.getMessage(), e);
          }
          sqlStringBuilder = new StringBuilder();
        }
      }
    }
    sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append("SELECT [URL] ");
    sqlStringBuilder.append("FROM [v_IOW_MissingCaseURLs];");
    final String missingCaseURLs = jdbc.queryResults(sqlStringBuilder.toString(), "", false);
    List<String> listMissingCaseURL = new ArrayList<>();
    if (!"".equals(missingCaseURLs)) {
      listMissingCaseURL = Arrays.asList(missingCaseURLs.split(Constants.NEWLINE));
    }
    LOG.debug("missingCaseURLArray:[{}]", listMissingCaseURL.size());
    sqlStringBuilder = new StringBuilder();
    for (final String missingCaseURL : listMissingCaseURL) {
      sqlStringBuilder = getCaseRecord(missingCaseURL, sqlStringBuilder, true);
      if (!"".equals(sqlStringBuilder.toString())) {
        try {
          LOG.debug(
              "sqlStringBuilder.toString():[{}{}]", Constants.NEWLINE, sqlStringBuilder.toString());
          jdbc.executeUpdate(sqlStringBuilder.toString(), false);
        } catch (final Exception e) {
          LOG.error("e:[{}]", e.getMessage(), e);
        }
        sqlStringBuilder = new StringBuilder();
      }
    }
    jdbc = null;
  }

  private StringBuilder getArrestRecord(String url, StringBuilder sqlStringBuilder) {
    getWebDriver().get(url);
    Map<String, String> inmateRecordMap = new HashMap<>();
    if (objectExists(byNoResults, 3)) {
      inmateRecordMap.putAll(getInmateNameDateMap());
      inmateRecordMap.putAll(getInmateProfileMap());
      inmateRecordMap.putAll(getInmateAddressMap());
      inmateRecordMap.putAll(getInmateHoldingLocationMap());
      for (final String key : inmateRecordMap.keySet()) {
        inmateRecordMap.put(key, "MISSING");
      }
      inmateRecordMap.put("Offender/Name ID", url);
      inmateRecordMap.put("URL", url);
      LOG.debug("inmateRecordMap:[{}]", inmateRecordMap.toString());
      sqlStringBuilder =
          SQL.appendStringBuilderSQLInsertRecord(
              "t_DOM_IOW_Prisoners", sqlStringBuilder, inmateRecordMap, true);
      return sqlStringBuilder;
    }
    final Map<String, String> inmateNameDateMap = getInmateNameDateMap();
    final Map<String, String> inmateProfileMap = getInmateProfileMap();
    final Map<String, String> inmateAddressMap = getInmateAddressMap();
    final Map<String, String> inmateHoldingLocationMap = getInmateHoldingLocationMap();
    for (final String key : inmateNameDateMap.keySet()) {
      // LOG.debug("key:[{}]", key);
      String value = "";
      if ("Name".equals(key)) {
        value =
            getLabel(
                By.xpath(
                    ".//*[@id='inmateNameDate']/tbody/tr[2]/th[text()[contains(.,'"
                        + key
                        + "')]]/../td"));
      } else {
        value =
            getLabel(
                By.xpath(
                    ".//*[@id='inmateNameDate']/tbody/tr/th[text()[contains(.,'"
                        + key
                        + "')]]/../td"));
      }

      inmateNameDateMap.put(key, value);
    }
    // LOG.debug("inmateNameDateMap:[{}]", inmateNameDateMap.toString());
    for (final String key : inmateProfileMap.keySet()) {
      // LOG.debug("key:[{}]", key);
      final String value =
          getLabel(
              By.xpath(
                  ".//*[@id='inmateProfile']/tbody/tr/th[text()[contains(.,'"
                      + key
                      + "')]]/../td"));
      inmateProfileMap.put(key, value);
    }
    // LOG.debug("inmateProfileMap:[{}]", inmateProfileMap.toString());
    for (final String key : inmateAddressMap.keySet()) {
      // LOG.debug("key:[{}]", key);
      String value =
          getLabel(
              By.xpath(".//*[@id='inmateAddress']/strong[text()[contains(.,'" + key + "')]]/.."));
      if (value.contains(Constants.NL)) {
        value = value.replaceAll(key + Constants.NL, "");
      } else {
        value = value.replaceAll(key, "");
      }
      inmateAddressMap.put(key, value);
    }
    // LOG.debug("inmateAddressMap:[{}]", inmateAddressMap.toString());
    for (final String key : inmateHoldingLocationMap.keySet()) {
      // LOG.debug("key:[{}]", key);
      String value =
          getLabel(
              By.xpath(".//*[@id='holdingLocation']/strong[text()[contains(.,'" + key + "')]]/.."));
      if (value.contains(Constants.NL)) {
        value = value.replaceAll(key + Constants.NL, "");
      } else {
        value = value.replaceAll(key, "");
      }
      inmateHoldingLocationMap.put(key, value);
    }
    // LOG.debug("inmateHoldingLocationMap:[{}]", inmateHoldingLocationMap.toString());
    inmateRecordMap = new HashMap<>();
    inmateRecordMap.putAll(inmateNameDateMap);
    inmateRecordMap.putAll(inmateProfileMap);
    inmateRecordMap.putAll(inmateAddressMap);
    inmateRecordMap.putAll(inmateHoldingLocationMap);
    inmateRecordMap.put("URL", url);
    LOG.debug("inmateRecordMap:[{}]", inmateRecordMap.toString());
    sqlStringBuilder =
        SQL.appendStringBuilderSQLInsertRecord(
            "t_DOM_IOW_Prisoners", sqlStringBuilder, inmateRecordMap, true);
    return sqlStringBuilder;
  }

  private StringBuilder getCaseRecord(
      String url, StringBuilder sqlStringBuilder, boolean refreshURL) {
    final By byInmateNameDate =
        By.xpath(
            ".//*[@id='inmateNameDate']/tbody/tr[1]/th[text()[contains(.,'Offender/Name"
                + " ID')]]/../td");
    if (refreshURL) {
      getWebDriver().get(url);
    }
    String offenderNameID = "MISSING";
    if (objectExists(byInmateNameDate, 3)) {
      final WebElement element = getWebDriver().findElement(byInmateNameDate);
      offenderNameID = element.getText();
    }
    final List<String> listMapRecord = new ArrayList<>();
    Map<String, String> mapRecord = getCaseMap();
    if (objectExists(byNoResults, 3)) {
      mapRecord.put("URL", url);
      mapRecord.put("Offender/Name ID", "MISSING");
      mapRecord.put("Case #", "MISSING");
      mapRecord.put("Description", "MISSING");
      mapRecord.put("Bond", "MISSING");
      mapRecord.put("Bond Type", "MISSING");
      LOG.debug("mapRecord:[{}]", mapRecord.toString());
      sqlStringBuilder =
          SQL.appendStringBuilderSQLInsertRecord(
              "t_DOM_IOW_Cases", sqlStringBuilder, mapRecord, true);
      return sqlStringBuilder;
    }
    final List<WebElement> rowElements =
        getWebDriver()
            .findElements(By.xpath(".//*[@id='ctl00_ContentPlaceHolder1_gvCharges']/tbody/tr"));
    int row = 0;
    for (final WebElement rowElement : rowElements) {
      row++;
      final List<WebElement> columnElements = rowElement.findElements(By.xpath("./td"));
      mapRecord = getCaseMap();
      int column = 0;
      for (final WebElement columnElement : columnElements) {
        column++;
        String value = columnElement.getText();
        switch (column) {
          case 1:
            if ("Case #".equals(value)) {
              break;
            }
            if (value.isEmpty()) {
              value = "-" + (row - 1);
            }
            mapRecord.put("Case #", value);
            break;
          case 2:
            mapRecord.put("Description", value);
            break;
          case 3:
            mapRecord.put("Bond", value);
            break;
          case 4:
            mapRecord.put("Bond Type", value);
            mapRecord.put("URL", url);
            if (!"Case #".equals(mapRecord.get("Case #"))) {
              // final String recordCount = jdbc.queryResults(
              // JDBCConstants.SELECT_COUNT_FROM +
              // "[t_DOM_IOW_Cases] " + JDBCConstants.WHERE
              // + "[Case #]='" + mapRecord.get("Case #") + "'",
              // "", false);
              // if (recordCount.equals("0"))
              // {
              mapRecord.put("Offender/Name ID", offenderNameID);
              LOG.debug("mapRecord:[{}]", mapRecord.toString());
              if (!listMapRecord.contains(mapRecord.toString())) {
                listMapRecord.add(mapRecord.toString());
                sqlStringBuilder =
                    SQL.appendStringBuilderSQLInsertRecord(
                        "t_DOM_IOW_Cases", sqlStringBuilder, mapRecord, true);
              }
              // }
            }
            break;
          default:
            LOG.warn("Unexpected column number: {}. Skipping.", column);
            break;
        }
      }
    }
    return sqlStringBuilder;
  }

  private Map<String, String> getInmateNameDateMap() {
    final Map<String, String> inmateNameDateMap = new HashMap<>();
    inmateNameDateMap.put("Offender/Name ID", "");
    inmateNameDateMap.put("Name", "");
    inmateNameDateMap.put("Book Date", "");
    return inmateNameDateMap;
  }

  private Map<String, String> getInmateProfileMap() {
    final Map<String, String> inmateProfileMap = new HashMap<>();
    inmateProfileMap.put("Age", "");
    inmateProfileMap.put("Height", "");
    inmateProfileMap.put("Weight", "");
    inmateProfileMap.put("Race", "");
    inmateProfileMap.put("Sex", "");
    inmateProfileMap.put("Eyes", "");
    inmateProfileMap.put("Hair", "");
    return inmateProfileMap;
  }

  private Map<String, String> getInmateAddressMap() {
    final Map<String, String> inmateProfileMap = new HashMap<>();
    inmateProfileMap.put("City", "");
    return inmateProfileMap;
  }

  private Map<String, String> getInmateHoldingLocationMap() {
    final Map<String, String> inmateProfileMap = new HashMap<>();
    inmateProfileMap.put("Holding Location", "");
    return inmateProfileMap;
  }

  private Map<String, String> getCaseMap() {
    final Map<String, String> caseMap = new HashMap<>();
    caseMap.put("Case #", "");
    caseMap.put("Description", "");
    caseMap.put("Bond", "");
    caseMap.put("Bond Type", "");
    return caseMap;
  }
}
