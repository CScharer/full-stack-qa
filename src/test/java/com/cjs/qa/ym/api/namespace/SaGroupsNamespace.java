package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaGroupsNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaGroupsNamespace.class));

  public Map<String, String> groupCreate(
      String groupCode,
      String typeID,
      String name,
      boolean active,
      boolean hidden,
      String accessibility,
      String membership,
      String messaging,
      boolean sendNewsletter,
      boolean enableFeed,
      boolean adminCanExportMembers,
      boolean photoApproval,
      String emailApproval,
      String emailOptionsAdmin,
      String emailOptionsMember,
      String shortDescription,
      String welcomeContent)
      throws Throwable {
    // Creates a Group and returns the newly created [GroupID].
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Groups_Group_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Groups.Group.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<GroupCode>" + groupCode + "</GroupCode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<TypeID>" + typeID + "</TypeID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Name>" + name + "</Name>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Active>" + active + "</Active>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Hidden>" + hidden + "</Hidden>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<Accessibility>" + accessibility + "</Accessibility>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Membership>" + membership + "</Membership>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Messaging>" + messaging + "</Messaging>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<SendNewsletter>" + sendNewsletter + "</SendNewsletter>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EnableFeed>" + enableFeed + "</EnableFeed>");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<AdminCanExportMembers>"
            + adminCanExportMembers
            + "</AdminCanExportMembers>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<PhotoApproval>" + photoApproval + "</PhotoApproval>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<EmailApproval>" + emailApproval + "</EmailApproval>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<EmailOptionsAdmin>" + emailOptionsAdmin + "</EmailOptionsAdmin>");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<EmailOptionsMember>"
            + emailOptionsMember
            + "</EmailOptionsMember>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<ShortDescription>" + shortDescription + "</ShortDescription>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<WelcomeContent>" + welcomeContent + "</WelcomeContent>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> groupGetMembershipLog(String groupID, String startDate, String itemID)
      throws Throwable {
    // Returns a list of group membership log entries by Group ID that may
    // be optionally filtered by timestamp. This method will return a
    // maximum of 1,000 results.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Groups_Group_GetMembershipLog",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Groups.Group.GetMembershipLog"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<GroupID>" + groupID + "</GroupID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<StartDate>" + startDate + "</StartDate>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ItemID>" + itemID + "</ItemID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> groupUpdate(
      String groupID,
      String groupCode,
      String typeID,
      String name,
      boolean active,
      boolean hidden,
      String accessibility,
      String membership,
      String messaging,
      boolean sendNewsletter,
      boolean enableFeed,
      boolean adminCanExportMembers,
      boolean photoApproval,
      String emailApproval,
      String emailOptionsAdmin,
      String emailOptionsMember,
      String shortDescription,
      String welcomeContent)
      throws Throwable {
    // Updates an existing Group. Omitted fields will be ignored.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Groups_Group_Update",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Groups.Group.Update"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<GroupID>" + groupID + "</GroupID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<GroupCode>" + groupCode + "</GroupCode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<TypeID>" + typeID + "</TypeID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Name>" + name + "</Name>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Active>" + active + "</Active>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Hidden>" + hidden + "</Hidden>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<Accessibility>" + accessibility + "</Accessibility>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Membership>" + membership + "</Membership>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Messaging>" + messaging + "</Messaging>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<SendNewsletter>" + sendNewsletter + "</SendNewsletter>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EnableFeed>" + enableFeed + "</EnableFeed>");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<AdminCanExportMembers>"
            + adminCanExportMembers
            + "</AdminCanExportMembers>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<PhotoApproval>" + photoApproval + "</PhotoApproval>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<EmailApproval>" + emailApproval + "</EmailApproval>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<EmailOptionsAdmin>" + emailOptionsAdmin + "</EmailOptionsAdmin>");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<EmailOptionsMember>"
            + emailOptionsMember
            + "</EmailOptionsMember>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<ShortDescription>" + shortDescription + "</ShortDescription>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<WelcomeContent>" + welcomeContent + "</WelcomeContent>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> groupTypesGet() throws Throwable {
    // Returns a list of all Group Types.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Groups_GroupTypes_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Groups.GroupTypes.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
