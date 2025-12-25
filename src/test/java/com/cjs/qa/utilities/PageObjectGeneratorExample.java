package com.cjs.qa.utilities;

/**
 * Example usage of PageObjectGenerator.
 *
 * <p>This class demonstrates how to use the PageObjectGenerator to create Page Object classes from
 * URLs.
 *
 * <p>Usage Example:
 *
 * <pre>
 * // Basic usage - generates Page Object with default options
 * PageObjectGenerator.generate(
 *     "https://example.com/login",
 *     "src/test/java/com/cjs/qa/example/pages/LoginPage.java",
 *     "com.cjs.qa.example.pages",
 *     "LoginPage"
 * );
 *
 * // Advanced usage - with custom options
 * PageObjectGenerator.generate(
 *     "https://example.com/register",
 *     "src/test/java/com/cjs/qa/example/pages/RegisterPage.java",
 *     "com.cjs.qa.example.pages",
 *     "RegisterPage",
 *     false,  // includeHiddenElements: skip hidden elements
 *     true,   // includePopulatePage: generate populatePage() method
 *     true    // includeValidatePage: generate validatePage() method
 * );
 * </pre>
 *
 * <p>After generation:
 *
 * <ol>
 *   <li>Review the generated code
 *   <li>Adjust locators if needed
 *   <li>Add custom business methods (e.g., login(), submitForm())
 *   <li>Test the generated Page Object
 * </ol>
 *
 * @author CJS QA Team
 * @see PageObjectGenerator
 */
public class PageObjectGeneratorExample {
  // This is an example/documentation class only
  // Actual usage is done via static methods in PageObjectGenerator
}
