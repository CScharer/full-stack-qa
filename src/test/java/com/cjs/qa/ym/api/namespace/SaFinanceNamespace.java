package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaFinanceNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaFinanceNamespace.class));

  public Map<String, String> batchCreate(
      String commerceType, String closingDate, boolean closedOnly) throws Throwable {
    switch (commerceType) {
      case "D":
        LOG.debug("commerceType:[D = Donations]");
        break;
      case "M":
        LOG.debug("commerceType:[M = Dues/Membership]");
        break;
      case "S":
        LOG.debug("commerceType:[S = Store]");
        break;
      default:
        commerceType = "";
        LOG.debug("commerceType:[All]");
        break;
    }
    // Starts an export of all invoice items in the specified batch and
    // returns the <ExportID>. To query the status of an export, call
    // Sa.Export.Status until receiving either a COMPLETE or FAILURE status
    // code. Upon receiving a COMPLETE status code, the download location of
    // your export file will be returned by Sa.Export.Status as <ExportURI>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Finance_Batch_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Finance.Batch.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<CommerceType>" + commerceType + "</CommerceType>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ClosingDate>" + closingDate + "</ClosingDate>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<ClosedOnly>" + closedOnly + "</ClosedOnly>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> batchesGet(String timestamp, String startBatchID) throws Throwable {
    // Returns a chronological list of Financial Batches using either a
    // specific batch (recommended) or timestamp as a starting point. This
    // method would typically be used in conjunction with subsequent calls
    // to Sa.Export.Finance.Batch for each <BatchID> returned for the
    // purposes of importing financial data into a 3rd-party accounting
    // sytem. Server time information is also returned, specifically the
    // current date/time <ServerTime> and GMT bias <ServerGmtBias>.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Finance_Batches_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Finance.Batches.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Timestamp>" + timestamp + "</Timestamp>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<StartBatchID>" + startBatchID + "</StartBatchID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> invoicePaymentCreate(
      String invoiceNo,
      String amount,
      String type,
      String refNo,
      String paymentDate,
      String payerName,
      String payerEmail,
      String billOrganization,
      boolean updateInvoiceInfo,
      boolean autoCloseInvoice,
      boolean useInvoiceAddress,
      String billAddress1,
      String billAddress2,
      String billCity,
      String billLocation,
      String billPostalCode,
      String billCountry,
      String billPhone,
      String paymentNotes,
      String internalNotes)
      throws Throwable {
    // Applies a payment to an invoice.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Finance_Invoice_Payment_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Finance.Invoice.Payment.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<InvoiceNo>" + invoiceNo + "</InvoiceNo>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Amount>" + amount + "</Amount>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Type>" + type + "</Type>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<RefNo>" + refNo + "</RefNo>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<PaymentDate>" + paymentDate + "</PaymentDate>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<PayerName>" + payerName + "</PayerName>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<PayerEmail>" + payerEmail + "</PayerEmail>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<BillOrganization>" + billOrganization + "</BillOrganization>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<UpdateInvoiceInfo>" + updateInvoiceInfo + "</UpdateInvoiceInfo>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<AutoCloseInvoice>" + autoCloseInvoice + "</AutoCloseInvoice>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<UseInvoiceAddress>" + useInvoiceAddress + "</UseInvoiceAddress>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<BillAddress1>" + billAddress1 + "</BillAddress1>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<BillAddress2>" + billAddress2 + "</BillAddress2>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<BillCity>" + billCity + "</BillCity>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<BillLocation>" + billLocation + "</BillLocation>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<BillPostalCode>" + billPostalCode + "</BillPostalCode>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<BillCountry>" + billCountry + "</BillCountry>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<BillPhone>" + billPhone + "</BillPhone>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<PaymentNotes>" + paymentNotes + "</PaymentNotes>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<InternalNotes>" + internalNotes + "</InternalNotes>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
