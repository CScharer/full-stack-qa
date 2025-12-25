package com.cjs.qa.jdbc;

import java.util.Locale;

import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.IExtension;

public final class DBConnections {

  private DBConnections() {
    // Utility class - prevent instantiation
  }

  private static DBParameters access() {
    final DBParameters dbParameters = new DBParameters("UCANACCESS");
    dbParameters.setServer(
        "Driver={Microsoft Access Driver (*" + IExtension.MDB + ", *" + IExtension.ACCDB + ")}");
    dbParameters.setName(
        "C:"
            + Constants.DELIMETER_PATH
            + "Temp"
            + Constants.DELIMETER_PATH
            + "Vivit-Operating_Budget"
            + IExtension.ACCDB);
    dbParameters.setMemory("false");
    dbParameters.setConnectionString(
        dbParameters.getUrlPrefix()
            + dbParameters.getName()
            + Constants.DELIMETER_LIST
            + "memory="
            + dbParameters.getMemory());
    return dbParameters;
  }

  private static DBParameters msAccess() {
    final DBParameters dbParameters = new DBParameters("UCANACCESS");
    dbParameters.setServer(
        "Driver={Microsoft Access Driver (*" + IExtension.MDB + ", *" + IExtension.ACCDB + ")}");
    dbParameters.setName(
        "C:"
            + Constants.DELIMETER_PATH
            + "Temp"
            + Constants.DELIMETER_PATH
            + "Vivit-Operating_Budget"
            + IExtension.ACCDB);
    dbParameters.setMemory("false");
    dbParameters.setConnectionString(
        dbParameters.getUrlPrefix()
            + dbParameters.getName()
            + Constants.DELIMETER_LIST
            + "memory="
            + dbParameters.getMemory());
    return dbParameters;
  }

  private static DBParameters msExcel() {
    final DBParameters dbParameters = new DBParameters("UCANACCESS");
    dbParameters.setServer(
        "Driver={Driver do Microsoft Excel (*" + IExtension.XLS + ", *" + IExtension.XLSX + ")}");
    dbParameters.setServer(
        "Driver={Driver do Microsoft Excel (*"
            + IExtension.XLS
            + ", *"
            + IExtension.XLSX
            + ", *.xlsm, *.xlsb)}");
    dbParameters.setServer("Driver={Microsoft Excel Driver (*" + IExtension.XLS + ")}");
    dbParameters.setServer(
        "Driver={Microsoft Excel Driver (*"
            + IExtension.XLS
            + ", *"
            + IExtension.XLSX
            + ", *"
            + IExtension.XLSM
            + ", *"
            + IExtension.XLSB
            + ")}");
    dbParameters.setName(CJSConstants.PATH_FILES_DATA + "Excel" + IExtension.XLSX);
    dbParameters.setMemory("false");
    dbParameters.setConnectionString(
        dbParameters.getUrlPrefix() + dbParameters.getServer() + ";Dbq=" + dbParameters.getName());
    return dbParameters;
  }

  private static DBParameters sqLite() {
    // org.sqlite.JDBC
    // jdbc:sqlite:C:\Workspace\Data\Databases\qadb.sqlite
    final DBParameters dbParameters = new DBParameters("SQLITE");
    dbParameters.setName(Constants.PATH_FILES_DATA_DATABASES + "qadb" + IExtension.SQLITE);
    dbParameters.setMemory("false");
    dbParameters.setConnectionString(dbParameters.getUrlPrefix() + dbParameters.getName());
    return dbParameters;
  }

  private static DBParameters sqlServer() {
    final DBParameters dbParameters = new DBParameters("SQLSERVER");
    dbParameters.setServer(CJSConstants.JIRA_TEST_DATABASE_SERVER);
    dbParameters.setPort(CJSConstants.JIRA_TEST_DATABASE_PORT);
    dbParameters.setName(CJSConstants.JIRA_TEST_DATABASE_NAME);
    dbParameters.setUser(CJSConstants.QA_SERVICE_ACCOUNT_NAME);
    dbParameters.setPassword(CJSConstants.QA_SERVICE_ACCOUNT_PASSWORD);
    dbParameters.setConnectionString(
        dbParameters.getUrlPrefix()
            + CJSConstants.JIRA_TEST_DATABASE_SERVER
            + ":"
            + CJSConstants.JIRA_TEST_DATABASE_PORT
            + ";DatabaseName="
            + CJSConstants.JIRA_TEST_DATABASE_NAME
            + ";user="
            + CJSConstants.QA_SERVICE_ACCOUNT_NAME
            + ";password="
            + CJSConstants.QA_SERVICE_ACCOUNT_PASSWORD);
    return dbParameters;
  }

  public static DBParameters getDBParametersAutoCoder(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        final DBParameters parameters = sqLite();
        parameters.setName(Constants.PATH_FILES_DATA_DATABASES + "AutoCoder" + IExtension.SQLITE);
        parameters.setConnectionString(parameters.getUrlPrefix() + parameters.getName());
        return parameters;
    }
  }

  public static DBParameters getDBParametersAutoCoderExcel(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        final DBParameters parameters = sqLite();
        parameters.setName(
            Constants.PATH_FILES_DATA_DATABASES + "AutoCoderExcel" + IExtension.SQLITE);
        parameters.setConnectionString(parameters.getUrlPrefix() + parameters.getName());
        return parameters;
    }
  }

  public static DBParameters getDBParametersMSAccess(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        return msAccess();
    }
  }

  public static DBParameters getDBParametersMSExcel(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        return msExcel();
    }
  }

  public static DBParameters getDBParametersJira(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        return sqlServer();
    }
  }

  public static DBParameters getDBParametersQAAuto(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        return sqLite();
    }
  }

  public static DBParameters getDBParametersQATools(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        DBParameters dbParameters = sqLite();
        dbParameters.setName(Constants.PATH_FILES_DATA_DATABASES + "qatools" + IExtension.SQLITE);
        dbParameters.setConnectionString(dbParameters.getUrlPrefix() + dbParameters.getName());
        return dbParameters;
    }
  }

  public static DBParameters getDBParametersQaToolsWeb(String environment) {
    switch (environment.toUpperCase(Locale.ENGLISH)) {
      default:
        return access();
    }
  }
}
