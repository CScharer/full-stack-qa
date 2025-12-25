package com.cjs.qa.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.DataProvider;

/**
 * CSV Data Provider Utility
 *
 * <p>Provides TestNG DataProvider methods for reading test data from CSV files. Supports header row
 * detection and flexible parsing.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * @DataProvider(name = "userData")
 * public Object[][] getUserData() {
 *     return CSVDataProvider.readCSV(
 *         "test-data/users.csv",
 *         true  // skip header row
 *     );
 * }
 *
 * @Test(dataProvider = "userData")
 * public void testLogin(String email, String password, String expectedResult) {
 *     // Test implementation
 * }
 * }</pre>
 */
public final class CSVDataProvider {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(CSVDataProvider.class));

  private CSVDataProvider() {
    // Utility class - prevent instantiation
  }

  /**
   * Reads CSV file and returns data as Object[][] for TestNG DataProvider.
   *
   * @param filePath Path to CSV file (relative to resources or absolute path)
   * @param skipHeaderRow If true, skips the first row (header row)
   * @return Object[][] array where each row is a test data set
   */
  public static Object[][] readCSV(String filePath, boolean skipHeaderRow) {
    LOG.info("Reading CSV file: {} (skipHeader: {})", filePath, skipHeaderRow);

    List<Object[]> dataList = new ArrayList<>();

    // Note: .build() is deprecated in Commons CSV 1.14.1 but still required
    // Using CSVParser.parse() is the recommended approach
    @SuppressWarnings("deprecation")
    CSVFormat formatWithHeader =
        CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();

    try (InputStream inputStream = getInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        CSVParser csvParser =
            skipHeaderRow
                ? CSVParser.parse(reader, formatWithHeader)
                : CSVParser.parse(reader, CSVFormat.DEFAULT)) {
      for (CSVRecord record : csvParser) {
        List<Object> rowData = new ArrayList<>();
        for (int i = 0; i < record.size(); i++) {
          String value = record.get(i);
          rowData.add(value != null ? value.trim() : "");
        }
        if (!rowData.isEmpty()) {
          dataList.add(rowData.toArray());
        }
      }

      LOG.info("Successfully read {} rows from CSV file", dataList.size());
    } catch (Exception e) {
      LOG.error("Error reading CSV file: {}", filePath, e);
      throw new RuntimeException("Failed to read CSV file: " + filePath, e);
    }

    return dataList.toArray(new Object[0][]);
  }

  /**
   * Reads CSV file with header row (header row is skipped automatically).
   *
   * @param filePath Path to CSV file
   * @return Object[][] array where each row is a test data set
   */
  public static Object[][] readCSV(String filePath) {
    return readCSV(filePath, true);
  }

  /**
   * Reads CSV file from test resources directory.
   *
   * @param resourcePath Path relative to src/test/resources
   * @param skipHeaderRow If true, skips the first row
   * @return Object[][] array where each row is a test data set
   */
  public static Object[][] readCSVFromResources(String resourcePath, boolean skipHeaderRow) {
    return readCSV(resourcePath, skipHeaderRow);
  }

  /** Gets InputStream for file (from resources or file system). */
  private static InputStream getInputStream(String filePath) throws IOException {
    // Try as resource first
    InputStream resourceStream =
        CSVDataProvider.class.getClassLoader().getResourceAsStream(filePath);
    if (resourceStream != null) {
      LOG.debug("Found file in resources: {}", filePath);
      return resourceStream;
    }

    // Try as file system path
    java.io.File file = new java.io.File(filePath);
    if (file.exists()) {
      LOG.debug("Found file in file system: {}", filePath);
      return new java.io.FileInputStream(file);
    }

    // Try relative to project root
    String projectRoot = System.getProperty("user.dir");
    java.io.File projectFile = new java.io.File(projectRoot, filePath);
    if (projectFile.exists()) {
      LOG.debug("Found file relative to project root: {}", projectFile.getAbsolutePath());
      return new java.io.FileInputStream(projectFile);
    }

    throw new IOException("File not found: " + filePath);
  }

  /** TestNG DataProvider that reads from CSV file. */
  @DataProvider(name = "csvData")
  public static Object[][] provide(String filePath, boolean skipHeaderRow) {
    return readCSV(filePath, skipHeaderRow);
  }
}
