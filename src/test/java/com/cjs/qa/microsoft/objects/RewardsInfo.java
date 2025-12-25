package com.cjs.qa.microsoft.objects;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.cjs.qa.microsoft.MicrosoftReport;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.colors.ColorsHEX;

public class RewardsInfo {

  public static final List<String> REPORT_FIELD_LIST =
      Arrays.asList("Method", "xPath", "Field", "Value");
  private static List<RewardsInfo> rewardsInfoList;

  public static List<String> getReportFieldList() {
    return REPORT_FIELD_LIST;
  }

  public static List<RewardsInfo> getRewardsInfoList() {
    return rewardsInfoList;
  }

  public static void setRewardsInfoList(List<RewardsInfo> rewardsInfoList) {
    RewardsInfo.rewardsInfoList = rewardsInfoList;
  }

  public static String toReport() {
    final StringBuilder stringBuilder = new StringBuilder();
    for (final RewardsInfo rewardsInfo : getRewardsInfoList()) {
      stringBuilder.append("<tr>");
      for (final String field : getReportFieldList()) {
        String value = MicrosoftReport.getFieldNotCoded(field);
        switch (field) {
          case "Method":
            value = MicrosoftReport.getFieldHTML(rewardsInfo.getMethod());
            break;
          case "xPath":
            value =
                MicrosoftReport.getFieldHTML(rewardsInfo.getxPath().replaceAll("By.xpath: ", ""));
            break;
          case "Field":
            value = MicrosoftReport.getFieldHTML(rewardsInfo.getField());
            break;
          case "Value":
            value = rewardsInfo.getValue();
            final String fieldName = rewardsInfo.getField();
            if (fieldName.contains("Points") && !fieldName.contains("Message")) {
              value =
                  JavaHelpers.formatNumber(
                      Double.valueOf(rewardsInfo.getValue()), MicrosoftReport.getFormatNumber());
            }
            if (fieldName.contains("Needed") && Double.valueOf(rewardsInfo.getValue()) > 0) {
              value =
                  "<div style="
                      + Constants.QUOTE_DOUBLE
                      + "background-color: "
                      + ColorsHEX.LIGHTYELLOW.getValue()
                      + ";"
                      + Constants.QUOTE_DOUBLE
                      + ">"
                      + JavaHelpers.formatNumber(
                          Double.valueOf(rewardsInfo.getValue()),
                          MicrosoftReport.getFormatNumber());
            }
            value = MicrosoftReport.getFieldHTML(value);
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
  private String xPath = "";
  private WebElement webElement;
  private String field = "Field";
  private String value = "Value";

  public RewardsInfo(String method, String xPath, String field, String value) {
    this.method = method;
    this.xPath = xPath;
    this.field = field;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public String getMethod() {
    return method;
  }

  public String getValue() {
    return value;
  }

  public WebElement getWebElement() {
    return webElement;
  }

  public String getxPath() {
    return xPath;
  }

  public void setField(String field) {
    this.field = field;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setValue(String value) {
    this.value = value;
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
    stringBuilder.append("xPath:" + getxPath());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Field:" + getField());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Value:" + getValue());
    return stringBuilder.toString();
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Method:" + getMethod());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("xPath:" + getxPath());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Field:" + getField());
    stringBuilder.append(Constants.NEWLINE + Constants.TAB);
    stringBuilder.append("Value:" + getValue());
    return stringBuilder.toString();
  }
}
