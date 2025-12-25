package com.cjs.qa.iadhs.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.iadhs.IaDhsEnvironment;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.Email;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.HTML;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.ParameterHelper;
import com.cjs.qa.utilities.colors.ColorsHEX;
import com.cjs.qa.vivit.VivitDataTests;

public class Payment {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Payment.class));

  public static final String HEADINGS =
      "Payment Withheld;Payment Received;Payment Distrib;Payment Amount;Type of Payment";
  public static final List<String> HEADING_LIST =
      Arrays.asList(HEADINGS.split(Constants.DELIMETER_LIST));
  public static final String LBLHEADING_PAYMENT_WITHHELD = "Payment Withheld";
  public static final String LBLHEADING_PAYMENT_RECIEVED = "Payment Received";
  public static final String LBLHEADING_PAYMENT_AMOUNT = "Payment Amount";
  public static final String LBLHEADING_TYPE_OF_PAYMENT = "Type of Payment";
  public static final String LBLHEADING_PAYMENT_DISTRIB = "Payment Distrib";
  public static final String STYLE_BORDER =
      "border: 1px solid black; border-collapse: collapse;width: 100%;";
  public static final String FILE_IADHS_PAYMENTS_CUMULATIVE =
      IaDhsEnvironment.FOLDER_DATA
          + "Data"
          + Constants.DELIMETER_PATH
          + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT)
          + "_IA DHS Child Support Payments"
          + IExtension.XLS;
  public static final String REPORT_IADHS_PAYMENTS_CUMULATIVE =
      FILE_IADHS_PAYMENTS_CUMULATIVE.replaceAll(IExtension.XLS, IExtension.HTM);
  public static final String TABLE_IADHS_PAYMENTS = "t_IADHS";
  public static final String VIEW_IADHS_PAYMENTS_CUMULATIVE = "v_IADHS_PaymentsCumulative";
  private Map<String, String> paymentMap;
  private String paymentWithheld;
  private String paymentReceived;
  private String paymentDistrib;
  private Double paymentAmount;
  private String typeOfPayment;

  /** Default constructor for data binding and instantiation. */
  public Payment() {
    // Default constructor for data binding
  }

  public Payment(
      String paymentWithheld,
      String paymentReceived,
      String paymentDistrib,
      Double paymentAmount,
      String typeOfPayment) {
    Map<String, String> paymentMap = new HashMap<>();
    paymentMap.put(LBLHEADING_PAYMENT_WITHHELD, paymentWithheld);
    paymentMap.put(LBLHEADING_PAYMENT_RECIEVED, paymentReceived);
    paymentMap.put(LBLHEADING_PAYMENT_AMOUNT, String.valueOf(paymentAmount));
    paymentMap.put(LBLHEADING_TYPE_OF_PAYMENT, typeOfPayment);
    paymentMap.put(LBLHEADING_PAYMENT_DISTRIB, paymentDistrib);
    this.paymentMap = paymentMap;
    this.paymentWithheld = paymentWithheld;
    this.paymentReceived = paymentReceived;
    this.paymentAmount = paymentAmount;
    this.typeOfPayment = typeOfPayment;
    this.paymentDistrib = paymentDistrib;
  }

  public Payment(Map<String, String> paymentMap) {
    this.paymentMap = paymentMap;
    this.paymentWithheld = paymentMap.get(LBLHEADING_PAYMENT_WITHHELD);
    this.paymentReceived = paymentMap.get(LBLHEADING_PAYMENT_RECIEVED);
    this.paymentAmount = Double.valueOf(paymentMap.get(LBLHEADING_PAYMENT_AMOUNT));
    this.typeOfPayment = paymentMap.get(LBLHEADING_TYPE_OF_PAYMENT);
    this.paymentDistrib = paymentMap.get(LBLHEADING_PAYMENT_DISTRIB);
  }

  private boolean append(Map<String, String> paymentMap) throws Throwable {
    int recordsUpdated = 0;
    if (!exists(paymentMap)) {
      StringBuilder sqlStringBuilder = new StringBuilder();
      sqlStringBuilder =
          SQL.appendStringBuilderSQLInsertRecord(
              TABLE_IADHS_PAYMENTS, sqlStringBuilder, paymentMap, true);
      recordsUpdated = SQL.execute(sqlStringBuilder.toString());
      LOG.debug("recordsUpdated:[[{}]", recordsUpdated + "]");
    }
    return recordsUpdated > 0;
  }

  public void append(List<Payment> paymentList) throws Throwable {
    if (paymentList.isEmpty()) {
      return;
    }
    List<String> appendedDistribDateList = new ArrayList<>();
    for (Payment payment : paymentList) {
      LOG.debug("paymentList:[{}]", payment.toString());
      Map<String, String> paymentMap = payment.getPaymentMap();
      String paymentDistribDate = paymentMap.get(LBLHEADING_PAYMENT_DISTRIB);
      if (!appendedDistribDateList.contains(paymentDistribDate)) {
        appendedDistribDateList.add(paymentDistribDate);
      }
      append(paymentMap);
    }
    JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL + JDBCConstants.FROM + "[" + VIEW_IADHS_PAYMENTS_CUMULATIVE + "];";
    final List<Map<String, String>> listMapReport = jdbc.queryResultsString(sql, false);
    final List<String> fieldsList = jdbc.getFieldNamesList(VIEW_IADHS_PAYMENTS_CUMULATIVE, true);
    String subject = "IA DHS Child Support Payments";
    String header = subject + " (" + CJSConstants.USERID_DHS + ")";
    String footer = "Last Payment will occur in December 2028";
    String report =
        getPaymentTable(header, footer, fieldsList, listMapReport, appendedDistribDateList);
    FSOTests.fileWrite(REPORT_IADHS_PAYMENTS_CUMULATIVE, report, false);
    JDBC.exportDataFromTableView(
        VIEW_IADHS_PAYMENTS_CUMULATIVE,
        FILE_IADHS_PAYMENTS_CUMULATIVE,
        "IADHS-Payments",
        VivitDataTests.DATABASE_DEFINITION,
        true);
    String body =
        toString()
            + Constants.NEWLINE
            + report
            + Constants.NEWLINE
            + IaDhsEnvironment.EMAIL_SIGNATURE;
    Email.sendEmail(
        CJSConstants.EMAIL_ADDRESS_GMAIL,
        EPasswords.EMAIL_GMAIL.getValue(),
        CJSConstants.EMAIL_ADDRESS_GMAIL,
        "",
        "",
        subject,
        body,
        Arrays.asList(FILE_IADHS_PAYMENTS_CUMULATIVE));
  }

  public boolean exists(Map<String, String> paymentMap) {
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.SELECT);
    sqlStringBuilder.append(JDBCConstants.COUNT + "(*) ");
    sqlStringBuilder.append(JDBCConstants.AS + "[Count] ");
    sqlStringBuilder.append(
        Constants.NEWLINE + JDBCConstants.FROM + "[" + TABLE_IADHS_PAYMENTS + "] ");
    sqlStringBuilder.append(
        Constants.NEWLINE
            + JDBCConstants.WHERE
            + "["
            + LBLHEADING_PAYMENT_WITHHELD
            + "]='"
            + paymentMap.get(LBLHEADING_PAYMENT_WITHHELD)
            + "' ");
    sqlStringBuilder.append(
        Constants.NEWLINE
            + JDBCConstants.AND
            + "["
            + LBLHEADING_PAYMENT_RECIEVED
            + "]='"
            + paymentMap.get(LBLHEADING_PAYMENT_RECIEVED)
            + "' ");
    sqlStringBuilder.append(
        Constants.NEWLINE
            + JDBCConstants.AND
            + "["
            + LBLHEADING_PAYMENT_AMOUNT
            + "]='"
            + paymentMap.get(LBLHEADING_PAYMENT_AMOUNT)
            + "' ");
    sqlStringBuilder.append(
        Constants.NEWLINE
            + JDBCConstants.AND
            + "["
            + LBLHEADING_TYPE_OF_PAYMENT
            + "]='"
            + paymentMap.get(LBLHEADING_TYPE_OF_PAYMENT)
            + "' ");
    sqlStringBuilder.append(
        Constants.NEWLINE
            + JDBCConstants.AND
            + "["
            + LBLHEADING_PAYMENT_DISTRIB
            + "]='"
            + paymentMap.get(LBLHEADING_PAYMENT_DISTRIB)
            + "';");
    LOG.debug("{}", "stringBuilderSQL:" + sqlStringBuilder.toString());
    JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
    List<Map<String, String>> resultsMapList =
        jdbc.queryResultsString(sqlStringBuilder.toString(), true);
    Map<String, String> resultsMap = resultsMapList.get(1);
    int count = Integer.parseInt(resultsMap.get("Count"));
    LOG.debug("Count:[{}]", count);
    return count != 0;
  }

  public static String getPaymentTable(
      String header,
      String footer,
      List<String> fields,
      List<Map<String, String>> listMapReport,
      List<String> appendedDistribDateList)
      throws Throwable {
    LOG.debug(
        "{}", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    LOG.debug(
        "{}",
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList("header", header),
                Arrays.asList("footer", footer),
                Arrays.asList("fields", fields))));
    int tab = 0;
    StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append(HTML.HEADER);
    stringBuilderReport.append(Constants.nlTab(1, tab++) + "<html>");
    stringBuilderReport.append(Constants.nlTab(1, tab++) + "<head>");
    stringBuilderReport.append(
        Constants.nlTab(1, tab--)
            + "<link rel="
            + Constants.QUOTE_DOUBLE
            + "stylesheet"
            + Constants.QUOTE_DOUBLE
            + " type="
            + Constants.QUOTE_DOUBLE
            + "text/css"
            + Constants.QUOTE_DOUBLE
            + " href="
            + Constants.QUOTE_DOUBLE
            + "TableReport.css"
            + Constants.QUOTE_DOUBLE
            + "></link>");
    stringBuilderReport.append(Constants.nlTab(1, tab) + "</head>");
    stringBuilderReport.append(Constants.nlTab(1, tab++) + "<body>");
    StringBuilder stringBuilderTable = new StringBuilder();
    boolean firstRecord = false;
    for (Map<String, String> mapReport : listMapReport) {
      if (!firstRecord) {
        firstRecord = true;
        if (header != null) {
          StringBuilder stringBuilderHeader = new StringBuilder();
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<table class="
                  + Constants.QUOTE_DOUBLE
                  + "scroll"
                  + Constants.QUOTE_DOUBLE
                  + " style="
                  + Constants.QUOTE_DOUBLE
                  + STYLE_BORDER
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(Constants.nlTab(1, tab++) + "<tr>");
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_GOLD.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "<b>" + header + "</b>");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</th>");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</tr>");
          stringBuilderHeader.append(Constants.nlTab(1, tab) + "</table>");
          stringBuilderTable.append(stringBuilderHeader.toString());
        }
        stringBuilderTable.append(
            Constants.nlTab(1, tab++)
                + "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilderTable.append(Constants.nlTab(1, tab++) + "<thead>");
        stringBuilderTable.append(Constants.nlTab(1, tab++) + "<tr>");
        for (final String field : fields) {
          stringBuilderTable.append(
              Constants.nlTab(1, tab++)
                  + "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderTable.append(Constants.nlTab(1, tab--) + "<b>" + field + "</b>");
          stringBuilderTable.append(Constants.nlTab(1, tab) + "</th>");
        }
        tab--;
        stringBuilderTable.append(Constants.nlTab(1, tab--) + "</tr>");
        stringBuilderTable.append(Constants.nlTab(1, tab) + "</thead>");
        stringBuilderTable.append(Constants.nlTab(1, tab++) + "<tbody>");
      }
      stringBuilderTable.append(Constants.nlTab(1, tab++) + "<tr>");
      boolean totalRow = false;
      boolean lastPayment = false;
      for (final String field : fields) {
        String value = mapReport.get(field);
        // if
        // (mapReport.toString().equals(listMapReport.get(2).toString()))
        if (appendedDistribDateList.contains(mapReport.get(LBLHEADING_PAYMENT_DISTRIB))) {
          lastPayment = true;
        }
        if (LBLHEADING_PAYMENT_AMOUNT.equals(field)) {
          value = JavaHelpers.formatNumber(value, "$#,##0.00");
        }
        if ("Index".equals(field) && value.contains("~All")) {
          totalRow = true;
        }
        if (totalRow) {
          stringBuilderTable.append(
              Constants.nlTab(1, tab++)
                  + "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_GRAY.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
        } else {
          if (lastPayment) {
            stringBuilderTable.append(
                Constants.nlTab(1, tab++)
                    + "<td style="
                    + Constants.QUOTE_DOUBLE
                    + "border: 1px solid black; border-collapse: collapse;"
                    + " background-color: "
                    + ColorsHEX.YELLOW.getValue()
                    + ";"
                    + Constants.QUOTE_DOUBLE
                    + ">");
          } else {
            stringBuilderTable.append(
                Constants.nlTab(1, tab++)
                    + "<td style="
                    + Constants.QUOTE_DOUBLE
                    + "border: 1px solid black; border-collapse: collapse;"
                    + Constants.QUOTE_DOUBLE
                    + ">");
          }
        }
        if (totalRow || lastPayment) {
          stringBuilderTable.append(Constants.nlTab(1, tab++) + "<b>");
          stringBuilderTable.append(Constants.nlTab(1, tab--) + value);
          stringBuilderTable.append(Constants.nlTab(1, tab--) + "</b>");
        } else {
          stringBuilderTable.append(Constants.nlTab(1, tab--) + value);
        }
        stringBuilderTable.append(Constants.nlTab(1, tab) + "</td>");
      }
      tab--;
      stringBuilderTable.append(Constants.nlTab(1, tab) + "</tr>");
    }
    tab--;
    stringBuilderTable.append(Constants.nlTab(1, tab--) + "</tbody>");
    stringBuilderTable.append(Constants.nlTab(1, tab--) + "</table>");
    if (footer != null) {
      tab++;
      StringBuilder stringBuilderFooter = new StringBuilder();
      stringBuilderFooter.append(
          Constants.nlTab(1, tab++)
              + "<table class="
              + Constants.QUOTE_DOUBLE
              + "scroll"
              + Constants.QUOTE_DOUBLE
              + " style="
              + Constants.QUOTE_DOUBLE
              + STYLE_BORDER
              + Constants.QUOTE_DOUBLE
              + ">");
      stringBuilderFooter.append(Constants.nlTab(1, tab++) + "<tr>");
      stringBuilderFooter.append(
          Constants.nlTab(1, tab++)
              + "<td style="
              + Constants.QUOTE_DOUBLE
              + "border: 1px solid black; border-collapse: collapse;"
              + " background-color: "
              + ColorsHEX.VIVIT_GOLD.getValue()
              + ";"
              + Constants.QUOTE_DOUBLE
              + ">");
      stringBuilderFooter.append(
          Constants.nlTab(1, tab--)
              + "<font color="
              + Constants.QUOTE_DOUBLE
              + "red"
              + Constants.QUOTE_DOUBLE
              + "><b>Note:</b> </font><i>"
              + footer
              + "</i>");
      stringBuilderFooter.append(Constants.nlTab(1, tab--) + "</td>");
      stringBuilderFooter.append(Constants.nlTab(1, tab--) + "</tr>");
      stringBuilderFooter.append(Constants.nlTab(1, tab--) + "</table>");
      stringBuilderTable.append(stringBuilderFooter.toString());
    }
    stringBuilderReport.append(stringBuilderTable.toString());
    stringBuilderReport.append(Constants.nlTab(1, tab--) + "</body>");
    stringBuilderReport.append(Constants.nlTab(1, 0) + "<html>");
    final String report = stringBuilderReport.toString();
    LOG.debug("{}", Constants.NEWLINE + report);
    return report;
  }

  public Map<String, String> getPaymentMap() {
    return paymentMap;
  }

  public void setPaymentMap(Map<String, String> paymentMap) {
    this.paymentMap = paymentMap;
  }

  public String getPaymentWithheld() {
    return paymentWithheld;
  }

  public void setPaymentWithheld(String paymentWithheld) {
    this.paymentWithheld = paymentWithheld;
  }

  public String getPaymentReceived() {
    return paymentReceived;
  }

  public void setPaymentReceived(String paymentReceived) {
    this.paymentReceived = paymentReceived;
  }

  public String getPaymentDistrib() {
    return paymentDistrib;
  }

  public void setPaymentDistrib(String paymentDistrib) {
    this.paymentDistrib = paymentDistrib;
  }

  public Double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public String getTypeOfPayment() {
    return typeOfPayment;
  }

  public void setTypeOfPayment(String typeOfPayment) {
    this.typeOfPayment = typeOfPayment;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(Constants.NEWLINE);
    stringBuilder.append(LBLHEADING_PAYMENT_WITHHELD + ":");
    stringBuilder.append(getPaymentWithheld());
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(LBLHEADING_PAYMENT_RECIEVED + ":");
    stringBuilder.append(getPaymentReceived());
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(LBLHEADING_PAYMENT_AMOUNT + ":");
    stringBuilder.append(getPaymentAmount());
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(LBLHEADING_TYPE_OF_PAYMENT + ":");
    stringBuilder.append(getTypeOfPayment());
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(LBLHEADING_PAYMENT_DISTRIB + ":");
    stringBuilder.append(getPaymentDistrib());
    return stringBuilder.toString();
  }
}
