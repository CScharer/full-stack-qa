package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JSON;

public class AuthNamespace extends GTWebinarServiceTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(AuthNamespace.class));

  public AuthNamespace() throws Throwable {
    setHeader(null);
  }

  private String header = null;

  public Map<String, String> authenticate(String credentials) throws Throwable {
    String url = API_GT_BASE + "/";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(0, 0) + "{");
    stringBuilder.append(Constants.nlTab(1, 0) + "}");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }

  public Map<String, String> oauth(String credentials) throws Throwable {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(0, 0) + "{");
    stringBuilder.append(Constants.nlTab(1, 1) + "\"grant_type\":\"" + "password" + "\",");
    stringBuilder.append(Constants.nlTab(1, 1) + "\"user_id\":\"" + getUserId() + "\",");
    stringBuilder.append(Constants.nlTab(1, 1) + "\"password\":\"" + getPassword() + "\",");
    stringBuilder.append(Constants.nlTab(1, 1) + "\"client_id\":\"" + getApiConsumerKey() + "\"");
    stringBuilder.append(Constants.nlTab(1, 0) + "}");
    // Map<String, String> mapResponse = getAPIJSONResponse(credentials,
    // "POST", stringBuilder.toString(),
    // url);
    StringBuilder curlStringBuilder = new StringBuilder();
    curlStringBuilder.append("curl ");
    curlStringBuilder.append("-X POST ");
    curlStringBuilder.append("-H \"Accept:application/json\" ");
    curlStringBuilder.append("-H \"Content-Type: application/x-www-form-urlencoded\" ");
    curlStringBuilder.append("\"https://api.getgo.com/oauth/access_token\" ");
    curlStringBuilder.append("-d \"grant_type=\"password\"");
    curlStringBuilder.append("&user_id=\"" + getUserId() + "\"");
    curlStringBuilder.append("&password=\"" + getPassword() + "\"");
    curlStringBuilder.append("&client_id=\"" + getApiConsumerKey() + "\"");
    String command = "cmd /C " + curlStringBuilder.toString();
    LOG.debug("command:[" + command + "]");
    final Map<String, String> mapResponse = CommandLineTests.runProcess(command, true);
    String json = mapResponse.get("lines");
    json = JSON.formatPretty(json, 4);
    LOG.debug("json Response:[" + Constants.NEWLINE + json + "]");
    final JSONObject jsonObject = new JSONObject(json);
    final String accessToken = jsonObject.getString("access_token");
    final String refreshToken = jsonObject.getString("refresh_token");
    final String organizerKey = jsonObject.getString("organizer_key");
    final String accountKey = jsonObject.getString("account_key");
    final String accountType = jsonObject.getString("account_type");
    LOG.debug(
        "accessToken:["
            + accessToken
            + "], refreshToken:["
            + refreshToken
            + "], organizerKey:["
            + organizerKey
            + "], accountKey:["
            + accountKey
            + "], accountType:["
            + accountType
            + "], ");
    setAccessToken(accessToken);
    setAccountKey(accountKey);
    return mapResponse;
  }

  public String getHeader() {
    return this.header;
  }

  public void setHeader(String header) {
    this.header = header;
  }
}
