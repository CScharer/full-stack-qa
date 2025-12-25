package com.cjs.qa.saucelabs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.saucelabs.saucerest.DataCenter;
import com.saucelabs.saucerest.SauceREST;
import com.saucelabs.saucerest.api.JobsEndpoint;
import com.saucelabs.saucerest.model.jobs.UpdateJobParameter;

public final class SauceUtils {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SauceUtils.class));

  public static final String DEFAULT_HOST = "sauceconnect.berkley-bts" + IExtension.COM;
  public static final String DEFAULT_PORT = "4445";
  public static final String DEFAULT_TUNNEL_IDENTIFIER = "prodSLTunnel";
  public static final String DEFAULT_PARENT_TUNNEL = "berkley-technology-services";
  public static final String DEFAULT_USER_NAME = "AUTO_SAUCELABS_USERNAME";
  public static final String DEFAULT_ACCESS_KEY = "AUTO_SAUCELABS_ACCESS_KEY";
  private static SauceREST sauceRESTClient;

  private SauceUtils() {}

  private static synchronized SauceREST getSauceRestClient(String userName, String accessKey) {
    if (sauceRESTClient == null) {
      // SauceREST 2.x requires DataCenter parameter
      // Default to US_EAST for midwest locations, but allow override via SAUCE_DATACENTER env var
      // or system property
      String dataCenterStr = JavaHelpers.readPropertyOrEnv("SAUCE_DATACENTER", "US_EAST");
      DataCenter dataCenter;
      try {
        dataCenter = DataCenter.fromString(dataCenterStr);
        LOG.debug("Using Sauce Labs DataCenter: [{}]", dataCenterStr);
      } catch (IllegalArgumentException e) {
        // If US_EAST is not available in this version, fall back to US_WEST
        LOG.debug("DataCenter [{}]", dataCenterStr + " not available, falling back to US_WEST");
        dataCenter = DataCenter.US_WEST;
      }
      sauceRESTClient = new SauceREST(userName, accessKey, dataCenter);
    }
    return sauceRESTClient;
  }

  public static void updateResults(
      String userName, String accessKey, boolean testResults, String sessionId) {
    final SauceREST sauceREST = getSauceRestClient(userName, accessKey);
    // SauceREST 2.x API: Use JobsEndpoint to update job info
    JobsEndpoint jobsEndpoint = sauceREST.getJobsEndpoint();
    UpdateJobParameter.Builder builder = new UpdateJobParameter.Builder();
    builder.setPassed(testResults);
    // Add build number if available
    String buildNumber = getBuildNumber();
    if (buildNumber != null && !buildNumber.isEmpty()) {
      builder.setBuild(buildNumber);
    }
    try {
      jobsEndpoint.updateJob(sessionId, builder.build());
    } catch (java.io.IOException e) {
      throw new RuntimeException("Failed to update Sauce Labs job: " + sessionId, e);
    }
  }

  private static String getBuildNumber() {
    for (Entry<String, String> entry : getBuildNumerKeyValues().entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      String buildNumber = JavaHelpers.readPropertyOrEnv(value, null);
      LOG.debug("Key:[{}], Value:[{}], buildNumber:[{}]", key, value, buildNumber);
      if (JavaHelpers.hasValue(buildNumber)) {
        return buildNumber;
      }
    }
    return null;
  }

  /**
   * Gets the build number from system property/environment variable with the following name:
   *
   * <ol>
   *   <li>SAUCE_BAMBOO_BUILDNUMBER
   *   <li>JENKINS_BUILD_NUMBER
   *   <li>BUILD_TAG
   *   <li>BUILD_NUMBER
   *   <li>TRAVIS_BUILD_NUMBER
   *   <li>CIRCLE_BUILD_NUM
   * </ol>
   *
   * @return build number string or null if not found
   */
  private static Map<String, String> getBuildNumerKeyValues() {
    // Defaults
    final Map<String, String> map = new HashMap<>();
    map.put("Bamboo", "SAUCE_BAMBOO_BUILDNUMBER");
    map.put("Jenkins", "JENKINS_BUILD_NUMBER");
    map.put("BUILD_TAG", "BUILD_TAG");
    map.put("BUILD_NUMBER", "BUILD_NUMBER");
    map.put("TRAVIS_BUILD_NUMBER", "TRAVIS_BUILD_NUMBER");
    map.put("CIRCLE_BUILD_NUM", "CIRCLE_BUILD_NUM");
    return map;
  }

  private static Map<String, Object> getDesiredCapabilitiesDefaults() {
    // Defaults
    final Map<String, Object> map = new HashMap<>();
    map.put("browser", "chrome");
    map.put("forkCount", 1);
    // Selenium-Specific Options
    map.put("browserName", "chrome");
    map.put("version", "latest");
    map.put("platform", "WINDOWS");
    // Other Selenium Options
    // map.put("seleniumVersion", "")
    // map.put("chromedriverVersion", "")
    // map.put("iedriverVersion", "")
    // General Options
    // Alerts
    map.put("autoAcceptAlerts", true);
    // Test Annotation
    map.put("name", "");
    map.put("build", "");
    map.put("tags", "");
    // map.put("passed", "")
    // map.put("custom-data", "{\"release\": \"1.0\", \"commit\":
    // \"0k392a9dkjr\", \"staging\": true,\"execution_number\":
    // 5,\"server\": \"server.com\"}")
    // Timeouts
    map.put("maxDuration", 10800);
    map.put("commandTimeout", 300);
    map.put("idleTimeout", 1000);
    // Sauce Testing Options
    map.put("host", DEFAULT_HOST);
    map.put("port", DEFAULT_PORT);
    map.put("userName", DEFAULT_USER_NAME);
    map.put("accessKey", DEFAULT_ACCESS_KEY);
    map.put("tunnelIdentifier", DEFAULT_TUNNEL_IDENTIFIER);
    map.put("parentTunnel", DEFAULT_PARENT_TUNNEL);
    map.put("screenResolution", "2560x1600"); // 1280x960
    map.put("timeZone", "Chicago");
    // map.put("avoidProxy", true)
    // Optional Sauce Testing Features
    // Improves Performance
    map.put("recordVideo", false);
    map.put("videoUploadOnPass", false);
    map.put("recordScreenshots", false);
    map.put("recordLogs", false);
    map.put("webdriver.remote.quietExceptions", false);
    map.put("extendedDebugging", false);
    return map;
  }

  public static DesiredCapabilities setDesiredCapabilitiesAutoIT(
      DesiredCapabilities desiredCapabilities, String executable, boolean background) {
    executable = "authenticate.exe";
    background = true;
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("executable", executable);
    jsonObject.put("background", String.valueOf(background));
    desiredCapabilities.setCapability("prerun", jsonObject);
    return desiredCapabilities;
  }

  public static DesiredCapabilities setDesiredCapabilitiesDefaults(
      DesiredCapabilities desiredCapabilities) {
    final Map<String, Object> propertiesDefault = getDesiredCapabilitiesDefaults();
    final Map<String, Object> propertiesDefined = new HashMap<>();
    final Map<String, Object> propertiesUndefined = new HashMap<>();
    for (final Entry<String, Object> entry : propertiesDefault.entrySet()) {
      final String setting = entry.getKey();
      final Object propertyDefined = System.getProperty(setting);
      if (propertyDefined != null) {
        propertiesDefined.put(setting, propertyDefined);
      } else {
        propertiesUndefined.put(setting, entry.getValue());
      }
    }
    LOG.debug("Defined Properties ({})", propertiesDefined.size());
    for (final Entry<String, Object> entry : propertiesDefined.entrySet()) {
      final String setting = entry.getKey();
      final Object propertyDefined = System.getProperty(setting);
      LOG.debug("***Defined***-[{}]", setting + ":[" + propertyDefined + "]");
      desiredCapabilities.setCapability(setting, propertyDefined);
    }
    LOG.debug("Undefined Properties ({})", propertiesUndefined.size());
    for (final Entry<String, Object> entry : propertiesUndefined.entrySet()) {
      final String setting = entry.getKey();
      final Object propertyDefault = propertiesDefault.get(setting);
      LOG.debug("***Undefined***-[{}]", setting + ":[" + propertyDefault + "]");
      System.setProperty(setting, String.valueOf(propertyDefault));
      desiredCapabilities.setCapability(setting, propertyDefault);
    }
    return desiredCapabilities;
  }
}
