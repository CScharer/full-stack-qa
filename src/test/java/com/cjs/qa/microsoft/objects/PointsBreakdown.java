package com.cjs.qa.microsoft.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.cjs.qa.microsoft.MicrosoftReport;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.JavaHelpers;

public class PointsBreakdown {

  public static final List<String> REPORT_FIELD_LIST =
      Arrays.asList(
          "Method",
          "xPath",
          "Index",
          "Group",
          "Description",
          "Points (Available)",
          "Points (Earned)");
  private static List<PointsBreakdown> pointsBreakdownList = new ArrayList<>();

  public static List<PointsBreakdown> getPointsBreakdownList() {
    return pointsBreakdownList;
  }

  public static List<String> getReportFieldList() {
    return REPORT_FIELD_LIST;
  }

  public static void setPointsBreakdownList(List<PointsBreakdown> pointsBreakdownList) {
    PointsBreakdown.pointsBreakdownList = pointsBreakdownList;
  }

  public static String toReport() {
    final StringBuilder stringBuilder = new StringBuilder();
    for (final PointsBreakdown pointsBreakdown : getPointsBreakdownList()) {
      stringBuilder.append("<tr>");
      for (final String field : getReportFieldList()) {
        String value = MicrosoftReport.getFieldNotCoded(field);
        switch (field) {
          case "Description":
            value = MicrosoftReport.getFieldHTML(pointsBreakdown.getDescription());
            break;
          case "Group":
            value = MicrosoftReport.getFieldHTML(pointsBreakdown.getGroup());
            break;
          case "Index":
            value = MicrosoftReport.getFieldHTML(String.valueOf(pointsBreakdown.getIndex()));
            break;
          case "Method":
            value = MicrosoftReport.getFieldHTML(pointsBreakdown.getMethod());
            break;
          case "Points (Available)":
            value =
                MicrosoftReport.getFieldHTML(
                    JavaHelpers.formatNumber(
                        Double.valueOf(pointsBreakdown.getPointsAvailable()),
                        MicrosoftReport.getFormatNumber()));
            break;
          case "Points (Earned)":
            value =
                MicrosoftReport.getFieldHTML(
                    JavaHelpers.formatNumber(
                        Double.valueOf(pointsBreakdown.getPointsEarned()),
                        MicrosoftReport.getFormatNumber()));
            break;
          case "xPath":
            value =
                MicrosoftReport.getFieldHTML(
                    pointsBreakdown.getxPath().replaceAll("By.xpath: ", ""));
            break;
          default:
            break;
        }
        stringBuilder.append(value);
      }
      stringBuilder.append("</tr>");
    }
    return stringBuilder.toString();
  }

  private String method = "Method";
  private int index = -1;
  private String xPath = "";
  private WebElement webElement;
  private String group = "Group";
  private String description = "Description";
  private int pointsAvailable = -1;
  private int pointsEarned = -1;

  public String getGroup() {
    return group;
  }

  public int getIndex() {
    return index;
  }

  public String getMethod() {
    return method;
  }

  public int getPointsAvailable() {
    return pointsAvailable;
  }

  public int getPointsEarned() {
    return pointsEarned;
  }

  public WebElement getWebElement() {
    return webElement;
  }

  public String getxPath() {
    return xPath;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setPointsAvailable(int pointsAvailable) {
    this.pointsAvailable = pointsAvailable;
  }

  public void setPointsEarned(int pointsEarned) {
    this.pointsEarned = pointsEarned;
  }

  public void setWebElement(WebElement webElement) {
    this.webElement = webElement;
  }

  public void setxPath(String xPath) {
    this.xPath = xPath;
  }

  public String toMessage() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Method:" + getMethod());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Group:" + getGroup());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Points Available:" + getPointsAvailable());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Points Earned:" + getPointsEarned());
    return stringBuilder.toString();
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Method:" + getMethod());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Index:" + getIndex());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("xPath:" + getxPath());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Group:" + getGroup());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Description:" + getDescription());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Points Available:" + getPointsAvailable());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Points Earned:" + getPointsEarned());
    return stringBuilder.toString();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
