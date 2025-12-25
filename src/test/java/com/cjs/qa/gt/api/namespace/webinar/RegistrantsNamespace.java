package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;

public class RegistrantsNamespace extends GTWebinarServiceTests {

  public Map<String, String> createRegistrant(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/registrants
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/registrants";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }

  public Map<String, String> deleteRegistrant(
      String credentials, String organizerKey, String webinarKey, String registrantKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/registrants/{registrantKey}
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/registrants/"
            + registrantKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "DELETE", stringBuilder.toString(), url);
  }

  public Map<String, String> getRegistrant(
      String credentials, String organizerKey, String webinarKey, String registrantKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/registrants/{registrantKey}
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/registrants/"
            + registrantKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getRegistrants(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/registrants
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/registrants";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> getRegistrationFields(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/registrants/fields
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/registrants/fields";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }
}
