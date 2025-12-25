package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.vivit.VivitFoldersFiles;
import com.cjs.qa.ym.YMDataTests;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaExportNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaExportNamespace.class));

  public Map<String, String> allInvoiceItems(boolean unicode, String date) throws Throwable {
    // Starts an export of all invoice items and returns the <ExportID>. To
    // query the status of an export, call Sa.Export.Status until receiving
    // either a COMPLETE or FAILURE status code. Upon receiving a COMPLETE
    // status code, the download location of your export file will be
    // returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_All_InvoiceItems",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.All.InvoiceItems"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> careerOpenings(boolean unicode, String date) throws Throwable {
    // Starts an export of career openings and returns the <ExportID>. To
    // query the status of an export, call Sa.Export.Status until receiving
    // either a COMPLETE or FAILURE status code. Upon receiving a COMPLETE
    // status code, the download location of your export file will be
    // returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Career_Openings",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Career.Openings"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> donationsInvoiceItems(boolean unicode, String date) throws Throwable {
    // Starts an export of donation invoice items and returns the
    // <ExportID>. To query the status of an export, call Sa.Export.Status
    // until receiving either a COMPLETE or FAILURE status code. Upon
    // receiving a COMPLETE status code, the download location of your
    // export file will be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Donations_InvoiceItems",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Donations.InvoiceItems"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> donationsTransactions(boolean unicode, String date) throws Throwable {
    // Starts an export of donation transactions and returns the <ExportID>.
    // To query the status of an export, call Sa.Export.Status until
    // receiving either a COMPLETE or FAILURE status code. Upon receiving a
    // COMPLETE status code, the download location of your export file will
    // be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Donations_Transactions",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Donations.Transactions"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> duesInvoiceItems(boolean unicode, String date) throws Throwable {
    // Starts an export of dues transactions and returns the <ExportID>. To
    // query the status of an export, call Sa.Export.Status until receiving
    // either a COMPLETE or FAILURE status code. Upon receiving a COMPLETE
    // status code, the download location of your export file will be
    // returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Dues_InvoiceItems",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Dues.InvoiceItems"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> duesTransactions(boolean unicode, String date) throws Throwable {
    // Starts an export of dues transactions and returns the <ExportID>. To
    // query the status of an export, call Sa.Export.Status until receiving
    // either a COMPLETE or FAILURE status code. Upon receiving a COMPLETE
    // status code, the download location of your export file will be
    // returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Dues_Transactions",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Dues.Transactions"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventRegistrations(
      boolean unicode,
      String eventID,
      String sessionIDs,
      String productID,
      String processed,
      String lastName,
      boolean attendedEvent)
      throws Throwable {
    switch (processed) {
      // 0=All, 1=Open Records, 2=Processed Records, -1=Cancelled Records
      case "Open":
        processed = "1";
        break;
      case "Processed":
        processed = "2";
        break;
      case "Cancelled":
        processed = "-1";
        break;
      case "All":
      default:
        processed = "0";
        break;
    }
    // Starts an export of registration records for an event and returns the
    // <ExportID>. To query the status of an export, call Sa.Export.Status
    // until receiving either a COMPLETE or FAILURE status code. Upon
    // receiving a COMPLETE status code, the download location of your
    // export file will be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Event_Registrations",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Event.Registrations"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EventID>" + eventID + "</EventID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<SessionIDs>" + sessionIDs + "</SessionIDs>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ProductID>" + productID + "</ProductID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Processed>" + processed + "</Processed>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<LastName>" + lastName + "</LastName>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<AttendedEvent>" + attendedEvent + "</AttendedEvent>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> financeBatch(boolean unicode, String batchID) throws Throwable {
    // Starts an export of all invoice items in the specified batch and
    // returns the <ExportID>. To query the status of an export, call
    // Sa.Export.Status until receiving either a COMPLETE or FAILURE status
    // code. Upon receiving a COMPLETE status code, the download location of
    // your export file will be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Finance_Batch",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Finance.Batch"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<BatchID>" + batchID + "</BatchID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> exportDataMembers(String dateTimeFrom) throws Throwable {
    if (!JavaHelpers.hasValue(dateTimeFrom)) {
      dateTimeFrom = YMDataTests.getInceptionDateTime();
    }
    Map<String, String> mapResults = members(false, true, dateTimeFrom);
    final String exportID = XML.getTag(mapResults.get("xml"), "ExportID");
    String exportStatus = null;
    String exportURI = null;
    int statusCheckCount = 0;
    do {
      statusCheckCount++;
      JavaHelpers.sleep(0, Constants.MILLISECONDS * 15);
      mapResults = status(exportID);
      exportStatus = XML.getTag(mapResults.get("xml"), "Status");
      exportURI = XML.getTag(mapResults.get("xml"), "ExportURI");
      exportStatus = ExportStatus.getValue(exportStatus);
      LOG.debug(
          "{}-statusCheckCount:[{}], exportStatus:[{}]",
          JavaHelpers.getCurrentClassMethodName(),
          statusCheckCount,
          exportStatus);
    } while (ExportStatus.WORKING.equals(exportStatus)
        || ExportStatus.UNKNOWN.equals(exportStatus));
    if (ExportStatus.COMPLETE.equals(exportStatus)) {
      FSOTests.fileDownloadFromURL(exportURI, VivitFoldersFiles.DATA_YMAPI_DATA_MEMBERS);
    } else {
      throw new QAException(
          JavaHelpers.getCurrentClassMethodDebugName() + Constants.NEWLINE + mapResults.toString());
    }
    return mapResults;
  }

  public Map<String, String> exportDataMemberGroups(String dateTimeFrom) throws Throwable {
    if (!JavaHelpers.hasValue(dateTimeFrom)) {
      dateTimeFrom = YMDataTests.getInceptionDateTime();
    }
    Map<String, String> mapResults = membersGroups(false, dateTimeFrom);
    final String exportID = XML.getTag(mapResults.get("xml"), "ExportID");
    String exportStatus = null;
    String exportURI = null;
    int statusCheckCount = 0;
    do {
      statusCheckCount++;
      JavaHelpers.sleep(0, Constants.MILLISECONDS * 15);
      mapResults = status(exportID);
      exportStatus = XML.getTag(mapResults.get("xml"), "Status");
      exportURI = XML.getTag(mapResults.get("xml"), "ExportURI");
      exportStatus = ExportStatus.getValue(exportStatus);
      LOG.debug(
          "{}-statusCheckCount:[{}], exportStatus:[{}]",
          JavaHelpers.getCurrentClassMethodName(),
          statusCheckCount,
          exportStatus);
    } while (ExportStatus.WORKING.equals(exportStatus)
        || ExportStatus.UNKNOWN.equals(exportStatus));
    if (ExportStatus.COMPLETE.equals(exportStatus)) {
      FSOTests.fileDownloadFromURL(exportURI, VivitFoldersFiles.DATA_YMAPI_DATA_MEMBER_GROUPS);
    } else {
      throw new QAException(
          JavaHelpers.getCurrentClassMethodDebugName() + Constants.NEWLINE + mapResults.toString());
    }
    return mapResults;
  }

  public Map<String, String> members(boolean unicode, boolean customFields, String timestamp)
      throws Throwable {
    // Starts an export of members and their related profile data and
    // returns the <ExportID>. To query the status of an export, call
    // Sa.Export.Status until receiving either a COMPLETE or FAILURE status
    // code. Upon receiving a COMPLETE status code, the download location of
    // your export file will be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Members",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Members"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CustomFields>" + customFields + "</CustomFields>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> membersGroups(boolean unicode, String timestamp) throws Throwable {
    // Starts an export of members and their related group membership data
    // and returns the <ExportID>. To query the status of an export, call
    // Sa.Export.Status until receiving either a COMPLETE or FAILURE status
    // code. Upon receiving a COMPLETE status code, the download location of
    // your export file will be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Members_Groups",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Members.Groups"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> status(String exportID) throws Throwable {
    // Returns the status of an export by <ExportID>. This method should be
    // called until a ststus of either FAILURE or COMPLETE is returned.
    // The returned Status code corresponds with one of the following:
    // FAILURE = -1
    // UNKNOWN = 0
    // WORKING = 1
    // COMPLETE = 2
    // If the export status is COMPLETE then the download location is
    // returned as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Status",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Status"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ExportID>" + exportID + "</ExportID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> storeInvoiceItems(boolean unicode, String date) throws Throwable {
    // Starts an export of store orders and returns the <ExportID>. To query
    // the status of an export, call Sa.Export.Status until receiving either
    // a COMPLETE or FAILURE status code. Upon receiving a COMPLETE status
    // code, the download location of your export file will be returned by
    // Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Store_InvoiceItems",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Store.InvoiceItems"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> storeOrders(boolean unicode, String date) throws Throwable {
    // Starts an export of store orders and returns the <ExportID>. To query
    // the status of an export, call Sa.Export.Status until receiving either
    // a COMPLETE or FAILURE status code. Upon receiving a COMPLETE status
    // code, the download location of your export file will be returned by
    // Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Export_Store_Orders",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Export.Store.Orders"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Unicode>" + unicode + "</Unicode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Date>" + date + "</Date>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
