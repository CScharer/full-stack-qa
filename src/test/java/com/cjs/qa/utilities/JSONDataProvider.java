package com.cjs.qa.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.testng.annotations.DataProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * JSON Data Provider Utility
 *
 * <p>Provides TestNG DataProvider methods for reading test data from JSON files. Supports both
 * array-based and object-based JSON structures.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * // For array-based JSON:
 * @DataProvider(name = "searchData")
 * public Object[][] getSearchData() {
 *     return JSONDataProvider.readJSONArray(
 *         "test-data/search-queries.json",
 *         "queries"
 *     );
 * }
 *
 * // For object-based JSON:
 * @DataProvider(name = "userData")
 * public Object[][] getUserData() {
 *     return JSONDataProvider.readJSONObject(
 *         "test-data/users.json"
 *     );
 * }
 * }</pre>
 */
public final class JSONDataProvider {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(JSONDataProvider.class));

  private JSONDataProvider() {
    // Utility class - prevent instantiation
  }

  /**
   * Reads JSON array from file and returns as Object[][] for TestNG DataProvider.
   *
   * <p>Expected JSON format:
   *
   * <pre>{@code
   * {
   *   "queries": [
   *     {"term": "Selenium", "expected": true},
   *     {"term": "TestNG", "expected": true}
   *   ]
   * }
   * }</pre>
   *
   * @param filePath Path to JSON file (relative to resources or absolute path)
   * @param arrayKey Key name for the array in JSON (null for root array)
   * @return Object[][] array where each element is a test data set
   */
  public static Object[][] readJSONArray(String filePath, String arrayKey) {
    LOG.info(
        "Reading JSON array from: {} (key: {})", filePath, arrayKey != null ? arrayKey : "root");

    try (InputStream inputStream = getInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      JsonElement jsonElement = JsonParser.parseReader(reader);
      JsonArray jsonArray;

      if (arrayKey == null || arrayKey.trim().isEmpty()) {
        // Root is array
        if (!jsonElement.isJsonArray()) {
          throw new IllegalArgumentException("Root element must be an array in: " + filePath);
        }
        jsonArray = jsonElement.getAsJsonArray();
      } else {
        // Array is nested under key
        if (!jsonElement.isJsonObject()) {
          throw new IllegalArgumentException("Root element must be an object in: " + filePath);
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.has(arrayKey)) {
          throw new IllegalArgumentException("Key '" + arrayKey + "' not found in: " + filePath);
        }
        JsonElement arrayElement = jsonObject.get(arrayKey);
        if (!arrayElement.isJsonArray()) {
          throw new IllegalArgumentException(
              "Element '" + arrayKey + "' must be an array in: " + filePath);
        }
        jsonArray = arrayElement.getAsJsonArray();
      }

      List<Object[]> dataList = new ArrayList<>();

      for (JsonElement element : jsonArray) {
        if (element.isJsonObject()) {
          JsonObject obj = element.getAsJsonObject();
          List<Object> rowData = new ArrayList<>();
          // Extract values in order of keys
          for (String key : obj.keySet()) {
            rowData.add(extractValue(obj.get(key)));
          }
          dataList.add(rowData.toArray());
        } else if (element.isJsonArray()) {
          // Array of arrays
          JsonArray innerArray = element.getAsJsonArray();
          List<Object> rowData = new ArrayList<>();
          for (JsonElement innerElement : innerArray) {
            rowData.add(extractValue(innerElement));
          }
          dataList.add(rowData.toArray());
        } else {
          // Primitive value
          dataList.add(new Object[] {extractValue(element)});
        }
      }

      LOG.info("Successfully read {} items from JSON array", dataList.size());
      return dataList.toArray(new Object[0][]);
    } catch (Exception e) {
      LOG.error("Error reading JSON file: {}", filePath, e);
      throw new RuntimeException("Failed to read JSON file: " + filePath, e);
    }
  }

  /**
   * Reads JSON object from file and returns as Object[][] for TestNG DataProvider.
   *
   * <p>Expected JSON format:
   *
   * <pre>{@code
   * {
   *   "user1": {"email": "user1@test.com", "password": "pass1"},
   *   "user2": {"email": "user2@test.com", "password": "pass2"}
   * }
   * }</pre>
   *
   * @param filePath Path to JSON file
   * @return Object[][] array where each object is a test data set
   */
  public static Object[][] readJSONObject(String filePath) {
    LOG.info("Reading JSON object from: {}", filePath);

    try (InputStream inputStream = getInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      JsonElement jsonElement = JsonParser.parseReader(reader);
      if (!jsonElement.isJsonObject()) {
        throw new IllegalArgumentException("Root element must be an object in: " + filePath);
      }

      JsonObject jsonObject = jsonElement.getAsJsonObject();
      List<Object[]> dataList = new ArrayList<>();

      for (String key : jsonObject.keySet()) {
        JsonElement value = jsonObject.get(key);
        if (value.isJsonObject()) {
          JsonObject obj = value.getAsJsonObject();
          List<Object> rowData = new ArrayList<>();
          rowData.add(key); // Include the key as first element
          for (String objKey : obj.keySet()) {
            rowData.add(extractValue(obj.get(objKey)));
          }
          dataList.add(rowData.toArray());
        } else {
          dataList.add(new Object[] {key, extractValue(value)});
        }
      }

      LOG.info("Successfully read {} objects from JSON file", dataList.size());
      return dataList.toArray(new Object[0][]);
    } catch (Exception e) {
      LOG.error("Error reading JSON file: {}", filePath, e);
      throw new RuntimeException("Failed to read JSON file: " + filePath, e);
    }
  }

  /** Reads JSON array from test resources directory. */
  public static Object[][] readJSONArrayFromResources(String resourcePath, String arrayKey) {
    return readJSONArray(resourcePath, arrayKey);
  }

  /** Gets InputStream for file (from resources or file system). */
  private static InputStream getInputStream(String filePath) throws IOException {
    // Try as resource first
    InputStream resourceStream =
        JSONDataProvider.class.getClassLoader().getResourceAsStream(filePath);
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

  /** Extracts value from JSON element, converting to appropriate Java type. */
  private static Object extractValue(JsonElement element) {
    if (element == null || element.isJsonNull()) {
      return null;
    }

    if (element.isJsonPrimitive()) {
      var primitive = element.getAsJsonPrimitive();
      if (primitive.isString()) {
        return primitive.getAsString();
      } else if (primitive.isNumber()) {
        // Try to return as integer if possible, otherwise double
        double num = primitive.getAsDouble();
        if (num == (long) num) {
          return (long) num;
        }
        return num;
      } else if (primitive.isBoolean()) {
        return primitive.getAsBoolean();
      }
    }

    // For complex types, return as JSON string
    return element.toString();
  }

  /** TestNG DataProvider that reads from JSON file. */
  @DataProvider(name = "jsonData")
  public static Object[][] provide(String filePath, String arrayKey) {
    return readJSONArray(filePath, arrayKey);
  }
}
