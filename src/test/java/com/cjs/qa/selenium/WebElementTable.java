package com.cjs.qa.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;

public class WebElementTable extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(WebElementTable.class));

  private static final String COLUMN_NAME_PREFIX = "#";
  private boolean highlightObjects = false;

  private String getColumnNamePrefix() {
    return COLUMN_NAME_PREFIX;
  }

  private boolean resetTable = false;
  private String xPathTable = ".//center/table/tbody//table";
  private String xpathHeadings = "./thead/tr/th";
  private String xpathRows = "./tbody/tr";
  private String xPathCells = "./td";
  private Map<String, Integer> headingsName = null;
  private Map<String, Integer> headingsNameLookup = null;
  private Map<Integer, String> headingsIndex = null;
  private WebElement webElementTable = null;
  private List<WebElement> tableHeadingsList = null;
  private List<WebElement> tableRowsList = null;

  /**
   * @param webDriver
   * @param xPathTable
   * @param xpathHeadings
   * @param xpathRows
   * @param xPathCells
   */
  public WebElementTable(
      WebDriver webDriver,
      String xPathTable,
      String xpathHeadings,
      String xpathRows,
      String xPathCells) {
    super(webDriver);
    if (xpathHeadings != null) {
      this.xpathHeadings = xpathHeadings;
    }
    if (xpathRows != null) {
      this.xpathRows = xpathRows;
    }
    if (xPathCells != null) {
      this.xPathCells = xPathCells;
    }
    if (xPathTable != null) {
      this.xPathTable = xPathTable;
    }
  }

  /**
   * @param columnIndex
   * @param row
   * @return
   */
  public boolean clickCell(int columnIndex, int row) {
    final String columnName = getColumnName(columnIndex);
    return clickCell(columnName, row);
  }

  /**
   * @param columnName
   * @param row
   * @return
   */
  public boolean clickCell(String columnName, int row) {
    populateTableObjects();
    final WebElement webElementRow = getTableRowsList().get(row - 1);
    final int column = getHeadingsNameLookup().get(columnName.toLowerCase(Locale.ENGLISH));
    final WebElement webElementCell = webElementRow.findElement(By.xpath("./td[" + column + "]"));
    highlightWebElement(webElementCell);
    webElementCell.click();
    return true;
  }

  /**
   * @param fileName
   */
  public void exportTable(String fileName) {
    populateTableObjects();
    final StringBuilder stringBuilderHeadings = new StringBuilder();
    final StringBuilder stringBuilderRecords = new StringBuilder();
    final StringBuilder stringBuilder = new StringBuilder();
    final int rowCount = getRowCount();
    for (final int key : getHeadingsIndex().keySet()) {
      String heading = getHeadingsIndex().get(key);
      if (heading.isEmpty()) {
        heading = getColumnNamePrefix() + key;
      }
      heading = prepareForCSV(heading);
      stringBuilderHeadings.append(heading + ",");
    }
    stringBuilder.append(stringBuilderHeadings.toString());
    for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
      stringBuilderRecords.append(Constants.NEWLINE);
      for (final int key : getHeadingsIndex().keySet()) {
        String value = getCellText(rowIndex, key);
        value = prepareForCSV(value);
        stringBuilderRecords.append(value + ",");
      }
    }
    stringBuilder.append(stringBuilderRecords.toString());
    FSOTests.fileWrite(fileName, stringBuilder.toString(), false);
  }

  /**
   * @param row
   * @param columnIndex
   * @param attribute
   * @return
   */
  public String getCellAttribute(int row, int columnIndex, String attribute) {
    final int rowIndex = row - 1;
    if (row > getTableRowsList().size()) {
      return null;
    }
    final WebElement webElement = getRowIndex(rowIndex);
    highlightWebElement(webElement);
    final List<WebElement> listWebElementCells = webElement.findElements(By.xpath(getxPathCells()));
    final WebElement webElementCell = listWebElementCells.get(columnIndex - 1);
    highlightWebElement(webElementCell);
    return webElementCell.getAttribute(attribute);
  }

  /**
   * @param row
   * @param columnName
   * @param attribute
   * @return
   */
  public String getCellAttribute(int row, String columnName, String attribute) {
    populateTableObjects();
    return getCellAttribute(
        row, getHeadingsNameLookup().get(columnName.toLowerCase(Locale.ENGLISH)), attribute);
  }

  /**
   * @param row
   * @param columnIndex
   * @return
   */
  public String getCellText(int row, int columnIndex) {
    populateTableObjects();
    final int rowIndex = row - 1;
    if (row > getTableRowsList().size()) {
      return null;
    }
    final WebElement webElement = getRowIndex(rowIndex);
    highlightWebElement(webElement);
    final List<WebElement> listWebElementCells = webElement.findElements(By.xpath(getxPathCells()));
    final WebElement webElementCell = listWebElementCells.get(columnIndex - 1);
    highlightWebElement(webElementCell);
    return webElementCell.getText();
  }

  /**
   * @param row
   * @param columnName
   * @return
   */
  public String getCellText(int row, String columnName) {
    return getCellText(row, getHeadingsNameLookup().get(columnName.toLowerCase(Locale.ENGLISH)));
  }

  public int getColumnCount() {
    setHeadingMaps();
    return getTableHeadingsList().size();
  }

  /**
   * @param columnName
   * @return
   */
  public int getColumnIndex(String columnName) {
    setHeadingMaps();
    int index = -1;
    try {
      index = getHeadingsNameLookup().get(columnName.toLowerCase(Locale.ENGLISH));
    } catch (final Exception e) {
      LOG.debug("getColumnIndex-No Column Found");
    }
    return index;
  }

  /**
   * @param columnName
   * @param caseSensitive
   * @return
   */
  public int getColumnIndex(String columnName, boolean caseSensitive) {
    setHeadingMaps();
    if (caseSensitive) {
      return getHeadingsName().get(columnName);
    } else {
      return getHeadingsNameLookup().get(columnName.toLowerCase(Locale.ENGLISH));
    }
  }

  /**
   * @param columnIndex
   * @return
   */
  public String getColumnName(int columnIndex) {
    setHeadingMaps();
    return getHeadingsIndex().get(columnIndex);
  }

  /**
   * @param by
   * @return
   */
  private List<String> getColumnRowValues(By by) {
    final List<String> listColumnValues = new ArrayList<>();
    final List<WebElement> columnElements = getWebElementTable().findElements(by);
    for (final WebElement webElement : columnElements) {
      highlightWebElement(webElement);
      final String cellValue = webElement.getText();
      listColumnValues.add(cellValue);
    }
    return listColumnValues;
  }

  /**
   * @param columnIndex
   * @return
   */
  public List<String> getColumnValues(int columnIndex) {
    return getColumnRowValues(
        By.xpath(getXpathRows() + getxPathCells().replace(".", "") + "[" + columnIndex + "]"));
  }

  /**
   * @param columnName
   * @return
   */
  public List<String> getColumnValues(String columnName) {
    final int columnIndex = getColumnIndex(columnName);
    return getColumnRowValues(
        By.xpath(getXpathRows() + getxPathCells().replace(".", "") + "[" + columnIndex + "]"));
  }

  public int getRowCount() {
    populateTableObjects();
    return getTableRowsList().size();
  }

  /**
   * @param rowIndex
   * @return
   */
  private WebElement getRowIndex(int rowIndex) {
    return getTableRowsList().get(rowIndex);
  }

  /**
   * @param row
   * @return
   */
  public List<String> getRowValues(int row) {
    return getColumnRowValues(
        By.xpath(getXpathRows() + "[" + row + "]" + getxPathCells().replace(".", "")));
  }

  /**
   * @param xpath
   * @return
   */
  private WebElement getTable(String xpath) {
    return getWebDriver().findElement(By.xpath(xpath));
  }

  /**
   * @param highlightObjects
   */
  public void highlightObjects(boolean highlightObjects) {
    this.highlightObjects = highlightObjects;
  }

  /**
   * @param webElement
   */
  public void highlightWebElement(WebElement webElement) {
    if (highlightObjects) {
      new Page(getWebDriver()).highlightCurrentElement(webElement);
    }
  }

  /**
   * @param map
   * @return
   */
  public int matchingRecordsCount(Map<String, String> map) {
    final List<Integer> recordsSearch = matchingRecordsList(map);
    return recordsSearch.size();
  }

  /**
   * @param map
   * @return
   */
  public boolean matchingRecordsExist(Map<String, String> map) {
    return matchingRecordsCount(map) > 0;
  }

  /**
   * @param map
   * @return
   */
  public List<Integer> matchingRecordsList(Map<String, String> map) {
    populateTableObjects();
    final List<Integer> listRecordsSearch = new ArrayList<>();
    for (int record = 1; record <= getRowCount(); record++) {
      listRecordsSearch.add(record);
    }
    for (final String keySearch : map.keySet()) {
      final List<Integer> listRecordsNoMatch = new ArrayList<>();
      final String columnName = keySearch;
      final String valueMatch = map.get(keySearch);
      final int columnIndex = getHeadingsNameLookup().get(columnName.toLowerCase(Locale.ENGLISH));
      for (final int record : listRecordsSearch) {
        final String cellValue = getCellText(record, columnIndex);
        if (!cellValue.equals(valueMatch)) {
          listRecordsNoMatch.add(record);
        }
      }
      listRecordsSearch.removeAll(listRecordsNoMatch);
    }
    return listRecordsSearch;
  }

  private void populateTableObjects() {
    setHeadingMaps();
    setRowList();
  }

  /**
   * @param value
   * @return
   */
  public String prepareForCSV(String value) {
    value = value.replaceAll(Constants.QUOTE_DOUBLE, "'");
    if (value.contains(",")
        || value.contains(Constants.CR)
        || value.contains(Constants.NL)
        || value.contains(Constants.NEWLINE)) {
      final String valueTemp = Constants.QUOTE_DOUBLE + value + Constants.QUOTE_DOUBLE;
      value = valueTemp;
    }
    return value;
  }

  public void reset() {
    this.resetTable = true;
  }

  private void setHeadingMaps() {
    setTable();
    if (!resetTable && getTableHeadingsList() != null) {
      return;
    }
    final List<WebElement> headings =
        getWebElementTable().findElements(By.xpath(getXpathHeadings()));
    setHeadingsName(new HashMap<>());
    setHeadingsNameLookup(new HashMap<>());
    setHeadingsIndex(new HashMap<>());
    for (int headingIndex = 0; headingIndex < headings.size(); headingIndex++) {
      final WebElement webElement = headings.get(headingIndex);
      highlightWebElement(webElement);
      String columnName = webElement.getText();
      if (columnName.isEmpty()) {
        columnName = getColumnNamePrefix() + String.valueOf(headingIndex + 1);
      }
      getHeadingsName().put(columnName, headingIndex + 1);
      getHeadingsIndex().put(headingIndex + 1, columnName);
      getHeadingsNameLookup().put(columnName.toLowerCase(Locale.ENGLISH).trim(), headingIndex + 1);
    }
    setTableHeadingsList(headings);
  }

  private void setRowList() {
    if (!resetTable && getTableRowsList() != null) {
      return;
    }
    setTableRowsList(getWebElementTable().findElements(By.xpath(getXpathRows())));
  }

  private void setTable() {
    setWebElementTable(getTable(getxPathTable()));
    highlightWebElement(getWebElementTable());
  }

  public boolean isHighlightObjects() {
    return highlightObjects;
  }

  /**
   * @param highlightObjects
   */
  public void setHighlightObjects(boolean highlightObjects) {
    this.highlightObjects = highlightObjects;
  }

  public boolean isResetTable() {
    return resetTable;
  }

  /**
   * @param resetTable
   */
  public void setResetTable(boolean resetTable) {
    this.resetTable = resetTable;
  }

  public String getxPathTable() {
    return xPathTable;
  }

  /**
   * @param xPathTable
   */
  public void setxPathTable(String xPathTable) {
    this.xPathTable = xPathTable;
  }

  public String getXpathHeadings() {
    return xpathHeadings;
  }

  /**
   * @param xpathHeadings
   */
  public void setXpathHeadings(String xpathHeadings) {
    this.xpathHeadings = xpathHeadings;
  }

  public String getXpathRows() {
    return xpathRows;
  }

  /**
   * @param xpathRows
   */
  public void setXpathRows(String xpathRows) {
    this.xpathRows = xpathRows;
  }

  public String getxPathCells() {
    return xPathCells;
  }

  /**
   * @param xPathCells
   */
  public void setxPathCells(String xPathCells) {
    this.xPathCells = xPathCells;
  }

  public Map<String, Integer> getHeadingsName() {
    return headingsName;
  }

  /**
   * @param headingsName
   */
  public void setHeadingsName(Map<String, Integer> headingsName) {
    this.headingsName = headingsName;
  }

  /**
   * @return
   */
  public Map<String, Integer> getHeadingsNameLookup() {
    return headingsNameLookup;
  }

  /**
   * @param headingsNameLookup
   */
  public void setHeadingsNameLookup(Map<String, Integer> headingsNameLookup) {
    this.headingsNameLookup = headingsNameLookup;
  }

  /**
   * @return
   */
  public Map<Integer, String> getHeadingsIndex() {
    return headingsIndex;
  }

  /**
   * @param headingsIndex
   */
  public void setHeadingsIndex(Map<Integer, String> headingsIndex) {
    this.headingsIndex = headingsIndex;
  }

  public WebElement getWebElementTable() {
    return webElementTable;
  }

  /**
   * @param table
   */
  public void setWebElementTable(WebElement table) {
    this.webElementTable = table;
  }

  public List<WebElement> getTableHeadingsList() {
    return tableHeadingsList;
  }

  /**
   * @param tableHeadingsList
   */
  public void setTableHeadingsList(List<WebElement> tableHeadingsList) {
    this.tableHeadingsList = tableHeadingsList;
  }

  public List<WebElement> getTableRowsList() {
    return tableRowsList;
  }

  /**
   * @param tableRowsList
   */
  public void setTableRowsList(List<WebElement> tableRowsList) {
    this.tableRowsList = tableRowsList;
  }
}
