package com.cjs.qa.polkcounty.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class Details extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Details.class));

  public Details(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By byNoResults = By.xpath(".//*[@id='ctl00_ContentPlaceHolder1_noResults']");

  public StringBuilder getArrestRecord(String url, StringBuilder sqlStringBuilder) {
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
              "t_DOM_IOW_Prisoners", sqlStringBuilder, inmateRecordMap);
      return sqlStringBuilder;
    }
    final Map<String, String> inmateNameDateMap = getInmateNameDateMap();
    final Map<String, String> inmateProfileMap = getInmateProfileMap();
    final Map<String, String> inmateAddressMap = getInmateAddressMap();
    final Map<String, String> inmateHoldingLocationMap = getInmateHoldingLocationMap();
    for (final String key : inmateNameDateMap.keySet()) {
      // LOG.debug("key:[[{}]", key + "]");
      String value = "";
      if ("Name".equals(key)) {
        // webElement =
        // getWebDriver().findElement(By.xpath(".//*[@id='inmateNameDate']/tbody/tr[2]/th[text()[contains(.,'"
        // + key + "')]]/../td"));
        value =
            getLabel(
                By.xpath(
                    ".//*[@id='inmateNameDate']/tbody/tr[2]/th[text()[contains(.,'"
                        + key
                        + "')]]/../td"));
      } else {
        // webElement =
        // getWebDriver().findElement(By.xpath(".//*[@id='inmateNameDate']/tbody/tr/th[text()[contains(.,'"
        // + key + "')]]/../td"));
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
      // LOG.debug("key:[[{}]", key + "]");
      // webElement =
      // getWebDriver().findElement(By.xpath(".//*[@id='inmateProfile']/tbody/tr/th[text()[contains(.,'"
      // + key + "')]]/../td"));
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
      // LOG.debug("key:[[{}]", key + "]");

      // webElement =
      // getWebDriver().findElement(By.xpath(".//*[@id='inmateAddress']/strong[text()[contains(.,'"
      // + key + "')]]/.."));
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
      // LOG.debug("key:[[{}]", key + "]");
      // webElement =
      // getWebDriver().findElement(By.xpath(".//*[@id='holdingLocation']/strong[text()[contains(.,'"
      // + key + "')]]/.."));
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
            "t_DOM_IOW_Prisoners", sqlStringBuilder, inmateRecordMap);
    return sqlStringBuilder;
  }

  public StringBuilder getCaseRecord(
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
    final List<String> mapRecordList = new ArrayList<>();
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
              value = "-" + row;
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
              // JDBCConstants.SELECT_ALL_FROM +
              // "[t_DOM_IOW_Cases] " + JDBCConstants.WHERE
              // + "[Case #]='" + mapRecord.get("Case #") + "'",
              // "", false);
              // if (recordCount.equals("0"))
              // {
              mapRecord.put("Offender/Name ID", offenderNameID);
              LOG.debug("mapRecord:[{}]", mapRecord.toString());
              if (!mapRecordList.contains(mapRecord.toString())) {
                mapRecordList.add(mapRecord.toString());
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
