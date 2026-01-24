package com.cjs.qa.junit.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class ExcelStatisticalTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ExcelStatisticalTests.class));
  // public static final String TIMINGS_PATH = Constants.DELIMETER_PATH +
  // Constants.DELIMETER_PATH + "wrbts" + Constants.DELIMETER_PATH + "shared"
  // + Constants.DELIMETER_PATH + "bts QA" + Constants.DELIMETER_PATH + "QA
  // Tools" + Constants.DELIMETER_PATH + "Timings" + Constants.DELIMETER_PATH
  public static final String PATH_TIMINGS =
      "C:"
          + Constants.DELIMETER_PATH
          + "Temp"
          + Constants.DELIMETER_PATH
          + "Excel_Timings"
          + Constants.DELIMETER_PATH;
  protected static final List<String> PATH_LIST = Arrays.asList("VALA", "VALA-LP");
  private ExcelTiming excelTimingControl = new ExcelTiming();
  private ExcelTiming excelTimingTest = new ExcelTiming();

  @Test
  public void getStatisticalTimings() throws IOException, QAException {
    for (final String path : PATH_LIST) {
      final String timingPath = PATH_TIMINGS + path + Constants.DELIMETER_PATH;
      switch (path) {
        case "VALA":
          getData(timingPath, excelTimingControl);
          break;
        case "VALA-LP":
          getData(timingPath, excelTimingTest);
          break;
        default:
          break;
      }
    }
    createStatisticalReport();
  }

  private void getData(String timingPath, ExcelTiming excelTiming) throws QAException, IOException {
    final String sheetName = "Sheet1";
    excelTiming.setFileList(FSOTests.filesList(timingPath, IExtension.XLS));
    for (final String filePathName : excelTiming.getFileList()) {
      LOG.debug("filePathName: [{}]", filePathName);
      final XLS excel = new XLS(filePathName, sheetName);
      final int rows = excel.getRowCount(sheetName);
      for (int row = 1; row < rows; row++) {
        String methodName = excel.readCell(sheetName, 0, row);
        methodName = methodName.toLowerCase(Locale.ENGLISH);
        final double timing = Double.parseDouble(excel.readCell(sheetName, 1, row));
        LOG.debug("methodName: [{}], timing: [{}]", methodName, timing);
        if (excelTiming.getMethodCallsMap().get(methodName) == null) {
          excelTiming.getMethodCallsMap().put(methodName, 1);
          excelTiming.getMethodTimingMap().put(methodName, timing);
        } else {
          excelTiming
              .getMethodCallsMap()
              .put(methodName, excelTiming.getMethodCallsMap().get(methodName) + 1);
          excelTiming
              .getMethodTimingMap()
              .put(methodName, excelTiming.getMethodTimingMap().get(methodName) + timing);
        }
      }
      excel.close();
    }
  }

  private void createStatisticalReport() throws QAException, IOException {
    final String sheetName = "Statistical Timings";
    final String filePathName =
        PATH_TIMINGS
            + sheetName
            + "_"
            + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_DATE_TIME_STAMP)
            + IExtension.XLS;
    final XLS excel = new XLS(filePathName, sheetName);
    int row = 1;
    // excel.writeCell(sheetName, 0, 0, "Method")
    excel.writeCell(sheetName, 1, 0, PATH_LIST.get(0));
    excel.writeCell(sheetName, 2, 0, "Files");
    excel.writeCell(sheetName, 3, 0, getExcelTimingControl().getFileList().size());
    excel.writeCell(sheetName, 4, 0, PATH_LIST.get(1));
    excel.writeCell(sheetName, 5, 0, "Files");
    excel.writeCell(sheetName, 6, 0, getExcelTimingTest().getFileList().size());
    // excel.writeCell(sheetName, 7, 0, "Difference")
    excel.writeCell(sheetName, 0, row, "Method");
    excel.writeCell(sheetName, 1, row, "Calls");
    excel.writeCell(sheetName, 2, row, "Time");
    excel.writeCell(sheetName, 3, row, "Average");
    excel.writeCell(sheetName, 4, row, "Calls");
    excel.writeCell(sheetName, 5, row, "Time");
    excel.writeCell(sheetName, 6, row, "Average");
    excel.writeCell(sheetName, 7, row, "Difference");
    for (int column = 0; column < 8; column++) {
      excel.setFormatHeading(sheetName, column, 0);
      excel.setFormatHeading(sheetName, column, row);
    }
    for (final Entry<String, Integer> entry :
        getExcelTimingControl().getMethodCallsMap().entrySet()) {
      row++;
      final String methodNameControl = entry.getKey();
      final Integer methodCallsControl = entry.getValue();
      final Double methodTimeControl =
          getExcelTimingControl().getMethodTimingMap().get(methodNameControl);
      final Double methodTimeControlAverage = methodTimeControl / methodCallsControl;
      excel.writeCell(sheetName, 0, row, methodNameControl);
      excel.writeCell(sheetName, 1, row, methodCallsControl);
      excel.writeCell(sheetName, 2, row, methodTimeControl);
      excel.writeCell(sheetName, 3, row, methodTimeControlAverage);
      LOG.debug(
          "methodNameControl: [{}], methodCallsControl: [{}], methodTimeControl: [{}], methodTimeControlAverage: [{}]",
          methodNameControl,
          methodCallsControl,
          methodTimeControl,
          methodTimeControlAverage);
      final Integer methodCallsTest =
          getExcelTimingTest().getMethodCallsMap().get(methodNameControl);
      final Double methodTimeTest =
          getExcelTimingTest().getMethodTimingMap().get(methodNameControl);
      final Double methodTimeTestAverage = methodTimeTest / methodCallsTest;
      LOG.debug(
          "methodNameTest: [{}], methodCallsTest: [{}], methodTimeTest: [{}], methodTimeTestAverage: [{}]",
          methodNameControl,
          methodCallsTest,
          methodTimeTest,
          methodTimeTestAverage);
      excel.writeCell(sheetName, 4, row, methodCallsTest);
      excel.writeCell(sheetName, 5, row, methodTimeTest);
      excel.writeCell(sheetName, 6, row, methodTimeTestAverage);
      excel.writeCell(sheetName, 7, row, methodTimeTestAverage - methodTimeControlAverage);
      // getExcelTimingControl().getMethodCallsMap().remove(methodNameControl)
      // getExcelTimingControl().getMethodTimingMap().remove(methodNameControl)
      // getExcelTimingTest().getMethodCallsMap().remove(methodNameControl)
      // getExcelTimingTest().getMethodTimingMap().remove(methodNameControl)
    }
    row++;
    excel.writeCell(sheetName, 0, row, "Totals");
    for (int column = 0; column < 8; column++) {
      if (column >= 1) {
        excel.writeCellFormula(
            sheetName,
            column,
            row,
            "Sum("
                + Convert.fromNumberToLetterExcel(column)
                + "3:"
                + Convert.fromNumberToLetterExcel(column)
                + row
                + ")");
      }
      excel.setFormatHeading(sheetName, column, row);
    }
    excel.autoSizeColumns(sheetName);
    excel.save();
    excel.close();
  }

  public ExcelTiming getExcelTimingControl() {
    return excelTimingControl;
  }

  public void setExcelTimingControl(ExcelTiming excelTimingControl) {
    this.excelTimingControl = excelTimingControl;
  }

  public ExcelTiming getExcelTimingTest() {
    return excelTimingTest;
  }

  public void setExcelTimingTest(ExcelTiming excelTimingTest) {
    this.excelTimingTest = excelTimingTest;
  }
}
