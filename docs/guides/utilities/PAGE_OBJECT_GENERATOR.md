# Page Object Generator Guide

**Type**: Guide
**Purpose**: Documentation for using the Page Object Generator utility
**Created**: 2025-11-13
**Last Updated**: 2025-11-13
**Maintained By**: CJS QA Team
**Status**: Active

---

## 📖 Overview

The **Page Object Generator** automatically creates Page Object classes from URLs, following your existing codebase patterns. It analyzes a web page, detects interactive elements, and generates a complete Page Object class with:

- Locator definitions
- Getter/Setter methods
- `populatePage()` method (for Cucumber DataTable support)
- `validatePage()` method (for Cucumber DataTable support)

**Time Savings**: Reduces Page Object creation from 30-60 minutes to 2-3 minutes per page.

---

## 🚀 Quick Start

### Basic Usage

```java
import com.cjs.qa.utilities.PageObjectGenerator;

// Generate a Page Object from a URL
PageObjectGenerator.generate(
    "https://example.com/login",
    "src/test/java/com/cjs/qa/example/pages/LoginPage.java",
    "com.cjs.qa.example.pages",
    "LoginPage"
);
```

### Advanced Usage (with Options)

```java
// Customize generation options
PageObjectGenerator.generate(
    "https://example.com/register",
    "src/test/java/com/cjs/qa/example/pages/RegisterPage.java",
    "com.cjs.qa.example.pages",
    "RegisterPage",
    false,  // includeHiddenElements: skip hidden elements (default: false)
    true,   // includePopulatePage: generate populatePage() method (default: true)
    true    // includeValidatePage: generate validatePage() method (default: true)
);
```

---

## 📋 What Gets Generated

### 1. Class Structure

```java
package com.cjs.qa.example.pages;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
// ... other imports

public class LoginPage extends Page {
  public LoginPage(WebDriver webDriver) {
    super(webDriver);
  }
  // ...
}
```

### 2. Locator Definitions

```java
// Locators
private final By editEmail = By.id("email");
private final By editPassword = By.id("password");
private final By buttonSignIn = By.xpath("//button[.='Sign In']");
```

### 3. Getter/Setter Methods

```java
// Edit fields
public void setEditEmail(String value) {
  setEdit(editEmail, value);
}

public String getEditEmail() {
  return getEdit(editEmail);
}

// Buttons
public void clickButtonSignIn() {
  clickObject(buttonSignIn);
}
```

### 4. DataTable Support (Cucumber)

```java
// populatePage() - for filling forms
public void populatePage(DataTable table) {
  // Automatically handles all fields via switch statement
}

// validatePage() - for validating form values
public void validatePage(DataTable table) {
  // Automatically validates all fields via switch statement
}
```

---

## 🎯 Element Detection

The generator automatically detects:

- **Input Fields**: `input[type='text']`, `input[type='email']`, `input[type='password']`, `input[type='number']`, `textarea`
- **Buttons**: `button`, `input[type='submit']`, `input[type='button']`
- **Links**: `a` tags (interactive links only)
- **Checkboxes**: `input[type='checkbox']`
- **Radio Buttons**: `input[type='radio']`
- **Dropdowns**: `select` elements

**Note**: Hidden elements are skipped by default (configurable).

---

## 🔍 Locator Strategy

The generator uses a priority-based locator strategy:

1. **ID** (Best): `By.id("email")`
2. **Name**: `By.name("email")`
3. **Data Attributes**: `By.cssSelector("[data-testid='email']")`
4. **XPath** (Fallback): `By.xpath(".//input[@type='email']")`

Generated locators prioritize stability and maintainability.

---

## ✏️ After Generation

### 1. Review Generated Code

- Check locators are correct
- Verify element names are meaningful
- Ensure all expected elements were detected

### 2. Adjust as Needed

```java
// Example: Improve a locator
private final By editEmail = By.xpath(".//input[@placeholder='Email']"); // Better locator
```

### 3. Add Custom Business Methods

```java
// Example: Add a login() method
public void login(String email, String password) {
  setEditEmail(email);
  setEditPassword(password);
  clickButtonSignIn();
}
```

### 4. Test the Page Object

```java
// Example: Use in a test
LoginPage loginPage = new LoginPage(webDriver);
loginPage.login("user@example.com", "password123");
```

---

## ⚙️ Configuration Options

### `includeHiddenElements` (default: `false`)

- `false`: Skip hidden elements (recommended)
- `true`: Include all elements, even if hidden

### `includePopulatePage` (default: `true`)

- `true`: Generate `populatePage(DataTable)` method
- `false`: Skip `populatePage()` generation

### `includeValidatePage` (default: `true`)

- `true`: Generate `validatePage(DataTable)` method
- `false`: Skip `validatePage()` generation

---

## 📝 Generated Code Patterns

The generator follows your existing codebase patterns:

- ✅ Extends `Page` class
- ✅ Uses `super(webDriver)` constructor
- ✅ Method naming: `setEditX()`, `getEditX()`, `clickButtonX()`
- ✅ Locator naming: `editX`, `buttonX`, `checkboxX`
- ✅ Uses existing `Page` methods: `setEdit()`, `getEdit()`, `clickObject()`, etc.
- ✅ Follows existing import structure
- ✅ Matches existing code style

**Result**: Generated code looks exactly like your manually-written Page Objects!

---

## 🎓 Best Practices

### 1. Generate First, Customize Second

1. Generate the Page Object
2. Review and adjust locators
3. Add custom business methods
4. Test thoroughly

### 2. Use Meaningful URLs

Generate from the actual page you'll be testing, not a mock or placeholder.

### 3. Review Element Names

The generator creates names from element attributes. Review and adjust if needed:

```java
// Generated
private final By editUserid = By.id("userid");

// Better (if you prefer)
private final By editUserId = By.id("userid");
```

### 4. Add Business Methods

Don't just use getters/setters - add meaningful business methods:

```java
// Good: Business method
public void login(String email, String password) {
  setEditEmail(email);
  setEditPassword(password);
  clickButtonSignIn();
}

// Usage in test
loginPage.login("user@example.com", "password");
```

---

## 🔧 Troubleshooting

### Issue: Missing Elements

**Solution**: Check if elements are hidden. Use `includeHiddenElements: true` if needed.

### Issue: Poor Locator Quality

**Solution**: Review generated locators and manually improve unstable ones (especially XPath fallbacks).

### Issue: Duplicate Element Names

**Solution**: The generator handles duplicates by using element attributes. Review and adjust names if needed.

### Issue: Page Not Loading

**Solution**: Ensure the URL is accessible and the page loads completely. The generator waits for page load.

---

## 📚 Related Documentation

- [Page Object Generator Analysis](../analysis/2025-11-13-comprehensive/PAGE_OBJECT_GENERATOR_ANALYSIS.md)
- [Page Object Model Best Practices](../analysis/2025-11-13-comprehensive/MODERN_CODING_STANDARDS.md)
- [AI Workflow Rules](../../process/AI_WORKFLOW_RULES.md)

---

## 🚀 Example: Complete Workflow

```java
// 1. Generate Page Object
PageObjectGenerator.generate(
    "https://example.com/login",
    "src/test/java/com/cjs/qa/example/pages/LoginPage.java",
    "com.cjs.qa.example.pages",
    "LoginPage"
);

// 2. Review generated code
// 3. Add custom business method
public void login(String email, String password) {
  setEditEmail(email);
  setEditPassword(password);
  clickButtonSignIn();
}

// 4. Use in test
@Test
public void testLogin() {
  LoginPage loginPage = new LoginPage(webDriver);
  loginPage.login("user@example.com", "password123");
  // Assert login success
}
```

---

**Ready to use!** Generate your first Page Object and see the time savings. 🎉
