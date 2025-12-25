package com.cjs.qa.vivit;

import java.util.Arrays;
import java.util.List;

import com.cjs.qa.utilities.Constants;

@Deprecated
public class VivitAPIDatabaseUpdates {

  private static List<String> fieldList = Arrays.asList("API Method", "Table", "Field", "Action");

  public static List<String> getFieldList() {
    return fieldList;
  }

  private String apiMethod;
  private String tableName;
  private String fieldName;
  private String action;

  public VivitAPIDatabaseUpdates(
      String apiMethod, String tableName, String fieldName, String action) {
    this.apiMethod = apiMethod;
    this.tableName = tableName;
    this.fieldName = fieldName;
    this.action = action;
  }

  public String getAction() {
    return action;
  }

  public String getApiMethod() {
    return apiMethod;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public void setApiMethod(String apiMethod) {
    this.apiMethod = apiMethod;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  private String getFieldHTML(String fieldValue) {
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

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<tr>");
    stringBuilder.append(getFieldHTML(getApiMethod()));
    stringBuilder.append(getFieldHTML(getTableName()));
    stringBuilder.append(getFieldHTML(getFieldName()));
    stringBuilder.append(getFieldHTML(getAction()));
    stringBuilder.append("</tr>");
    return stringBuilder.toString();
  }
}
