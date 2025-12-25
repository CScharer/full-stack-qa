package com.cjs.qa.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.annotations.DataProvider;

/**
 * Excel Data Provider Utility
 *
 * <p>Provides TestNG DataProvider methods for reading test data from Excel files (.xlsx, .xls).
 * Supports multiple sheets and flexible data reading.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * @DataProvider(name = "loginData")
 * public Object[][] getLoginData() {
 *     return ExcelDataProvider.readExcel(
 *         "test-data/login-scenarios.xlsx",
 *         "Sheet1"
 *     );
 * }
 *
 * @Test(dataProvider = "loginData")
 * public void testLogin(String email, String password, String expectedResult) {
 *     // Test implementation
 * }
 * }</pre>
 */
public final class ExcelDataProvider {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ExcelDataProvider.class));

  private ExcelDataProvider() {
    // Utility class - prevent instantiation
  }

  /**
   * Reads Excel file and returns data as Object[][] for TestNG DataProvider.
   *
   * @param filePath Path to Excel file (relative to project root or absolute path)
   * @param sheetName Name of the sheet to read (null or empty for first sheet)
   * @return Object[][] array where each row is a test data set
   */
  public static Object[][] readExcel(String filePath, String sheetName) {
    return readExcel(filePath, sheetName, true);
  }

  /**
   * Reads Excel file and returns data as Object[][] for TestNG DataProvider.
   *
   * @param filePath Path to Excel file (relative to project root or absolute path)
   * @param sheetName Name of the sheet to read (null or empty for first sheet)
   * @param skipHeaderRow If true, skips the first row (header row)
   * @return Object[][] array where each row is a test data set
   */
  public static Object[][] readExcel(String filePath, String sheetName, boolean skipHeaderRow) {
    LOG.info(
        "Reading Excel file: {} (Sheet: {})", filePath, sheetName != null ? sheetName : "first");

    List<Object[]> dataList = new ArrayList<>();

    try (InputStream inputStream = getInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(inputStream)) {
      Sheet sheet;
      if (sheetName == null || sheetName.trim().isEmpty()) {
        sheet = workbook.getSheetAt(0);
        LOG.debug("Using first sheet: {}", sheet.getSheetName());
      } else {
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
          throw new IllegalArgumentException(
              "Sheet '" + sheetName + "' not found in file: " + filePath);
        }
      }

      int startRow = skipHeaderRow ? 1 : 0;
      int lastRowNum = sheet.getLastRowNum();

      LOG.debug(
          "Reading rows {} to {} from sheet '{}'", startRow, lastRowNum, sheet.getSheetName());

      for (int rowIndex = startRow; rowIndex <= lastRowNum; rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
          LOG.debug("Skipping empty row: {}", rowIndex);
          continue;
        }

        List<Object> rowData = new ArrayList<>();
        int lastCellNum = row.getLastCellNum();

        for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
          Cell cell = row.getCell(cellIndex);
          Object cellValue = getCellValue(cell);
          rowData.add(cellValue);
        }

        if (!rowData.isEmpty()) {
          dataList.add(rowData.toArray());
          LOG.debug("Read row {}: {} columns", rowIndex, rowData.size());
        }
      }

      LOG.info("Successfully read {} rows from Excel file", dataList.size());
    } catch (Exception e) {
      LOG.error("Error reading Excel file: {}", filePath, e);
      throw new RuntimeException("Failed to read Excel file: " + filePath, e);
    }

    return dataList.toArray(new Object[0][]);
  }

  /**
   * Reads Excel file from test resources directory.
   *
   * @param resourcePath Path relative to src/test/resources
   * @param sheetName Name of the sheet to read
   * @return Object[][] array where each row is a test data set
   */
  public static Object[][] readExcelFromResources(String resourcePath, String sheetName) {
    String fullPath = "src/test/resources/" + resourcePath;
    return readExcel(fullPath, sheetName);
  }

  /** Gets InputStream for file (from resources or file system). */
  private static InputStream getInputStream(String filePath) throws IOException {
    // Try as resource first
    InputStream resourceStream =
        ExcelDataProvider.class.getClassLoader().getResourceAsStream(filePath);
    if (resourceStream != null) {
      LOG.debug("Found file in resources: {}", filePath);
      return resourceStream;
    }

    // Try as file system path
    java.io.File file = new java.io.File(filePath);
    if (file.exists()) {
      LOG.debug("Found file in file system: {}", filePath);
      return new FileInputStream(file);
    }

    // Try relative to project root
    String projectRoot = System.getProperty("user.dir");
    java.io.File projectFile = new java.io.File(projectRoot, filePath);
    if (projectFile.exists()) {
      LOG.debug("Found file relative to project root: {}", projectFile.getAbsolutePath());
      return new FileInputStream(projectFile);
    }

    throw new IOException("File not found: " + filePath);
  }

  /** Extracts value from Excel cell, handling different cell types. */
  private static Object getCellValue(Cell cell) {
    if (cell == null) {
      return "";
    }

    CellType cellType = cell.getCellType();
    if (cellType == CellType.FORMULA) {
      cellType = cell.getCachedFormulaResultType();
    }

    // Java 17: Switch expression (more concise and expression-based)
    return switch (cellType) {
      case STRING -> cell.getStringCellValue().trim();
      case NUMERIC -> {
        // Check if it's a date
        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
          yield cell.getDateCellValue();
        }
        // Check if it's an integer
        double numericValue = cell.getNumericCellValue();
        if (numericValue == (long) numericValue) {
          yield (long) numericValue;
        }
        yield numericValue;
      }
      case BOOLEAN -> cell.getBooleanCellValue();
      case BLANK -> "";
      default -> cell.toString().trim();
    };
  }

  /**
   * TestNG DataProvider that reads from Excel file.
   *
   * <p>Example usage:
   *
   * <pre>{@code
   * @DataProvider(name = "userData")
   * public Object[][] getUserData() {
   *     return ExcelDataProvider.provide("test-data/users.xlsx", "Users");
   * }
   * }</pre>
   */
  @DataProvider(name = "excelData")
  public static Object[][] provide(String filePath, String sheetName) {
    return readExcel(filePath, sheetName);
  }
}
