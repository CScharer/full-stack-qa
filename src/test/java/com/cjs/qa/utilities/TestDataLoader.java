package com.cjs.qa.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Test Data Loader for Java/Selenide tests
 *
 * <p>Utility class to load test data from the centralized test-data directory
 */
public final class TestDataLoader {

  private TestDataLoader() {
    // Utility class - prevent instantiation
  }

  private static final String TEST_DATA_ROOT = "test-data";
  private static final Gson GSON = new Gson();

  /**
   * Load test data from JSON file
   *
   * @param filePath - Relative path from test-data directory (e.g., "demoqa/practice-form.json")
   * @return JsonObject containing the test data
   * @throws IOException if file cannot be read
   */
  public static JsonObject loadTestData(String filePath) throws IOException {
    Path projectRoot = getProjectRoot();
    Path fullPath = projectRoot.resolve(TEST_DATA_ROOT).resolve(filePath);

    if (!Files.exists(fullPath)) {
      throw new IOException("Test data file not found: " + fullPath.toAbsolutePath());
    }

    String content = new String(Files.readAllBytes(fullPath));
    return JsonParser.parseString(content).getAsJsonObject();
  }

  /**
   * Load test data and convert to a specific class
   *
   * @param filePath - Relative path from test-data directory
   * @param clazz - Class to deserialize into
   * @return Instance of the specified class
   * @throws IOException if file cannot be read
   */
  public static <T> T loadTestData(String filePath, Class<T> clazz) throws IOException {
    Path projectRoot = getProjectRoot();
    Path fullPath = projectRoot.resolve(TEST_DATA_ROOT).resolve(filePath);

    if (!Files.exists(fullPath)) {
      throw new IOException("Test data file not found: " + fullPath.toAbsolutePath());
    }

    String content = new String(Files.readAllBytes(fullPath));
    return GSON.fromJson(content, clazz);
  }

  /** Get project root directory Assumes we're running from project root or target/test-classes */
  private static Path getProjectRoot() {
    // Try to find project root by looking for test-data directory
    Path currentPath = Paths.get("").toAbsolutePath();

    // Check if we're already at project root
    if (Files.exists(currentPath.resolve(TEST_DATA_ROOT))) {
      return currentPath;
    }

    // If running from target/test-classes, go up to project root
    if (currentPath.toString().contains("target")) {
      Path projectRoot = currentPath;
      while (projectRoot != null && !Files.exists(projectRoot.resolve(TEST_DATA_ROOT))) {
        Path parent = projectRoot.getParent();
        if (parent == null || parent.equals(projectRoot)) {
          break;
        }
        projectRoot = parent;
      }
      if (Files.exists(projectRoot.resolve(TEST_DATA_ROOT))) {
        return projectRoot;
      }
    }

    // Default: assume we're at project root
    return currentPath;
  }
}
