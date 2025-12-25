package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaPeopleNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaPeopleNamespace.class));

  public Map<String, String> allGetIDs(String timestamp, int websiteID, String groups)
      throws Throwable {
    // Returns a list of member and non-member IDs that may be optionally
    // filtered by timestamp. This method is provided for data
    // synchronization purposes and will return a maximum of 10,000 results.
    // It would typically be used in conjunction with subsequent calls to
    // Sa.People.Profile.Get for each <ID> returned. Server time information
    // is also returned, specifically the current date/time <ServerTime> and
    // GMT bias <ServerGmtBias>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_People_All_GetIDs",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.People.All.GetIDs"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<WebsiteID>" + websiteID + "</WebsiteID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Groups>" + groups + "</Groups>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> profileFindID(
      String importID, String constituentID, int websiteID, String username, String email)
      throws Throwable {
    // Finds and returns a member or non-member <ID> using Import ID,
    // Constituent ID or Website/Profile ID as criteria. If a single ID
    // cannot be uniquely identified based on the criteria supplied then
    // error code 406 is returned.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_People_Profile_FindID",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.People.Profile.FindID"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ImportID>" + importID + "</ImportID>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<ConstituentID>" + constituentID + "</ConstituentID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<WebsiteID>" + websiteID + "</WebsiteID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Username>" + username + "</Username>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Email>" + email + "</Email>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> profileGet(String id) throws Throwable {
    // Returns a person's profile data.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_People_Profile_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.People.Profile.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + id + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> profileGroupsGet(String iD) throws Throwable {
    // Returns a person's group relationship data. There are three types of
    // relationships that members may have with particular groups;
    // "Administrator", "Member" and "Representative". Groups are listed
    // within nodes respective of their relationship type.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_People_Profile_Groups_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.People.Profile.Groups.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> profileUpdate(String iD, String xml) throws Throwable {
    // Updates an existing person's profile.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_People_Profile_Update",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.People.Profile.Update"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + xml);
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public boolean updateMemberData(StringBuilder apiGUIDSQL, String xml) throws Throwable {
    JDBC jdbc = new JDBC("", "");
    final String memberList = jdbc.queryResults(apiGUIDSQL.toString(), "", false);
    jdbc.close();
    final String[] arrayMembers = memberList.split(Constants.NEWLINE);
    boolean success = false;
    for (final String apiGUID : arrayMembers) {
      success = false;
      Map<String, String> mapResults = profileUpdate(apiGUID, xml);
      LOG.debug("mapResults:[{}]", mapResults.toString());
      final String responseXml = mapResults.get("xml");
      try {
        final int errCode =
            Integer.parseInt(XML.getNode(responseXml, "YourMembershipResponse", "ErrCode"));
        switch (errCode) {
          case 0:
            success = true;
            break;
          case 1:
          default:
            break;
        }
      } catch (final Exception e) {
        LOG.error("Exception occurred", e);
      }
    }
    return success;
  }
}
