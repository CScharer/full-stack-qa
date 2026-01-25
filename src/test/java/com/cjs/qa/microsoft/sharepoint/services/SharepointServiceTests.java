package com.cjs.qa.microsoft.sharepoint.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IHTTP;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.VivitEnvironment;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class SharepointServiceTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SharepointServiceTests.class));

  public static final String URL_MICROSOFT_API_BASE =
      "https://login.microsoftonline.com/extSTS.srf";
  public static final String URL_MICROSOFT = URL_MICROSOFT_API_BASE;
  public static final String URL_SHAREPOINT_API_BASE =
      "https://login.microsoftonline.com/extSTS.srf";
  public static final String URL_SHAREPOINT = URL_SHAREPOINT_API_BASE;
  public static final String URL_SIGNIN =
      "https://vivitworldwide.sharepoint.com/_forms/default.aspx?wa=wsignin1.0";
  public static final String CONNECTED_TO = "Connection to [";

  public SharepointServiceTests() {
    try {
      final HttpURLConnection httpUrlConnection =
          (HttpURLConnection) URI.create(URL_SHAREPOINT).toURL().openConnection();
      httpUrlConnection.setRequestMethod("HEAD");
      final int responseCode = httpUrlConnection.getResponseCode();
      if (!(responseCode >= HttpURLConnection.HTTP_OK
          && responseCode < HttpURLConnection.HTTP_BAD_REQUEST)) {
        LOG.warn(
            "{}] unsuccessful with response of [{}:{}].",
            CONNECTED_TO + URL_SHAREPOINT,
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
              + URL_SHAREPOINT
              + "] unsuccessful with response of ["
              + responseCode
              + ":"
              + IHTTP.getResponseValue(responseCode)
              + "].");
      LOG.debug(
          "{}] successfull with response of [{}:{}].",
          CONNECTED_TO + URL_SHAREPOINT,
          responseCode,
          IHTTP.getResponseValue(responseCode));
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
  }

  @Test
  public void sharepointServiceTest() throws Throwable {
    String microsoftLoginRequest =
        getMicrosoftLoginRequest(VivitDataTests.getEmailFrom(), VivitDataTests.getEmailPassword());
    LOG.debug("microsoftLoginRequest:[{}]", microsoftLoginRequest);
    Map<String, String> resultsMap =
        getAPIXMLResponse("POST", microsoftLoginRequest, URL_MICROSOFT);
    String xml = resultsMap.get("xml");
    xml = XML.formatPretty(xml);
    LOG.debug("xml:[{}]", xml);
    String binarySecurityToken = XML.getTag(xml, "wsse:BinarySecurityToken");
    LOG.debug("binarySecurityToken:[[{}]", binarySecurityToken + "]");
    resultsMap = getAPIXMLResponse("POST", binarySecurityToken, URL_SIGNIN);
    xml = resultsMap.get("xml");
    xml = XML.formatPretty(xml);
    LOG.debug("xml:[{}]", xml);
  }

  public static Map<String, String> getAPIXMLResponse(
      String requestMethod, String apiRequest, String url) throws QAException {
    // final String request = API_HEADER + getRequiredHeaders() + apiRequest
    // + API_FOOTER
    final String request = apiRequest;
    final Map<String, String> map = new HashMap<>();
    map.put("requestMethod", requestMethod);
    map.put("url", url);
    map.put("apiRequest", request);
    if (VivitEnvironment.isLogAPI()) {
      LOG.debug(
          "{} Parameters:{}\nrequestMethod:[{}]{}\nurl:[{}]{}\nrequest[{}]",
          JavaHelpers.getCurrentMethodName(),
          Constants.nlTab(1, 1),
          requestMethod,
          Constants.nlTab(1, 1),
          url,
          Constants.nlTab(1, 1),
          request);
    }
    int responseCode = -1;
    HttpURLConnection httpURLConnection = null;
    try {
      final URL oURL = URI.create(url).toURL();
      httpURLConnection = (HttpURLConnection) oURL.openConnection();
      httpURLConnection.setDoOutput(true);
      // httpURLConnection.setInstanceFollowRedirects(false);
      httpURLConnection.setInstanceFollowRedirects(true);
      if (!requestMethod.isEmpty()) {
        httpURLConnection.setRequestMethod(requestMethod);
      }
      httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      // httpURLConnection.setRequestProperty("Content-Type",
      // "application/xml");
      // httpURLConnection.setRequestProperty("Accept", "application/json;
      // odata-verbose");
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
        map.put("xml", stringBuilder.toString());
        // LOG.debug("xml:[{}]", xml);
      }
      if (responseCode != HttpURLConnection.HTTP_OK) {
        LOG.debug("responseCode:[[{}]", responseCode + "]");
        LOG.debug("responseMessage:[[{}]", responseMessage + "]");
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

  private String getMicrosoftLoginRequest(String userName, String password) throws Throwable {
    final String nl = "\n";
    final String nlTab = nl + "\t";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"");
    stringBuilder.append(nlTab + "xmlns:a=\"http://www.w3.org/2005/08/addressing\"");
    stringBuilder.append(
        nlTab
            + "xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">");
    stringBuilder.append(nlTab + "<s:Header>");
    stringBuilder.append(nlTab + "<a:Action");
    stringBuilder.append(
        nlTab
            + "s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>");
    stringBuilder.append(nlTab + "<a:ReplyTo>");
    stringBuilder.append(
        nlTab + "<a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address>");
    stringBuilder.append(nlTab + "</a:ReplyTo>");
    stringBuilder.append(nlTab + "<a:To");
    stringBuilder.append(nlTab + "s:mustUnderstand=\"1\">" + URL_MICROSOFT_API_BASE + "</a:To>");
    stringBuilder.append(nlTab + "<o:Security s:mustUnderstand=\"1\"");
    stringBuilder.append(
        nlTab
            + "xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">");
    stringBuilder.append(nlTab + "<o:UsernameToken>");
    stringBuilder.append(nlTab + "<o:Username>" + userName + "</o:Username>");
    stringBuilder.append(nlTab + "<o:Password>" + password + "</o:Password>");
    stringBuilder.append(nlTab + "</o:UsernameToken>");
    stringBuilder.append(nlTab + "</o:Security>");
    stringBuilder.append(nlTab + "</s:Header>");
    stringBuilder.append(nlTab + "<s:Body>");
    stringBuilder.append(nlTab + "<t:RequestSecurityToken");
    stringBuilder.append(nlTab + "xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">");
    stringBuilder.append(nlTab + "<wsp:AppliesTo");
    stringBuilder.append(nlTab + "xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">");
    stringBuilder.append(nlTab + "<a:EndpointReference>");
    stringBuilder.append(
        nlTab + "<a:Address>https://vivitworldwide.sharepoint.com/SitePages/Home.aspx</a:Address>");
    stringBuilder.append(nlTab + "</a:EndpointReference>");
    stringBuilder.append(nlTab + "</wsp:AppliesTo>");
    stringBuilder.append(
        nlTab + "<t:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</t:KeyType>");
    stringBuilder.append(
        nlTab + "<t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType>");
    stringBuilder.append(
        nlTab + "<t:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType>");
    stringBuilder.append(nlTab + "</t:RequestSecurityToken>");
    stringBuilder.append(nlTab + "</s:Body>");
    stringBuilder.append(nl + "</s:Envelope>");
    return stringBuilder.toString();
  }
}
