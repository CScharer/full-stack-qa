package com.cjs.qa.gt;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.vivit.VivitDataTests;

public class GTWAPIMethodsTests {

  public static final String GTW_METHOD_TABLE = "t_DOM_GTWNamespaces-Methods";

  @Test
  public void createGTWNamespaceClasses() {
    JDBC jdbc = null;
    try {
      String thisClassPath = this.getClass().getName();
      thisClassPath = thisClassPath.replace("GTWAPIMethods", "api\\namespace\\webinar");
      thisClassPath = thisClassPath.replaceAll("\\.", "\\\\");
      final String gtwNamespaceClassPath =
          Constants.PATH_PROJECT
              + "src"
              + Constants.DELIMETER_PATH
              + "test"
              + Constants.DELIMETER_PATH
              + "java"
              + Constants.DELIMETER_PATH
              + thisClassPath
              + Constants.DELIMETER_PATH;
      jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
      StringBuilder sqlStringBuilder = new StringBuilder();
      for (String namespace : getGTWNamespaces(jdbc)) {
        sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append(JDBCConstants.SELECT_ALL_FROM + "[" + GTW_METHOD_TABLE + "] ");
        sqlStringBuilder.append(JDBCConstants.WHERE + "[Namespace]='" + namespace + "' ");
        sqlStringBuilder.append(JDBCConstants.ORDER_BY + "[Method Name];");
        List<Map<String, String>> gtwMethodMapList =
            jdbc.queryResultsString(sqlStringBuilder.toString(), false);
        StringBuilder stringBuilderClass = new StringBuilder();
        for (int gtwMethodMapListIndex = 0;
            gtwMethodMapListIndex < gtwMethodMapList.size();
            gtwMethodMapListIndex++) {
          Map<String, String> gtwMethodMap = gtwMethodMapList.get(gtwMethodMapListIndex);
          String className = gtwMethodMap.get("Namespace") + "Namespace";
          final String filePathClassName = gtwNamespaceClassPath + className + IExtension.JAVA;
          if (gtwMethodMapListIndex == 0) {
            stringBuilderClass = getClassHeader(stringBuilderClass, className);
          }
          stringBuilderClass = getMethod(stringBuilderClass, gtwMethodMap);
          if (gtwMethodMapListIndex != gtwMethodMapList.size() - 1) {
            stringBuilderClass.append("\n");
          } else {
            stringBuilderClass = getClassFooter(stringBuilderClass);
          }
          FSOTests.fileWrite(filePathClassName, stringBuilderClass.toString(), false);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (jdbc != null) {
        jdbc.close();
        jdbc = null;
      }
    } finally {
      if (jdbc != null) {
        jdbc.close();
        jdbc = null;
      }
    }
  }

  public static String[] getGTWNamespaces(JDBC jdbc) {
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.SELECT + "[Namespace]");
    sqlStringBuilder.append(JDBCConstants.FROM + "[" + GTW_METHOD_TABLE + "] ");
    sqlStringBuilder.append(JDBCConstants.GROUP_BY + "[Namespace];");
    String namespaces = jdbc.queryResults(sqlStringBuilder.toString(), "", false);
    return namespaces.split(Constants.NEWLINE);
  }

  public static StringBuilder getClassHeader(StringBuilder stringBuilder, String className) {
    stringBuilder.append("package com.cjs.qa.gt.api.namespace.webinar;");
    stringBuilder.append("\n\nimport java.util.Map;");
    stringBuilder.append("\n\nimport com.cjs.qa.gt.api.services.GTWebinarServiceTests;");
    stringBuilder.append("\n\npublic class " + className + " extends GTWebinarService");
    stringBuilder.append("\n{");
    return stringBuilder;
  }

  public static StringBuilder getClassFooter(StringBuilder stringBuilder) {
    stringBuilder.append("\n}");
    return stringBuilder;
  }

  private StringBuilder getMethod(StringBuilder stringBuilder, Map<String, String> gtwMethodMap) {
    String url = gtwMethodMap.get("URL");
    String methodName = gtwMethodMap.get("Method Name");
    stringBuilder.append("\n\tpublic Map<String, String> " + methodName + "(");
    stringBuilder.append(getParameters(url));
    stringBuilder.append(") throws Throwable");
    stringBuilder.append("\n\t{");
    stringBuilder.append("\n\t\t// " + url);
    stringBuilder.append("\n\t\tString url = API_GT_BASE + \"" + getURL(url) + ";");
    stringBuilder.append("\n\t\tStringBuilder stringBuilder = new StringBuilder();");
    stringBuilder.append("\n\t\tstringBuilder.append(\"\");");
    String requestMethod = gtwMethodMap.get("Request Method");
    stringBuilder.append(
        "\n\t\treturn getAPIJSONResponse(credentials, \""
            + requestMethod
            + "\", stringBuilder.toString(), url);");
    stringBuilder.append("\n\t}");
    return stringBuilder;
  }

  private String getParameters(String url) {
    // String credentials, String organizerKey, String webinarKey
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("String credentials");
    if (url.contains("{")) {
      String[] paramterArray = url.split("\\{");
      for (int parameterIndex = 1; parameterIndex < paramterArray.length; parameterIndex++) {
        stringBuilder.append(",");
        String parameter = paramterArray[parameterIndex];
        String parameterValue = parameter.substring(0, parameter.indexOf('}'));
        stringBuilder.append("String " + parameterValue);
      }
    }
    return stringBuilder.toString();
  }

  private String getURL(String url) {
    String urlNew = url;
    if (!urlNew.endsWith("}")) {
      urlNew += "\"";
    }
    urlNew = urlNew.replaceAll("/\\{", "/\" + ");
    urlNew = urlNew.replaceAll("}/", " + \"/");
    urlNew = urlNew.replaceAll("}", "");
    return urlNew;
  }
}
