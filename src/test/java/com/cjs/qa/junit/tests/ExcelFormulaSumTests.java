package com.cjs.qa.junit.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class ExcelFormulaSumTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ExcelFormulaSumTests.class));
  public static final String PATH_SUM =
      Constants.PATH_TEMP + "Excel_FormulaSum" + Constants.DELIMETER_PATH;
  public static final String SHEET_NAME = IExcel.SHEET_SUMMARY;
  public static final int COUNT_ROWS = 10;
  protected static final Map<String, Integer> COLUMNS_MAP = getColumns();

  @Test
  public void testExcelSum() throws IOException, QAException {
    final String filePathName = PATH_SUM + "FormulaSum" + IExtension.XLS;
    LOG.info("filePathName: [{}]", filePathName);
    // Ensure directory exists (cross-platform)
    FSOTests.folderCreate(PATH_SUM);
    if (FSOTests.fileExists(filePathName)) {
      FSOTests.fileDelete(filePathName);
    }
    final XLS excel = new XLS(filePathName, SHEET_NAME);
    createData(excel);
    sumData(excel);
    excel.autoSizeColumns(SHEET_NAME);
    excel.save();
    excel.close();
  }

  private void createData(XLS excel) throws QAException {
    for (Entry<String, Integer> entry : COLUMNS_MAP.entrySet()) {
      String columnName = entry.getKey();
      int column = entry.getValue();
      excel.writeCell(SHEET_NAME, column, 0, columnName);
      excel.setFormatHeading(SHEET_NAME, column, 0);
      for (int row = 1; row <= COUNT_ROWS; row++) {
        int randomNumber;
        if (columnName.contains("Passed")) {
          randomNumber = JavaHelpers.generateRandomInteger(100);
          excel.writeCell(SHEET_NAME, column, row, String.valueOf(randomNumber));
          excel.setFormatPass(SHEET_NAME, column, row);
        } else if (columnName.contains("Failed")) {
          randomNumber = JavaHelpers.generateRandomInteger(20);
          excel.writeCell(SHEET_NAME, column, row, String.valueOf(randomNumber));
          excel.setFormatFail(SHEET_NAME, column, row);
        }
      }
    }
  }

  private void sumData(XLS excel) throws QAException {
    final int rows = excel.getRowCount(SHEET_NAME);
    for (Entry<String, Integer> entry : COLUMNS_MAP.entrySet()) {
      String columnName = entry.getKey();
      int column = entry.getValue();
      String columnLetter = Convert.fromNumberToLetterExcel(column);
      String formulaSum = "SUM(" + columnLetter + "2:" + columnLetter + (rows + 1) + ")";
      LOG.debug("columnName: [{}], formulaSum: [{}]", columnName, formulaSum);
      excel.writeCellFormula(SHEET_NAME, column, rows + 1, formulaSum);
      if (columnName.contains("Passed")) {
        excel.setFormatPass(SHEET_NAME, column, rows + 1);
      } else if (columnName.contains("Failed")) {
        excel.setFormatFail(SHEET_NAME, column, rows + 1);
      }
    }
  }

  private static Map<String, Integer> getColumns() {
    Map<String, Integer> columns = new HashMap<>();
    columns.put("Summaries (Passed)", columns.size());
    columns.put("Summaries (Failed)", columns.size());
    columns.put("Coverages (Passed)", columns.size());
    columns.put("Coverages (Failed)", columns.size());
    columns.put("Forms (Passed)", columns.size());
    columns.put("Forms (Failed)", columns.size());
    return columns;
  }
}
