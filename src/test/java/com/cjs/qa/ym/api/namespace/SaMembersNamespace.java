package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaMembersNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaMembersNamespace.class));

  public Map<String, String> allGetIDs(String timestamp, int websiteID, String groups)
      throws Throwable {
    // Returns a list of member IDs that may be optionally filtered by
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
        "Sa_Members_All_GetIDs",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.All.GetIDs"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<WebsiteID>" + websiteID + "</WebsiteID>");
    if (!groups.isEmpty()) {
      stringBuilder.append(Constants.nlTab(1, 2) + "<Groups>" + groups + "</Groups>");
    }
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> allMemberTypesGet() throws Throwable {
    // Returns a list of all Group Types.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_All_MemberTypes_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.All.MemberTypes.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> allRecentActivity() throws Throwable {
    // Returns member community activity information for the purpose of
    // creating a navigation control.
    // A subset of data is returned for each of the following:
    // Newest member
    // Latest post
    // Latest comment on a post
    // Latest photo
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_All_RecentActivity",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.All.RecentActivity"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> certificationsJournalEntryCreate(
      String iD,
      String cEUsEarned,
      String cEUsExpireDate,
      String certificationID,
      String certificationName,
      String description,
      String entryDate,
      String scorePercent,
      String creditTypeCode)
      throws Throwable {
    // Create a CEU Journal Entry.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Certifications_JournalEntry_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Certifications.JournalEntry.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<CEUsEarned>" + cEUsEarned + "</CEUsEarned>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CEUsExpireDate>" + cEUsExpireDate + "</CEUsExpireDate>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CertificationID>" + certificationID + "</CertificationID>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CertificationName>" + certificationName + "</CertificationName>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Description>" + description + "</Description>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EntryDate>" + entryDate + "</EntryDate>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<ScorePercent>" + scorePercent + "</ScorePercent>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CreditTypeCode>" + creditTypeCode + "</CreditTypeCode>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> commerceStoreGetOrderIDs(String iD, String timestamp, String status)
      throws Throwable {
    switch ("") {
      // -1=Cancelled; 0=Open; 1=Processed; 2=Shipped/Closed
      case "Cancelled":
        status = "-1";
        break;
      case "Open":
        status = "0";
        break;
      case "Processed":
        status = "1";
        break;
      case "Shipped/Closed":
        status = "2";
        break;
      default:
        break;
    }
    // Returns a list of order IDs for a specified member that may be
    // optionally filtered by timestamp and status. This method will return
    // a maximum of 1,000 results.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Commerce_Store_GetOrderIDs",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Commerce.Store.GetOrderIDs"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Status>" + status + "</Status>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventsEventRegistrationGet(String eventID, String iD)
      throws Throwable {
    // Returns Event Registration details for the provided Event and Member
    // ID. Includes all Event Registration details for Primary Registrant
    // and Additional Registrants. If the Event Registration contains a
    // related Custom Form, the form data will be included in the <DataSet>
    // element as it is stored in our database.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Events_Event_Registration_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Events.Event.Registration.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EventID>" + eventID + "</EventID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> groupsAdd(
      String iD,
      String groupCode,
      boolean addAsMember,
      boolean isPrimaryGroup,
      boolean addAsGroupAdmin,
      String addGroupRepTitle)
      throws Throwable {
    // Creates relationships between the supplied Member ID and Group Code.
    // There are three types of relationships that may be created for a
    // group: "Member", "Administrator" and "Representative". When adding a
    // member to a group as a member, "IsPrimaryGroup" should be specified.
    // When adding a member to a group as a "Representative", a Title should
    // be specified in the "AddGroupRepTitle" argument. If the title does
    // not exist, it will be created automatically.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Groups_Add",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Groups.Add"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<GroupCode>" + groupCode + "</GroupCode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<AddAsMember>" + addAsMember + "</AddAsMember>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<IsPrimaryGroup>" + isPrimaryGroup + "</IsPrimaryGroup>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<AddAsGroupAdmin>" + addAsGroupAdmin + "</AddAsGroupAdmin>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<AddGroupRepTitle>" + addGroupRepTitle + "</AddGroupRepTitle>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> groupsRemove(
      String iD,
      String groupCode,
      boolean removeAsMember,
      boolean removeAsGroupAdmin,
      String removeGroupRepTitle)
      throws Throwable {
    // Removes relationships between the supplied Member ID and Group Code.
    // There are three types of relationships that may be removed for a
    // group: "Member", "Administrator" and "Representative". When removing
    // a member from a group as a "Representative", a "Title" should be
    // specified.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Groups_Remove",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Groups.Remove"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<GroupCode>" + groupCode + "</GroupCode>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<RemoveAsMember>" + removeAsMember + "</RemoveAsMember>");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<RemoveAsGroupAdmin>"
            + removeAsGroupAdmin
            + "</RemoveAsGroupAdmin>");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<RemoveGroupRepTitle>"
            + removeGroupRepTitle
            + "</RemoveGroupRepTitle>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> profileCreate(boolean useMemberTypeSetting) throws Throwable {
    // Creates a new member profile and returns the new member's <ID> and
    // <WebsiteID>. The returned <ID> must be supplied when performing
    // future updates to the member's profile. The returned <WebsiteID>
    // represents the numeric identifier used by the YourMembership.com
    // application for navigation purposes. It may be used to provide direct
    // navigation to a member's profile, photo gallery, personal blog, etc.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Profile_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Profile.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(
        Constants.nlTab(1, 2)
            + "<UseMemberTypeSetting>"
            + useMemberTypeSetting
            + "</UseMemberTypeSetting>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> referralsGet(String iD, String timestamp) throws Throwable {
    // Returns a list of a member's referrals.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_Referrals_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.Referrals.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> subAccountsGet(String iD, String timestamp) throws Throwable {
    // Returns a list of a member's sub-accounts.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Members_SubAccounts_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Members.SubAccounts.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
