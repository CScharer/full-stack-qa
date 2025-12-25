package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaNonMembersNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaNonMembersNamespace.class));

  public Map<String, String> allGetIDs(String timestamp, int websiteID, String groups)
      throws Throwable {
    // Returns a list of non-member IDs that may be optionally filtered by
    // timestamp. This method is provided for data synchronization purposes
    // and will return a maximum of 10,000 results. It would typically be
    // used in conjunction with subsequent calls to Sa.People.Profile.Get
    // for each <ID> returned. Server time information is also returned,
    // specifically the current date/time <ServerTime> and GMT bias
    // <ServerGmtBias>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_NonMembers_All_GetIDs",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.NonMembers.All.GetIDs"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<WebsiteID>" + websiteID + "</WebsiteID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Groups>" + groups + "</Groups>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> profileCreate() throws Throwable {
    // Creates a new non-member profile and returns the new non-member's
    // <ID> and <WebsiteID>. The returned <ID> must be supplied when
    // performing future updates to the non-member's profile. The returned
    // <WebsiteID> represents the numeric identifier used by the
    // YourMembership.com application for navigation purposes.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_NonMembers_Profile_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.NonMembers.Profile.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
