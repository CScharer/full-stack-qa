package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JSON;

public class WebinarsNamespace extends GTWebinarServiceTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(WebinarsNamespace.class));

  public Map<String, String> cancelWebinar(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "DELETE", stringBuilder.toString(), url);
  }

  public Map<String, String> createWebinar(String credentials, String organizerKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }

  public Map<String, String> getAllInsessionWebinars(String credentials, String organizerKey)
      throws Throwable {
    // /organizers/{organizerKey}/insessionWebinars
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/insessionWebinars";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getAllWebinarsForAnAccount(String credentials, String accountKey)
      throws Throwable {
    // /accounts/{accountKey}/webinars
    final String url = API_GT_BASE + "/accounts/" + accountKey + "/webinars";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    // return getAPIJSONResponse(credentials, "GET",
    // stringBuilder.toString(), url);
    StringBuilder curlStringBuilder = new StringBuilder();
    curlStringBuilder.append("curl ");
    curlStringBuilder.append("-X POST ");
    curlStringBuilder.append("-H \"Accept:application/json\" ");
    curlStringBuilder.append("-H \"Content-Type: application/x-www-form-urlencoded\" ");
    curlStringBuilder.append("\"" + url + "\" ");
    curlStringBuilder.append("-d \"Authorization=\"" + getAccessToken() + "\"");
    // curlStringBuilder.append("&accountKey=\"" + getAccountKey() + "\"");
    String formatDateLeft = "yyyy-MM-dd";
    String formatDateRight = "HH:mm:ss";
    curlStringBuilder.append(
        "&fromTime=\""
            + DateHelpersTests.getCurrentDateTime(formatDateLeft)
            + "T"
            + DateHelpersTests.getCurrentDateTime(formatDateRight)
            + "Z\"");
    curlStringBuilder.append(
        "&toTime=\""
            + DateHelpersTests.getCurrentDatePlusMinusDays(formatDateLeft, 365)
            + "T"
            + DateHelpersTests.getCurrentDatePlusMinusDays(formatDateRight, 365)
            + "Z\"");
    String command = "cmd /C " + curlStringBuilder.toString();
    LOG.debug("command:[" + command + "]");
    Map<String, String> mapResponse = CommandLineTests.runProcess(command, true);
    String json = mapResponse.get("lines");
    json = JSON.formatPretty(json, 4);
    LOG.debug("json Response:[" + Constants.NEWLINE + json + "]");
    return mapResponse;
  }

  public Map<String, String> getAttendeesForAllWebinarSessions(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/attendees
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/attendees";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getAudioInformation(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/audio
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/audio";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getPerformanceForAllWebinarSessions(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/performance
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/performance";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getWebinar(String credentials, String organizerKey, String webinarKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getWebinarMeetingTimes(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/meetingtimes
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/meetingtimes";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getWebinars(String credentials, String organizerKey) throws Throwable {
    // /organizers/{organizerKey}/webinars
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> updateAudioInformation(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/audio
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/audio";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }

  public Map<String, String> updateWebinar(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "PUT", stringBuilder.toString(), url);
  }
}
