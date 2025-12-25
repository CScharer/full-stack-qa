package com.cjs.qa.microsoft.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class XlsReader {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(XlsReader.class));

  private static String filename =
      System.getProperty("user.dir")
          + Constants.DELIMETER_PATH
          + "TestData"
          + Constants.DELIMETER_PATH;
  private String path;
  private FileInputStream fileInputStream = null;
  private FileOutputStream fileOutputStream = null;
  private POIFSFileSystem poifsFileSystem = null;
  private XSSFWorkbook workbook = null;

  @SuppressWarnings("PMD.SingularField")
  private XSSFSheet sheet = null; // reused across multiple methods as temporary variable

  @SuppressWarnings("PMD.SingularField")
  private XSSFRow row = null; // reused across multiple methods as temporary variable

  @SuppressWarnings("PMD.SingularField")
  private XSSFCell cell = null; // reused across multiple methods as temporary variable

  public XlsReader(String path) {
    this.path = path;
    try {
      fileInputStream = new FileInputStream(path);
      workbook = new XSSFWorkbook(fileInputStream);
      sheet = workbook.getSheetAt(0);
      fileInputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public static String getFilename() {
    return filename;
  }

  public String getPath() {
    return path;
  }

  public FileInputStream getFileInputStream() {
    return fileInputStream;
  }

  public FileOutputStream getFileOutputStream() {
    return fileOutputStream;
  }

  public POIFSFileSystem getPoifsFileSystem() {
    return poifsFileSystem;
  }

  public int getRowCount(String sheetName) {
    final int index = workbook.getSheetIndex(sheetName);
    if (index == -1) {
      return 0;
    } else {
      sheet = workbook.getSheetAt(index);
      return sheet.getLastRowNum() + 1;
    }
  }

  public int getSheetCount() {
    boolean error = false;
    int index = 0;
    while (!error) {
      try {
        workbook.getSheetAt(index);
        index++;
      } catch (final Exception e) {
        // e.printStackTrace();
        error = true;
      }
    }
    return index;
  }

  public String getSheetName(int index) {
    try {
      final XSSFSheet sheet = workbook.getSheetAt(index);
      return sheet.getSheetName();
    } catch (final Exception e) {
      e.printStackTrace();
      return "Sheet at index [" + index + " does not exist in xls";
    }
  }

  public String getCellData(String sheetName, String columnName, int rowNumber) {
    try {
      if (rowNumber <= 0) {
        return "";
      }
      final int sheetIndex = workbook.getSheetIndex(sheetName);
      if (sheetIndex == -1) {
        return "";
      }
      sheet = workbook.getSheetAt(sheetIndex);
      row = sheet.getRow(0);
      int columnNumber = -1;
      for (int rowIndex = 0; rowIndex < row.getLastCellNum(); rowIndex++) {
        if (row.getCell(rowIndex).getStringCellValue().trim().equals(columnName.trim())) {
          columnNumber = rowIndex;
        }
      }
      if (columnNumber == -1) {
        LOG.warn("The [{}] Column could not be found on the [{}] sheet.", columnName, sheetName);
        return "";
      }
      sheet = workbook.getSheetAt(sheetIndex);
      row = sheet.getRow(rowNumber - 1);
      if (row == null) {
        return "";
      }
      cell = row.getCell(columnNumber);
      if (cell == null) {
        return "";
      }
      if (cell.getCellType() == CellType.STRING) {
        return cell.getStringCellValue();
      } else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
        String cellText = String.valueOf(cell.getNumericCellValue());
        if (DateUtil.isCellDateFormatted(cell)) {
          // format in form of M/D/YY
          final double d = cell.getNumericCellValue();
          final Calendar cal = Calendar.getInstance();
          cal.setTime(DateUtil.getJavaDate(d));
          cellText = String.valueOf(cal.get(Calendar.YEAR)).substring(2);
          cellText =
              cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + 1 + "/" + cellText;
        }
        return cellText;
      } else if (cell.getCellType() == CellType.BLANK) {
        return "";
      } else {
        return String.valueOf(cell.getBooleanCellValue());
      }
    } catch (final Exception e) {
      e.printStackTrace();
      return "row " + rowNumber + " or column " + columnName + " does not exist in xls";
    }
  }

  public String getCellData(String sheetName, int columnNumber, int rowNumber) {
    try {
      if (rowNumber <= 0) {
        return "";
      }
      final int index = workbook.getSheetIndex(sheetName);
      if (index == -1) {
        return "";
      }
      sheet = workbook.getSheetAt(index);
      row = sheet.getRow(rowNumber - 1);
      if (row == null) {
        return "";
      }
      cell = row.getCell(columnNumber);
      if (cell == null) {
        return "";
      }
      if (cell.getCellType() == CellType.STRING) {
        return cell.getStringCellValue();
      } else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
        /*
         * if (XSSFDateUtil.isCellDateFormatted(cell)) { //format in
         * form of M/D/YY double d = cell.getNumericCellValue();
         *
         * Calendar cal =Calendar.getInstance();
         * cal.setTime(XSSFDateUtil.getJavaDate(d)); cellText =
         * (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
         * cellText = cal.get(Calendar.MONTH)+1 + "/" +
         * cal.get(Calendar.DAY_OF_MONTH) + "/" + cellText;
         *
         * //LOG.debug("{}", cellText); }
         */
        return String.valueOf(cell.getNumericCellValue());
      } else if (cell.getCellType() == CellType.BLANK) {
        return "";
      } else {
        return String.valueOf(cell.getBooleanCellValue());
      }
    } catch (final Exception e) {
      e.printStackTrace();
      return "row " + rowNumber + " or column " + columnNumber + " does not exist in xls";
    }
  }

  public boolean setCellData(String sheetName, String columnName, int rowNumber, String data) {
    try {
      fileInputStream = new FileInputStream(path);
      workbook = new XSSFWorkbook(fileInputStream);
      if (rowNumber <= 0) {
        return false;
      }
      final int sheetIndex = workbook.getSheetIndex(sheetName);
      if (sheetIndex == -1) {
        return false;
      }
      sheet = workbook.getSheetAt(sheetIndex);
      row = sheet.getRow(0);
      int columnNumber = -1;
      for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
        if (row.getCell(columnIndex).getStringCellValue().trim().equals(columnName)) {
          columnNumber = columnIndex;
        }
      }
      if (columnNumber == -1) {
        return false;
      }
      sheet.autoSizeColumn(columnNumber);
      row = sheet.getRow(rowNumber - 1);
      if (row == null) {
        row = sheet.createRow(rowNumber - 1);
      }
      cell = row.getCell(columnNumber);
      if (cell == null) {
        cell = row.createCell(columnNumber);
      }
      final CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setWrapText(true);
      cell.setCellStyle(cellStyle);
      cell.setCellValue(data);
      fileOutputStream = new FileOutputStream(path);
      workbook.write(fileOutputStream);
      fileOutputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean setCellData(
      String sheetName, String columnName, int rowNumber, String data, String url) {
    try {
      fileInputStream = new FileInputStream(path);
      workbook = new XSSFWorkbook(fileInputStream);
      if (rowNumber <= 0) {
        return false;
      }
      final int sheetIndex = workbook.getSheetIndex(sheetName);
      if (sheetIndex == -1) {
        return false;
      }
      sheet = workbook.getSheetAt(sheetIndex);
      row = sheet.getRow(0);
      int columnNumber = -1;
      for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
        if (row.getCell(columnIndex).getStringCellValue().trim().equalsIgnoreCase(columnName)) {
          columnNumber = columnIndex;
        }
      }
      if (columnNumber == -1) {
        return false;
      }
      sheet.autoSizeColumn(columnNumber); // ashish
      row = sheet.getRow(rowNumber - 1);
      if (row == null) {
        row = sheet.createRow(rowNumber - 1);
      }
      cell = row.getCell(columnNumber);
      if (cell == null) {
        cell = row.createCell(columnNumber);
      }
      cell.setCellValue(data);
      final XSSFCreationHelper createHelper = workbook.getCreationHelper();
      final CellStyle cellStyle = workbook.createCellStyle();
      final XSSFFont xssfFont = workbook.createFont();
      xssfFont.setUnderline(XSSFFont.U_SINGLE);
      xssfFont.setColor(IndexedColors.BLUE.getIndex());
      cellStyle.setFont(xssfFont);
      // cellStyle.setWrapText(true);
      final XSSFHyperlink xssfHyperlink = createHelper.createHyperlink(HyperlinkType.URL);
      xssfHyperlink.setAddress(url);
      cell.setHyperlink(xssfHyperlink);
      cell.setCellStyle(cellStyle);
      fileOutputStream = new FileOutputStream(path);
      workbook.write(fileOutputStream);
      fileOutputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean addSheet(String sheetName) {
    try {
      workbook.createSheet(sheetName);
      try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
        workbook.write(fileOutputStream);
      }
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean removeSheet(String sheetName) {
    final int sheetIndex = workbook.getSheetIndex(sheetName);
    if (sheetIndex == -1) {
      return false;
    }
    try {
      workbook.removeSheetAt(sheetIndex);
      try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
        workbook.write(fileOutputStream);
      }
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean addColumn(String sheetName, String columnName) {
    try {
      fileInputStream = new FileInputStream(path);
      workbook = new XSSFWorkbook(fileInputStream);
      final int index = workbook.getSheetIndex(sheetName);
      if (index == -1) {
        return false;
      }
      final XSSFCellStyle xssfCellStyle = workbook.createCellStyle();
      xssfCellStyle.setFillForegroundColor(
          HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
      xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      sheet = workbook.getSheetAt(index);
      row = sheet.getRow(0);
      if (row == null) {
        row = sheet.createRow(0);
      }
      if (row.getLastCellNum() == -1) {
        cell = row.createCell(0);
      } else {
        cell = row.createCell(row.getLastCellNum());
      }
      cell.setCellValue(columnName);
      cell.setCellStyle(xssfCellStyle);
      fileOutputStream = new FileOutputStream(path);
      workbook.write(fileOutputStream);
      fileOutputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean removeColumn(String sheetName, int columnNumber) {
    try {
      if (!isSheetExist(sheetName)) {
        return false;
      }
      fileInputStream = new FileInputStream(path);
      workbook = new XSSFWorkbook(fileInputStream);
      sheet = workbook.getSheet(sheetName);
      final XSSFCellStyle xssfCellStyle = workbook.createCellStyle();
      xssfCellStyle.setFillForegroundColor(
          HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
      xssfCellStyle.setFillPattern(FillPatternType.NO_FILL);
      for (int columnIndex = 0; columnIndex < getRowCount(sheetName); columnIndex++) {
        row = sheet.getRow(columnIndex);
        if (row != null) {
          cell = row.getCell(columnNumber);
          if (cell != null) {
            cell.setCellStyle(xssfCellStyle);
            row.removeCell(cell);
          }
        }
      }
      fileOutputStream = new FileOutputStream(path);
      workbook.write(fileOutputStream);
      fileOutputStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean isSheetExist(String sheetName) {
    int sheetIndex = workbook.getSheetIndex(sheetName);
    if (sheetIndex == -1) {
      sheetIndex = workbook.getSheetIndex(sheetName.toUpperCase(Locale.ENGLISH));
      return sheetIndex != -1;
    } else {
      return true;
    }
  }

  public int getColumnCount(String sheetName) {
    if (!isSheetExist(sheetName)) {
      return -1;
    }
    sheet = workbook.getSheet(sheetName);
    row = sheet.getRow(0);
    if (row == null) {
      return -1;
    }
    return row.getLastCellNum();
  }

  public int getScenarioRow(String sheet, String scenarioFind) {
    for (int row = 2; row <= getRowCount(sheet); row++) {
      final String scenario = getCellData(sheet, "Scenario", row);
      if (scenarioFind.equalsIgnoreCase(scenario)) {
        return row;
      }
    }
    return 0;
  }

  public boolean addHyperLink(
      String sheetName,
      String screenShotColName,
      String testCaseName,
      int index,
      String url,
      String message) {
    url = url.replace(Constants.DELIMETER_PATH, "/");
    if (!isSheetExist(sheetName)) {
      return false;
    }
    sheet = workbook.getSheet(sheetName);
    for (int columnIndex = 2; columnIndex <= getRowCount(sheetName); columnIndex++) {
      if (getCellData(sheetName, 0, columnIndex).equalsIgnoreCase(testCaseName)) {
        setCellData(sheetName, screenShotColName, columnIndex + index, message, url);
        break;
      }
    }
    return true;
  }

  public int getCellRowNum(String sheetName, String columnName, String cellValue) {
    for (int columnIndex = 2; columnIndex <= getRowCount(sheetName); columnIndex++) {
      if (getCellData(sheetName, columnName, columnIndex).equalsIgnoreCase(cellValue)) {
        return columnIndex;
      }
    }
    return -1;
  }
}
