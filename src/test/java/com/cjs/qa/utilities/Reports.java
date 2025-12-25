package com.cjs.qa.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.microsoft.excel.xls.XLS;

import io.cucumber.java.Scenario;

public final class Reports {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Reports.class));

  private Reports() {
    // Utility class - prevent instantiation
  }

  public static final String JIRA_URL = "http://jira-tst.wrberkley" + IExtension.COM + "/browse/";
  public static final int DEFAULT_SHEET_COUNT = 2;
  public static final String STRING_SUMMARY =
      "Test Set;Test ID;Environment;Company;LOB;Test Name;Scenario"
          + " Name;Status;Error(s);Started;Completed;Elapsed (seconds);Browser;Scenario"
          + " ID;Session ID;API;DB;GUI";
  public static final String STRING_COUNTS =
      "Tests;Passed;Failed;Compare;Copy;Rate;Issue;Policy;Product;Forms";
  private static final List<String> LIST_SUMMARY =
      Arrays.asList(STRING_SUMMARY.split(Constants.DELIMETER_LIST));
  private static final List<String> LIST_COUNTS =
      Arrays.asList(STRING_COUNTS.split(Constants.DELIMETER_LIST));
  private static final Map<String, Integer> MAP_INDEX_SUMMARY = getColumnsIndex(STRING_SUMMARY);
  private static final Map<String, Integer> MAP_INDEX_COUNTS = getColumnsIndex(STRING_COUNTS);
  private static final Map<String, String> MAP_STRING_SUMMARY = getColumnsString(STRING_SUMMARY);
  private static Map<String, Integer> resultColumns = null;

  public static synchronized void updateReport(File filePathName, byte[] byteArray)
      throws IOException {
    FileChannel fileChannel = null;
    try {
      final Path path = FileSystems.getDefault().getPath(filePathName.getCanonicalPath());
      fileChannel = FileChannel.open(path, StandardOpenOption.WRITE);
      // Aquire lock
      LOG.debug("Acquiring lock");
      fileChannel.lock();
      LOG.debug("Lock acquired");
      // Read in the excel file as Apache POI object
      // Append stuffToAdd to that
      // Write to file
      final ByteBuffer buf = ByteBuffer.allocate(1024);
      buf.clear();
      buf.put(byteArray);
      buf.flip();
      while (buf.hasRemaining()) {
        fileChannel.write(buf);
      }
    } finally {
      // Release lock
      if (fileChannel != null) {
        fileChannel.close();
        LOG.debug("Lock released");
      }
    }
  }

  public static synchronized void createReportExcelLock(
      Scenario scenarioObject, Map<String, List<Map<String, String>>> mapListTest)
      throws Exception, QAException {
    final String fileNameLock = CJSConstants.PATH_FILES_DATA + "Parallel.lck";
    FileChannel fileChannel = null;
    try {
      final File file = new File(fileNameLock);
      final Path path = FileSystems.getDefault().getPath(file.getCanonicalPath());
      try {
        // fileChannel = FileChannel.open(path,
        // StandardOpenOption.WRITE);
        fileChannel = FileChannel.open(path, StandardOpenOption.APPEND);
      } catch (final Exception e) {
        e.printStackTrace();
      }
      // Aquire lock
      if (fileChannel != null) {
        LOG.debug("Acquiring Lock: [{}]", fileNameLock);
        fileChannel.lock();
        LOG.debug("Lock Acquired: [{}]", fileNameLock);
        // Read in the excel file as Apache POI object
        final byte[] byteArray =
            (mapListTest.get("Summary").toString() + Constants.NEWLINE).getBytes();
        createReportExcel(scenarioObject, mapListTest);
        // Write to file
        final ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.clear();
        buf.put(byteArray);
        buf.flip();
        while (buf.hasRemaining()) {
          fileChannel.write(buf);
        }
      }
    } finally {
      // Release lock
      if (fileChannel != null) {
        fileChannel.close();
        LOG.debug("Lock Released: [{}]", fileNameLock);
      }
    }
  }

  private static void createReportExcel(
      Scenario scenarioObject, Map<String, List<Map<String, String>>> mapListTest) {
    LOG.debug("Writing Report: [{}]", mapListTest.toString());
    final String sheetNameSummary = IExcel.SHEET_SUMMARY;
    boolean writeData = true;
    try {
      final List<Map<String, String>> listMapSummary = mapListTest.get("Summary");
      final Map<String, String> mapSummary = listMapSummary.get(0);
      String filePathName = CJSConstants.PATH_FILES_DATA + "RESULTS";
      // filePathName += ("_" + mapSummary.get("Status")).toUpperCase();
      filePathName += IExtension.XLS;
      if (scenarioObject.isFailed()) {
        writeData = true;
      }
      if (!FSOTests.fileExists(filePathName)) {
        createResultFileDefaults(filePathName);
      }
      setColumnsIndex(STRING_SUMMARY);
      final XLS excel = new XLS(filePathName, sheetNameSummary);
      updateCount(excel, "Tests");
      final String sheetNameFailure =
          Environment.getSheetFailure(
              filePathName, DEFAULT_SHEET_COUNT, mapSummary.get("Policy Control"));
      if ("passed".equals(mapSummary.get("Status"))) {
        updateCount(excel, "Passed");
        if (writeData) {
          mapSummary.put("Error(s)", "N/A-See Sheet (" + sheetNameFailure + ")");
        } else {
          mapSummary.put("Error(s)", "N/A");
        }
      } else {
        updateCount(excel, "Failed");
        mapSummary.put("Error(s)", "See Sheet (" + sheetNameFailure + ")");
      }
      final int rowSummary = excel.getRowCount(sheetNameSummary) + 1;
      for (final String columnName : MAP_STRING_SUMMARY.keySet()) {
        final String value = mapSummary.get(columnName);
        if (JavaHelpers.hasValue(value)) {
          final int columnIndex = MAP_INDEX_SUMMARY.get(columnName);
          excel.writeCell(sheetNameSummary, columnIndex, rowSummary, mapSummary.get(columnName));
        }
      }
      excel.save();
      final String hyperlinkTestID = JIRA_URL + mapSummary.get("Test ID");
      if (JavaHelpers.hasValue(mapSummary.get("Test ID"))) {
        excel.addLink(
            sheetNameSummary,
            resultColumns.get("Test ID"),
            rowSummary,
            "URL",
            mapSummary.get("TestID"),
            hyperlinkTestID);
      }
      if (writeData) {
        if (JavaHelpers.hasValue(sheetNameFailure)) {
          excel.createSheet(sheetNameFailure);
          final String hyperlinkSheetSummary = "'" + sheetNameSummary + "'!A1";
          final String hyperlinkSheetFailure = "'" + sheetNameFailure + "'!A1";
          excel.addLink(
              sheetNameSummary,
              MAP_INDEX_SUMMARY.get("Error(s)"),
              rowSummary,
              "DOCUMENT",
              mapSummary.get("Error(s)"),
              hyperlinkSheetFailure);
          // "See Sheet (" + sheetNameFailure + ")",
          excel.addLink(
              sheetNameFailure, 0, 0, "DOCUMENT", sheetNameSummary, hyperlinkSheetSummary);
          for (final Integer key : Environment.getScenarioErrors().keySet()) {
            final int rowSheetFailure = excel.getRowCount(sheetNameFailure) + 1;
            excel.writeCell(
                sheetNameFailure, 0, rowSheetFailure, "Failure (" + String.valueOf(key) + ")");
            excel.autoSizeColumn(sheetNameFailure, 0);
            excel.setCellAlignment(sheetNameFailure, 0, 0);
            excel.writeCell(
                sheetNameFailure, 1, rowSheetFailure, Environment.getScenarioErrors().get(key));
            excel.autoSizeColumn(sheetNameFailure, 1);
            excel.setCellAlignment(sheetNameFailure, 1, 0);
            excel.setCellWrap(sheetNameFailure, 1, 0, true);
            scenarioObject.log(
                "Scenario Failure (" + key + "): " + Environment.getScenarioErrors().get(key));
          }
          writeFailureData(excel, sheetNameFailure, mapListTest, "Policy");
          writeFailureData(excel, sheetNameFailure, mapListTest, "Product");
          writeFailureData(excel, sheetNameFailure, mapListTest, "Forms");
          excel.autoSizeColumns(sheetNameFailure);
        }
      } else {
        excel.writeCell(
            sheetNameSummary,
            MAP_INDEX_SUMMARY.get("Error(s)"),
            rowSummary,
            mapSummary.get("Error(s)"));
      }
      excel.autoSizeColumns(sheetNameSummary);
      excel.autoSizeColumns(IExcel.SHEET_COUNTS);
      excel.writeCell(sheetNameSummary, 0, 0, excel.readCell(sheetNameSummary, 0, 0));
      // excel.save();
      excel.close();
    } catch (final Exception | QAException e) {
      e.printStackTrace();
    }
  }

  private static void createResultFileDefaults(String filePathName)
      throws QAException, IOException {
    XLS excel = null;
    try {
      String sheetName = IExcel.SHEET_SUMMARY;
      excel = new XLS(filePathName, sheetName);
      int row = 0;
      for (int columnIndex = 0; columnIndex < LIST_SUMMARY.size(); columnIndex++) {
        final String columnName = LIST_SUMMARY.get(columnIndex);
        excel.writeCell(sheetName, columnIndex, row, columnName);
        excel.setFormatBold(sheetName, columnIndex, row);
        excel.setFormatFontBackgroundColor(sheetName, columnIndex, row, "grey_25");
      }
      excel.autoSizeColumns(sheetName);
      sheetName = IExcel.SHEET_COUNTS;
      excel.createSheet(sheetName);
      row = 0;
      for (int columnIndex = 0; columnIndex < LIST_COUNTS.size(); columnIndex++) {
        final String columnName = LIST_COUNTS.get(columnIndex);
        excel.writeCell(sheetName, columnIndex, row, columnName);
        excel.setFormatBold(sheetName, columnIndex, row);
        excel.setFormatFontBackgroundColor(sheetName, columnIndex, row, "grey_25");
        excel.writeCell(sheetName, columnIndex, row + 1, 0);
      }
      excel.autoSizeColumns(sheetName);
      // excel.save();
      excel.close();
      excel = null;
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      if (excel != null) {
        excel.close();
        excel = null;
      }
    }
  }

  public static Map<String, Integer> getColumnsIndex(String fields) {
    final Map<String, Integer> map = new HashMap<>();
    final List<String> list = Arrays.asList(fields.split(Constants.DELIMETER_LIST));
    for (int index = 0; index < list.size(); index++) {
      final String field = list.get(index);
      map.put(field, index);
    }
    return map;
  }

  public static Map<String, String> getColumnsString(String fields) {
    final Map<String, String> map = new HashMap<>();
    final List<String> list = Arrays.asList(fields.split(Constants.DELIMETER_LIST));
    for (final String field : list) {
      map.put(field, "");
    }
    return map;
  }

  public static String getLogStatus(String report) {
    return Environment.noPMDUselessParentheses(
        "dateTimeStamp:["
            + DateHelpersTests.getCurrentDateTime(
                DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME + ".SSS")
            + "], "
            + report
            + Constants.NEWLINE);
  }

  private static void setColumnsIndex(String columns) {
    final List<String> list = Arrays.asList(columns.split(Constants.DELIMETER_LIST));
    resultColumns = new HashMap<>();
    for (int index = 0; index < list.size(); index++) {
      resultColumns.put(list.get(index), index);
    }
  }

  private static void writeFailureData(
      XLS excel,
      String sheetName,
      Map<String, List<Map<String, String>>> mapListTest,
      String section)
      throws QAException {
    try {
      final List<Map<String, String>> listMapSection = mapListTest.get(section);
      if (listMapSection == null) {
        return;
      }
      int row = excel.getRowCount(sheetName) + 1;
      int column = 0;
      excel.writeCell(sheetName, column, row, section);
      excel.setFormatBold(sheetName, column, row);
      excel.setFormatFontBackgroundColor(sheetName, column, row, "grey_25");
      String fields = "";
      switch (section) {
        case "Policy":
          fields = "Action;Index;Summary Item;Summary Amount";
          break;
        case "Product":
          fields =
              "Action;Index;Node;Element;Product;UnitState;CategoryDescription;"
                  + "SubUnit;Total;Coverage;SubCoverage;Identifier;Premium";
          break;
        case "Forms":
          fields = "Action;Index;ProductName;FormNumber;EditionDate;State";
          break;
        default:
          LOG.warn("Unknown section: {}. Using empty fields.", section);
          fields = "";
          break;
      }
      final List<String> fieldNames = Arrays.asList(fields.split(Constants.DELIMETER_LIST));
      setColumnsIndex(fields);
      row = excel.getRowCount(sheetName) + 1;
      for (int indexRecord = 0; indexRecord < listMapSection.size(); indexRecord++) {
        row = excel.getRowCount(sheetName) + 1;
        if (indexRecord == 0) {
          for (final String fieldName : fieldNames) {
            column = resultColumns.get(fieldName);
            excel.writeCell(sheetName, column, row, fieldName);
            excel.setFormatBold(sheetName, column, row);
            excel.setFormatFontBackgroundColor(sheetName, column, row, "grey_25");
          }
        }
        row = excel.getRowCount(sheetName) + 1;
        final Map<String, String> mapData = listMapSection.get(indexRecord);
        final String action = mapData.get("Action");
        if (!"Match".equals(action)) {
          for (final String fieldName : fieldNames) {
            column = resultColumns.get(fieldName);
            final String fieldValue = mapData.get(fieldName);
            if (JavaHelpers.hasValue(fieldValue)) {
              excel.writeCell(sheetName, column, row, fieldValue);
            }
          }
        }
      }
      for (final String fieldName : fieldNames) {
        column = resultColumns.get(fieldName);
        excel.autoSizeColumn(sheetName, column);
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private static void updateCount(XLS excel, String field) throws QAException {
    try {
      final String sheetName = IExcel.SHEET_COUNTS;
      final int column = MAP_INDEX_COUNTS.get(field);
      final String value = excel.readCell(sheetName, column, 1);
      int currentValue = Integer.valueOf(value);
      currentValue++;
      excel.writeCell(sheetName, column, 1, currentValue);
      excel.save();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
