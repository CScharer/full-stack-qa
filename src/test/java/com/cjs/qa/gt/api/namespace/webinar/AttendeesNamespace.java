package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;

public class AttendeesNamespace extends GTWebinarServiceTests {

  public Map<String, String> getAttendee(
      String credentials,
      String organizerKey,
      String webinarKey,
      String sessionKey,
      String registrantKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/attendees/{registrantKey}
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/attendees/"
            + registrantKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getAttendeePollAnswers(
      String credentials,
      String organizerKey,
      String webinarKey,
      String sessionKey,
      String registrantKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/attendees/{registrantKey}/polls
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/attendees/"
            + registrantKey
            + "/polls";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getAttendeeQuestions(
      String credentials,
      String organizerKey,
      String webinarKey,
      String sessionKey,
      String registrantKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/attendees/{registrantKey}/questions
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/attendees/"
            + registrantKey
            + "/questions";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getAttendeeSurveyAnswers(
      String credentials,
      String organizerKey,
      String webinarKey,
      String sessionKey,
      String registrantKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/attendees/{registrantKey}/surveys
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/attendees/"
            + registrantKey
            + "/surveys";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getSessionAttendees(
      String credentials, String organizerKey, String webinarKey, String sessionKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/sessions/{sessionKey}/attendees
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/sessions/"
            + sessionKey
            + "/attendees";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }
}
