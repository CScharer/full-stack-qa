package com.cjs.qa.ym;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.vivit.VivitEnvironment;
import com.cjs.qa.ym.api.namespace.SessionNamespace;
import com.cjs.qa.ym.api.services.YMAPI;

public class YMAPIDebug {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(YMAPIDebug.class));

  public void runTests() throws Throwable {
    final String dateTimeCurrent =
        DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME);
    final String ymInceptionDate = DateHelpersTests.getCurrentDateTime("2007-07-01 00:00:00");
    LOG.debug("dateTimeFormat:[[{}]", DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME + "]");
    LOG.debug("dateTimeCurrent:[[{}]", dateTimeCurrent + "]");
    LOG.debug("ymInceptionDate:[[{}]", ymInceptionDate + "]");
    final Map<String, String> mapCScharer = new HashMap<>();
    mapCScharer.put("userID", CJSConstants.USERID_VIVIT);
    mapCScharer.put("password", EPasswords.VIVIT.getValue());
    mapCScharer.put("webSiteMemberID", "10969660");
    mapCScharer.put("apiGUID", "9B22C96F-4EFF-4D73-A2E3-09BB462687E3");
    mapCScharer.put("passwordHash", "b2e2c25251a62b7c26ad52f50f285bd8c7dac60e");
    mapCScharer.put("emailAddress", CJSConstants.EMAIL_ADDRESS_VIVIT);
    mapCScharer.put("vivitURL", VivitEnvironment.URL_LOGIN);
    LOG.debug("mapCScharer:[[{}]", mapCScharer + "]");
    // YMAPI Instantiate
    final YMAPI ymAPI = new YMAPI();
    Map<String, String> mapResults = new HashMap<>();
    LOG.debug("{}", "mapResults:]" + mapResults.toString() + "]");
    // Auth Namespace
    mapResults =
        ymAPI
            .getAuthNamespace()
            .authenticate(mapCScharer.get("userID"), mapCScharer.get("password"));
    mapResults =
        ymAPI
            .getAuthNamespace()
            .createToken(
                mapCScharer.get("userID"),
                mapCScharer.get("password"),
                true,
                mapCScharer.get("vivitURL"));
    // Convert Namespace
    mapResults = ymAPI.getConvertNamespace().toEasternTime(dateTimeCurrent, "-5");
    // Events Namespace
    mapResults = ymAPI.getEventsNamespace().allSearch("Symposium", 100, 1);
    mapResults = ymAPI.getEventsNamespace().eventAttendeesGet(0);
    mapResults = ymAPI.getEventsNamespace().eventGet(0);
    // Feeds Namespace
    mapResults = ymAPI.getFeedsNamespace().feedGet("NOW", "25", "1");
    mapResults = ymAPI.getFeedsNamespace().get();
    // Member Namespace
    mapResults = ymAPI.getMemberNamespace().certificationsGet(true);
    mapResults =
        ymAPI
            .getMemberNamespace()
            .certificationsJournalGet(true, "startDate", "entryID", "certificationID", 100, 1);
    mapResults = ymAPI.getMemberNamespace().commerceStoreGetOrderIDs("timestamp", "status");
    mapResults = ymAPI.getMemberNamespace().commerceStoreOrderGet("invoiceID");
    mapResults =
        ymAPI
            .getMemberNamespace()
            .connectionApprove(Integer.valueOf(mapCScharer.get("webSiteMemberID")), true);
    mapResults = ymAPI.getMemberNamespace().isAuthenticated();
    mapResults = ymAPI.getMemberNamespace().mediaGalleryUpload("albumID", "caption", true, true);
    mapResults = ymAPI.getMemberNamespace().messagesGetInbox(100, 1);
    mapResults = ymAPI.getMemberNamespace().messagesGetSent(100, 1);
    mapResults = ymAPI.getMemberNamespace().messagesMessageRead("messageID");
    mapResults = ymAPI.getMemberNamespace().messagesMessageSend("iD", "subject", "body");
    mapResults =
        ymAPI
            .getMemberNamespace()
            .passwordInitializeReset(mapCScharer.get("userID"), mapCScharer.get("emailAddress"));
    mapResults =
        ymAPI
            .getMemberNamespace()
            .passwordUpdate("resetToken", mapCScharer.get("password"), mapCScharer.get("password"));
    mapResults = ymAPI.getMemberNamespace().profileGet();
    mapResults = ymAPI.getMemberNamespace().profileGetMini();
    mapResults = ymAPI.getMemberNamespace().wallPost("iD", "postText");
    // Members Namespace
    mapResults = ymAPI.getMembersNamespace().connectionsCategoriesGet("iD");
    mapResults = ymAPI.getMembersNamespace().connectionsGet("iD", "categoryID", 100, 1);
    mapResults = ymAPI.getMembersNamespace().mediaGalleryAlbumsGet("iD");
    mapResults = ymAPI.getMembersNamespace().mediaGalleryGet("iD", "albumID", 100, 1);
    mapResults = ymAPI.getMembersNamespace().mediaGalleryItemGet("iD", "itemID");
    mapResults = ymAPI.getMembersNamespace().wallGet("iD", 100, 1);
    // People Namespace
    mapResults = ymAPI.getPeopleNamespace().allSearch("Scharer", "25", "1");
    mapResults = ymAPI.getPeopleNamespace().profileGet(mapCScharer.get("apiGUID"));
    // Namespace
    mapResults =
        ymAPI
            .getSaAuthNamespace()
            .authenticate(
                mapCScharer.get("userID"),
                mapCScharer.get("password"),
                mapCScharer.get("passwordHash"));
    // Sa.Certifications Namespace
    mapResults = ymAPI.getSaCertificationsNamespace().allGet(true);
    mapResults = ymAPI.getSaCertificationsNamespace().creditTypesAllGet();
    // Sa.Commerce Namespace
    mapResults = ymAPI.getSaCommerceNamespace().storeOrderGet("invoiceID");
    mapResults = ymAPI.getSaCommerceNamespace().productGet("productID");
    mapResults = ymAPI.getSaCommerceNamespace().productsAllGetIDs("productName", "productType");
    // Sa.Events Namespace
    mapResults =
        ymAPI
            .getSaEventsNamespace()
            .allGetIDs(ymInceptionDate, dateTimeCurrent, "name", null, ymInceptionDate);
    mapResults = ymAPI.getSaEventsNamespace().eventGet(0);
    mapResults = ymAPI.getSaEventsNamespace().eventRegistrationGet("registrationID", "badgeNumber");
    mapResults = ymAPI.getSaEventsNamespace().eventRegistrationsGetIDs(0, null);
    // Sa.Export Namespace
    mapResults = ymAPI.getSaExportNamespace().allInvoiceItems(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().careerOpenings(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().donationsTransactions(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().donationsInvoiceItems(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().duesTransactions(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().duesInvoiceItems(false, ymInceptionDate);
    mapResults =
        ymAPI
            .getSaExportNamespace()
            .eventRegistrations(
                false, "eventID", "sessionIDs", "productID", "All", "lastName", true);
    mapResults = ymAPI.getSaExportNamespace().financeBatch(false, "batchID");
    mapResults = ymAPI.getSaExportNamespace().members(false, true, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().exportDataMembers(ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().membersGroups(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().exportDataMemberGroups(ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().storeOrders(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().storeInvoiceItems(false, ymInceptionDate);
    mapResults = ymAPI.getSaExportNamespace().status("exportID");
    // Sa.Finance Namespace
    mapResults = ymAPI.getSaFinanceNamespace().batchCreate("", dateTimeCurrent, false);
    mapResults = ymAPI.getSaFinanceNamespace().batchesGet(ymInceptionDate, "startBatchID");
    mapResults =
        ymAPI
            .getSaFinanceNamespace()
            .invoicePaymentCreate(
                "invoiceNo",
                "amount",
                "type",
                "refNo",
                dateTimeCurrent,
                "payerName",
                "payerEmail",
                "billOrganization",
                false,
                false,
                false,
                "billAddress1",
                "billAddress2",
                "billCity",
                "billLocation",
                "billPostalCode",
                "billCountry",
                "billPhone",
                "paymentNotes",
                "internalNotes");
    // Sa.Groups Namespace
    mapResults =
        ymAPI
            .getSaGroupsNamespace()
            .groupCreate(
                "groupCode",
                "typeID",
                "name",
                false,
                true,
                "accessibility",
                "membership",
                "messaging",
                false,
                false,
                true,
                false,
                "emailApproval",
                "emailOptionsAdmin",
                "emailOptionsMember",
                "shortDescription",
                "welcomeContent");
    mapResults =
        ymAPI.getSaGroupsNamespace().groupGetMembershipLog("groupID", ymInceptionDate, "itemID");
    mapResults =
        ymAPI
            .getSaGroupsNamespace()
            .groupUpdate(
                "groupID",
                "groupCode",
                "typeID",
                "name",
                false,
                true,
                "accessibility",
                "membership",
                "messaging",
                false,
                false,
                true,
                false,
                "emailApproval",
                "emailOptionsAdmin",
                "emailOptionsMember",
                "shortDescription",
                "welcomeContent");
    mapResults = ymAPI.getSaGroupsNamespace().groupTypesGet();
    // Sa.Member Namespace
    mapResults = ymAPI.getSaMemberNamespace().certificationsGet("iD", true);
    mapResults =
        ymAPI
            .getSaMemberNamespace()
            .certificationsJournalGet(
                "iD", true, ymInceptionDate, "entryID", "certificationID", 100, 1);
    // Sa.Members Namespace
    mapResults = ymAPI.getSaMembersNamespace().allGetIDs(ymInceptionDate, 0, "");
    mapResults = ymAPI.getSaMembersNamespace().allMemberTypesGet();
    mapResults = ymAPI.getSaMembersNamespace().allRecentActivity();
    mapResults =
        ymAPI
            .getSaMembersNamespace()
            .certificationsJournalEntryCreate(
                "iD",
                "cEUsEarned",
                "",
                "certificationID",
                "certificationName",
                "description",
                "",
                "scorePercent",
                "creditTypeCode");
    mapResults =
        ymAPI.getSaMembersNamespace().commerceStoreGetOrderIDs("iD", ymInceptionDate, "Open");
    mapResults = ymAPI.getSaMembersNamespace().eventsEventRegistrationGet("eventID", "iD");
    mapResults =
        ymAPI.getSaMembersNamespace().groupsAdd("iD", "groupCode", false, false, false, "");
    mapResults = ymAPI.getSaMembersNamespace().groupsRemove("iD", "groupCode", false, false, "");
    mapResults = ymAPI.getSaMembersNamespace().profileCreate(true);
    mapResults = ymAPI.getSaMembersNamespace().referralsGet("iD", ymInceptionDate);
    mapResults = ymAPI.getSaMembersNamespace().subAccountsGet("iD", ymInceptionDate);
    // Sa.NonMembers Namespace
    mapResults = ymAPI.getSaNonMembersNamespace().allGetIDs(ymInceptionDate, 0, "");
    mapResults = ymAPI.getSaNonMembersNamespace().profileCreate();
    // Sa.People Namespace
    mapResults = ymAPI.getSaPeopleNamespace().allGetIDs(ymInceptionDate, 0, "");
    mapResults =
        ymAPI
            .getSaPeopleNamespace()
            .profileFindID("importID", "constituentID", 0, "username", "email");
    mapResults = ymAPI.getSaPeopleNamespace().profileGet("iD");
    mapResults = ymAPI.getSaPeopleNamespace().profileGroupsGet("iD");
    mapResults = ymAPI.getSaPeopleNamespace().profileUpdate("iD", "");
    // Session Namespace
    mapResults = SessionNamespace.create(true);
    mapResults = ymAPI.getSessionNamespace().ping();
    mapResults = ymAPI.getSessionNamespace().abandon();
  }
}
