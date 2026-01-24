package com.cjs.qa.autocoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.QAException;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.RegularExpression;

public class CoderTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(CoderTests.class));

  private static JDBC jdbc = new JDBC("", "AutoCoderExcel");
  public static final String COMMENT = "// ";
  private static final String METHOD_NAME_POPULATE = "populatePage";
  private static final String METHOD_NAME_VALIDATE = "validatePage";
  private static final String CASE_NOT_DEFINED = "Case Not Defined";
  private static final String LABEL_BREAK = "break;";
  private static final String LABEL_FIELD = " field.";
  private static final String LABEL_BUTTON = "Button";
  private static final String LABEL_CHECKBOX = "Checkbox";
  private static final String LABEL_DROPDOWN = "Dropdown";
  private static final String LABEL_EDIT = "Edit";
  private static final String LABEL_OPTION = "Option";
  private static final String LABEL_RADIOBUTTON = "RadioButton";
  protected static final List<String> TYPES_INPUT =
      List.of(LABEL_CHECKBOX, LABEL_DROPDOWN, LABEL_EDIT, LABEL_OPTION, LABEL_RADIOBUTTON);

  @Test
  public void testCodeCreation() throws QAException, Exception {
    String pageName = "ConnectionsPage";
    String code = createCode(pageName);
    String fileNamePath =
        jdbc.getDbParameters().getName().replace(IExtension.SQLITE, IExtension.JAVA);
    FSOTests.fileWrite(fileNamePath, code, false);
  }

  private static void getRecordsFromExcel(String pageName) throws QAException, Exception {
    final String sheetName = pageName; // IExcel.SHEET_DEFAULT;
    String filePathName =
        jdbc.getDbParameters().getName().replaceAll(IExtension.SQLITE, IExtension.XLS);
    // IExcel.updateSummarySheetXLS(filePathName, sheetName, sheetLinkName);
    final XLS excel = new XLS(filePathName, sheetName);
    LOG.debug("excel.getSheetCount():[{}]", excel.getSheetCount());
    int rows = excel.getRowCount(sheetName);
    LOG.debug("rows:[[{}]", rows + "]");
    jdbc.executeUpdate(JDBCConstants.DELETE_FROM + "[tObjects];", false);
    jdbc.executeUpdate(JDBCConstants.DELETE_FROM + "[tObjectPages];", false);
    jdbc.executeUpdate(
        JDBCConstants.INSERT_INTO
            + "[tObjectPages] ([Page],[URL]) VALUES ('"
            + pageName
            + "','URL');",
        false);
    Map<String, String> recordMap = new HashMap<>();
    for (int row = 1; row <= rows; row++) {
      recordMap.put("Page", pageName);
      recordMap.put("Location", String.valueOf(row));
      recordMap.put("Type", excel.readCell(sheetName, 0, row));
      recordMap.put("Name", excel.readCell(sheetName, 1, row));
      recordMap.put("xPath", excel.readCell(sheetName, 2, row));
      recordMap.put("HasParameter", excel.readCell(sheetName, 3, row));
      LOG.debug("recordMap:[{}]", recordMap.toString());
      StringBuilder stringBuilder =
          SQL.appendStringBuilderSQLInsertRecord("tObjects", new StringBuilder(), recordMap, true);
      LOG.debug("stringBuilder.toString():[{}]", stringBuilder.toString());
      jdbc.executeUpdate(stringBuilder.toString(), false);
    }
    excel.close();
  }

  public static String createCode(String currentPage) throws QAException, Exception {
    getRecordsFromExcel(currentPage);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL_FROM);
    stringBuilder.append("[vMethods_All] ");
    stringBuilder.append(JDBCConstants.WHERE + "[Page]='" + currentPage + "' ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Index], [Location], [Method];");
    LOG.debug("stringBuilder.toString():[{}]", stringBuilder.toString());
    final String codeQuery = stringBuilder.toString();
    stringBuilder = new StringBuilder();
    stringBuilder.append(
        JDBCConstants.SELECT_DISTINCT + "[Type], [Name], [PagePopulate], [PageValidate] ");
    stringBuilder.append(JDBCConstants.FROM + "[vMethods_All] ");
    stringBuilder.append(JDBCConstants.WHERE + "[Page]='" + currentPage + "' ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Name];");
    LOG.debug("stringBuilder.toString():[{}]", stringBuilder.toString());
    List<Map<Integer, String>> recordsOtherListMap =
        jdbc.queryResultsIndex(stringBuilder.toString(), true);
    stringBuilder = new StringBuilder();
    StringBuilder stringBuilderAll = new StringBuilder();
    Map<String, Integer> mapOtherFields = null;
    if (!recordsOtherListMap.isEmpty()) {
      mapOtherFields = getHashMapFields(recordsOtherListMap.get(0));
      recordsOtherListMap.remove(0);
    }
    stringBuilderAll.append(getPackageImports(currentPage));
    stringBuilderAll.append(getDocumentation(recordsOtherListMap, mapOtherFields));
    stringBuilderAll.append(getInputs(recordsOtherListMap, mapOtherFields));
    stringBuilderAll.append(getTable(recordsOtherListMap, mapOtherFields));
    stringBuilderAll.append(Constants.nlTab(1, 0));
    final List<Map<Integer, String>> recordsCodeListMap = jdbc.queryResultsIndex(codeQuery, true);
    Map<String, Integer> mapCodeFields = null;
    if (!recordsCodeListMap.isEmpty()) {
      mapCodeFields = getHashMapFields(recordsCodeListMap.get(0));
      recordsCodeListMap.remove(0);
    }
    for (Map<Integer, String> mapRecord : recordsCodeListMap) {
      if (mapCodeFields == null) {
        continue; // Skip if mapCodeFields is null
      }
      String type = mapRecord.get(mapCodeFields.get("Type"));
      String name = mapRecord.get(mapCodeFields.get("Name"));
      String xPath = mapRecord.get(mapCodeFields.get("xPath"));
      String methodCode = mapRecord.get(mapCodeFields.get("MethodCode"));
      String methodName = RegularExpression.getOnlyAlphaNumericCharacters(name);
      methodCode =
          methodCode
              .replaceAll(
                  Constants.SYMBOL_TRADEMARK + "OBJECT_TYPE" + Constants.SYMBOL_TRADEMARK, type)
              .replaceAll(
                  Constants.SYMBOL_TRADEMARK + "OBJECT_NAME" + Constants.SYMBOL_TRADEMARK,
                  methodName)
              .replaceAll(Constants.SYMBOL_TRADEMARK + "XPATH" + Constants.SYMBOL_TRADEMARK, xPath);
      stringBuilder.append(methodCode);
      stringBuilder.append(Constants.nlTab(1, 0));
    }
    stringBuilderAll.append(stringBuilder.toString());
    stringBuilderAll.append(getPagePopulateValidate(recordsOtherListMap, mapOtherFields));
    stringBuilderAll.append(Constants.nlTab(1, 0) + "}");
    return stringBuilderAll.toString();
  }

  private static Map<String, Integer> getHashMapFields(Map<Integer, String> map) {
    Map<String, Integer> mapFields = new HashMap<>();
    for (Entry<Integer, String> entry : map.entrySet()) {
      int index = entry.getKey();
      String fieldName = entry.getValue();
      mapFields.put(fieldName, index);
    }
    return mapFields;
  }

  private static String getPackageImports(String currentPage) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(0, 0) + "package com.cjsconsulting.gui;");
    stringBuilder.append(Constants.nlTab(2, 0) + "import java.util.ArrayList;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import java.util.HashMap;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import java.util.List;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import java.util.Map;");
    stringBuilder.append(Constants.nlTab(2, 0) + "import org.junit.Assert;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import org.openqa.selenium.By;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import org.openqa.selenium.WebDriver;");
    stringBuilder.append(Constants.nlTab(2, 0) + "import org.apache.logging.log4j.LogManager;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import org.apache.logging.log4j.Logger;");
    stringBuilder.append(Constants.nlTab(2, 0) + "import com.cjs.qa.selenium.Page;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import com.cjs.qa.selenium.ISelenium;");
    stringBuilder.append(Constants.nlTab(1, 0) + "import com.cjs.qa.utilities.CJSConstants;");
    stringBuilder.append(Constants.nlTab(2, 0) + "import io.cucumber.datatable.DataTable;");
    stringBuilder.append(Constants.nlTab(2, 0) + "public class " + currentPage + " extends Page");
    stringBuilder.append(Constants.nlTab(1, 0) + "{");
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + "private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger("
            + currentPage
            + ".class));");
    stringBuilder.append(Constants.nlTab(1, 0));
    stringBuilder.append(Constants.nlTab(1, 1) + "public " + currentPage + "(WebDriver webDriver)");
    stringBuilder.append(Constants.nlTab(1, 1) + "{");
    stringBuilder.append(Constants.nlTab(1, 2) + "super(webDriver);");
    stringBuilder.append(Constants.nlTab(1, 1) + "}");
    return stringBuilder.toString();
  }

  private static String getDocumentation(
      List<Map<Integer, String>> recordsOtherListMap, Map<String, Integer> mapFields) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(1, 0));
    stringBuilder.append(Constants.nlTab(1, 1) + COMMENT + "DOCUMENTATION");
    for (Map<Integer, String> mapRecord : recordsOtherListMap) {
      String type = mapRecord.get(mapFields.get("Type"));
      String name = mapRecord.get(mapFields.get("Name"));
      String methodName = RegularExpression.getOnlyAlphaNumericCharacters(name);
      switch (type) {
        case LABEL_BUTTON:
        case "Link":
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "click"
                  + type
                  + methodName
                  + ":"
                  + Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "Clicks the ["
                  + name
                  + "] "
                  + type
                  + ".");
          break;
        case LABEL_CHECKBOX:
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "get"
                  + type
                  + methodName
                  + ":"
                  + Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "Gets the value of the ["
                  + name
                  + "] "
                  + type
                  + LABEL_FIELD);
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "set"
                  + type
                  + methodName
                  + ":"
                  + Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "Sets the value of the ["
                  + name
                  + "] "
                  + type
                  + LABEL_FIELD);
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "toggle"
                  + type
                  + methodName
                  + ":"
                  + Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "Toggles the value of the ["
                  + name
                  + "] Field regardless of what the current value is.");
          break;
        case LABEL_DROPDOWN:
        case LABEL_EDIT:
        case LABEL_OPTION:
        case LABEL_RADIOBUTTON:
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "get"
                  + type
                  + methodName
                  + ":"
                  + Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "Gets the value of the ["
                  + name
                  + "] "
                  + type
                  + LABEL_FIELD);
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "set"
                  + type
                  + methodName
                  + ":"
                  + Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "Sets the value of the ["
                  + name
                  + "] "
                  + type
                  + LABEL_FIELD);
          break;
        default:
          stringBuilder.append(CASE_NOT_DEFINED + "-getDocumentation");
          break;
      }
    }
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + COMMENT
            + Constants.nlTab(0, 1)
            + METHOD_NAME_POPULATE
            + ":"
            + Constants.nlTab(1, 1)
            + COMMENT
            + Constants.nlTab(0, 1)
            + "Populates the value of the all of the fields.");
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + COMMENT
            + Constants.nlTab(0, 1)
            + METHOD_NAME_VALIDATE
            + ":"
            + Constants.nlTab(1, 1)
            + COMMENT
            + Constants.nlTab(0, 1)
            + "Validates the value of the all of the fields.");
    return stringBuilder.toString();
  }

  private static String getInputs(
      List<Map<Integer, String>> recordsOtherListMap, Map<String, Integer> mapFields) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(1, 0));
    stringBuilder.append(Constants.nlTab(1, 1) + COMMENT + "INPUTS");
    for (Map<Integer, String> mapRecord : recordsOtherListMap) {
      String type = mapRecord.get(mapFields.get("Type"));
      String name = mapRecord.get(mapFields.get("Name"));
      String methodName = RegularExpression.getOnlyAlphaNumericCharacters(name);
      switch (type) {
        case LABEL_BUTTON:
        case "Link":
          break;
        case LABEL_CHECKBOX:
          stringBuilder.append(
              Constants.nlTab(1, 1) + COMMENT + Constants.nlTab(0, 1) + "|" + name + "|checked|");
          break;
        default:
          stringBuilder.append(
              Constants.nlTab(1, 1)
                  + COMMENT
                  + Constants.nlTab(0, 1)
                  + "|"
                  + name
                  + "|"
                  + methodName
                  + "|");
          break;
      }
    }
    return stringBuilder.toString();
  }

  private static String getTable(
      List<Map<Integer, String>> recordsOtherListMap, Map<String, Integer> mapFields) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(1, 0));
    stringBuilder.append(Constants.nlTab(1, 1) + COMMENT + "TABLE DEF");
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + COMMENT
            + " DataTable dataTable = Convert.toDataTable(Arrays.asList(");
    for (Map<Integer, String> mapRecord : recordsOtherListMap) {
      String type = mapRecord.get(mapFields.get("Type"));
      String name = mapRecord.get(mapFields.get("Name"));
      switch (type) {
        case LABEL_BUTTON:
        case "Label":
          break;
        case LABEL_CHECKBOX:
          stringBuilder.append(
              Constants.nlTab(1, 3)
                  + COMMENT
                  + "Arrays.asList("
                  + Constants.QUOTE_DOUBLE
                  + name
                  + Constants.QUOTE_DOUBLE
                  + ", "
                  + Constants.QUOTE_DOUBLE
                  + "checked"
                  + Constants.QUOTE_DOUBLE
                  + "),");
          break;
        default:
          stringBuilder.append(
              Constants.nlTab(1, 3)
                  + COMMENT
                  + "Arrays.asList("
                  + Constants.QUOTE_DOUBLE
                  + name
                  + Constants.QUOTE_DOUBLE
                  + ", "
                  + Constants.QUOTE_DOUBLE
                  + name
                  + Constants.QUOTE_DOUBLE
                  + "),");
          break;
      }
    }
    String newString = stringBuilder.toString();
    newString = newString.substring(0, newString.length() - 1) + "))";
    return newString;
  }

  private static String getPagePopulateValidate(
      List<Map<Integer, String>> recordsOtherListMap, Map<String, Integer> mapFields) {
    StringBuilder stringBuilder = new StringBuilder();
    // Populate
    stringBuilder.append(getPagePre(METHOD_NAME_POPULATE));
    for (Map<Integer, String> mapRecord : recordsOtherListMap) {
      String type = mapRecord.get(mapFields.get("Type"));
      String name = mapRecord.get(mapFields.get("Name"));
      String pagePopulate = mapRecord.get(mapFields.get("PagePopulate"));
      String methodName = RegularExpression.getOnlyAlphaNumericCharacters(name);
      if ("true".equals(pagePopulate)) {
        stringBuilder.append(getPageCasePopulate(type, name, methodName));
      }
    }
    stringBuilder.append(getPagePost(METHOD_NAME_POPULATE));
    stringBuilder.append(Constants.nlTab(1, 0));
    // Validate
    stringBuilder.append(getPagePre(METHOD_NAME_VALIDATE));
    for (Map<Integer, String> mapRecord : recordsOtherListMap) {
      String type = mapRecord.get(mapFields.get("Type"));
      String name = mapRecord.get(mapFields.get("Name"));
      String pageValidate = mapRecord.get(mapFields.get("PageValidate"));
      String methodName = RegularExpression.getOnlyAlphaNumericCharacters(name);
      if ("true".equals(pageValidate)) {
        stringBuilder.append(getPageCaseValidate(type, name, methodName));
      }
    }
    stringBuilder.append(getPagePost(METHOD_NAME_VALIDATE));
    return stringBuilder.toString();
  }

  private static String getPageCasePopulate(String type, String name, String methodName) {
    StringBuilder stringBuilder = new StringBuilder();
    if (TYPES_INPUT.contains(type)) {
      stringBuilder.append(
          Constants.nlTab(1, 4)
              + "case "
              + Constants.QUOTE_DOUBLE
              + name.toLowerCase(Locale.ENGLISH)
              + Constants.QUOTE_DOUBLE
              + ":");
      String methodCall = "";
      switch (type) {
        case LABEL_BUTTON:
        case "Link":
          methodCall = "click" + type + methodName + "();";
          break;
        case LABEL_CHECKBOX:
        case LABEL_EDIT:
          methodCall = "set" + type + methodName + "(value);";
          break;
        case LABEL_DROPDOWN:
        case LABEL_OPTION:
        case LABEL_RADIOBUTTON:
          methodCall = "select" + type + methodName + "(value);";
          break;
        default:
          LOG.warn("{}:getPageCasePopulate", CASE_NOT_DEFINED);
          break;
      }
      stringBuilder.append(Constants.nlTab(1, 5) + methodCall);
      stringBuilder.append(Constants.nlTab(1, 5) + LABEL_BREAK);
    }
    return stringBuilder.toString();
  }

  private static String getPageCaseValidate(String type, String name, String methodName) {
    List<String> cases =
        Arrays.asList("Checkbox;Dropdown;Edit;RadioButton".split(Constants.DELIMETER_LIST));
    StringBuilder stringBuilder = new StringBuilder();
    if (cases.contains(type)) {
      stringBuilder.append(
          Constants.nlTab(1, 4)
              + "case "
              + Constants.QUOTE_DOUBLE
              + name.toLowerCase(Locale.ENGLISH)
              + Constants.QUOTE_DOUBLE
              + " :");
      stringBuilder.append(Constants.nlTab(1, 5) + "value = get" + type + methodName + "();");
      stringBuilder.append(Constants.nlTab(1, 5) + LABEL_BREAK);
    }
    return stringBuilder.toString();
  }

  private static String getPagePost(String methodName) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.nlTab(1, 4) + "default:");
    stringBuilder.append(
        Constants.nlTab(1, 5)
            + "value = "
            + Constants.QUOTE_DOUBLE
            + "["
            + Constants.QUOTE_DOUBLE
            + " + field + "
            + Constants.QUOTE_DOUBLE
            + "]"
            + Constants.QUOTE_DOUBLE
            + " + ISelenium.FIELD_NOT_CODED;");
    stringBuilder.append(
        Constants.nlTab(1, 5)
            + "LOG.warn("
            + Constants.QUOTE_DOUBLE
            + "[{}]{}]"
            + Constants.QUOTE_DOUBLE
            + ", field, ISelenium.FIELD_NOT_CODED);");
    stringBuilder.append(Constants.nlTab(1, 5) + LABEL_BREAK);
    stringBuilder.append(Constants.nlTab(1, 4) + "}");
    if (METHOD_NAME_VALIDATE.equals(methodName)) {
      stringBuilder.append(Constants.nlTab(1, 4) + "actual.put(field, value);");
    }
    stringBuilder.append(Constants.nlTab(1, 3) + "}");
    stringBuilder.append(Constants.nlTab(1, 2) + "}");
    if (METHOD_NAME_VALIDATE.equals(methodName)) {
      stringBuilder.append(
          Constants.nlTab(1, 2)
              + "Assert.assertSame(this.getClass().getName() + "
              + Constants.QUOTE_DOUBLE
              + "validatePage"
              + Constants.QUOTE_DOUBLE
              + ", expected.toString(), actual.toString());");
    }
    stringBuilder.append(Constants.nlTab(1, 1) + "}");
    return stringBuilder.toString();
  }

  private static String getPagePre(String methodName) {
    StringBuilder stringBuilder = new StringBuilder();
    if (METHOD_NAME_POPULATE.equals(methodName)) {
      stringBuilder.append(Constants.nlTab(1, 1) + COMMENT + "SWITCHES POPULATE");
    } else {
      stringBuilder.append(Constants.nlTab(1, 1) + COMMENT + "SWITCHES VALIDATE");
    }
    stringBuilder.append(
        Constants.nlTab(1, 1) + "public void " + methodName + "(DataTable dataTable) {");
    if (METHOD_NAME_VALIDATE.equals(methodName)) {
      stringBuilder.append(
          Constants.nlTab(1, 2) + "Map<String, String> expected = new HashMap<>();");
      stringBuilder.append(Constants.nlTab(1, 2) + "Map<String, String> actual = new HashMap<>();");
    }
    stringBuilder.append(Constants.nlTab(1, 2) + "List<List<String>> list = dataTable.asLists();");
    stringBuilder.append(Constants.nlTab(1, 2) + "for (List<?> item : list) {");
    stringBuilder.append(Constants.nlTab(1, 3) + "String field = (String) item.get(0);");
    stringBuilder.append(Constants.nlTab(1, 3) + "String value = (String) item.get(1);");
    stringBuilder.append(
        Constants.nlTab(1, 3)
            + "if (!value.equals("
            + Constants.QUOTE_DOUBLE
            + Constants.QUOTE_DOUBLE
            + ")) {");
    stringBuilder.append(
        Constants.nlTab(1, 4)
            + "LOG.debug("
            + Constants.QUOTE_DOUBLE
            + "{Field}: {}, {Value}: {}"
            + Constants.QUOTE_DOUBLE
            + ", field, value);");
    if (METHOD_NAME_VALIDATE.equals(methodName)) {
      stringBuilder.append(Constants.nlTab(1, 4) + "expected.put(field, value);");
    }
    stringBuilder.append(Constants.nlTab(1, 4) + "switch (field.toLowerCase(Locale.ENGLISH)) {");
    return stringBuilder.toString();
  }
}
