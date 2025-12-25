package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;

public class PanelistsNamespace extends GTWebinarServiceTests {

  public Map<String, String> createPanelists(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/panelists
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/panelists";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }

  public Map<String, String> deleteWebinarPanelist(
      String credentials, String organizerKey, String webinarKey, String panelistKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/panelists/{panelistKey}
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/panelists/"
            + panelistKey;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "DELETE", stringBuilder.toString(), url);
  }

  public Map<String, String> getWebinarPanelists(
      String credentials, String organizerKey, String webinarKey) throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/panelists
    String url =
        API_GT_BASE + "/organizers/" + organizerKey + "/webinars/" + webinarKey + "/panelists";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "GET", stringBuilder.toString(), url);
  }

  public Map<String, String> resendPanelistInvitation(
      String credentials, String organizerKey, String webinarKey, String panelistKey)
      throws Throwable {
    // /organizers/{organizerKey}/webinars/{webinarKey}/panelists/{panelistKey}/resendInvitation
    String url =
        API_GT_BASE
            + "/organizers/"
            + organizerKey
            + "/webinars/"
            + webinarKey
            + "/panelists/"
            + panelistKey
            + "/resendInvitation";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }
}
