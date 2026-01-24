package com.cjs.qa.junit.tests;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.SeleniumWebDriver;
import com.cjs.qa.selenium.WebElementTable;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

import io.cucumber.datatable.DataTable;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class WebElementTableTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(WebElementTableTests.class));

  public static final String LABEL_TITLE = "Title";
  public static final String LABEL_GROSS = "Gross";
  public static final String LABEL_WEEKS = "Weeks";
  private WebDriver webDriver;
  private SeleniumWebDriver seleniumWebDriver;
  private String methodName = null;
  private WebElementTable webElementTable = null;
  private final String pathOutput =
      Constants.PATH_FILES_DATA + this.getClass().getName() + Constants.DELIMETER_PATH;
  private final String boxOfficeMojoDate =
      DateHelpersTests.getCurrentDatePlusMinusDays("YYYY-MM-dd", -2);

  @BeforeEach
  void testSetup(TestInfo testInfo) throws Throwable {
    Environment.setEnvironmentVariableValues();
    setMethodName(getTestName(testInfo));
    final String[] methodElements = getMethodName().split("_");
    final String methodTest = methodElements[0];
    // Environment.setEnvironmentFileStructure(methodTest)
    setSeleniumWebDriver(new SeleniumWebDriver(null, Environment.isRunRemote(), null, null, null));
    setWebDriver(getSeleniumWebDriver().getWebDriver());
    LOG.info("Setup-Test Method: [{}], methodTest: [{}]", getMethodName(), methodTest);
  }

  @AfterEach
  void testTeardown() {
    LOG.info("TearDown");
    getSeleniumWebDriver().killBrowser();
  }

  private String getBoxOfficeMojoDate() {
    return boxOfficeMojoDate;
  }

  private String getMethodName() {
    return methodName;
  }

  private String getPathOutput() {
    return pathOutput;
  }

  private SeleniumWebDriver getSeleniumWebDriver() {
    return seleniumWebDriver;
  }

  private DataTable getTableDataSearch(int record) {
    return Convert.fromListToDataTable(
        Arrays.asList(
            Arrays.asList("#1", getWebElementTable().getCellText(record, "#1")),
            Arrays.asList(LABEL_TITLE, getWebElementTable().getCellText(record, LABEL_TITLE)),
            Arrays.asList("Weekend", getWebElementTable().getCellText(record, "Weekend")),
            Arrays.asList(LABEL_GROSS, getWebElementTable().getCellText(record, LABEL_GROSS)),
            Arrays.asList(LABEL_WEEKS, getWebElementTable().getCellText(record, LABEL_WEEKS)),
            Arrays.asList("#6", getWebElementTable().getCellText(record, "#6"))));
  }

  private DataTable getTableDataSearchHardCoded(int record) {
    // final Map<String, String> mapData = new HashMap<String, String>()
    // dataTable = Convert.toDataTable(Arrays.asList(Arrays.asList("I1",
    // mapData.get("I1")), Arrays.asList("I2", mapData.get("I2"))))
    DataTable dataTable = null;
    if ("tBoxOfficeSearch".equals(getMethodName())) {
      switch (record) {
        case 1:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Jigsaw"),
                      Arrays.asList("Weekend", "$16.6M"),
                      Arrays.asList(LABEL_GROSS, "$16.6M"),
                      Arrays.asList(LABEL_WEEKS, "1")));
          break;
        case 2:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Tyler Perry's Boo 2! A Madea Halloween"),
                      Arrays.asList("Weekend", "$10.1M"),
                      Arrays.asList(LABEL_GROSS, "$35.6M"),
                      Arrays.asList(LABEL_WEEKS, "2")));
          break;
        case 3:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Geostorm"),
                      Arrays.asList("Weekend", "$5.9M"),
                      Arrays.asList(LABEL_GROSS, "$23.8M"),
                      Arrays.asList(LABEL_WEEKS, "2")));
          break;
        case 4:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Happy Death Day"),
                      Arrays.asList("Weekend", "$5.1M"),
                      Arrays.asList(LABEL_GROSS, "$48.4M"),
                      Arrays.asList(LABEL_WEEKS, "3")));
          break;
        case 5:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Blade Runner 2049"),
                      Arrays.asList("Weekend", "$4.1M"),
                      Arrays.asList(LABEL_GROSS, "$81.5M"),
                      Arrays.asList(LABEL_WEEKS, "4")));
          break;
        case 6:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Thank You for Your Service"),
                      Arrays.asList("Weekend", "$3.8M"),
                      Arrays.asList(LABEL_GROSS, "$3.8M"),
                      Arrays.asList(LABEL_WEEKS, "1")));
          break;
        case 7:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Only the Brave"),
                      Arrays.asList("Weekend", "$3.5M"),
                      Arrays.asList(LABEL_GROSS, "$12.0M"),
                      Arrays.asList(LABEL_WEEKS, "2")));
          break;
        case 8:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "The Foreigner"),
                      Arrays.asList("Weekend", "$3.4M"),
                      Arrays.asList(LABEL_GROSS, "$29.1M"),
                      Arrays.asList(LABEL_WEEKS, "3")));
          break;
        case 9:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "Suburbicon"),
                      Arrays.asList("Weekend", "$2.8M"),
                      Arrays.asList(LABEL_GROSS, "$2.8M"),
                      Arrays.asList(LABEL_WEEKS, "1")));
          break;
        case 10:
          dataTable =
              Convert.fromListToDataTable(
                  Arrays.asList(
                      Arrays.asList(LABEL_TITLE, "It"),
                      Arrays.asList("Weekend", "$2.5M"),
                      Arrays.asList(LABEL_GROSS, "$323.9M"),
                      Arrays.asList(LABEL_WEEKS, "8")));
          break;
        default:
          break;
      }
    }
    return dataTable;
  }

  private String getTestName(TestInfo testInfo) {
    return testInfo.getTestMethod().map(Method::getName).orElse("Unknown");
  }

  private WebDriver getWebDriver() {
    return webDriver;
  }

  private WebElementTable getWebElementTable() {
    return webElementTable;
  }

  private void readWriteSysOut(int startColumn, int startRow, int endColumn, int endRow) {
    final int columnCount = getWebElementTable().getColumnCount();
    LOG.debug("columnCount: [{}]", columnCount);
    final int rowCount = getWebElementTable().getRowCount();
    LOG.debug("rowCount: [{}]", rowCount);
    if (endColumn == 0) {
      endColumn = columnCount;
    }
    if (endRow == 0) {
      endRow = rowCount;
    }
    for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
      for (int columnIndex = startColumn; columnIndex <= endColumn; columnIndex++) {
        final String columnName = getWebElementTable().getColumnName(columnIndex);
        final int columnTableIndex = getWebElementTable().getColumnIndex(columnName);
        final String cellValue = getWebElementTable().getCellText(rowIndex, columnName);
        final String cellValueIndex = getWebElementTable().getCellText(rowIndex, columnIndex);
        final String cellAttribute =
            getWebElementTable().getCellAttribute(rowIndex, columnName, "class");
        LOG.debug(
            "Column Name By Index (Column Index: [{}]): [{}], "
                + "Column Index By Name (Column Name: [{}]): [{}], "
                + "Cell Value By Name: [{}], Cell Value By Index: [{}], "
                + "Cell 'class' Attribute By Index: [{}]",
            columnIndex,
            columnName,
            columnName,
            columnTableIndex,
            cellValue,
            cellValueIndex,
            cellAttribute);
      }
    }
    LOG.debug(
        "Name: [{}], Index: [{}], Value: [{}]",
        getWebElementTable().getColumnName(2),
        getWebElementTable().getColumnIndex(LABEL_TITLE),
        getWebElementTable().getCellText(rowCount + 1, 2));
    LOG.debug(
        "Name: [{}], Index: [{}], Value: [{}]",
        getWebElementTable().getColumnName(columnCount + 1),
        getWebElementTable().getColumnIndex(LABEL_TITLE),
        getWebElementTable().getCellText(rowCount + 1, 2));
  }

  private void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  private void setSeleniumWebDriver(SeleniumWebDriver seleniumWebDriver) {
    this.seleniumWebDriver = seleniumWebDriver;
  }

  private void setTable() {
    String url = null;
    String xPathTable = null;
    // ".//*[@id='boxoffice']/table/thead/../tbody/tr/td[@class='titleColumn']"
    String xPathHeadings = null; // "./thead/tr/th"
    String xPathRows = null; // "./tbody/tr"
    String xPathCells = null; // "./td"
    final String currentURL = getWebDriver().getCurrentUrl();
    switch (getMethodName()) {
      case "tBoxOfficeClickColumnRow":
      case "tBoxOfficeClickRowColumn":
        Environment.setScrollToObject(true);
        break;
      default:
        break;
    }
    switch (getMethodName()) {
      case "tBoxOffice":
      case "tBoxOfficeClickColumnRow":
      case "tBoxOfficeClickRowColumn":
      case "tBoxOfficeLists":
      case "tBoxOfficeSearch":
        url =
            "http://www.imdb"
                + IExtension.COM
                + "/chart/boxoffice?pf_rd_m=A2FGELUUNOQJNL&pf_rd_p=2773216402"
                + "&pf_rd_r=01DVJQ5VZWPDNQW51NYR&pf_rd_s=right-7"
                + "&pf_rd_t=15061&pf_rd_i=homepage&ref_=hm_cht_sm";
        xPathTable = ".//*[@id='boxoffice']/table";
        break;
      case "testBoxOfficeMojo00":
      case "tBoxOfficeMojo_01":
      case "testBoxOfficeMojo07":
      case "testBoxOfficeMojo34":
        final String[] aDays = getMethodName().split("_");
        final String days = aDays[1];
        if ("0".equals(days)) {
          url =
              "http://www.boxofficemojo"
                  + IExtension.COM
                  + "/daily/chart/?sortdate="
                  + getBoxOfficeMojoDate()
                  + "&p="
                  + IExtension.HTML;
        } else {
          // url = "http://www.boxofficemojo" + IExtension.COM +
          // "/daily/chart/?view=1day&sortdate=" +
          // getBoxOfficeMojoDate() + "&p="+ IExtension.HTML
          url =
              "http://www.boxofficemojo"
                  + IExtension.COM
                  + "/daily/chart/?view="
                  + days
                  + "day&sortdate="
                  + getBoxOfficeMojoDate()
                  + "&p="
                  + IExtension.HTML;
        }
        if ("1".equals(days)) {
          xPathTable = ".//*[@id='body']/center/center/table/tbody/tr[2]/td/table";
          xPathHeadings = ".//tr[@bgcolor='#dcdcdc']/td/font";
          xPathRows = ".//tr[@bgcolor!='#dcdcdc']";
          xPathCells = "./td/font";
        } else {
          xPathTable = ".//*[@id='body']//center/table/tbody/tr[2]/td/table";
          xPathHeadings = ".//tr/th";
          xPathRows = ".//tr/td[@valign='top']/..";
          xPathCells = "./td";
        }
        break;
      default:
        break;
    }
    if (url != null && !url.equals(currentURL)) {
      getWebDriver().get(url);
    }
    setWebElementTable(
        new WebElementTable(getWebDriver(), xPathTable, xPathHeadings, xPathRows, xPathCells));
    // getWebElementTable()highlightObjects(true)
  }

  private void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  private void setWebElementTable(WebElementTable webElementTable) {
    this.webElementTable = webElementTable;
  }

  @Test
  void tBoxOffice(TestInfo testInfo) {
    setTable();
    readWriteSysOut(1, 1, 0, 0);
    getWebElementTable().highlightObjects(false);
    getWebElementTable().exportTable(getPathOutput() + getMethodName() + IExtension.CSV);
    getWebElementTable().reset();
  }

  @Test
  void tBoxOfficeClickColumnRow(TestInfo testInfo) {
    final List<String> columnsToClick = Arrays.asList("#1", LABEL_TITLE, "#6");
    setTable();
    final int columns = getWebElementTable().getColumnCount();
    final int rows = getWebElementTable().getRowCount();
    for (int column = 1; column <= columns; column++) {
      final String columnName = getWebElementTable().getColumnName(column);
      if (columnsToClick.contains(columnName)) {
        for (int row = 1; row <= rows; row++) {
          getWebElementTable().clickCell(columnName, row);
          setTable();
        }
      }
    }
  }

  @Test
  void tBoxOfficeClickRowColumn(TestInfo testInfo) {
    final List<String> columnsToClick = Arrays.asList("#1", LABEL_TITLE, "#6");
    setTable();
    final int columns = getWebElementTable().getColumnCount();
    final int rows = getWebElementTable().getRowCount();
    for (int row = 1; row <= rows; row++) {
      for (int column = 1; column <= columns; column++) {
        final String columnName = getWebElementTable().getColumnName(column);
        if (columnsToClick.contains(columnName)) {
          getWebElementTable().clickCell(columnName, row);
          setTable();
        }
      }
    }
  }

  @Test
  void tBoxOfficeLists(TestInfo testInfo) {
    setTable();
    final int columnCount = getWebElementTable().getColumnCount();
    LOG.debug("columnCount: [{}]", columnCount);
    final int rowCount = getWebElementTable().getRowCount();
    LOG.debug("rowCount: [{}]", rowCount);
    List<String> listValues = null;
    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
      final String columnName = getWebElementTable().getColumnName(columnIndex);
      listValues = getWebElementTable().getColumnValues(columnIndex);
      LOG.debug("listValues ({}): [{}]", columnName, listValues.toString());
      if (columnName.isEmpty()) {
        LOG.debug("listValues ({}): []", columnName);
      } else {
        listValues = getWebElementTable().getColumnValues(columnName);
        LOG.debug("listValues ({}): [{}]", columnName, listValues.toString());
      }
    }
    for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
      listValues = getWebElementTable().getRowValues(rowIndex);
      LOG.debug("listValues ({}): [{}]", rowIndex, listValues.toString());
    }
  }

  @Test
  void testBoxOfficeMojo00(TestInfo testInfo) {
    setTable();
    readWriteSysOut(1, 1, 0, 0);
    getWebElementTable().highlightObjects(false);
    getWebElementTable()
        .exportTable(
            getPathOutput() + getMethodName() + "_" + getBoxOfficeMojoDate() + IExtension.CSV);
    getWebElementTable().reset();
  }

  @Test
  void testBoxOfficeMojo01(TestInfo testInfo) {
    setTable();
    readWriteSysOut(1, 1, 0, 0);
    getWebElementTable().highlightObjects(false);
    getWebElementTable()
        .exportTable(
            getPathOutput() + getMethodName() + "_" + getBoxOfficeMojoDate() + IExtension.CSV);
    getWebElementTable().reset();
  }

  @Test
  void testBoxOfficeMojo07(TestInfo testInfo) {
    setTable();
    readWriteSysOut(1, 1, 0, 0);
    getWebElementTable().highlightObjects(false);
    getWebElementTable()
        .exportTable(
            getPathOutput() + getMethodName() + "_" + getBoxOfficeMojoDate() + IExtension.CSV);
    getWebElementTable().reset();
  }

  @Test
  void testBoxOfficeMojo34(TestInfo testInfo) {
    setTable();
    readWriteSysOut(1, 1, 0, 0);
    getWebElementTable().highlightObjects(false);
    getWebElementTable()
        .exportTable(
            getPathOutput() + getMethodName() + "_" + getBoxOfficeMojoDate() + IExtension.CSV);
    getWebElementTable().reset();
  }

  @Test
  void tBoxOfficeSearch(TestInfo testInfo) {
    setTable();
    final int rowsCount = getWebElementTable().getRowCount();
    for (int searchRecord = 1; searchRecord <= rowsCount; searchRecord++) {
      DataTable dataTable = getTableDataSearchHardCoded(searchRecord);
      dataTable = getTableDataSearch(searchRecord);
      LOG.debug("dataTable: [{}]", dataTable.toString());
      final Map<String, String> map = Convert.fromDataTableToMap(dataTable, false);
      LOG.debug("map: [{}]", map.toString());
      LOG.debug("matchingRecordsExist: [{}]", getWebElementTable().matchingRecordsExist(map));
      LOG.debug("matchingRecordsCount: [{}]", getWebElementTable().matchingRecordsCount(map));
      LOG.debug("matchingRecordsList: [{}]", getWebElementTable().matchingRecordsList(map));
    }
    final List<String> listWeeks = getWebElementTable().getColumnValues(LABEL_WEEKS);
    final List<String> listWeeksTested = new ArrayList<>();
    for (final String listWeek : listWeeks) {
      if (!listWeeksTested.contains(listWeek)) {
        listWeeksTested.add(listWeek);
        final Map<String, String> map = new HashMap<>();
        map.put(LABEL_WEEKS, listWeek);
        LOG.debug(
            "matchingRecordsExist (listWeek: [{}]): [{}]",
            listWeek,
            getWebElementTable().matchingRecordsExist(map));
        LOG.debug(
            "matchingRecordsCount (listWeek: [{}]): [{}]",
            listWeek,
            getWebElementTable().matchingRecordsCount(map));
        LOG.debug(
            "matchingRecordsList (listWeek: [{}]): [{}]",
            listWeek,
            getWebElementTable().matchingRecordsList(map));
      }
    }
  }
}
