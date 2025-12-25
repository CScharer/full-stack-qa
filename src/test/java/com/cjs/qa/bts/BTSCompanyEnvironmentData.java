package com.cjs.qa.bts;

import java.io.File;
import java.util.Locale;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;

public class BTSCompanyEnvironmentData {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BTSCompanyEnvironmentData.class));

  public static final String FILE_NAME = "Companies";
  public static final String NODE_COMPANY = "//Company";
  public static final String NODE_ENVIRONMENT = "/Environment";
  public static final String NODE_TEXT = "/text()";
  private Document document = null;
  private String companyAbbreviation = null;
  private String environment = null;
  private String companyName = null;
  private String companyNumber = null;
  private String filenetSplit = null;
  private String serviceAccount = null;
  private String serviceAccountPassword = null;
  private String serviceAccountPasswordJava = null;
  private String environmentURL = null;
  private String databaseServer = null;
  private String databasePortNumber = null;
  private String databaseInstance = null;
  private String database = null;

  public BTSCompanyEnvironmentData(String companyAbbreviation, String environment)
      throws Exception {
    final String filePathName = FSOTests.fileReadAll(getEnvironmentsFilePathName());
    setDocument(XML.createDocument(filePathName));
    setCompanyAbbreviation(companyAbbreviation);
    setEnvironment(environment);
    final String nodeCompanyAbbreviation = NODE_COMPANY + "/" + getCompanyAbbreviation() + "/";
    final String nodeEnvironment =
        nodeCompanyAbbreviation + NODE_ENVIRONMENT + getEnvironment() + "/";
    setCompanyName(getData(nodeCompanyAbbreviation + "Name" + NODE_TEXT));
    setCompanyNumber(getData(nodeCompanyAbbreviation + "Number" + NODE_TEXT));
    setFilenetSplit(getData(nodeCompanyAbbreviation + "FilenetSplit" + NODE_TEXT));
    setServiceAccount(getData(nodeCompanyAbbreviation + "Service_Account" + NODE_TEXT));
    setServiceAccountPassword(getData(nodeCompanyAbbreviation + "Password" + NODE_TEXT));
    setServiceAccountPasswordJava(getData(nodeCompanyAbbreviation + "Password_Java" + NODE_TEXT));
    setEnvironmentURL(getData(nodeEnvironment + "URL" + NODE_TEXT));
    setDatabaseServer(getData(nodeEnvironment + "Server" + NODE_TEXT));
    setDatabasePortNumber(getData(nodeEnvironment + "PortNumber" + NODE_TEXT));
    setDatabaseInstance(getData(nodeEnvironment + "Instance" + NODE_TEXT));
    setDatabase(getData(nodeEnvironment + "Database" + NODE_TEXT));
  }

  private Document getDocument() {
    return this.document;
  }

  public String getCompanyAbbreviation() {
    return companyAbbreviation;
  }

  public String getEnvironment() {
    return environment;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public String getCompanyNumber() {
    return this.companyNumber;
  }

  public String getFilenetSplit() {
    return this.filenetSplit;
  }

  public String getServiceAccount() {
    return this.serviceAccount;
  }

  public String getServiceAccountPassword() {
    return this.serviceAccountPassword;
  }

  public String getServiceAccountPasswordJava() {
    return this.serviceAccountPasswordJava;
  }

  public String getEnvironmentURL() {
    return this.environmentURL;
  }

  public String getDatabaseServer() {
    return this.databaseServer;
  }

  public String getDatabasePortNumber() {
    return this.databasePortNumber;
  }

  public String getDatabaseInstance() {
    return this.databaseInstance;
  }

  public String getDatabase() {
    return this.database;
  }

  private void setDocument(Document document) {
    this.document = document;
  }

  private void setCompanyAbbreviation(String companyAbbreviation) {
    this.companyAbbreviation = companyAbbreviation;
  }

  private void setEnvironment(String environment) {
    this.environment = environment;
  }

  private void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  private void setCompanyNumber(String companyNumber) {
    this.companyNumber = companyNumber;
  }

  private void setFilenetSplit(String filenetSplit) {
    this.filenetSplit = filenetSplit;
  }

  private void setServiceAccount(String serviceAccount) {
    this.serviceAccount = serviceAccount;
  }

  private void setServiceAccountPassword(String serviceAccountPassword) {
    this.serviceAccountPassword = serviceAccountPassword;
  }

  private void setServiceAccountPasswordJava(String serviceAccountPasswordJava) {
    this.serviceAccountPasswordJava = serviceAccountPasswordJava;
  }

  private void setEnvironmentURL(String environmentURL) {
    this.environmentURL = environmentURL;
  }

  private void setDatabaseServer(String databaseServer) {
    this.databaseServer = databaseServer;
  }

  private void setDatabasePortNumber(String databasePortNumber) {
    this.databasePortNumber = databasePortNumber;
  }

  private void setDatabaseInstance(String databaseInstance) {
    this.databaseInstance = databaseInstance;
  }

  private void setDatabase(String database) {
    this.database = database;
  }

  public static String getEnvironmentsFilePathString() {
    final String folderPath = Constants.PATH_PROJECT + "XML\\";
    FSOTests.folderCreate(folderPath);
    return folderPath;
  }

  public static String getEnvironmentsFilePathName() {
    return getEnvironmentsFilePathString() + FILE_NAME + IExtension.XML;
  }

  public File getEnvironmentsFilePathFile() {
    return new File(getEnvironmentsFilePathName());
  }

  private String getData(String xPath) {
    XPathExpression xpathExpression = null;
    NodeList nodeList = null;
    try {
      xpathExpression = XML.XPATH.compile(xPath);
      try {
        nodeList = (NodeList) xpathExpression.evaluate(getDocument(), XPathConstants.NODESET);
      } catch (XPathExpressionException e) {
        LOG.debug(
            "{}",
            JavaHelpers.getCurrentMethodName()
                + "nodeList = (NodeList) xpathExpression.evaluate(getDocument(),"
                + " XPathConstants.NODESET);");
      }
    } catch (XPathExpressionException e) {
      LOG.debug(
          "{}",
          JavaHelpers.getCurrentMethodName()
              + "xpathExpression = XML.XPATH.compile("
              + xPath
              + ");");
    }
    if (xpathExpression == null || nodeList == null) {
      return null;
    }
    if (nodeList.getLength() == 0) {
      LOG.debug("{}", JavaHelpers.getCurrentMethodName() + "No Data Found For:[" + xPath + "]");
      return null;
    }
    final String value = nodeList.item(0).getNodeValue();
    if (xPath.toLowerCase(Locale.ENGLISH).contains("password")) {
      LOG.debug(
          "{} Records Found For:[{}] using Value[***Password***]", nodeList.getLength(), xPath);
    } else {
      LOG.debug("{} Records Found For:[{}] using Value[{}]", nodeList.getLength(), xPath, value);
    }
    return value;
  }
}
