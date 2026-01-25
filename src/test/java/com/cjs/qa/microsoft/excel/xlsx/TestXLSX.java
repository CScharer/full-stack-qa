package com.cjs.qa.microsoft.excel.xlsx;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class TestXLSX {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(TestXLSX.class));

  private static final String FILE_NAME = "existing" + IExtension.XLSX;

  @Test
  public void testXLSX() throws IOException, QAException {
    String sheetName = IExcel.SHEET_SUMMARY;
    if (FSOTests.fileExists(FILE_NAME)) {
      FSOTests.fileDelete(FILE_NAME);
    }
    LOG.debug("Creating sheet [[{}]", sheetName + "]");
    final XLSX excel = new XLSX(FILE_NAME, sheetName);
    excel.writeCell(sheetName, 0, 0, "Sheet Summary");
    for (int sheetIndex = 0; sheetIndex < 10; sheetIndex++) {
      Double sheetIndexDouble = Double.valueOf(sheetIndex) + 1;
      sheetName = "Sheet_" + JavaHelpers.formatNumber(sheetIndexDouble, "000");
      LOG.debug("Creating sheet [[{}]", sheetName + "]");
      excel.createSheet(sheetName);
      final String hyperlinkSheet = "'" + sheetName + "'!A1";
      excel.addLink(IExcel.SHEET_SUMMARY, 0, sheetIndex + 1, "DOCUMENT", sheetName, hyperlinkSheet);
      createFontsColumns(excel, sheetName);
      createFontsRows(excel, sheetName);
      createFontsCells(excel, sheetName);
      excel.save();
    }
    excel.setSheet(0);
    excel.writeCell(IExcel.SHEET_SUMMARY, 0, 0, excel.readCell(IExcel.SHEET_SUMMARY, 0, 0));
    excel.addComment(
        IExcel.SHEET_SUMMARY,
        0,
        0,
        "This sheet contains summary information and links to the associated data sheets.",
        false);
    excel.setFormatHeading(IExcel.SHEET_SUMMARY, 0, 0);
    excel.autoSizeColumns(IExcel.SHEET_SUMMARY);
    excel.close();
  }

  private void createFontsColumns(XLSX excel, String sheetName) throws QAException {
    for (int column = 0; column < IExcel.CELL_STYLE_LIST.size(); column++) {
      final XLSXCellStyles xlsCellStyles = XLSXCellStyles.getStylesList().get(column);
      final String format = xlsCellStyles.getStyleName();
      excel.writeCell(sheetName, column, 0, format);
      switch (format) {
        case IExcel.FORMAT_NAME_BOLD:
          excel.setFormatBold(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_HEADING:
          excel.setFormatHeading(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_HYPERLINK:
          excel.setFormatHyperlink(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_NORMAL:
          excel.setFormatNormal(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_PASS:
          excel.setFormatPass(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_FAIL:
          excel.setFormatFail(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_SECTION:
          excel.setFormatSection(sheetName, column, 0);
          break;
        case IExcel.FORMAT_NAME_STATUS:
          excel.setFormatStatus(sheetName, column, 0);
          break;
        default:
          break;
      }
    }
  }

  private void createFontsRows(XLSX excel, String sheetName) throws QAException {
    for (int row = 0; row < IExcel.CELL_STYLE_LIST.size(); row++) {
      final XLSXCellStyles xlsCellStyles = XLSXCellStyles.getStylesList().get(row);
      final String format = xlsCellStyles.getStyleName();
      excel.writeCell(sheetName, 0, row, format);
      switch (format) {
        case IExcel.FORMAT_NAME_BOLD:
          excel.setFormatBold(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_HEADING:
          excel.setFormatHeading(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_HYPERLINK:
          excel.setFormatHyperlink(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_NORMAL:
          excel.setFormatNormal(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_PASS:
          excel.setFormatPass(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_FAIL:
          excel.setFormatFail(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_SECTION:
          excel.setFormatSection(sheetName, 0, row);
          break;
        case IExcel.FORMAT_NAME_STATUS:
          excel.setFormatStatus(sheetName, 0, row);
          break;
        default:
          break;
      }
    }
  }

  private void createFontsCells(XLSX excel, String sheetName) throws QAException {
    final int styleListSize = IExcel.CELL_STYLE_LIST.size();
    // int maxColumns = IExcel.MAX_COLUMNS_XLS
    final int maxColumns = styleListSize;
    // int maxRows = IExcel.MAX_ROWS_XLS
    final int maxRows = styleListSize;
    for (int column = 0; column < maxColumns; column++) {
      final String columnLetter = Convert.fromNumberToLetterExcel(column + 1);
      for (int row = 0; row < maxRows; row++) {
        final int styleIndex = row % styleListSize;
        final XLSXCellStyles xlsxCellStyles = XLSXCellStyles.getStylesList().get(styleIndex);
        final String format = xlsxCellStyles.getStyleName();
        final String value = columnLetter + 1 + " (" + format + ")";
        excel.writeCell(sheetName, column + 1, row + 1, value);
        switch (format) {
          case IExcel.FORMAT_NAME_BOLD:
            excel.setFormatBold(sheetName, column + 1, row + 1);
            break;
          case IExcel.FORMAT_NAME_HEADING:
            excel.setFormatHeading(sheetName, column + 1, row + 1);
            break;
          case IExcel.FORMAT_NAME_HYPERLINK:
            final String hyperlink =
                "'" + IExcel.SHEET_SUMMARY + "'!A" + (excel.getSheetCount() - 1);
            excel.addLink(sheetName, column + 1, row + 1, "DOCUMENT", value, hyperlink);
            break;
          case IExcel.FORMAT_NAME_NORMAL:
            excel.setFormatNormal(sheetName, column + 1, row + 1);
            break;
          case IExcel.FORMAT_NAME_PASS:
            excel.setFormatPass(sheetName, column + 1, row + 1);
            break;
          case IExcel.FORMAT_NAME_FAIL:
            excel.setFormatFail(sheetName, column + 1, row + 1);
            break;
          case IExcel.FORMAT_NAME_SECTION:
            excel.setFormatSection(sheetName, column + 1, row + 1);
            break;
          case IExcel.FORMAT_NAME_STATUS:
            excel.setFormatStatus(sheetName, column + 1, row + 1);
            break;
          default:
            break;
        }
      }
    }
    excel.autoSizeColumns(sheetName);
  }
}
