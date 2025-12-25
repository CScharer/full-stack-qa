package com.cjs.qa.microsoft;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.microsoft.objects.PointsBreakdown;
import com.cjs.qa.microsoft.objects.PointsCard;
import com.cjs.qa.microsoft.objects.RewardsInfo;
import com.cjs.qa.microsoft.pages.BingPage;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.colors.ColorsHEX;

public class MicrosoftReport {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(MicrosoftReport.class));

  public static final String FORMAT_NUMBER = "###,###,##0";

  public static String getFieldHTML(String fieldValue) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<td style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse;"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(fieldValue);
    stringBuilder.append("</td>");
    return stringBuilder.toString();
  }

  public static String getFormatNumber() {
    return FORMAT_NUMBER;
  }

  public static String getFieldNotCoded(String field) {
    return getFieldHTML("Field [" + field + "] Not Coded");
  }

  private static String getReportFooter() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    return stringBuilder.toString();
  }

  private static String getReportHeader(String section, List<String> fieldList) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse;width: 100%;"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(section);
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse;width: 100%;"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<thead>");
    stringBuilder.append("<tr>");
    for (final String fieldName : fieldList) {
      stringBuilder.append(
          "<th style="
              + Constants.QUOTE_DOUBLE
              + "border: 1px solid black; border-collapse: collapse;"
              + " background-color: "
              + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
              + ";"
              + Constants.QUOTE_DOUBLE
              + ">");
      stringBuilder.append("<b>" + fieldName + "</b>");
      stringBuilder.append("</th>");
    }
    stringBuilder.append("</tr>");
    stringBuilder.append("</thead>");
    stringBuilder.append("<tbody>");
    return stringBuilder.toString();
  }

  private String getReportBreakdown() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        getReportHeader("Points (Breakdown)", PointsBreakdown.getReportFieldList()));
    stringBuilder.append(PointsBreakdown.toReport());
    stringBuilder.append(getReportFooter());
    return stringBuilder.toString();
  }

  private String getReportCards() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getReportHeader("Points (Cards)", PointsCard.getReportFieldList()));
    stringBuilder.append(PointsCard.toReport());
    stringBuilder.append(getReportFooter());
    return stringBuilder.toString();
  }

  public String getReportPoints() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getReportHeader("Points (Dashboard)", RewardsInfo.getReportFieldList()));
    stringBuilder.append(RewardsInfo.toReport());
    stringBuilder.append(getReportFooter());
    return stringBuilder.toString();
  }

  private String getReportRewardsInfo() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        getReportHeader("Dashboard (Reward Info)", RewardsInfo.getReportFieldList()));
    stringBuilder.append(RewardsInfo.toReport());
    stringBuilder.append(getReportFooter());
    return stringBuilder.toString();
  }

  private String getReportSearhes() {
    final StringBuilder stringBuilder = new StringBuilder();
    List<String> wordsList = BingPage.getWordsList();
    if (wordsList == null) {
      wordsList = Arrays.asList("No searches were made at this time.");
    }
    final String heading = "Words (" + BingPage.getSearchesMade() + ")";
    LOG.debug("{}", heading + Constants.NEWLINE + wordsList.toString());
    stringBuilder.append(getReportHeader("Searches", Arrays.asList(heading)));
    for (int index = 0; index <= BingPage.getSearchesMade(); index++) {
      final String value = wordsList.get(index);
      stringBuilder.append("<tr>" + getFieldHTML(value) + "</tr>");
    }
    stringBuilder.append(getReportFooter());
    return stringBuilder.toString();
  }

  public String printReports() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getReportRewardsInfo());
    stringBuilder.append(getReportBreakdown());
    // stringBuilder.append(getReportPoints());
    stringBuilder.append(getReportCards());
    stringBuilder.append(getReportSearhes());
    return stringBuilder.toString();
  }
}
