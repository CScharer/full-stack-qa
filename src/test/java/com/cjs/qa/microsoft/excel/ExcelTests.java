package com.cjs.qa.microsoft.excel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.microsoft.excel.xlsx.XLSX;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class ExcelTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ExcelTests.class));

  private static final String SHEET_MISSING = "MISSING";

  @Test
  public void mainTest() throws IOException, QAException {
    final String filePath = Constants.PATH_DESKTOP + "Test" + Constants.DELIMETER_PATH;
    FileUtils.forceMkdir(new File(filePath));
    final List<String> fileTypeList = Arrays.asList(IExtension.XLS, IExtension.XLSX);
    for (final String fileType : fileTypeList) {
      final String filePathName = filePath + "ExcelTest" + fileType;
      FSOTests.fileDelete(filePathName);
      LOG.debug("filePathName:[[{}]", filePathName + "]");
      switch (IExcel.getFileType(filePathName)) {
        case IExtension.XLSX:
          final XLSX xlsx = new XLSX(filePathName, null);
          testExcelType(xlsx);
          break;
        case IExtension.XLS:
        default:
          final XLS xls = new XLS(filePathName, null);
          testExcelType(xls);
          break;
      }
    }
  }

  private static void testExcelType(XLSX excel) throws QAException, IOException {
    excel.getFileName();
    excel.writeCell(IExcel.SHEET_DEFAULT, 0, 0, "Testing");
    excel.createSheet("String");
    excel.createHeadings("String", "String 1;String 2;String 3");
    LOG.debug("{}", excel.readCell("String", 0, 0));
    LOG.debug("{}", excel.readCell("String", 1, 0));
    LOG.debug("{}", excel.readCell("String", 2, 0));
    excel.setFormatBold("String", 0, 0);
    excel.setFormatBold("String", 1, 0);
    excel.setFormatBold("String", 2, 0);
    excel.createSheet("List");
    excel.createHeadings("List", Arrays.asList("List 1", "List 2", "List 3"));
    LOG.debug("{}", excel.readCell("List", 0, 0));
    LOG.debug("{}", excel.readCell("List", 1, 0));
    LOG.debug("{}", excel.readCell("List", 2, 0));
    excel.setFormatBold("List", 0, 0);
    excel.setFormatBold("List", 1, 0);
    excel.setFormatBold("List", 2, 0);
    LOG.debug("No Sheet Value:[{}]", excel.readCell("None", "List 4", 0));
    LOG.debug("No Sheet Value:[{}]", excel.readCell(9, "List 4", 0));
    LOG.debug("No Sheet Value:[{}]", excel.readCell(1, 9, 0));
    LOG.debug("No Row Value:[{}]", excel.readCell("List", "List 1", 9));
    LOG.debug("No Column Value:[{}]", excel.readCell("List", "List 4", 0));
    LOG.debug("No Column Value:[{}]", excel.readCell("List", 9, 0));
    excel.writeCell("New Sheet", 0, 0, "New Sheet 1");
    LOG.debug("Sheet Count:[{}]", excel.getSheetCount());
    LOG.debug(
        "Sheet Exists({}):[{}]", IExcel.SHEET_DEFAULT, excel.sheetExists(IExcel.SHEET_DEFAULT));
    LOG.debug("Sheet Exists(1):[{}]", excel.sheetExists(1));
    LOG.debug("Sheet Exists({}):[{}]", SHEET_MISSING, excel.sheetExists(SHEET_MISSING));
    LOG.debug("Sheet Exists(5):[{}]", excel.sheetExists(5));
    //
    LOG.debug(
        "Row Exists({}, 0):[{}]", IExcel.SHEET_DEFAULT, excel.rowExists(IExcel.SHEET_DEFAULT, 0));
    LOG.debug("Row Exists(0, 0):[{}]", excel.rowExists(0, 0));
    LOG.debug(
        "Row Exists({}, 1):[{}]", IExcel.SHEET_DEFAULT, excel.rowExists(IExcel.SHEET_DEFAULT, 1));
    LOG.debug("Row Exists(0, 1):[{}]", excel.rowExists(0, 1));
    //
    LOG.debug(
        "Column Exists({}, New Sheet 1, 0):[{}]",
        IExcel.SHEET_DEFAULT,
        excel.columnExists(IExcel.SHEET_DEFAULT, "New Sheet 1", 0));
    LOG.debug(
        "Column Exists({}, 0, 0):[{}]",
        IExcel.SHEET_DEFAULT,
        excel.columnExists(IExcel.SHEET_DEFAULT, 0, 0));
    LOG.debug("Column Exists(0, Testing, 0):[{}]", excel.columnExists(0, "Testing", 0));
    LOG.debug("Column Exists(0, 0, 0):[{}]", excel.columnExists(0, 0, 0));
    //
    LOG.debug(
        "Column Exists({}, New Sheet 1, 0):[{}]",
        SHEET_MISSING,
        excel.columnExists(SHEET_MISSING, "New Sheet 1", 0));
    LOG.debug(
        "Column Exists({}, 1, 0):[{}]",
        IExcel.SHEET_DEFAULT,
        excel.columnExists(IExcel.SHEET_DEFAULT, 1, 0));
    LOG.debug("Column Exists(0, New Sheet 1, 1):[{}]", excel.columnExists(0, "New Sheet 1", 1));
    LOG.debug("Column Exists(0, 1, 0):[{}]", excel.columnExists(0, 1, 0));
    LOG.debug("Column Exists(0, 0, 1):[{}]", excel.columnExists(0, 0, 1));
    //
    LOG.debug("{}", excel.readCell(IExcel.SHEET_DEFAULT, 0, 0));
    LOG.debug("renameSheet(0, NewSheet): {}", excel.renameSheet(0, "NewSheet"));
    LOG.debug(
        "renameSheet(NewSheet, {}): {}",
        IExcel.SHEET_DEFAULT,
        excel.renameSheet("NewSheet", IExcel.SHEET_DEFAULT));
    // LOG.debug("renameSheet(9, NewSheet): {}", excel.renameSheet(9, "NewSheet"));
    LOG.debug("renameSheet(1, NoNewSheet): {}", excel.renameSheet(1, "NoNewSheet"));
    LOG.debug("renameSheet(NoNewSheet, NoSheet): {}", excel.renameSheet("NoNewSheet", "NoSheet"));
    LOG.debug("createSheet(NoSheet): {}", excel.createSheet("NoSheet"));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("deleteSheet({}): {}", SHEET_MISSING, excel.deleteSheet(SHEET_MISSING));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("deleteSheet(1): {}", excel.deleteSheet(1));
    // LOG.debug("deleteSheet({}): {}", 0));
    // LOG.debug("deleteSheet({}): {}", 0));
    // LOG.debug("deleteSheet({}): {}", 9));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("deleteSheet({}): {}", SHEET_MISSING, excel.deleteSheet(SHEET_MISSING));
    // LOG.debug("deleteSheet({}): {}", SHEET_MISSING, excel.deleteSheet(SHEET_MISSING));
    // LOG.debug("renameSheet(NoNewSheet, {}): {}", SHEET_MISSING, excel.renameSheet("NoNewSheet",
    // SHEET_MISSING));
    excel.setFormatBold("New Sheet", 0, 0);
    excel.writeCell("New Sheet", 1, 0, "1");
    excel.writeCell("New Sheet", 2, 0, "Column 3");
    excel.writeCell("New Sheet", 3, 0, "3");
    excel.writeCell("New Sheet", 4, 0, "Column 5");
    excel.writeCell("New Sheet", 5, 0, "5");
    // LOG.debug("File:[{}]", xls.getFileName());
    // xls.close();
    // LOG.debug(xls.readCell(xls.DEFAULT_SHEET, 0, 0));
    // FSOTests.fileDelete(fileName);
    // xls.convertToCSV(xls.getFileName() + IExtension.CSV);
    excel.autoSizeColumns("New Sheet");
    excel.save();
    excel.close();
  }

  private static void testExcelType(XLS excel) throws QAException, IOException {
    excel.getFileName();
    excel.writeCell(IExcel.SHEET_DEFAULT, 0, 0, "Testing");
    excel.createSheet("String");
    excel.createHeadings("String", "String 1;String 2;String 3");
    LOG.debug("{}", excel.readCell("String", 0, 0));
    LOG.debug("{}", excel.readCell("String", 1, 0));
    LOG.debug("{}", excel.readCell("String", 2, 0));
    excel.setFormatBold("String", 0, 0);
    excel.setFormatBold("String", 1, 0);
    excel.setFormatBold("String", 2, 0);
    excel.createSheet("List");
    excel.createHeadings("List", Arrays.asList("List 1", "List 2", "List 3"));
    LOG.debug("{}", excel.readCell("List", 0, 0));
    LOG.debug("{}", excel.readCell("List", 1, 0));
    LOG.debug("{}", excel.readCell("List", 2, 0));
    excel.setFormatBold("List", 0, 0);
    excel.setFormatBold("List", 1, 0);
    excel.setFormatBold("List", 2, 0);
    LOG.debug("No Sheet Value:[{}]", excel.readCell("None", "List 4", 0));
    LOG.debug("No Sheet Value:[{}]", excel.readCell(9, "List 4", 0));
    LOG.debug("No Sheet Value:[{}]", excel.readCell(1, 9, 0));
    LOG.debug("No Row Value:[{}]", excel.readCell("List", "List 1", 9));
    LOG.debug("No Column Value:[{}]", excel.readCell("List", "List 4", 0));
    LOG.debug("No Column Value:[{}]", excel.readCell("List", 9, 0));
    excel.writeCell("New Sheet", 0, 0, "New Sheet 1");
    LOG.debug("Sheet Count:[{}]", excel.getSheetCount());
    LOG.debug(
        "Sheet Exists({}):[{}]", IExcel.SHEET_DEFAULT, excel.sheetExists(IExcel.SHEET_DEFAULT));
    LOG.debug("Sheet Exists(1):[{}]", excel.sheetExists(1));
    LOG.debug("Sheet Exists({}):[{}]", SHEET_MISSING, excel.sheetExists(SHEET_MISSING));
    LOG.debug("Sheet Exists(5):[{}]", excel.sheetExists(5));
    //
    LOG.debug(
        "Row Exists({}, 0):[{}]", IExcel.SHEET_DEFAULT, excel.rowExists(IExcel.SHEET_DEFAULT, 0));
    LOG.debug("Row Exists(0, 0):[{}]", excel.rowExists(0, 0));
    LOG.debug(
        "Row Exists({}, 1):[{}]", IExcel.SHEET_DEFAULT, excel.rowExists(IExcel.SHEET_DEFAULT, 1));
    LOG.debug("Row Exists(0, 1):[{}]", excel.rowExists(0, 1));
    //
    LOG.debug(
        "Column Exists({}, New Sheet 1, 0):[{}]",
        IExcel.SHEET_DEFAULT,
        excel.columnExists(IExcel.SHEET_DEFAULT, "New Sheet 1", 0));
    LOG.debug(
        "Column Exists({}, 0, 0):[{}]",
        IExcel.SHEET_DEFAULT,
        excel.columnExists(IExcel.SHEET_DEFAULT, 0, 0));
    LOG.debug("Column Exists(0, Testing, 0):[{}]", excel.columnExists(0, "Testing", 0));
    LOG.debug("Column Exists(0, 0, 0):[{}]", excel.columnExists(0, 0, 0));
    //
    LOG.debug(
        "Column Exists({}, New Sheet 1, 0):[{}]",
        SHEET_MISSING,
        excel.columnExists(SHEET_MISSING, "New Sheet 1", 0));
    LOG.debug(
        "Column Exists({}, 1, 0):[{}]",
        IExcel.SHEET_DEFAULT,
        excel.columnExists(IExcel.SHEET_DEFAULT, 1, 0));
    LOG.debug("Column Exists(0, New Sheet 1, 1):[{}]", excel.columnExists(0, "New Sheet 1", 1));
    LOG.debug("Column Exists(0, 1, 0):[{}]", excel.columnExists(0, 1, 0));
    LOG.debug("Column Exists(0, 0, 1):[{}]", excel.columnExists(0, 0, 1));
    //
    LOG.debug("{}", excel.readCell(IExcel.SHEET_DEFAULT, 0, 0));
    LOG.debug("renameSheet(0, NewSheet): {}", excel.renameSheet(0, "NewSheet"));
    LOG.debug(
        "renameSheet(NewSheet, {}): {}",
        IExcel.SHEET_DEFAULT,
        excel.renameSheet("NewSheet", IExcel.SHEET_DEFAULT));
    // LOG.debug("renameSheet(9, NewSheet): {}", excel.renameSheet(9, "NewSheet"));
    LOG.debug("renameSheet(1, NoNewSheet): {}", excel.renameSheet(1, "NoNewSheet"));
    LOG.debug("renameSheet(NoNewSheet, NoSheet): {}", excel.renameSheet("NoNewSheet", "NoSheet"));
    LOG.debug("createSheet(NoSheet): {}", excel.createSheet("NoSheet"));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("deleteSheet({}): {}", SHEET_MISSING, excel.deleteSheet(SHEET_MISSING));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("deleteSheet(1): {}", excel.deleteSheet(1));
    // LOG.debug("deleteSheet({}): {}", 0));
    // LOG.debug("deleteSheet({}): {}", 0));
    // LOG.debug("deleteSheet({}): {}", 9));
    LOG.debug("createSheet({}): {}", SHEET_MISSING, excel.createSheet(SHEET_MISSING));
    LOG.debug("deleteSheet({}): {}", SHEET_MISSING, excel.deleteSheet(SHEET_MISSING));
    // LOG.debug("deleteSheet({}): {}", SHEET_MISSING, excel.deleteSheet(SHEET_MISSING));
    // LOG.debug("renameSheet(NoNewSheet, {}): {}", SHEET_MISSING, excel.renameSheet("NoNewSheet",
    // SHEET_MISSING));
    excel.setFormatBold("New Sheet", 0, 0);
    excel.writeCell("New Sheet", 1, 0, "1");
    excel.writeCell("New Sheet", 2, 0, "Column 3");
    excel.writeCell("New Sheet", 3, 0, "3");
    excel.writeCell("New Sheet", 4, 0, "Column 5");
    excel.writeCell("New Sheet", 5, 0, "5");
    // LOG.debug("File:[{}]", xls.getFileName());
    // xls.close();
    // LOG.debug(xls.readCell(xls.DEFAULT_SHEET, 0, 0));
    // FSOTests.fileDelete(fileName);
    // xls.convertToCSV(xls.getFileName() + IExtension.CSV);
    excel.autoSizeColumns("New Sheet");
    excel.save();
    excel.close();
  }
}
