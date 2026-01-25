package com.cjs.qa.junit.tests;

import static com.codeborne.selenide.Condition.visible;

import java.time.Duration;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cjs.qa.config.EnvironmentConfig;
import com.cjs.qa.junit.pages.HomePage;
import com.cjs.qa.utilities.GuardedLogger;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;

import io.qameta.allure.*;

/** Simple test for the HomePage */
@Epic("HomePage Tests")
@Feature("HomePage Navigation")
@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class HomePageTests {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(HomePageTests.class));
  private HomePage homePage;

  @BeforeMethod
  public void setUp() {
    // Configure Selenide
    Configuration.browser = "chrome";
    Configuration.headless = !"false".equalsIgnoreCase(System.getProperty("headless", "true"));
    Configuration.timeout = 5000;
    Configuration.pageLoadTimeout = 10000;
    // Maximize browser window (set to large size to ensure sidebar is visible)
    Configuration.browserSize = "1920x1080";

    // Set base URL - use EnvironmentConfig for centralized configuration
    String baseUrl = System.getProperty("baseUrl", System.getenv("BASE_URL"));
    if (baseUrl == null || baseUrl.isEmpty()) {
      // Use centralized config instead of hardcoded value
      baseUrl = EnvironmentConfig.getFrontendUrl();
    }
    Configuration.baseUrl = baseUrl;

    homePage = new HomePage();
    LOGGER.info("HomePage test setup complete. Base URL: {}", Configuration.baseUrl);
  }

  @Test
  @Story("HomePage Display")
  @Description("Test that the home page loads")
  public void testHomePageLoads() {
    LOGGER.info(">>> Test: HomePage Loads");

    homePage.navigate();

    // Verify the page title in the browser tab contains "Job Search Application"
    String pageTitle = com.codeborne.selenide.Selenide.title();
    Assert.assertTrue(
        pageTitle.toLowerCase(Locale.ROOT).contains("job search application"),
        "Page title should contain 'Job Search Application', but was: " + pageTitle);

    LOGGER.info("✅ HomePage loaded. Title: {}", pageTitle);
  }

  @Test
  @Story("HomePage Navigation")
  @Description("Test that the navigation panel is visible")
  public void testNavigationPanel() {
    LOGGER.info(">>> Test: Navigation Panel");

    homePage.navigate();

    // Wait for page to load completely
    try {
      Thread.sleep(2000); // Give page time to render
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Verify navigation panel elements are visible (with extended timeout)
    // Sidebar may be hidden on small screens, so check if it exists first
    SelenideElement sidebar = homePage.getSidebar();
    sidebar.shouldBe(visible, Duration.ofSeconds(5));

    SelenideElement navTitleElement = homePage.getNavigationTitle();
    navTitleElement.shouldBe(visible, Duration.ofSeconds(5));
    String navTitle = navTitleElement.getText();
    Assert.assertTrue(
        navTitle.contains("Navigation"),
        "Navigation title should contain 'Navigation', but was: " + navTitle);

    homePage.getSidebarNavigation().shouldBe(visible, Duration.ofSeconds(5));

    LOGGER.info("✅ Navigation panel is visible");
  }
}
