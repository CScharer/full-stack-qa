package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaMemberNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaMemberNamespace.class));

  public Map<String, String> certificationsGet(String iD, boolean isArchived) throws Throwable {
    // Returns a list of Certifications for the specified user.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Member_Certifications_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Member.Certifications.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<IsArchived>" + isArchived + "</IsArchived>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> certificationsJournalGet(
      String iD,
      boolean showExpired,
      String startDate,
      String entryID,
      String certificationID,
      int pageSize,
      int pageNumber)
      throws Throwable {
    // Returns a list of Certification Journal Entries for the specified
    // user that may be optionally filterd by date, expiration, and paging.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Member_Certifications_Journal_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Member.Certifications.Journal.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ID>" + iD + "</ID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ShowExpired>" + showExpired + "</ShowExpired>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<StartDate>" + startDate + "</StartDate>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EntryID>" + entryID + "</EntryID>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CertificationID>" + certificationID + "</CertificationID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<PageSize>" + pageSize + "</PageSize>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<PageNumber>" + pageNumber + "</PageNumber>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
