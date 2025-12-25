package com.cjs.qa.oracle.api.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.api.WebService;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Encoder;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

public class SSOService extends WebService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SSOService.class));
  private final String baseAPISSO =
      "https://SSOAPI."
          + OracleConstants.API_BASE
          + ".net/"
          + Environment.getEnvironment()
          + "/"
          + OracleConstants.API_SSO_VERSION_MAJOR
          + "/"
          + OracleConstants.API_SSO_VERSION_MINOR;
  private static final Encoder ENCODER = new Encoder("");

  /**
   * @param userName
   * @param eMail
   * @param applicationAbbreviation
   * @return
   */
  public String apiAddProject(String userName, String eMail, String applicationAbbreviation) {
    Map<String, String> map = new HashMap<>();
    if (userName == null || userName.isEmpty()) {
      userName = eMail.substring(0, eMail.indexOf("@"));
    }
    Map<String, String> oAuthenticate = new HashMap<>();
    oAuthenticate = authenticate(eMail);
    final String ssoUserTokenId = oAuthenticate.get("ssoUserTokenId");
    final String partyID = oAuthenticate.get("partyID");
    map = createAccount(ssoUserTokenId, partyID, applicationAbbreviation, userName);
    return map.get("JSON");
  }

  /**
   * @param eMail
   * @return
   */
  public Map<String, String> authenticate(String eMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Email: "
            + Constants.QUOTE_DOUBLE
            + eMail
            + Constants.QUOTE_DOUBLE
            + ","
            + "Password: "
            + Constants.QUOTE_DOUBLE
            + "Password1!"
            + Constants.QUOTE_DOUBLE
            + "}";
    final String requestURL = baseAPISSO + "/Auth/";
    final String ssoUserTokenId = "";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    try {
      map.put(
          "ssoUserTokenId", new JSONObject(json).getJSONObject("body").getString("ssoUserTokenId"));
      map.put("partyID", new JSONObject(json).getJSONObject("body").getString("partyId"));
    } catch (final JSONException e) {
      e.printStackTrace();
    }
    return map;
  }

  /**
   * @param eMail
   * @return
   */
  public String confirmUser(String eMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Email: " + Constants.QUOTE_DOUBLE + eMail + Constants.QUOTE_DOUBLE + "}";
    final String requestURL = baseAPISSO + "/User/Confirm/";
    final String ssoUserTokenId = "";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    return json;
  }

  public String createEmailExpirationToken(String eMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final UUID guid = UUID.randomUUID();
    final String emailToken = guid.toString();
    final String apiRequest =
        "{ETID: "
            + Constants.QUOTE_DOUBLE
            + eMail
            + Constants.QUOTE_DOUBLE
            + ","
            + "Token: "
            + Constants.QUOTE_DOUBLE
            + emailToken
            + Constants.QUOTE_DOUBLE
            + "}";
    final String requestURL = baseAPISSO + "/EmailExpiration/";
    final String ssoUserTokenId = "";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    return emailToken;
  }

  public void resetPassword(String eMail) {
    final String emailToken = createEmailExpirationToken(eMail);
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{EmailToken: "
            + Constants.QUOTE_DOUBLE
            + emailToken
            + Constants.QUOTE_DOUBLE
            + ","
            + "ETID: "
            + Constants.QUOTE_DOUBLE
            + eMail
            + Constants.QUOTE_DOUBLE
            + ","
            + "NewPassword: "
            + Constants.QUOTE_DOUBLE
            + CJSConstants.PASSWORD
            + Constants.QUOTE_DOUBLE
            + "}";
    final String requestURL = baseAPISSO + "/Auth/ResetPassword";
    final String ssoUserTokenId = "";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
  }

  /**
   * @param ssoUserTokenId
   * @param applicationAbbreviation
   * @param password
   * @param userName
   * @return
   */
  public Map<String, String> createAccount(
      String ssoUserTokenId, String partyID, String applicationAbbreviation, String userName) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{"
            + Constants.QUOTE_DOUBLE
            + "ApplicationAbbreviation"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + applicationAbbreviation
            + Constants.QUOTE_DOUBLE
            + ","
            + Constants.QUOTE_DOUBLE
            + "Password"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + CJSConstants.PASSWORD
            + Constants.QUOTE_DOUBLE
            + ", "
            + Constants.QUOTE_DOUBLE
            + "Username"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + userName
            + Constants.QUOTE_DOUBLE
            + ",},";
    final String requestURL = baseAPISSO + "/User/" + partyID + "/Accounts/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    if (!json.contains(OracleConstants.API_JSON_SUCCESS)) {
      LOG.debug(json);
    }
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param eMail
   * @return
   */
  public Map<String, String> deleteSecurityQuestions(String ssoUserTokenId, String eMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{"
            + Constants.QUOTE_DOUBLE
            + "ETID"
            + Constants.QUOTE_DOUBLE
            + ":"
            + Constants.QUOTE_DOUBLE
            + eMail
            + Constants.QUOTE_DOUBLE
            + ",},";
    final String requestURL = baseAPISSO + "/Auth/" + ssoUserTokenId + "/SecurityQuestions/";
    final String json = getAPIJSONResponse("DELETE", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    if (!json.contains(OracleConstants.API_JSON_SUCCESS)) {
      LOG.debug(json);
    }
    return map;
  }

  /**
   * @param eMail
   * @return
   */
  public Map<String, String> forgotPassword(String eMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String requestURL = baseAPISSO + "/Auth/ForgotPassword" + "?emailAddress=" + eMail;
    String json = "";
    String line = "s";
    try {
      final URL oURL = URI.create(requestURL).toURL();
      final HttpURLConnection oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
      oHttpURLConnection.setRequestMethod("GET");
      final String responseCode = String.valueOf(oHttpURLConnection.getResponseCode());
      final String responseMessage = String.valueOf(oHttpURLConnection.getResponseMessage());
      try (BufferedReader oBufferedReader =
          new BufferedReader(new InputStreamReader(oHttpURLConnection.getInputStream()))) {
        while ((line = oBufferedReader.readLine()) != null) {
          json += line;
        }
      }
      if (!"200".equals(responseCode)) {
        LOG.warn("Response Code: {}", responseCode);
        LOG.warn("Response Message: {}", responseMessage);
      }
      oHttpURLConnection.disconnect();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    map.put("JSON", json);
    if (!json.contains(OracleConstants.API_JSON_SUCCESS)) {
      LOG.debug(json);
    }
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param etid
   * @param lockType
   * @return
   */
  // public Map<String, String> lock(String ssoUserTokenId, String etid, int
  // lockType) {
  public Map<String, String> lock(String etid, int lockType) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String ssoUserTokenId = authenticate(etid).get("ssoUserTokenId");
    final String apiRequest =
        "{"
            + Constants.QUOTE_DOUBLE
            + "ETID"
            + Constants.QUOTE_DOUBLE
            + ":"
            + Constants.QUOTE_DOUBLE
            + etid
            + Constants.QUOTE_DOUBLE
            + ", "
            + Constants.QUOTE_DOUBLE
            + "LockType"
            + Constants.QUOTE_DOUBLE
            + ":"
            + lockType
            + "},";
    final String requestURL = baseAPISSO + "/Lock/";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    if (!json.contains(OracleConstants.API_JSON_SUCCESS)) {
      LOG.debug(json);
    }
    return map;
  }

  /**
   * Registers a uniquely generated new user without security questions.
   *
   * @param confirmUser Set to "true" to confirm the user.
   * @return New user's email address.
   */
  public String registerUser(boolean confirmUser) {
    return registerUser(confirmUser, false);
  }

  /**
   * Registers a uniquely generated new user with options to confirm the user and add security
   * questions.
   *
   * @param confirmUser
   * @param addSecurityQuestions
   * @return
   */
  public String registerUser(boolean confirmUser, boolean addSecurityQuestions) {
    return registerUser(confirmUser, addSecurityQuestions, true);
  }

  /**
   * Registers a uniquely generated user.
   *
   * @param confirmUser Set to true to confirm user.
   * @param addSecurityQuestions Set to true to add security questions
   * @param addNames Set to true to add names to user.
   * @return New user's email address.
   */
  public String registerUser(boolean confirmUser, boolean addSecurityQuestions, boolean addNames) {
    return registerUser(confirmUser, addSecurityQuestions, addNames, false);
  }

  /**
   * @param confirmUser
   * @param addSecurityQuestions
   * @param addNames
   * @param addAlternateEMail
   * @return
   */
  public String registerUser(
      boolean confirmUser,
      boolean addSecurityQuestions,
      boolean addNames,
      boolean addAlternateEMail) {
    return registerUser("", confirmUser, addSecurityQuestions, addNames, addAlternateEMail);
  }

  /**
   * @param name
   * @param confirmUser
   * @param addSecurityQuestions
   * @param addNames
   * @param addAlternateEMail
   * @return
   */
  public String registerUser(
      String name,
      boolean confirmUser,
      boolean addSecurityQuestions,
      boolean addNames,
      boolean addAlternateEMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    if (name.isEmpty()) {
      name = ENCODER.getEncodedValue("");
    }
    final String eMail = name + CJSConstants.MAILDOMAIN_GMAIL;
    LOG.debug(
        "Registering:["
            + eMail
            + "] (confirmUser:["
            + confirmUser
            + "], addSecurityQuestions:["
            + addSecurityQuestions
            + "], addNames:["
            + addNames
            + "], addAlternateEMail:["
            + addAlternateEMail
            + "]");
    final String requestURL = baseAPISSO + "/User/";
    String apiRequest =
        "{Password: "
            + Constants.QUOTE_DOUBLE
            + CJSConstants.PASSWORD
            + Constants.QUOTE_DOUBLE
            + ",PrimaryEmail: "
            + Constants.QUOTE_DOUBLE
            + eMail
            + Constants.QUOTE_DOUBLE
            + "}";
    if (addSecurityQuestions) {
      final String toAdd =
          ","
              + Constants.QUOTE_DOUBLE
              + "QuestionsAnswers"
              + Constants.QUOTE_DOUBLE
              + ":[{"
              + Constants.QUOTE_DOUBLE
              + "SecurityAnswer"
              + Constants.QUOTE_DOUBLE
              + ":"
              + Constants.QUOTE_DOUBLE
              + "abcde"
              + Constants.QUOTE_DOUBLE
              + ","
              + Constants.QUOTE_DOUBLE
              + "SecurityQuestionId"
              + Constants.QUOTE_DOUBLE
              + ":2},{"
              + Constants.QUOTE_DOUBLE
              + "SecurityAnswer"
              + Constants.QUOTE_DOUBLE
              + ":"
              + Constants.QUOTE_DOUBLE
              + "abcde"
              + Constants.QUOTE_DOUBLE
              + ","
              + Constants.QUOTE_DOUBLE
              + "SecurityQuestionId"
              + Constants.QUOTE_DOUBLE
              + ":7},{"
              + Constants.QUOTE_DOUBLE
              + "SecurityAnswer"
              + Constants.QUOTE_DOUBLE
              + ":"
              + Constants.QUOTE_DOUBLE
              + "abcde"
              + Constants.QUOTE_DOUBLE
              + ","
              + Constants.QUOTE_DOUBLE
              + "SecurityQuestionId"
              + Constants.QUOTE_DOUBLE
              + ":11}]}";
      apiRequest = apiRequest.replace(apiRequest.substring(apiRequest.length() - 1), toAdd);
    }
    if (addNames) {
      final String toAdd =
          ", "
              + Constants.QUOTE_DOUBLE
              + "FirstName"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + OracleDynamicVariables.getFirstName(name)
              + Constants.QUOTE_DOUBLE
              + ","
              + Constants.QUOTE_DOUBLE
              + "LastName"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + OracleDynamicVariables.getLastName(name)
              + Constants.QUOTE_DOUBLE
              + "}";
      apiRequest = apiRequest.replace(apiRequest.substring(apiRequest.length() - 1), toAdd);
    }
    if (addAlternateEMail) {
      final String toAdd =
          ", "
              + Constants.QUOTE_DOUBLE
              + "AlternateEmail"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + "Alt_"
              + eMail
              + Constants.QUOTE_DOUBLE
              + "}";
      apiRequest = apiRequest.replace(apiRequest.substring(apiRequest.length() - 1), toAdd);
    }
    final String ssoUserTokenId = "";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    if (confirmUser) {
      final String jsonConfirmUser = confirmUser(eMail);
      if (!jsonConfirmUser.contains(OracleConstants.API_JSON_SUCCESS)) {
        LOG.debug(jsonConfirmUser);
      }
    }
    return eMail;
  }

  /**
   * @param count
   */
  public void registerUsers(int count) {
    for (int i = 1; i <= count; i++) {
      registerUser(true);
    }
  }

  /**
   * @param ssoUserTokenId
   * @param eMail
   * @return
   */
  public Map<String, String> securityQuestions(String ssoUserTokenId, String eMail) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{"
            + Constants.QUOTE_DOUBLE
            + "ETID"
            + Constants.QUOTE_DOUBLE
            + ":"
            + Constants.QUOTE_DOUBLE
            + eMail
            + Constants.QUOTE_DOUBLE
            + "}";
    final String requestURL = baseAPISSO + "/Auth/" + ssoUserTokenId + "/SecurityQuestions/";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    if (!json.contains(OracleConstants.API_JSON_SUCCESS)) {
      LOG.debug(json);
    }
    return map;
  }
}
