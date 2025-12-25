package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;

public class CoOrganizersNamespace extends GTWebinarServiceTests {

  public Map<String, String> createCoOrganizers(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/coorganizers
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/coorganizers";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }

  public Map<String, String> deleteCoOrganizer(
      String credentials, String organizerKey, String webinarKey, String coorganizerKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/coorganizers/{coorganizerKey}
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/coorganizers/"
            + coorganizerKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "DELETE", stringBuilder.toString(), url);
  }

  public Map<String, String> getCoOrganizers(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/coorganizers
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/coorganizers";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> resendInvitation(
      String credentials, String organizerKey, String webinarKey, String coorganizerKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/coorganizers/{coorganizerKey}/resendInvitation
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/coorganizers/"
            + coorganizerKey
            + "/resendInvitation";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }
}
