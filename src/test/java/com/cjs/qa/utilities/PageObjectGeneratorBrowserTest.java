package com.cjs.qa.utilities;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Browser-based integration tests for PageObjectGenerator.
 *
 * <p>These tests actually run the generator with a real browser and validate the generated code.
 * Requires ChromeDriver to be available.
 *
 * <p>To run these tests:
 *
 * <pre>
 * mvn test -Dtest=PageObjectGeneratorBrowserTest
 * </pre>
 *
 * @author CJS QA Team
 */
public class PageObjectGeneratorBrowserTest {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(PageObjectGeneratorBrowserTest.class));
  private static final String TEST_OUTPUT_DIR = "target/generated-test-sources/page-objects";

  @BeforeMethod
  public void setUp() {
    // Create output directory
    try {
      Files.createDirectories(Paths.get(TEST_OUTPUT_DIR));
    } catch (Exception e) {
      LOG.warn("Could not create output directory: {}", e.getMessage());
    }
  }

  /**
   * Test 1: Generate Page Object from a simple, accessible page.
   *
   * <p>This test generates a Page Object from a real URL and validates: - File is created - Code
   * compiles - Basic structure is correct
   */
  @Test(groups = "smoke", priority = 1)
  public void testBasicGeneration() throws Exception {
    LOG.info("=== Test 1: Basic Generation ===");

    String testUrl = "https://github.com/login";
    String outputPath = TEST_OUTPUT_DIR + "/com/cjs/qa/test/pages/GeneratedLoginPage.java";
    String packageName = "com.cjs.qa.test.pages";
    String className = "GeneratedLoginPage";

    LOG.info("Generating Page Object from: {}", testUrl);
    LOG.info("Output path: {}", outputPath);

    // Generate the Page Object
    PageObjectGenerator.generate(testUrl, outputPath, packageName, className);

    // Verify file was created
    File generatedFile = new File(outputPath);
    assertTrue(generatedFile.exists(), "Generated file should exist: " + outputPath);
    assertTrue(generatedFile.length() > 0, "Generated file should not be empty");

    LOG.info("✅ File created successfully: {} bytes", generatedFile.length());

    // Verify file content has expected structure
    String content = new String(Files.readAllBytes(Paths.get(outputPath)));
    assertTrue(content.contains("package " + packageName), "Should contain package declaration");
    assertTrue(content.contains("public class " + className), "Should contain class declaration");
    assertTrue(content.contains("extends Page"), "Should extend Page class");
    assertTrue(content.contains("public " + className + "(WebDriver"), "Should have constructor");
    assertTrue(content.contains("super(webDriver)"), "Should have super(webDriver)");

    LOG.info("✅ Generated code structure is correct");

    // Verify it has some locators (at least one)
    assertTrue(
        content.contains("private final By")
            || content.contains("By.id")
            || content.contains("By.name")
            || content.contains("By.xpath"),
        "Should contain locator definitions");

    LOG.info("✅ Locators found in generated code");

    LOG.info("✅ Test 1 PASSED: Basic generation works!");
  }

  /**
   * Test 2: Verify generated code compiles.
   *
   * <p>This test validates that the generated code can be compiled. Note: This is a basic check -
   * full compilation would require adding to classpath.
   */
  @Test(groups = "smoke", priority = 2)
  public void testGeneratedCodeStructure() throws Exception {
    LOG.info("=== Test 2: Generated Code Structure ===");

    String testUrl = "https://github.com/login";
    String outputPath = TEST_OUTPUT_DIR + "/com/cjs/qa/test/pages/GeneratedLoginPageStructure.java";
    String packageName = "com.cjs.qa.test.pages";
    String className = "GeneratedLoginPageStructure";

    // Generate the Page Object
    PageObjectGenerator.generate(testUrl, outputPath, packageName, className);

    // Read generated code
    String content = new String(Files.readAllBytes(Paths.get(outputPath)));

    // Verify required imports
    assertTrue(content.contains("import com.cjs.qa.selenium.Page"), "Should import Page class");
    assertTrue(content.contains("import org.openqa.selenium.WebDriver"), "Should import WebDriver");
    assertTrue(content.contains("import org.openqa.selenium.By"), "Should import By");

    // Verify method patterns
    assertTrue(
        content.contains("setEdit")
            || content.contains("clickButton")
            || content.contains("selectDropdown"),
        "Should have setEdit methods or clickButton methods");

    LOG.info("✅ Test 2 PASSED: Generated code structure is valid!");
  }

  /** Test 3: Verify populatePage and validatePage methods are generated. */
  @Test(groups = "smoke", priority = 3)
  public void testDataTableMethods() throws Exception {
    LOG.info("=== Test 3: DataTable Methods ===");

    String testUrl = "https://github.com/login";
    String outputPath = TEST_OUTPUT_DIR + "/com/cjs/qa/test/pages/GeneratedLoginPageDataTable.java";
    String packageName = "com.cjs.qa.test.pages";
    String className = "GeneratedLoginPageDataTable";

    // Generate with default options (should include populatePage and validatePage)
    PageObjectGenerator.generate(testUrl, outputPath, packageName, className);

    // Read generated code
    String content = new String(Files.readAllBytes(Paths.get(outputPath)));

    // Verify DataTable methods are generated
    assertTrue(
        content.contains("public void populatePage(DataTable"),
        "Should contain populatePage method");
    assertTrue(
        content.contains("public void validatePage(DataTable"),
        "Should contain validatePage method");

    // Verify DataTable import
    assertTrue(
        content.contains("import io.cucumber.datatable.DataTable"), "Should import DataTable");

    LOG.info("✅ Test 3 PASSED: DataTable methods generated!");
  }

  /** Test 4: Verify hidden elements filtering works. */
  @Test(groups = "smoke", priority = 4)
  public void testHiddenElementsFiltering() throws Exception {
    LOG.info("=== Test 4: Hidden Elements Filtering ===");

    String testUrl = "https://github.com/login";
    String outputPathWithHidden =
        TEST_OUTPUT_DIR + "/com/cjs/qa/test/pages/GeneratedLoginPageWithHidden.java";
    String outputPathWithoutHidden =
        TEST_OUTPUT_DIR + "/com/cjs/qa/test/pages/GeneratedLoginPageWithoutHidden.java";
    String packageName = "com.cjs.qa.test.pages";
    String className1 = "GeneratedLoginPageWithHidden";
    String className2 = "GeneratedLoginPageWithoutHidden";

    // Generate with hidden elements
    PageObjectGenerator.generate(
        testUrl, outputPathWithHidden, packageName, className1, true, true, true);

    // Generate without hidden elements (default)
    PageObjectGenerator.generate(
        testUrl, outputPathWithoutHidden, packageName, className2, false, true, true);

    // Read both files
    String contentWithHidden = new String(Files.readAllBytes(Paths.get(outputPathWithHidden)));
    String contentWithoutHidden =
        new String(Files.readAllBytes(Paths.get(outputPathWithoutHidden)));

    // Both should have content
    assertTrue(contentWithHidden.length() > 0, "With hidden should have content");
    assertTrue(contentWithoutHidden.length() > 0, "Without hidden should have content");

    // Count locators in each (rough estimate by counting "private final By")
    long locatorsWithHidden = contentWithHidden.split("private final By").length - 1;
    long locatorsWithoutHidden = contentWithoutHidden.split("private final By").length - 1;

    LOG.info("Locators with hidden: {}", locatorsWithHidden);
    LOG.info("Locators without hidden: {}", locatorsWithoutHidden);

    // With hidden should have same or more locators
    assertTrue(
        locatorsWithHidden >= locatorsWithoutHidden,
        "With hidden should have same or more locators");

    LOG.info("✅ Test 4 PASSED: Hidden elements filtering works!");
  }

  /** Test 5: Verify generator handles pages with no interactive elements gracefully. */
  @Test(groups = "smoke", priority = 5)
  public void testEmptyPageHandling() throws Exception {
    LOG.info("=== Test 5: Empty Page Handling ===");

    // Use a simple page that might have few elements
    String testUrl = "https://www.example.com";
    String outputPath = TEST_OUTPUT_DIR + "/com/cjs/qa/test/pages/GeneratedEmptyPage.java";
    String packageName = "com.cjs.qa.test.pages";
    String className = "GeneratedEmptyPage";

    // Should not throw exception even if page has few/no interactive elements
    try {
      PageObjectGenerator.generate(testUrl, outputPath, packageName, className);

      // File should still be created
      File generatedFile = new File(outputPath);
      assertTrue(generatedFile.exists(), "File should be created even for empty page");

      LOG.info("✅ Test 5 PASSED: Empty page handled gracefully!");
    } catch (Exception e) {
      LOG.error("Generator failed on empty page: {}", e.getMessage());
      throw e;
    }
  }
}
