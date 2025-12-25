package com.cjs.qa.microsoft.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.cjs.qa.microsoft.MicrosoftReport;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.colors.ColorsHEX;

public class PointsCard {

  public static final List<String> REPORT_FIELD_LIST =
      Arrays.asList(
          "Method",
          "xPath",
          "Index",
          "Collectable",
          "Collected",
          "Points Top",
          "Group",
          "Description",
          "Points Bottom");
  private static List<PointsCard> pointsCardList = new ArrayList<>();

  public static List<PointsCard> getPointsCardList() {
    return pointsCardList;
  }

  public static List<String> getReportFieldList() {
    return REPORT_FIELD_LIST;
  }

  public static void setPointsCardList(List<PointsCard> pointsCardList) {
    PointsCard.pointsCardList = pointsCardList;
  }

  public static String toReport() {
    final StringBuilder stringBuilder = new StringBuilder();
    for (final PointsCard pointsCard : getPointsCardList()) {
      stringBuilder.append("<tr");
      String backgroundColor = ColorsHEX.WHITE.getValue();
      final String method = pointsCard.getMethod();
      switch (method) {
        case "getPointsDashboardAvailable20MoreActivities":
          backgroundColor = ColorsHEX.LIGHTGRAY.getValue();
          break;
        case "getPointsDashboardAvailable30SneakPeek":
          backgroundColor = ColorsHEX.DARKGRAY.getValue();
          break;
        case "getPointsDashboardAvailable10DailySet":
        default:
          backgroundColor = ColorsHEX.WHITE.getValue();
          break;
      }
      stringBuilder.append(
          " style="
              + Constants.QUOTE_DOUBLE
              + "background-color:"
              + backgroundColor
              + Constants.QUOTE_DOUBLE);
      stringBuilder.append(">");
      for (final String field : getReportFieldList()) {
        String value = MicrosoftReport.getFieldNotCoded(field);
        switch (field) {
          case "Description":
            value = MicrosoftReport.getFieldHTML(pointsCard.getDescription());
            break;
          case "Collectable":
            value = MicrosoftReport.getFieldHTML(String.valueOf(pointsCard.isCollectable()));
            break;
          case "Collected":
            value = MicrosoftReport.getFieldHTML(String.valueOf(pointsCard.isCollected()));
            break;
          case "Group":
            value = MicrosoftReport.getFieldHTML(pointsCard.getGroup());
            break;
          case "Index":
            value = MicrosoftReport.getFieldHTML(String.valueOf(pointsCard.getIndex()));
            break;
          case "Method":
            value = MicrosoftReport.getFieldHTML(pointsCard.getMethod());
            break;
          case "Points Bottom":
            value = MicrosoftReport.getFieldHTML(pointsCard.getPointsBottom());
            break;
          case "Points Top":
            value =
                MicrosoftReport.getFieldHTML(
                    JavaHelpers.formatNumber(
                        Double.valueOf(pointsCard.getPointsTop()),
                        MicrosoftReport.getFormatNumber()));
            break;
          case "xPath":
            value =
                MicrosoftReport.getFieldHTML(pointsCard.getxPath().replaceAll("By.xpath: ", ""));
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
  private boolean collectable = false;
  private String group = "Group";
  private boolean collected = false;
  private String description = "Description";
  private int pointsTop = -1;
  private String pointsBottom = "0 points";

  public String getDescription() {
    return description;
  }

  public String getGroup() {
    return group;
  }

  public int getIndex() {
    return index;
  }

  public String getMethod() {
    return method;
  }

  public String getPointsBottom() {
    return pointsBottom;
  }

  public int getPointsTop() {
    return pointsTop;
  }

  public WebElement getWebElement() {
    return webElement;
  }

  public String getxPath() {
    return xPath;
  }

  public boolean isCollectable() {
    return collectable;
  }

  public boolean isCollected() {
    return collected;
  }

  public void setCollectable(boolean collectable) {
    this.collectable = collectable;
  }

  public void setCollected(boolean collected) {
    this.collected = collected;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public void setPointsBottom(String pointsBottom) {
    this.pointsBottom = pointsBottom;
  }

  public void setPointsTop(int pointsTop) {
    this.pointsTop = pointsTop;
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
    stringBuilder.append("Points Top:" + getPointsTop());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Collectable:" + isCollectable());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Collected:" + isCollected());
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
    stringBuilder.append("Collectable:" + isCollectable());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Collected:" + isCollected());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Points Top:" + getPointsTop());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Group:" + getGroup());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Description:" + getDescription());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Points Bottom:" + getPointsBottom());
    return stringBuilder.toString();
  }
}
