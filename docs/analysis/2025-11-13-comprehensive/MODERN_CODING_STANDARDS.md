# Modern Coding Standards & Best Practices
## Java 17 + Selenium 4 Best Practices Guide

**Date**: November 13, 2025
**Target**: Java 17, Selenium 4.26.0, Maven 3.9.11
**Purpose**: Upgrade existing code to modern standards

---

## 📚 TABLE OF CONTENTS

1. [Java 17 Modern Features](#java-17-features)
2. [Selenium 4 Best Practices](#selenium-4-practices)
3. [Testing Patterns](#testing-patterns)
4. [Error Handling](#error-handling)
5. [Logging Standards](#logging-standards)
6. [Code Organization](#code-organization)
7. [Performance Optimization](#performance-optimization)

---

## 🎯 JAVA 17 MODERN FEATURES

### 1. Use Records for Data Objects

**❌ Old Way (Too Much Boilerplate)**:
```java
public class TestResult {
    private final String testName;
    private final String status;
    private final Duration executionTime;
    private final List<String> screenshots;

    public TestResult(String testName, String status,
                     Duration executionTime, List<String> screenshots) {
        this.testName = testName;
        this.status = status;
        this.executionTime = executionTime;
        this.screenshots = screenshots;
    }

    // Getters, equals(), hashCode(), toString()... 50+ lines!
}
```

**✅ New Way (Records)**:
```java
public record TestResult(
    String testName,
    String status,
    Duration executionTime,
    List<String> screenshots
) {
    // Optional: Add validation
    public TestResult {
        Objects.requireNonNull(testName, "Test name cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
    }

    // Optional: Add custom methods
    public boolean isPassed() {
        return "PASSED".equals(status);
    }
}

// Usage:
TestResult result = new TestResult(
    "testLogin",
    "PASSED",
    Duration.ofSeconds(5),
    List.of("screenshot1.png", "screenshot2.png")
);

System.out.println(result.testName());      // Auto-generated getter
System.out.println(result.isPassed());       // Custom method
```

**Where to Use**:
- `Flight.java` → Use record
- `TestResult` objects → Use record
- API response objects → Use record
- Configuration objects → Use record

---

### 2. Switch Expressions

**❌ Old Way**:
```java
String browserPath;
switch(browser) {
    case "chrome":
        browserPath = "/path/to/chrome";
        break;
    case "firefox":
        browserPath = "/path/to/firefox";
        break;
    case "edge":
        browserPath = "/path/to/edge";
        break;
    default:
        throw new IllegalArgumentException("Unknown browser: " + browser);
}
```

**✅ New Way**:
```java
String browserPath = switch(browser.toLowerCase()) {
    case "chrome" -> "/path/to/chrome";
    case "firefox" -> "/path/to/firefox";
    case "edge" -> "/path/to/edge";
    default -> throw new IllegalArgumentException("Unknown browser: " + browser);
};

// Multi-line statements:
String driverClass = switch(browser) {
    case "chrome" -> {
        WebDriverManager.chromedriver().setup();
        yield "ChromeDriver";
    }
    case "firefox" -> {
        WebDriverManager.firefoxdriver().setup();
        yield "FirefoxDriver";
    }
    default -> throw new IllegalArgumentException("Unknown: " + browser);
};
```

**Where to Use**:
- `SeleniumWebDriver.initializeWebDriver()` - browser selection
- All switch statements in the codebase

---

### 3. Text Blocks

**❌ Old Way**:
```java
String json = "{\n" +
    "  \"username\": \"" + username + "\",\n" +
    "  \"password\": \"" + password + "\",\n" +
    "  \"rememberMe\": true\n" +
    "}";

String xpath = "//div[@class='container']" +
               "//input[@type='text']" +
               "[@name='username']";
```

**✅ New Way**:
```java
String json = """
    {
      "username": "%s",
      "password": "%s",
      "rememberMe": true
    }
    """.formatted(username, password);

String xpath = """
    //div[@class='container']
      //input[@type='text']
            [@name='username']
    """.trim();

// SQL queries:
String query = """
    SELECT u.id, u.name, u.email
    FROM users u
    WHERE u.status = 'active'
      AND u.created_date > ?
    ORDER BY u.name
    """;
```

**Where to Use**:
- JSON payloads in API tests
- SQL queries in database tests
- Complex XPath expressions
- HTML/XML templates
- Multi-line log messages

---

### 4. Pattern Matching (instanceof)

**❌ Old Way**:
```java
if (result instanceof Success) {
    Success success = (Success) result;
    log.info("Test passed: {}", success.getMessage());
} else if (result instanceof Failure) {
    Failure failure = (Failure) result;
    log.error("Test failed: {}", failure.getError());
}
```

**✅ New Way**:
```java
if (result instanceof Success success) {
    log.info("Test passed: {}", success.getMessage());
} else if (result instanceof Failure failure) {
    log.error("Test failed: {}", failure.getError());
}
```

---

### 5. Sealed Classes (Java 17)

**Use Case**: Define restricted class hierarchies.

```java
// Define sealed interface
public sealed interface TestResult
    permits Success, Failure, Skipped {
    String testName();
    Duration executionTime();
}

// Implementations
public record Success(
    String testName,
    Duration executionTime,
    String message
) implements TestResult {}

public record Failure(
    String testName,
    Duration executionTime,
    Throwable error,
    String stackTrace
) implements TestResult {}

public record Skipped(
    String testName,
    Duration executionTime,
    String reason
) implements TestResult {}

// Exhaustive pattern matching (future Java versions):
String message = switch(result) {
    case Success s -> "✅ " + s.message();
    case Failure f -> "❌ " + f.error().getMessage();
    case Skipped s -> "⏭️  " + s.reason();
    // No default needed - compiler knows all cases!
};
```

---

### 6. Helpful NullPointerExceptions

**Java 14+**: Better NPE messages!

```java
// Before Java 14:
String email = user.getProfile().getEmail().toLowerCase();
// NPE: "null" (which null??)

// Java 14+:
// NPE: "Cannot invoke String.toLowerCase() because the return value of
//       User.getProfile().getEmail() is null"
```

**Best Practice**: Still use Optional to prevent NPEs:
```java
String email = Optional.ofNullable(user)
    .map(User::getProfile)
    .map(Profile::getEmail)
    .map(String::toLowerCase)
    .orElse("no-email@example.com");
```

---

## 🌐 SELENIUM 4 BEST PRACTICES

### 1. Relative Locators

**❌ Old Way (Complex XPath)**:
```java
By targetElement = By.xpath(
    "//label[text()='Email']/following-sibling::input"
);
```

**✅ New Way (Relative Locators)**:
```java
import static org.openqa.selenium.support.locators.RelativeLocator.with;

By emailLabel = By.xpath("//label[text()='Email']");
WebElement emailInput = driver.findElement(
    with(By.tagName("input"))
        .toRightOf(emailLabel)
);

// More examples:
WebElement submitButton = driver.findElement(
    with(By.tagName("button"))
        .below(passwordInput)
        .above(footer)
);

WebElement checkbox = driver.findElement(
    with(By.tagName("input"))
        .near(termsLabel)  // Within 50 pixels
);
```

---

### 2. Better Waits

**❌ Old Way**:
```java
Thread.sleep(5000);  // ❌ Don't do this!

// Or:
driver.manage().timeouts()
    .implicitlyWait(10, TimeUnit.SECONDS);  // Deprecated API
```

**✅ New Way**:
```java
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;

// Modern duration API:
driver.manage().timeouts()
    .implicitlyWait(Duration.ofSeconds(10));

// Explicit waits with FluentWait:
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class)
    .until(ExpectedConditions.elementToBeClickable(loginButton));

// Custom wait conditions:
wait.until(driver -> {
    return driver.findElement(By.id("status"))
        .getText()
        .equals("Ready");
});
```

---

### 3. Chrome DevTools Protocol (CDP)

**New Feature**: Access Chrome DevTools directly!

```java
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v119.network.Network;

ChromeDriver driver = new ChromeDriver();
DevTools devTools = driver.getDevTools();
devTools.createSession();

// Monitor network traffic:
devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
devTools.addListener(Network.requestWillBeSent(), request -> {
    System.out.println("Request: " + request.getRequest().getUrl());
});

// Mock geolocation:
devTools.send(Emulation.setGeolocationOverride(
    Optional.of(37.7749),  // Latitude
    Optional.of(-122.4194), // Longitude
    Optional.of(1)          // Accuracy
));

// Capture console logs:
devTools.addListener(Log.entryAdded(), logEntry -> {
    System.out.println("Console: " + logEntry.getText());
});

// Block URLs (testing error handling):
devTools.send(Network.setBlockedURLs(
    ImmutableList.of("*.analytics.com*", "*.ads.com*")
));

// Simulate offline:
devTools.send(Network.emulateNetworkConditions(
    false,                          // offline
    100,                            // latency
    10000,                          // download throughput
    5000,                           // upload throughput
    Optional.of(ConnectionType.WIFI)
));
```

---

### 4. Better Screenshots

**✅ Modern Approach**:
```java
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.nio.file.Files;
import java.nio.file.Paths;

// Full page screenshot:
File screenshot = ((TakesScreenshot) driver)
    .getScreenshotAs(OutputType.FILE);

Files.move(
    screenshot.toPath(),
    Paths.get("screenshots/test-" + System.currentTimeMillis() + ".png")
);

// Element screenshot (Selenium 4):
WebElement element = driver.findElement(By.id("logo"));
File elementShot = element.getScreenshotAs(OutputType.FILE);

// Screenshot as Base64 (for embedding in reports):
String base64 = ((TakesScreenshot) driver)
    .getScreenshotAs(OutputType.BASE64);
```

---

### 5. Better Window Management

**✅ Modern Approach**:
```java
// New window/tab (Selenium 4):
driver.switchTo().newWindow(WindowType.TAB);
driver.get("https://example.com");

driver.switchTo().newWindow(WindowType.WINDOW);
driver.get("https://another-site.com");

// Get all windows:
Set<String> windows = driver.getWindowHandles();

// Switch back to original:
driver.switchTo().window(windows.iterator().next());

// Position and size:
driver.manage().window().setPosition(new Point(0, 0));
driver.manage().window().setSize(new Dimension(1920, 1080));

// Or maximize:
driver.manage().window().maximize();

// Or fullscreen:
driver.manage().window().fullscreen();
```

---

## 🧪 TESTING PATTERNS

### 1. Use Page Object Model (Already Implemented ✅)

**Your Code** (Good!):
```java
public class LoginPage extends Page {
    private static final By usernameInput = By.id("username");
    private static final By passwordInput = By.id("password");
    private static final By loginButton = By.xpath("//button[@type='submit']");

    public void login(String username, String password) {
        setEdit(usernameInput, username);
        setEdit(passwordInput, password);
        click(loginButton);
    }
}
```

**Enhancement**: Add fluent interface:
```java
public class LoginPage extends Page {
    // ... existing code ...

    public LoginPage enterUsername(String username) {
        setEdit(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        setEdit(passwordInput, password);
        return this;
    }

    public HomePage clickLogin() {
        click(loginButton);
        return new HomePage(driver);
    }
}

// Usage (method chaining):
HomePage home = new LoginPage(driver)
    .enterUsername("user@example.com")
    .enterPassword("password123")
    .clickLogin();
```

---

### 2. Use TestNG Features Properly

**Data Providers**:
```java
@DataProvider(name = "browsers")
public Object[][] browserProvider() {
    return new Object[][] {
        {"chrome"},
        {"firefox"},
        {"edge"}
    };
}

@Test(dataProvider = "browsers")
public void testOnAllBrowsers(String browser) {
    // Test code
}

// Parallel data provider:
@DataProvider(name = "users", parallel = true)
public Object[][] userProvider() {
    return new Object[][] {
        {"user1@test.com", "pass1"},
        {"user2@test.com", "pass2"},
        {"user3@test.com", "pass3"}
    };
}
```

**Dependencies**:
```java
@Test
public void test1_CreateUser() {
    // Creates test user
}

@Test(dependsOnMethods = "test1_CreateUser")
public void test2_LoginUser() {
    // Uses user from test1
}

@Test(dependsOnMethods = "test2_LoginUser")
public void test3_DeleteUser() {
    // Cleanup
}
```

**Groups**:
```java
@Test(groups = {"smoke", "login"})
public void testQuickLogin() { }

@Test(groups = {"regression", "login"})
public void testLoginWithOAuth() { }

@Test(groups = {"smoke", "search"})
public void testQuickSearch() { }

// Run only smoke tests:
// mvn test -Dgroups=smoke
```

---

### 3. Use Soft Assertions

**❌ Old Way (Fails on first assertion)**:
```java
@Test
public void testUserProfile() {
    Assert.assertEquals(page.getName(), "John Doe");
    Assert.assertEquals(page.getEmail(), "john@example.com");  // Never reaches if first fails
    Assert.assertEquals(page.getPhone(), "555-0100");
}
```

**✅ New Way (Collects all failures)**:
```java
@Test
public void testUserProfile() {
    SoftAssert soft = new SoftAssert();

    soft.assertEquals(page.getName(), "John Doe", "Name mismatch");
    soft.assertEquals(page.getEmail(), "john@example.com", "Email mismatch");
    soft.assertEquals(page.getPhone(), "555-0100", "Phone mismatch");

    soft.assertAll();  // Throws if any assertion failed
}
```

---

## 🚨 ERROR HANDLING

### 1. Use Try-With-Resources

**❌ Old Way**:
```java
FileInputStream fis = null;
try {
    fis = new FileInputStream("test-data.xlsx");
    // Use fis
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (fis != null) {
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**✅ New Way**:
```java
try (FileInputStream fis = new FileInputStream("test-data.xlsx")) {
    // Use fis - automatically closed!
} catch (IOException e) {
    log.error("Failed to read test data", e);
    throw new TestDataException("Cannot load test data", e);
}
```

---

### 2. Create Custom Exceptions

```java
// Base exception
public class TestAutomationException extends RuntimeException {
    public TestAutomationException(String message) {
        super(message);
    }

    public TestAutomationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Specific exceptions
public class ElementNotFoundException extends TestAutomationException {
    private final By locator;

    public ElementNotFoundException(By locator) {
        super("Element not found: " + locator);
        this.locator = locator;
    }

    public By getLocator() {
        return locator;
    }
}

public class TestDataException extends TestAutomationException {
    public TestDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage:
try {
    driver.findElement(loginButton);
} catch (NoSuchElementException e) {
    throw new ElementNotFoundException(loginButton);
}
```

---

### 3. Fail Fast, Fail Clearly

**✅ Good Error Messages**:
```java
// Bad:
Assert.assertTrue(result);

// Good:
Assert.assertTrue(result,
    "Expected login to succeed but got error: " + errorMessage);

// Better:
String errorMsg = String.format(
    "Login failed for user '%s' with error: %s",
    username, errorMessage
);
Assert.assertTrue(result, errorMsg);

// Best (with context):
Assert.assertTrue(result, () -> String.format(
    """
    Login failed for user '%s'
    Error: %s
    URL: %s
    Timestamp: %s
    """,
    username,
    errorMessage,
    driver.getCurrentUrl(),
    Instant.now()
));
```

---

## 📝 LOGGING STANDARDS

### 1. Use SLF4J + Log4j 2

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginTests {
    private static final Logger log = LoggerFactory.getLogger(LoginTests.class);

    @Test
    public void testLogin() {
        log.info("Starting login test for user: {}", testUser.getEmail());
        log.debug("Test data: {}", testUser);

        try {
            loginPage.login(testUser.getEmail(), testUser.getPassword());
            log.info("Login successful");
        } catch (Exception e) {
            log.error("Login failed for user: {}", testUser.getEmail(), e);
            throw e;
        }
    }
}
```

---

### 2. Log Levels

```java
// TRACE: Very detailed (method entry/exit)
log.trace("Entering method: login()");

// DEBUG: Detailed info for debugging
log.debug("Searching for element: {}", locator);

// INFO: General information
log.info("Test started: {}", testName);

// WARN: Potential issues
log.warn("Element took {}ms to load (expected <2000ms)", duration);

// ERROR: Errors that should be investigated
log.error("Failed to login after 3 retries", exception);
```

---

### 3. Structured Logging

```java
// Add context to logs:
import org.slf4j.MDC;

@BeforeMethod
public void setupTest(Method method) {
    MDC.put("testName", method.getName());
    MDC.put("threadId", String.valueOf(Thread.currentThread().getId()));
    MDC.put("sessionId", driver.getSessionId().toString());
}

@AfterMethod
public void teardownTest() {
    MDC.clear();
}

// Log4j2.xml pattern:
// %d{ISO8601} [%X{testName}] [%X{sessionId}] %level - %msg%n
```

---

## 📂 CODE ORGANIZATION

### 1. Package Structure

```
src/test/java/com/cjs/qa/
├── core/                      # Core framework
│   ├── driver/                # WebDriver management
│   ├── config/                # Configuration
│   └── exceptions/            # Custom exceptions
├── pages/                     # Page objects (by domain)
│   ├── google/
│   ├── microsoft/
│   └── common/                # Shared page components
├── tests/                     # Test classes
│   ├── smoke/
│   ├── regression/
│   └── api/
├── data/                      # Test data providers
│   ├── providers/
│   └── models/
└── utils/                     # Utilities
    ├── TestDataFactory
    ├── ScreenshotHelper
    └── ReportingHelper
```

---

### 2. Naming Conventions

```java
// Test classes:
public class LoginTests { }           // Plural: Tests
public class UserProfileTest { }       // Singular: Test

// Test methods:
@Test
public void testSuccessfulLogin() { }  // test prefix

@Test
public void shouldDisplayErrorForInvalidCredentials() { }  // BDD style

// Page objects:
public class LoginPage { }             // Page suffix
public class UserProfilePage { }

// Utilities:
public class WebDriverManager { }      // Manager suffix
public class ScreenshotHelper { }      // Helper suffix
public class TestDataFactory { }       // Factory suffix

// Constants:
public static final int DEFAULT_TIMEOUT = 30;     // SCREAMING_SNAKE_CASE
public static final String BASE_URL = "...";

// Variables:
private String userName;               // camelCase
private WebDriver driver;
```

---

### 3. File Organization

```java
// Order within a class:
public class LoginPage {
    // 1. Static constants
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);
    private static final By USERNAME = By.id("username");

    // 2. Instance variables
    private final WebDriver driver;
    private final WebDriverWait wait;

    // 3. Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 4. Public methods
    public void login(String username, String password) { }

    public boolean isLoginSuccessful() { }

    // 5. Private helper methods
    private void waitForPageLoad() { }

    private void clearCookies() { }
}
```

---

## ⚡ PERFORMANCE OPTIMIZATION

### 1. Reuse WebDriver Sessions

**❌ Slow**:
```java
@Test
public void test1() {
    WebDriver driver = new ChromeDriver();  // New session
    // test
    driver.quit();
}

@Test
public void test2() {
    WebDriver driver = new ChromeDriver();  // Another new session
    // test
    driver.quit();
}
```

**✅ Fast**:
```java
public class BaseTest {
    protected WebDriver driver;

    @BeforeClass
    public void setupClass() {
        driver = new ChromeDriver();  // One session for class
    }

    @BeforeMethod
    public void setupTest() {
        driver.manage().deleteAllCookies();
        // Reset state
    }

    @AfterClass
    public void teardownClass() {
        driver.quit();
    }
}
```

---

### 2. Parallel Execution

**testng.xml**:
```xml
<suite name="Test Suite" parallel="classes" thread-count="5">
    <test name="Smoke Tests">
        <classes>
            <class name="com.cjs.qa.tests.LoginTests"/>
            <class name="com.cjs.qa.tests.SearchTests"/>
            <class name="com.cjs.qa.tests.CheckoutTests"/>
        </classes>
    </test>
</suite>
```

**Thread-Safe WebDriver**:
```java
public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(new ChromeDriver());
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

---

### 3. Smart Waits

```java
// Don't poll too frequently:
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.pollingEvery(Duration.ofMillis(500));  // Not 100ms

// Ignore expected exceptions:
wait.ignoring(StaleElementReferenceException.class)
    .ignoring(NoSuchElementException.class);

// Use specific waits:
wait.until(ExpectedConditions.presenceOfElementLocated(element));  // In DOM
wait.until(ExpectedConditions.visibilityOfElementLocated(element)); // Visible
wait.until(ExpectedConditions.elementToBeClickable(element));      // Clickable
```

---

## ✅ CHECKLIST: Modernize Your Code

### High Impact:
- [ ] Convert data objects to Records (Flight, TestResult, etc.)
- [ ] Replace switch statements with switch expressions
- [ ] Use text blocks for multi-line strings
- [ ] Implement try-with-resources everywhere
- [ ] Use Duration API instead of TimeUnit
- [ ] Add proper logging (SLF4J + Log4j 2)

### Medium Impact:
- [ ] Use pattern matching for instanceof
- [ ] Implement relative locators
- [ ] Use Chrome DevTools Protocol
- [ ] Add soft assertions where appropriate
- [ ] Create custom exceptions
- [ ] Improve error messages

### Low Impact (Nice to Have):
- [ ] Use sealed classes for restricted hierarchies
- [ ] Add fluent interfaces to page objects
- [ ] Implement structured logging with MDC
- [ ] Optimize parallel execution
- [ ] Use Optional for null safety

---

## 📚 Additional Resources

### Official Documentation:
- [Java 17 New Features](https://openjdk.org/projects/jdk/17/)
- [Selenium 4 Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [Log4j 2 Documentation](https://logging.apache.org/log4j/2.x/)

### Best Practices:
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Effective Java (Book)](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Clean Code (Book)](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)

---

**Questions or need help implementing these?**
Create a GitHub issue or check other docs in `/docs` folder!

---

*Last Updated: November 13, 2025*
*Version: 1.0*
