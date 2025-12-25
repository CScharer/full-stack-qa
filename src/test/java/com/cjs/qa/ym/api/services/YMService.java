package com.cjs.qa.ym.api.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EAPIKeys;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.IHTTP;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.ym.api.namespace.SessionNamespace;

public class YMService {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(YMService.class));

  // API credentials migrated to Google Cloud Secret Manager via EAPIKeys enum
  // See: docs/issues/open/hardcoded-api-keys-and-secrets.md
  public static String getApiKeyValue() {
    return EAPIKeys.VIVIT_YM_API_KEY.getValue();
  }

  public static String getApiSaPasscodeValue() {
    return EAPIKeys.VIVIT_YM_API_SA_PASSCODE.getValue();
  }

  // 2.25
  public static final String API_VERSION = "2.30";
  public static final String API_HEADER =
      Constants.nlTab(1, 0) + XML.HEADING_INFO + Constants.nlTab(1, 0) + "<YourMembership>";
  public static final String API_FOOTER = Constants.nlTab(1, 0) + "</YourMembership>";
  public static final String URL_YM_API_BASE = "https://api.yourmembership" + IExtension.COM;
  // "/reference/2_25/";
  public static final String URL_YM_API_DOC = URL_YM_API_BASE + "/reference/2_30/";
  public static final String URL_YM = URL_YM_API_BASE;
  public static final String CONNECTED_TO = "Connection to [";
  private static final AtomicBoolean SERVICE_ACTIVE = new AtomicBoolean(false);
  private static int callID = 0;

  public YMService() {
    if (!SERVICE_ACTIVE.get()) {
      try {
        final HttpURLConnection httpUrlConnection =
            (HttpURLConnection) URI.create(URL_YM).toURL().openConnection();
        httpUrlConnection.setRequestMethod("HEAD");
        final int responseCode = httpUrlConnection.getResponseCode();
        if (!(responseCode >= HttpURLConnection.HTTP_OK
            && responseCode < HttpURLConnection.HTTP_BAD_REQUEST)) {
          LOG.warn(
              "{}{}] unsuccessful with response of [{}:{}].",
              CONNECTED_TO,
              URL_YM,
              responseCode,
              IHTTP.getResponseValue(responseCode));
          // Environment.addScenarioError(CONNECTED_TO + URL_YM + "]
          // unsuccessful with response of [" + responseCode + ":" +
          // IHTTP.getResponseValue(responseCode) + "].")
        }
        Assert.assertTrue(
            CONNECTED_TO
                + URL_YM
                + "] unsuccessful with response of ["
                + responseCode
                + ":"
                + IHTTP.getResponseValue(responseCode)
                + "].",
            responseCode >= HttpURLConnection.HTTP_OK
                && responseCode < HttpURLConnection.HTTP_BAD_REQUEST);
        LOG.debug(
            "{}{}] successfull with response of [{}:{}].",
            CONNECTED_TO,
            URL_YM,
            responseCode,
            IHTTP.getResponseValue(responseCode));
        SERVICE_ACTIVE.set(true); // Constructor sets service availability state
      } catch (final Exception e) {
        LOG.error("Exception occurred", e);
      }
    }
  }

  public static Map<String, String> getAPIXMLResponse(String requestMethod, String apiRequest)
      throws Throwable {
    final String request = API_HEADER + getRequiredHeaders() + apiRequest + API_FOOTER;
    final Map<String, String> map = new HashMap<>();
    map.put("requestMethod", requestMethod);
    map.put("url", URL_YM);
    map.put("apiRequest", request);
    if (Environment.isLogAPI()) {
      LOG.debug(
          "{} Parameters:{}{}requestMethod:[{}]{}{}URL_YM:[{}]{}{}request[{}]",
          JavaHelpers.getCurrentMethodName(),
          Constants.nlTab(1, 1),
          requestMethod,
          Constants.nlTab(1, 1),
          URL_YM,
          Constants.nlTab(1, 1),
          request);
    }
    if (request.contains("CallID")) {
      try {
        LOG.debug("CallID:[{}]", XML.getTag(request, "CallID"));
      } catch (Exception e) {
        // CallID logging is optional - continue without it
        if (Environment.isLogAll()) {
          LOG.debug("CallID not available");
        }
      }
    }
    int responseCode = -1;
    HttpURLConnection httpURLConnection = null;
    String xml = "";
    try {
      final URL oURL = URI.create(URL_YM).toURL();
      httpURLConnection = (HttpURLConnection) oURL.openConnection();
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setInstanceFollowRedirects(false);
      if (!requestMethod.isEmpty()) {
        httpURLConnection.setRequestMethod(requestMethod);
      }
      httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      httpURLConnection.setRequestProperty("Accept", "application/xml");
      httpURLConnection.setRequestProperty("charset", StandardCharsets.UTF_8.toString());
      httpURLConnection.setRequestProperty(
          "Content-Length", "" + Integer.toString(request.getBytes().length));
      httpURLConnection.setUseCaches(false);
      try (DataOutputStream dataOutputStream =
          new DataOutputStream(httpURLConnection.getOutputStream())) {
        dataOutputStream.writeBytes(request);
        dataOutputStream.flush();
      }
      responseCode = httpURLConnection.getResponseCode();
      map.put("responseCode", String.valueOf(responseCode));
      final String responseMessage = String.valueOf(httpURLConnection.getResponseMessage());
      map.put("responseMessage", responseMessage);
      if (responseCode == HttpURLConnection.HTTP_OK) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
          String line = "";
          while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
          }
        } catch (Exception e) {
          throw new QAException("BufferedReader", e);
        }
        xml = stringBuilder.toString();
        xml = XML.formatPretty(xml);
        map.put("xml", xml);
        // LOG.debug("xml:[{}]", xml);
      }
      if (responseCode != HttpURLConnection.HTTP_OK) {
        LOG.debug("responseCode:[" + responseCode + "]");
        LOG.debug("responseMessage:[" + responseMessage + "]");
      }
      if (!"0".equals(XML.getTag(xml, "ErrCode"))) {
        String message = JavaHelpers.getCallingMethodName() + ":" + XML.getTag(xml, "ErrDesc");
        throw new QAException(message);
      }
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    } finally {
      if (httpURLConnection != null) {
        httpURLConnection.disconnect();
      }
    }
    // LOG.debug("map:[{}]", map.toString());
    return map;
  }

  public static String getRequiredHeaders() throws Throwable {
    return getVersion() + getAPIKey() + getCallID();
  }

  public String getRequiredHeadersAdminTrue() throws Throwable {
    return getRequiredHeaders() + SessionNamespace.getSessionID();
  }

  public String getRequiredHeadersAdminFalse() throws Throwable {
    return getRequiredHeaders() + getSAPasscode() + SessionNamespace.getSessionID();
  }

  public static String getAPIKey() throws Throwable {
    return Constants.nlTab(1, 1) + "<ApiKey>" + getApiKeyValue() + "</ApiKey>";
  }

  public static String getCallID() throws Throwable {
    // SessionNamespace.callID++
    // final String callID = String.format("%03d", SessionNamespace.callID)
    callID++;
    final String callID = String.format("%03d", YMService.callID);
    LOG.debug("callID:[{}]", callID);
    return Constants.nlTab(1, 1) + "<CallID>" + callID + "</CallID>";
  }

  public static void resetCallID() throws Throwable {
    callID = 0;
  }

  public static String getSAPasscode() throws Throwable {
    return Constants.nlTab(1, 1) + "<SaPasscode>" + getApiSaPasscodeValue() + "</SaPasscode>";
  }

  public static String getVersion() throws Throwable {
    return Constants.nlTab(1, 1) + "<Version>" + API_VERSION + "</Version>";
  }
}
