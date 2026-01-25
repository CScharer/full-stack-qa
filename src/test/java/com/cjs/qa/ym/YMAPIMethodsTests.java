package com.cjs.qa.ym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.ym.api.services.YMService;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public final class YMAPIMethodsTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(YMAPIMethodsTests.class));

  public static final String LABEL_NAME = "Name";
  public static final String LABEL_DESCRIPTION = "Description";
  public static final String LABEL_BOOLEAN = "Boolean";
  public static final String LABEL_INTEGER = "Integer";
  public static final String LABEL_STRING = "String";
  public static final String VIEW_VIVIT_YMAPI_NAMESPACES = "v_Vivit_YMAPI_Namespaces";
  public static final String VIEW_VIVIT_YMAPI_NAMESPACESMETHODS = "v_Vivit_YMAPI_NamespacesMethods";
  public static final String VIEW_VIVIT_YMAPI_METHODWITHPARAMETERS =
      "v_Vivit_YMAPI_MethodWithParameters";
  // CJSConstants.PATH_FILES_TEMPLATES
  static final String PATH_TEMPLATES =
      Environment.getFolderData() + "Templates" + Constants.DELIMETER_PATH;
  private static JDBC jdbc = new JDBC("", "QAAuto");

  private YMAPIMethodsTests() {
    // Empty
  }

  @Test
  public void createClassesTest() throws Throwable {
    createClasses();
  }

  public static void createClasses() throws Throwable {
    try {
      final String fileTemplateYMAPIClass = PATH_TEMPLATES + "TemplateYMAPIClass" + IExtension.TXT;
      LOG.debug("fileTemplateYMAPIClass:[[{}]", fileTemplateYMAPIClass + "]");
      final String templateYMAPIClass = FSOTests.fileReadAll(fileTemplateYMAPIClass);
      LOG.debug("templateClass:[[{}]", templateYMAPIClass + "]");
      final String namespaces =
          jdbc.queryResults(
              JDBCConstants.SELECT_ALL
                  + JDBCConstants.FROM
                  + "["
                  + VIEW_VIVIT_YMAPI_NAMESPACES
                  + "]",
              "",
              false);
      final String[] arrayNamespaces = namespaces.split(Constants.NEWLINE);
      for (final String namespace : arrayNamespaces) {
        final String newNamespace = namespace.replaceAll(Constants.DELIMETER_PATH + ".", "_");
        String newTemplateYMAPIClass = templateYMAPIClass;
        newTemplateYMAPIClass =
            newTemplateYMAPIClass.replace("[CLASS_NAMESPACE]", newNamespace + "Namespace");
        final String methods = getMethods(namespace);
        LOG.debug("methods:[[{}]", methods + "]");
        newTemplateYMAPIClass = newTemplateYMAPIClass.replace("[CLASS_METHODS]", methods);
        newTemplateYMAPIClass =
            newTemplateYMAPIClass.replaceAll(
                Constants.NEWLINE + Constants.NEWLINE, Constants.NEWLINE);
        LOG.debug("newTemplateYMAPIClass:[[{}]", newTemplateYMAPIClass + "]");
        final String fileClass = PATH_TEMPLATES + newNamespace + "Namespace.java";
        FSOTests.fileWrite(fileClass, newTemplateYMAPIClass, false);
      }
      jdbc.close();
    } catch (final Exception e) {
      if (jdbc != null) {
        jdbc.close();
      }
      LOG.error("Exception occurred", e);
    }
    jdbc = null;
  }

  private static String getMethods(String namespace) throws QAException {
    final String fileTemplateYMAPIMethod = PATH_TEMPLATES + "TemplateYMAPIMethod" + IExtension.TXT;
    final String templateYMAPIMethod = FSOTests.fileReadAll(fileTemplateYMAPIMethod);
    LOG.debug("templateYMAPIMEthod:[[{}]", templateYMAPIMethod + "]");
    final String methods =
        jdbc.queryResults(
            JDBCConstants.SELECT_ALL
                + JDBCConstants.FROM
                + "["
                + VIEW_VIVIT_YMAPI_NAMESPACESMETHODS
                + "] "
                + JDBCConstants.WHERE
                + " [Namespace] = '"
                + namespace
                + "'",
            Constants.DELIMETER_LIST,
            false);
    final String[] arrayNamespaceMethods = methods.split(Constants.NEWLINE);
    final StringBuilder stringBuilderMethods = new StringBuilder();
    for (final String namespaceMethod : arrayNamespaceMethods) {
      final String[] arrayNamespaceMethod = namespaceMethod.split(Constants.DELIMETER_LIST);
      final String method = arrayNamespaceMethod[1];
      final String newTemplateYMAPIMethod = templateYMAPIMethod;
      final String newMethod = getMethod(newTemplateYMAPIMethod, namespace, method);
      stringBuilderMethods.append(newMethod);
      stringBuilderMethods.append(Constants.NEWLINE);
      LOG.debug("newTemplateYMAPIMethod:[[{}]", newTemplateYMAPIMethod + "]");
    }
    return stringBuilderMethods.toString();
  }

  private static String getMethod(String newTemplateYMAPIMethod, String namespace, String method)
      throws QAException {
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.SELECT_ALL);
    sqlStringBuilder.append(JDBCConstants.FROM + "[t_DOM_Vivit_YMAPIMethod] ");
    sqlStringBuilder.append(JDBCConstants.WHERE + "[" + LABEL_NAME + "]='" + method + "'");
    final List<Map<String, String>> listMapYMAPIMethods =
        jdbc.queryResultsString(sqlStringBuilder.toString(), false);
    for (final Map<String, String> mapMethod : listMapYMAPIMethods) {
      final String callMethod = method;
      // final List<String> listMethod =
      // Arrays.asList(callMethod.split(Constants.PATH_DELIMETER + "."))
      // listMethod.get((listMethod.size() - 1))
      String methodName = method.replace(namespace + ".", "");
      methodName = methodName.replace(".", "");
      methodName = methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
      String methodDescription = "Description: " + mapMethod.get(LABEL_DESCRIPTION);
      methodDescription = methodDescription.replaceAll(Constants.NEWLINE, Constants.NEWLINE + "//");
      final String methodURL = "URL: " + mapMethod.get("URL");
      newTemplateYMAPIMethod = newTemplateYMAPIMethod.replace("[METHOD_NAME]", methodName);
      newTemplateYMAPIMethod =
          newTemplateYMAPIMethod.replace("[METHOD_DESCRIPTION]", methodDescription);
      newTemplateYMAPIMethod = newTemplateYMAPIMethod.replace("[METHOD_URL]", methodURL);
      newTemplateYMAPIMethod = newTemplateYMAPIMethod.replace("[METHOD_CALL]", callMethod);
      sqlStringBuilder = new StringBuilder();
      sqlStringBuilder.append(JDBCConstants.SELECT_ALL);
      sqlStringBuilder.append(
          JDBCConstants.FROM + "[" + VIEW_VIVIT_YMAPI_METHODWITHPARAMETERS + "] ");
      sqlStringBuilder.append(JDBCConstants.WHERE + "[" + LABEL_NAME + "]='" + callMethod + "'");
      final List<Map<String, String>> listMapMethodParameters =
          jdbc.queryResultsString(sqlStringBuilder.toString(), false);
      StringBuilder stringBuilderCallParameters = new StringBuilder();
      StringBuilder stringBuilderArguments = new StringBuilder();
      for (final Map<String, String> mapMethodParameters : listMapMethodParameters) {
        final String parameterPType = mapMethodParameters.get("PType");
        if ("Call Arguments".equals(parameterPType)) {
          final String paramterCall = mapMethodParameters.get("Name:1");
          // final String parameterType =
          // mapMethodParameters.get("Type")
          // final String parameterMaxLength =
          // mapMethodParameters.get("MaxLength")
          // final String parameterRequired =
          // mapMethodParameters.get("Required")
          String parameterDeclarationType = LABEL_STRING;
          switch (parameterDeclarationType) {
            // type)
            case "32-bit Int":
            case LABEL_INTEGER:
            case "Number":
              parameterDeclarationType = LABEL_INTEGER;
              break;
            case "Decimal":
              parameterDeclarationType = "Double";
              break;
            case LABEL_BOOLEAN:
              parameterDeclarationType = LABEL_BOOLEAN;
              break;
            case "Comma Delimited List":
            case "Date":
            case "DateTime":
            case "GUID":
            case LABEL_STRING:
            case "String Collection":
              break;
            default:
              break;
          }
          if (!"".equals(stringBuilderArguments.toString())) {
            stringBuilderArguments.append(",");
          }
          String paramterName =
              paramterCall.substring(0, 1).toLowerCase(Locale.ENGLISH) + paramterCall.substring(1);
          paramterName = paramterName.replace(" ", "");
          stringBuilderArguments.append(parameterDeclarationType + " " + paramterName);
          stringBuilderCallParameters.append(
              Constants.nlTab(1, 2)
                  + "stringBuilder.append(Constants.nlTab(1, 2) + "
                  + Constants.QUOTE_DOUBLE
                  + "<"
                  + paramterCall
                  + ">"
                  + Constants.QUOTE_DOUBLE
                  + " + "
                  + paramterName
                  + " + "
                  + Constants.QUOTE_DOUBLE
                  + "</"
                  + paramterCall
                  + ">"
                  + Constants.QUOTE_DOUBLE
                  + ");");
        }
      }
      newTemplateYMAPIMethod =
          newTemplateYMAPIMethod.replace("[METHOD_PARAMETERS]", stringBuilderArguments.toString());
      newTemplateYMAPIMethod =
          newTemplateYMAPIMethod.replace(
              "[METHOD_CALL_PARAMETERS]", stringBuilderCallParameters.toString());
    }
    return newTemplateYMAPIMethod;
  }

  public static void createClasses2() throws QAException {
    final String fileTemplateYMAPIClass = PATH_TEMPLATES + "TemplateYMAPIClass" + IExtension.TXT;
    LOG.debug("fileTemplateYMAPIClass:[[{}]", fileTemplateYMAPIClass + "]");
    final String fileTemplateYMAPIMethod = PATH_TEMPLATES + "TemplateYMAPIMethod" + IExtension.TXT;
    LOG.debug("fileTemplateYMAPIMethod:[[{}]", fileTemplateYMAPIClass + "]");
    final String templateYMAPIClass = FSOTests.fileReadAll(fileTemplateYMAPIClass);
    LOG.debug("templateClass:[[{}]", templateYMAPIClass + "]");
    final String templateYMAPIMethod = FSOTests.fileReadAll(fileTemplateYMAPIMethod);
    LOG.debug("templateYMAPIMEthod:[[{}]", templateYMAPIMethod + "]");
    try {
      StringBuilder sqlStringBuilder = new StringBuilder();
      sqlStringBuilder.append(JDBCConstants.SELECT_ALL);
      sqlStringBuilder.append(JDBCConstants.FROM + "[t_DOM_Vivit_YMAPIMethod]");
      final List<Map<String, String>> listMapYMAPIMethods =
          jdbc.queryResultsString(sqlStringBuilder.toString(), false);
      for (final Map<String, String> mapMethod : listMapYMAPIMethods) {
        String newTemplateYMAPIClass = templateYMAPIClass;
        // final String newTemplateYMAPIMethod = templateYMAPIMethod
        final String callMethod = mapMethod.get(LABEL_NAME);
        final List<String> listMethod =
            Arrays.asList(callMethod.split(Constants.DELIMETER_PATH + "."));
        final String methodNameSpace = listMethod.get(0) + "Namespace";
        String methodName = listMethod.get(listMethod.size() - 1);
        methodName =
            methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
        final String methodDescription = mapMethod.get(LABEL_DESCRIPTION);
        final String methodURL = mapMethod.get("URL");
        newTemplateYMAPIClass =
            newTemplateYMAPIClass.replace("[METHOD_NAMESPACE]", methodNameSpace);
        newTemplateYMAPIClass = newTemplateYMAPIClass.replace("[METHOD_NAME]", methodName);
        newTemplateYMAPIClass =
            newTemplateYMAPIClass.replace("[METHOD_DESCRIPTION]", methodDescription);
        newTemplateYMAPIClass = newTemplateYMAPIClass.replace("[METHOD_URL]", methodURL);
        newTemplateYMAPIClass = newTemplateYMAPIClass.replace("[METHOD_CALL]", callMethod);
        List<Map<String, String>> listMapMethodParameters = null;
        if (jdbc != null) {
          sqlStringBuilder = new StringBuilder();
          sqlStringBuilder.append(JDBCConstants.SELECT_ALL);
          sqlStringBuilder.append(
              JDBCConstants.FROM + "[" + VIEW_VIVIT_YMAPI_METHODWITHPARAMETERS + "] ");
          sqlStringBuilder.append(JDBCConstants.WHERE);
          sqlStringBuilder.append("[Name]='" + callMethod + "'");
          listMapMethodParameters = jdbc.queryResultsString(sqlStringBuilder.toString(), false);
        }
        StringBuilder stringBuilderCallParameters = new StringBuilder();
        StringBuilder stringBuilderArguments = new StringBuilder();
        if (listMapMethodParameters != null) {
          for (final Map<String, String> mapMethodParameters : listMapMethodParameters) {
            final String parameterPType = mapMethodParameters.get("PType");
            if ("Call Arguments".equals(parameterPType)) {
              final String paramterCall = mapMethodParameters.get("Name:1");
              // final String parameterType =
              // mapMethodParameters.get("Type")
              // final String parameterMaxLength =
              // mapMethodParameters.get("MaxLength")
              // final String parameterRequired =
              // mapMethodParameters.get("Required")
              String parameterDeclarationType = LABEL_STRING;
              switch (newTemplateYMAPIClass) {
                // type)
                case "32-bit Int":
                case "Decimal":
                case LABEL_INTEGER:
                case "Number":
                  parameterDeclarationType = "int";
                  break;
                case LABEL_BOOLEAN:
                  parameterDeclarationType = "boolean";
                  break;
                case "Comma Delimited List":
                case "Date":
                case "DateTime":
                case "GUID":
                case LABEL_STRING:
                case "String Collection":
                  break;
                default:
                  break;
              }
              if (!"".equals(stringBuilderArguments.toString())) {
                stringBuilderArguments.append(",");
              }
              final String paramterName =
                  paramterCall.substring(0, 1).toLowerCase(Locale.ENGLISH)
                      + paramterCall.substring(1);
              stringBuilderArguments.append(parameterDeclarationType + " " + paramterName);
              stringBuilderCallParameters.append(
                  Constants.nlTab(1, 2)
                      + "stringBuilder.append(Constants.nlTab(1, 2) + "
                      + Constants.QUOTE_DOUBLE
                      + "<"
                      + paramterCall
                      + ">"
                      + Constants.QUOTE_DOUBLE
                      + " + "
                      + paramterName
                      + " + "
                      + Constants.QUOTE_DOUBLE
                      + "</"
                      + paramterCall
                      + ">"
                      + Constants.QUOTE_DOUBLE
                      + ");");
            }
          }
        }
        newTemplateYMAPIClass =
            newTemplateYMAPIClass.replace("[METHOD_PARAMETERS]", stringBuilderArguments.toString());
        newTemplateYMAPIClass =
            newTemplateYMAPIClass.replace(
                "[METHOD_CALL_PARAMETERS]", stringBuilderCallParameters.toString());
        LOG.debug("newTemplateYMAPIClass:[[{}]", newTemplateYMAPIClass + "]");
        final String classFileName = PATH_TEMPLATES + methodNameSpace + ".java";
        FSOTests.fileWrite(classFileName, newTemplateYMAPIClass, false);
        jdbc = null;
      }
    } catch (final Exception e) {
      jdbc = null;
      LOG.error("Exception occurred", e);
    }
  }

  public static void createMapping(WebDriver webDriver) throws QAException {
    Assertions.fail("This has already created the tables.");
    SQL.execute(JDBCConstants.DELETE_FROM + "[t_DOM_Vivit_YMAPIMethod];");
    SQL.execute(JDBCConstants.DELETE_FROM + "[t_DOM_Vivit_YMAPIMethodParameters];");
    final By byMethods = By.xpath("html/body/p/a");
    final By byNamespaces = By.xpath("html/body/h3");
    webDriver.get(YMService.URL_YM_API_DOC + "Methods" + IExtension.HTML);
    final List<WebElement> listWebElementsNamespaces = webDriver.findElements(byNamespaces);
    final List<String> listNamespaces = new ArrayList<>();
    for (final WebElement webElement : listWebElementsNamespaces) {
      String namespace = webElement.getText();
      namespace = namespace.replace(" Namespace", "");
      listNamespaces.add(namespace);
    }
    LOG.debug("listNamespaces:[[{}]", listNamespaces + "]");
    final List<WebElement> listMethods = webDriver.findElements(byMethods);
    final List<String> listMethodLinks = new ArrayList<>();
    final List<Map<String, String>> listMapMethodLinks = new ArrayList<>();
    for (final WebElement webElement : listMethods) {
      final String methodName = webElement.getText();
      listMethodLinks.add(methodName);
      final Map<String, String> mapMethodLinks = new HashMap<>();
      mapMethodLinks.put(methodName, webElement.getAttribute("href"));
      listMapMethodLinks.add(mapMethodLinks);
    }
    LOG.debug("{}", listMapMethodLinks.toString());
    StringBuilder stringBuilder;
    int methodID = 0;
    for (final Map<String, String> mapMethodLinks : listMapMethodLinks) {
      for (Entry<String, String> entry : mapMethodLinks.entrySet()) {
        final String methodName = entry.getKey();
        // final String methodURL = mapMethodLinks.get(methodName)
        final String methodURL = entry.getValue();
        final String methodNamespace = getNamespace(methodName, listNamespaces);
        webDriver.get(methodURL);
        methodID++;
        final WebElement webElement = webDriver.findElement(By.xpath("html/body"));
        final String pageText = webElement.getText();
        LOG.debug("{}", pageText);
        stringBuilder = new StringBuilder();
        stringBuilder.append(JDBCConstants.INSERT_INTO + "[t_DOM_Vivit_YMAPIMethod] (");
        stringBuilder.append(
            "[Namespace],[ID],[URL],[Name],[Description],[ExampleRequest],[ExampleResponse]");
        stringBuilder.append(")" + JDBCConstants.VALUES + "(");
        stringBuilder.append("'" + methodNamespace + "',");
        stringBuilder.append("'" + methodID + "',");
        stringBuilder.append("'" + methodURL + "',");
        stringBuilder.append("'" + methodName + "',");
        Map<String, String> mapValues = new HashMap<>();
        mapValues = getMapValues(methodName, mapValues, pageText);
        stringBuilder.append("'" + mapValues.get("description") + "',");
        stringBuilder.append("'" + mapValues.get("exampleRequest") + "',");
        stringBuilder.append("'" + mapValues.get("exampleResponse") + "');");
        SQL.execute(stringBuilder.toString());
        int parameterID = 0;
        final List<WebElement> webElementRows =
            webDriver.findElements(By.xpath("html/body/table/tbody/tr"));
        String parameterType = "";
        for (final WebElement webElementRow : webElementRows) {
          final List<WebElement> webElementColumns = webElementRow.findElements(By.xpath("./td"));
          int field = 0;
          for (final WebElement webElementColumn : webElementColumns) {
            field++;
            if (webElementColumns.size() == 1) {
              parameterType = webElementColumn.getText();
              LOG.debug("parameterType:[[{}]", parameterType + "]");
            } else {
              final String value = webElementColumn.getText();
              LOG.debug("value:[[{}]", value + "]");
              switch (field) {
                case 1:
                  parameterID++;
                  mapValues.put("Name", value);
                  break;
                case 2:
                  mapValues.put("Type", value);
                  break;
                case 3:
                  mapValues.put("MaxLength", value);
                  break;
                case 4:
                  mapValues.put("Required", value);
                  break;
                case 5:
                  mapValues.put(LABEL_DESCRIPTION, value);
                  stringBuilder = new StringBuilder();
                  stringBuilder.append(
                      JDBCConstants.INSERT_INTO + "[t_DOM_Vivit_YMAPIMethodParameters] (");
                  stringBuilder.append(
                      "[MethodID],[ID],[PType],[Name],[Type],[MaxLength],[Required],[Description]");
                  stringBuilder.append(")" + JDBCConstants.VALUES + "(");
                  stringBuilder.append("'" + methodID + "',");
                  stringBuilder.append("'" + parameterID + "',");
                  stringBuilder.append("'" + parameterType + "',");
                  stringBuilder.append("'" + mapValues.get("Name").replaceAll("'", "''") + "',");
                  stringBuilder.append("'" + mapValues.get("Type").replaceAll("'", "''") + "',");
                  stringBuilder.append(
                      "'" + mapValues.get("MaxLength").replaceAll("'", "''") + "',");
                  stringBuilder.append(
                      "'" + mapValues.get("Required").replaceAll("'", "''") + "',");
                  stringBuilder.append(
                      "'" + mapValues.get(LABEL_DESCRIPTION).replaceAll("'", "''") + "');");
                  SQL.execute(stringBuilder.toString());
                  break;
                default:
                  break;
              }
            }
          }
        }
      }
    }
  }

  private static String getNamespace(String methodName, List<String> listNamespaces)
      throws QAException {
    final String namespaceAdmin = "Sa";
    String namespace = null;
    final String[] methodElements = methodName.split(Constants.DELIMETER_PATH + ".");
    String methodNameSearch = methodElements[0];
    if (methodElements[0].equals(namespaceAdmin)) {
      methodNameSearch = methodElements[0] + "." + methodElements[1];
    }
    for (final String namespaceFound : listNamespaces) {
      if (methodNameSearch.equals(namespaceFound)) {
        namespace = namespaceFound;
        break;
      }
    }
    if (namespace == null) {
      LOG.error("ERROR");
    }
    LOG.debug("namespace:[[{}]", namespace + "], methodName:[" + methodName + "]");
    return namespace;
  }

  private static Map<String, String> getMapValues(
      String methodName, Map<String, String> mapValues, String pageText) throws QAException {
    mapValues.put("description", "");
    mapValues.put("exampleRequest", "");
    mapValues.put("exampleResponse", "");
    try {
      String description =
          pageText.substring(
              pageText.indexOf(methodName) + methodName.length(), pageText.indexOf("Parameters"));
      description = description.trim();
      description = description.replaceAll("'", "''");
      mapValues.put("description", description);
    } catch (final Exception e1) {
      try {
        String description = pageText.substring(0, pageText.indexOf("Parameters"));
        description = description.trim();
        description = description.replaceAll("'", "''");
        mapValues.put("description", description);
      } catch (final Exception e2) {
        // Intentionally empty - field may not exist in API response
        if (Environment.isLogAll()) {
          LOG.debug("Description field not found in API response (optional)");
        }
      }
    }
    try {
      String exampleRequest =
          pageText.substring(
              pageText.indexOf("Example XML Request"), pageText.indexOf("Example XML Response"));
      exampleRequest = exampleRequest.trim();
      exampleRequest = exampleRequest.replaceAll("'", "''");
      mapValues.put("exampleRequest", exampleRequest);
    } catch (final Exception e) {
      // Intentionally empty - field may not exist in API response
      if (Environment.isLogAll()) {
        LOG.debug("Example Request field not found in API response (optional)");
      }
    }
    try {
      String exampleResponse =
          pageText.substring(
              pageText.indexOf("Example XML Response"), pageText.indexOf("See Also"));
      exampleResponse = exampleResponse.trim();
      exampleResponse = exampleResponse.replaceAll("'", "''");
      mapValues.put("exampleResponse", exampleResponse);
    } catch (final Exception e) {
      // Intentionally empty - field may not exist in API response
      if (Environment.isLogAll()) {
        LOG.debug("Example Response field not found in API response (optional)");
      }
    }
    return mapValues;
  }
}
