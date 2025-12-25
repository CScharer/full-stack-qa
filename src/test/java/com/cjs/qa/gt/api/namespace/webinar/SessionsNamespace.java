package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;

public class SessionsNamespace extends GTWebinarServiceTests {

  public Map<String, String> getOrganizerSessions(String credentials, String organizerKey)
      throws Throwable {
    // /organizers/{organizerKey}/sessions
    String url = API_GT_BASE + "/organizers/" + organizerKey + "/sessions";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getSessionPerformance(
      String credentials, String organizerKey, String webinarKey, String sessionKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/performance
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/performance";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getSessionPolls(
      String credentials, String organizerKey, String webinarKey, String sessionKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/polls
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/polls";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getSessionQuestions(
      String credentials, String organizerKey, String webinarKey, String sessionKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/questions
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/questions";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getSessionSurveys(
      String credentials, String organizerKey, String webinarKey, String sessionKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/surveys
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/surveys";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getWebinarSession(
      String credentials, String organizerKey, String webinarKey, String sessionKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getWebinarSessions(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/sessions";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }
}
