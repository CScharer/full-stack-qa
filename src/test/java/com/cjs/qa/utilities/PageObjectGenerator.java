package com.cjs.qa.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Generator - Generates Page Object classes from URLs.
 *
 * <p>This utility generates Page Object classes following the existing patterns in the codebase: -
 * Extends Page class - Uses same method naming conventions (setEditX, getEditX, clickButtonX) -
 * Generates populatePage() and validatePage() methods for Cucumber support - Skips hidden elements
 * by default (optional to include)
 *
 * <p>Usage:
 *
 * <pre>
 * PageObjectGenerator.generate(
 *     "https://example.com/login",
 *     "src/test/java/com/cjs/qa/example/pages/LoginPage.java",
 *     "com.cjs.qa.example.pages",
 *     "LoginPage"
 * );
 * </pre>
 *
 * @author CJS QA Team
 * @version 1.0
 */
public class PageObjectGenerator {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(PageObjectGenerator.class));

  // Element type constants
  private static final String TYPE_EDIT = "Edit";
  private static final String TYPE_BUTTON = "Button";
  private static final String TYPE_LINK = "Link";
  private static final String TYPE_CHECKBOX = "Checkbox";
  private static final String TYPE_RADIOBUTTON = "RadioButton";
  private static final String TYPE_DROPDOWN = "Dropdown";

  // Configuration
  private final boolean includeHiddenElements;
  private final boolean includePopulatePage;
  private final boolean includeValidatePage;

  /** Default constructor - uses recommended defaults. */
  public PageObjectGenerator() {
    this(false, true, true); // Skip hidden, include populate/validate
  }

  /**
   * Constructor with options.
   *
   * @param includeHiddenElements If true, includes hidden elements (default: false)
   * @param includePopulatePage If true, generates populatePage() method (default: true)
   * @param includeValidatePage If true, generates validatePage() method (default: true)
   */
  public PageObjectGenerator(
      boolean includeHiddenElements, boolean includePopulatePage, boolean includeValidatePage) {
    this.includeHiddenElements = includeHiddenElements;
    this.includePopulatePage = includePopulatePage;
    this.includeValidatePage = includeValidatePage;
  }

  /**
   * Generate a Page Object class from a URL.
   *
   * @param url The URL of the page to analyze
   * @param outputPath The file path where the generated class should be saved
   * @param packageName The Java package name for the generated class
   * @param className The name of the generated class
   * @throws Exception If generation fails
   */
  public static void generate(String url, String outputPath, String packageName, String className)
      throws Exception {
    generate(url, outputPath, packageName, className, false, true, true);
  }

  /**
   * Generate a Page Object class from a URL with options.
   *
   * @param url The URL of the page to analyze
   * @param outputPath The file path where the generated class should be saved
   * @param packageName The Java package name for the generated class
   * @param className The name of the generated class
   * @param includeHiddenElements If true, includes hidden elements
   * @param includePopulatePage If true, generates populatePage() method
   * @param includeValidatePage If true, generates validatePage() method
   * @throws Exception If generation fails
   */
  public static void generate(
      String url,
      String outputPath,
      String packageName,
      String className,
      boolean includeHiddenElements,
      boolean includePopulatePage,
      boolean includeValidatePage)
      throws Exception {
    PageObjectGenerator generator =
        new PageObjectGenerator(includeHiddenElements, includePopulatePage, includeValidatePage);
    generator.generatePageObject(url, outputPath, packageName, className);
  }

  /** Internal method to generate the Page Object. */
  private void generatePageObject(
      String url, String outputPath, String packageName, String className) throws Exception {
    LOG.info("Generating Page Object for: {}", url);
    LOG.info("Output: {}", outputPath);
    LOG.info("Package: {}", packageName);
    LOG.info("Class: {}", className);

    WebDriver driver = null;
    try {
      // Initialize WebDriver
      driver = createWebDriver();
      driver.get(url);

      // Wait for page to load
      waitForPageLoad(driver);

      // Find all interactive elements
      List<ElementInfo> elements = findInteractiveElements(driver);

      LOG.info("Found {} interactive elements", elements.size());

      // Generate code
      String code = generateCode(packageName, className, elements);

      // Write to file
      writeToFile(outputPath, code);

      LOG.info("Page Object generated successfully: {}", outputPath);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  /** Create and configure WebDriver for page analysis. */
  private WebDriver createWebDriver() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");

    // Check if running in CI/Docker with Selenium Grid
    String gridUrl = System.getenv("SELENIUM_REMOTE_URL");
    if (gridUrl != null && !gridUrl.isEmpty()) {
      LOG.info("Using Selenium Grid: {}", gridUrl);
      try {
        return new RemoteWebDriver(new URI(gridUrl).toURL(), options);
      } catch (Exception e) {
        LOG.error("Failed to connect to Grid at {}: {}", gridUrl, e.getMessage());
        LOG.warn("Falling back to local ChromeDriver");
        return new ChromeDriver(options);
      }
    }

    // Local execution - use ChromeDriver directly
    LOG.info("Using local ChromeDriver");
    return new ChromeDriver(options);
  }

  /** Wait for page to load completely. */
  private void waitForPageLoad(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(
        (webDriver) ->
            "complete"
                .equals(
                    ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    // Additional wait for dynamic content
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /** Find all interactive elements on the page. */
  private List<ElementInfo> findInteractiveElements(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();

    // Find input fields
    elements.addAll(findInputFields(driver));

    // Find buttons
    elements.addAll(findButtons(driver));

    // Find links
    elements.addAll(findLinks(driver));

    // Find checkboxes
    elements.addAll(findCheckboxes(driver));

    // Find radio buttons
    elements.addAll(findRadioButtons(driver));

    // Find dropdowns
    elements.addAll(findDropdowns(driver));

    return elements;
  }

  /** Find all input fields (text, email, password, number, textarea). */
  private List<ElementInfo> findInputFields(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();
    List<WebElement> inputs =
        driver.findElements(
            By.cssSelector(
                "input[type='text'], input[type='email'], input[type='password'], input[type='number'], textarea"));

    for (WebElement input : inputs) {
      if (shouldIncludeElement(input)) {
        String name = getElementName(input);
        By locator = generateBestLocator(input);
        if (locator != null) {
          elements.add(new ElementInfo(TYPE_EDIT, name, locator, input.getAttribute("type")));
        }
      }
    }

    return elements;
  }

  /** Find all buttons. */
  private List<ElementInfo> findButtons(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();
    List<WebElement> buttons =
        driver.findElements(By.cssSelector("button, input[type='submit'], input[type='button']"));

    for (WebElement button : buttons) {
      if (shouldIncludeElement(button)) {
        String name = getElementName(button);
        By locator = generateBestLocator(button);
        if (locator != null) {
          elements.add(new ElementInfo(TYPE_BUTTON, name, locator, null));
        }
      }
    }

    return elements;
  }

  /** Find all links (that look like buttons or are interactive). */
  private List<ElementInfo> findLinks(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();
    List<WebElement> links = driver.findElements(By.tagName("a"));

    for (WebElement link : links) {
      if (shouldIncludeElement(link) && isInteractiveLink(link)) {
        String name = getElementName(link);
        By locator = generateBestLocator(link);
        if (locator != null) {
          elements.add(new ElementInfo(TYPE_LINK, name, locator, null));
        }
      }
    }

    return elements;
  }

  /** Find all checkboxes. */
  private List<ElementInfo> findCheckboxes(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();
    List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));

    for (WebElement checkbox : checkboxes) {
      if (shouldIncludeElement(checkbox)) {
        String name = getElementName(checkbox);
        By locator = generateBestLocator(checkbox);
        if (locator != null) {
          elements.add(new ElementInfo(TYPE_CHECKBOX, name, locator, null));
        }
      }
    }

    return elements;
  }

  /** Find all radio buttons. */
  private List<ElementInfo> findRadioButtons(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();
    List<WebElement> radios = driver.findElements(By.cssSelector("input[type='radio']"));

    for (WebElement radio : radios) {
      if (shouldIncludeElement(radio)) {
        String name = getElementName(radio);
        By locator = generateBestLocator(radio);
        if (locator != null) {
          elements.add(new ElementInfo(TYPE_RADIOBUTTON, name, locator, null));
        }
      }
    }

    return elements;
  }

  /** Find all dropdowns (select elements). */
  private List<ElementInfo> findDropdowns(WebDriver driver) {
    List<ElementInfo> elements = new ArrayList<>();
    List<WebElement> selects = driver.findElements(By.tagName("select"));

    for (WebElement select : selects) {
      if (shouldIncludeElement(select)) {
        String name = getElementName(select);
        By locator = generateBestLocator(select);
        if (locator != null) {
          elements.add(new ElementInfo(TYPE_DROPDOWN, name, locator, null));
        }
      }
    }

    return elements;
  }

  /** Determine if element should be included (skip hidden if configured). */
  private boolean shouldIncludeElement(WebElement element) {
    if (!includeHiddenElements) {
      try {
        return element.isDisplayed();
      } catch (Exception e) {
        return false;
      }
    }
    return true;
  }

  /** Determine if a link is interactive (has href, looks like button, etc.). */
  private boolean isInteractiveLink(WebElement link) {
    String href = link.getAttribute("href");
    String className = link.getAttribute("class");
    // Include links with href or button-like styling
    return (href != null && !href.isEmpty()) || (className != null && className.contains("button"));
  }

  /** Get a meaningful name for an element. */
  private String getElementName(WebElement element) {
    // Try multiple strategies to get a good name
    String name = element.getAttribute("name");
    if (name != null && !name.isEmpty()) {
      return sanitizeName(name);
    }

    name = element.getAttribute("id");
    if (name != null && !name.isEmpty()) {
      return sanitizeName(name);
    }

    name = element.getAttribute("data-testid");
    if (name != null && !name.isEmpty()) {
      return sanitizeName(name);
    }

    name = element.getAttribute("aria-label");
    if (name != null && !name.isEmpty()) {
      return sanitizeName(name);
    }

    String text = element.getText();
    if (text != null && !text.trim().isEmpty()) {
      return sanitizeName(text.trim());
    }

    String tagName = element.getTagName();
    String type = element.getAttribute("type");
    return sanitizeName(tagName + (type != null ? type : ""));
  }

  /** Sanitize element name to valid Java identifier. */
  private String sanitizeName(String name) {
    // Remove special characters, keep alphanumeric
    String sanitized = name.replaceAll("[^a-zA-Z0-9]", "");
    if (sanitized.isEmpty()) {
      sanitized = "element";
    }
    // Capitalize first letter for method names
    return sanitized.substring(0, 1).toUpperCase(Locale.ROOT) + sanitized.substring(1);
  }

  /** Generate the best locator for an element (ID > Name > XPath). */
  private By generateBestLocator(WebElement element) {
    // Priority 1: ID
    String id = element.getAttribute("id");
    if (id != null && !id.isEmpty()) {
      return By.id(id);
    }

    // Priority 2: Name
    String name = element.getAttribute("name");
    if (name != null && !name.isEmpty()) {
      return By.name(name);
    }

    // Priority 3: Data attributes
    String testId = element.getAttribute("data-testid");
    if (testId != null && !testId.isEmpty()) {
      return By.cssSelector("[data-testid='" + testId + "']");
    }

    // Priority 4: XPath (relative, stable)
    return generateXPathLocator(element);
  }

  /** Generate a relative XPath locator for an element. */
  private By generateXPathLocator(WebElement element) {
    // This is a simplified version - in production, you'd want more sophisticated XPath generation
    String tagName = element.getTagName();
    String type = element.getAttribute("type");
    String className = element.getAttribute("class");

    if (type != null && !type.isEmpty()) {
      return By.xpath(".//" + tagName + "[@type='" + type + "']");
    }

    if (className != null && !className.isEmpty()) {
      // Use first class name
      String firstClass = className.split("\\s+")[0];
      return By.xpath(".//" + tagName + "[@class='" + firstClass + "']");
    }

    // Fallback: tag name with position (less stable but works)
    return By.xpath(".//" + tagName);
  }

  /** Generate the complete Java code for the Page Object class. */
  private String generateCode(String packageName, String className, List<ElementInfo> elements) {
    StringBuilder code = new StringBuilder();

    // Package and imports
    code.append("package ").append(packageName).append(";\n\n");
    code.append("import com.cjs.qa.selenium.ISelenium;\n");
    code.append("import com.cjs.qa.selenium.Page;\n");
    code.append("import io.cucumber.datatable.DataTable;\n");
    code.append("import java.util.HashMap;\n");
    code.append("import java.util.List;\n");
    code.append("import java.util.Locale;\n");
    code.append("import java.util.Map;\n");
    code.append("import org.apache.logging.log4j.LogManager;\n");
    code.append("import org.apache.logging.log4j.Logger;\n");
    code.append("import org.junit.Assert;\n");
    code.append("import org.openqa.selenium.By;\n");
    code.append("import org.openqa.selenium.WebDriver;\n\n");

    // Class declaration
    code.append("public class ").append(className).append(" extends Page {\n");
    code.append("  private static final Logger LOG = LogManager.getLogger(")
        .append(className)
        .append(".class);\n");
    code.append("\n");
    code.append("  public ").append(className).append("(WebDriver webDriver) {\n");
    code.append("    super(webDriver);\n");
    code.append("  }\n\n");

    // Locator definitions
    code.append("  // Locators\n");
    for (ElementInfo element : elements) {
      String locatorName = getLocatorName(element);
      code.append("  private static final By ").append(locatorName).append(" = ");
      code.append(formatLocator(element.getLocator())).append(";\n");
    }
    code.append("\n");

    // Getter/Setter methods
    code.append("  // Getter/Setter Methods\n");
    for (ElementInfo element : elements) {
      code.append(generateMethodsForElement(element));
    }
    code.append("\n");

    // Optional: populatePage() method
    if (includePopulatePage) {
      code.append(generatePopulatePageMethod(elements));
    }

    // Optional: validatePage() method
    if (includeValidatePage) {
      code.append(generateValidatePageMethod(elements));
    }

    // Close class
    code.append("}\n");

    return code.toString();
  }

  /** Get locator variable name for an element. */
  private String getLocatorName(ElementInfo element) {
    String name = element.getName().toLowerCase(Locale.ROOT);
    // Java 17: Switch expression
    return switch (element.getType()) {
      case TYPE_EDIT -> "edit" + name;
      case TYPE_BUTTON -> "button" + name;
      case TYPE_LINK -> "link" + name;
      case TYPE_CHECKBOX -> "checkbox" + name;
      case TYPE_RADIOBUTTON -> "radio" + name;
      case TYPE_DROPDOWN -> "dropdown" + name;
      default -> "element" + name;
    };
  }

  /** Format locator as Java code. */
  private String formatLocator(By locator) {
    String locatorStr = locator.toString();
    if (locatorStr.startsWith("By.id:")) {
      String id = locatorStr.substring(7);
      return "By.id(\"" + id + "\")";
    } else if (locatorStr.startsWith("By.name:")) {
      String name = locatorStr.substring(9);
      return "By.name(\"" + name + "\")";
    } else if (locatorStr.startsWith("By.cssSelector:")) {
      String selector = locatorStr.substring(16);
      return "By.cssSelector(\"" + selector + "\")";
    } else if (locatorStr.startsWith("By.xpath:")) {
      String xpath = locatorStr.substring(10);
      return "By.xpath(\"" + xpath + "\")";
    }
    return "By.xpath(\"" + locatorStr + "\")";
  }

  /** Generate getter/setter methods for an element. */
  private String generateMethodsForElement(ElementInfo element) {
    StringBuilder methods = new StringBuilder();
    String locatorName = getLocatorName(element);
    String methodName = element.getName();

    switch (element.getType()) {
      case TYPE_EDIT:
        // Setter
        methods.append("  public void setEdit").append(methodName).append("(String value) {\n");
        methods.append("    setEdit(").append(locatorName).append(", value);\n");
        methods.append("  }\n\n");
        // Getter
        methods.append("  public String getEdit").append(methodName).append("() {\n");
        methods.append("    return getEdit(").append(locatorName).append(");\n");
        methods.append("  }\n\n");
        break;
      case TYPE_BUTTON:
      case TYPE_LINK:
        methods
            .append("  public void click")
            .append(element.getType())
            .append(methodName)
            .append("() {\n");
        methods.append("    clickObject(").append(locatorName).append(");\n");
        methods.append("  }\n\n");
        break;
      case TYPE_CHECKBOX:
        // Setter (check/uncheck)
        methods
            .append("  public void set")
            .append(element.getType())
            .append(methodName)
            .append("(String value) {\n");
        methods.append("    setCheckbox(").append(locatorName).append(", value);\n");
        methods.append("  }\n\n");
        // Getter
        methods
            .append("  public String get")
            .append(element.getType())
            .append(methodName)
            .append("() {\n");
        methods.append("    return getCheckbox(").append(locatorName).append(");\n");
        methods.append("  }\n\n");
        break;
      case TYPE_DROPDOWN:
        // Setter
        methods
            .append("  public void select")
            .append(element.getType())
            .append(methodName)
            .append("(String value) {\n");
        methods.append("    selectDropdown(").append(locatorName).append(", value);\n");
        methods.append("  }\n\n");
        // Getter
        methods
            .append("  public String get")
            .append(element.getType())
            .append(methodName)
            .append("() {\n");
        methods.append("    return getDropdown(").append(locatorName).append(");\n");
        methods.append("  }\n\n");
        break;
      case TYPE_RADIOBUTTON:
        methods
            .append("  public void select")
            .append(element.getType())
            .append(methodName)
            .append("() {\n");
        methods.append("    clickObject(").append(locatorName).append(");\n");
        methods.append("  }\n\n");
        break;
      default:
        // Unknown element type - generate basic click method
        methods
            .append("  public void click")
            .append(element.getType())
            .append(methodName)
            .append("() {\n");
        methods.append("    clickObject(").append(locatorName).append(");\n");
        methods.append("  }\n\n");
        break;
    }

    return methods.toString();
  }

  /** Generate populatePage() method. */
  private String generatePopulatePageMethod(List<ElementInfo> elements) {
    StringBuilder method = new StringBuilder();
    method.append("  // DataTable Support - populatePage()\n");
    method.append("  public void populatePage(DataTable table) {\n");
    method.append("    final List<List<String>> data = table.asLists();\n");
    method.append("    for (final List<?> item : data) {\n");
    method.append("      final String field = (String) item.get(0);\n");
    method.append("      final String value = (String) item.get(1);\n");
    method.append("      if (!value.isEmpty()) {\n");
    method.append("        LOG.debug(\"{}: [{}]\", field, value);\n");
    method.append("        switch (field.toLowerCase(Locale.ENGLISH)) {\n");

    for (ElementInfo element : elements) {
      String fieldName = element.getName().toLowerCase(Locale.ROOT);
      String methodCall = getPopulateMethodCall(element);
      method.append("          case \"").append(fieldName).append("\":\n");
      method.append("            ").append(methodCall).append("\n");
      method.append("            break;\n");
    }

    method.append("          default:\n");
    method.append("            LOG.warn(\"[{}]{}\", field, ISelenium.FIELD_NOT_CODED);\n");
    method.append("            break;\n");
    method.append("        }\n");
    method.append("      }\n");
    method.append("    }\n");
    method.append("  }\n\n");

    return method.toString();
  }

  /** Generate validatePage() method. */
  private String generateValidatePageMethod(List<ElementInfo> elements) {
    StringBuilder method = new StringBuilder();
    method.append("  // DataTable Support - validatePage()\n");
    method.append("  public void validatePage(DataTable table) {\n");
    method.append("    final Map<String, String> expected = new HashMap<>();\n");
    method.append("    final Map<String, String> actual = new HashMap<>();\n");
    method.append("    final List<List<String>> data = table.asLists();\n");
    method.append("    for (final List<?> item : data) {\n");
    method.append("      final String field = (String) item.get(0);\n");
    method.append("      final String value = (String) item.get(1);\n");
    method.append("      if (!value.isEmpty()) {\n");
    method.append("        expected.put(field, value);\n");
    method.append("        switch (field.toLowerCase(Locale.ENGLISH)) {\n");

    for (ElementInfo element : elements) {
      String fieldName = element.getName().toLowerCase(Locale.ROOT);
      String methodCall = getValidateMethodCall(element);
      method.append("          case \"").append(fieldName).append("\":\n");
      method.append("            ").append(methodCall).append("\n");
      method.append("            break;\n");
    }

    method.append("          default:\n");
    method.append("            LOG.warn(\"[{}]{}\", field, ISelenium.FIELD_NOT_CODED);\n");
    method.append("            break;\n");
    method.append("        }\n");
    method.append("      }\n");
    method.append("    }\n");
    method.append("    Assert.assertSame(this.getClass().getName() + \"validatePage\", ");
    method.append("expected.toString(), actual.toString());\n");
    method.append("  }\n\n");

    return method.toString();
  }

  /** Get method call for populatePage() switch case. */
  private String getPopulateMethodCall(ElementInfo element) {
    String methodName = element.getName();
    // Java 17: Switch expression
    return switch (element.getType()) {
      case TYPE_EDIT -> "setEdit" + methodName + "(value);";
      case TYPE_BUTTON, TYPE_LINK -> "click" + element.getType() + methodName + "();";
      case TYPE_CHECKBOX -> "set" + element.getType() + methodName + "(value);";
      case TYPE_DROPDOWN, TYPE_RADIOBUTTON -> "select" + element.getType() + methodName + "();";
      default -> "// TODO: Implement for " + element.getType();
    };
  }

  /** Get method call for validatePage() switch case. */
  private String getValidateMethodCall(ElementInfo element) {
    String methodName = element.getName();
    // Java 17: Switch expression
    return switch (element.getType()) {
      case TYPE_EDIT -> "actual.put(field, getEdit" + methodName + "());";
      case TYPE_CHECKBOX, TYPE_DROPDOWN ->
          "actual.put(field, get" + element.getType() + methodName + "());";
      default -> "// TODO: Implement validation for " + element.getType();
    };
  }

  /** Write generated code to file. */
  private void writeToFile(String outputPath, String code) throws IOException {
    File file = new File(outputPath);
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }
    FSOTests.fileWrite(outputPath, code, false);
  }

  /** Inner class to hold element information. */
  private static class ElementInfo {

    private final String type;
    private final String name;
    private final By locator;
    private final String inputType;

    public ElementInfo(String type, String name, By locator, String inputType) {
      this.type = type;
      this.name = name;
      this.locator = locator;
      this.inputType = inputType;
    }

    public String getType() {
      return type;
    }

    public String getName() {
      return name;
    }

    public By getLocator() {
      return locator;
    }

    @SuppressWarnings("unused")
    public String getInputType() {
      return inputType; // Reserved for future use
    }
  }
}
