package com.cjs.qa.gt.api.services;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.security.EAPIKeys;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.rest.REST;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.IHTTP;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class GTWebinarServiceTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(GTWebinarServiceTests.class));

  // Credentials migrated to Google Cloud Secret Manager
  // See: docs/issues/open/hardcoded-api-keys-and-secrets.md
  public static String getUserId() {
    return EAPIKeys.VIVIT_GT_WEBINAR_USER_ID.getValue();
  }

  public static String getPassword() {
    return EPasswords.VIVIT_GT_WEBINAR_PASSWORD.getValue();
  }

  public static String getApiConsumerKey() {
    return EAPIKeys.VIVIT_GT_WEBINAR_CONSUMER_KEY.getValue();
  }

  public static String getApiConsumerSecret() {
    return EAPIKeys.VIVIT_GT_WEBINAR_CONSUMER_SECRET.getValue();
  }

  public static final String API_VERSION = "v2";
  public static final String URL_GT = "https://api.getgo" + IExtension.COM + "/";
  public static final String API_GT_AUTH = URL_GT + "/oauth/" + API_VERSION;
  public static final String API_GT_BASE = URL_GT + "/G2W/rest/" + API_VERSION;
  public static final String CONNECTED_TO = "Connection to [";
  private static String accountKey = null;
  private static String accessToken = null;
  private static final AtomicBoolean SERVICE_ACTIVE = new AtomicBoolean(true); // false;

  // private static int callID = 0;

  @Test
  public void gtwWebinarServiceTest() {
    LOG.debug("Testing!");
  }

  public GTWebinarServiceTests() {
    if (!SERVICE_ACTIVE.get()) {
      try {
        final HttpURLConnection httpUrlConnection =
            (HttpURLConnection) URI.create(URL_GT).toURL().openConnection();
        httpUrlConnection.setRequestMethod("HEAD");
        final int responseCode = httpUrlConnection.getResponseCode();
        if (!(responseCode >= HttpURLConnection.HTTP_OK
            && responseCode < HttpURLConnection.HTTP_BAD_REQUEST)) {
          LOG.warn(
              "{}{}] unsuccessful with response of [{}:{}].",
              CONNECTED_TO,
              URL_GT,
              responseCode,
              IHTTP.getResponseValue(responseCode));
          // Environment.addScenarioError(CONNECTED_TO + URL_YM + "]
          // unsuccessful with response of [" + responseCode + ":" +
          // IHTTP.getResponseValue(responseCode) + "].")
        }
        Assertions.assertTrue(
            responseCode >= HttpURLConnection.HTTP_OK
                && responseCode < HttpURLConnection.HTTP_BAD_REQUEST,
            CONNECTED_TO
                + URL_GT
                + "] unsuccessful with response of ["
                + responseCode
                + ":"
                + IHTTP.getResponseValue(responseCode)
                + "].");
        LOG.debug(
            "{}{}] successfull with response of [{}:{}].",
            CONNECTED_TO,
            URL_GT,
            responseCode,
            IHTTP.getResponseValue(responseCode));
        SERVICE_ACTIVE.set(true); // Constructor sets service availability state
      } catch (final Exception e) {
        LOG.error("Exception occurred", e);
      }
    }
  }

  public static Map<String, String> getAPIJSONResponse(
      String credentials, String requestMethod, String apiRequest, String url) throws Throwable {
    return REST.getAPIJSONResponse(credentials, requestMethod, apiRequest, url);
  }

  public static String getAPIKey() throws Throwable {
    return Constants.nlTab(1, 1) + "<ApiKey>" + getApiConsumerKey() + "</ApiKey>";
  }

  public static String getAccessToken() {
    return accessToken;
  }

  public static void setAccessToken(String accessToken) {
    GTWebinarServiceTests.accessToken = accessToken;
  }

  public static String getAccountKey() {
    return accountKey;
  }

  public static void setAccountKey(String accountKey) {
    GTWebinarServiceTests.accountKey = accountKey;
  }
}
