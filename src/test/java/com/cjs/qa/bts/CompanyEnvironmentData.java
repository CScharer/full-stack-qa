package com.cjs.qa.bts;

import java.io.File;

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

public class CompanyEnvironmentData {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(CompanyEnvironmentData.class));

  public static final String FILE_NAME = "JVMSettings";
  public static final String NODE_ROOT = "//Settings/";
  public static final String NODE_TEXT = "/text()";
  private Document document = null;
  private String userName = null;
  private String userPassword = null;
  private String browserName = null;
  private String browserVersion = null;
  private String browserPlatform = null;
  private String hubHost = null;
  private String hubPort = null;
  private String hubUserName = null;
  private String hubUserAccessKey = null;
  private String hubParentTunnel = null;
  private String hubTunnelIdentifier = null;

  public CompanyEnvironmentData() throws Exception {
    final String filePathName = FSOTests.fileReadAll(getEnvironmentsFilePathName());
    setDocument(XML.createDocument(filePathName));
    String node = "User/";
    setUserName(getData(NODE_ROOT + node + "name" + NODE_TEXT));
    setUserPassword(getData(NODE_ROOT + node + "password" + NODE_TEXT));
    node = "Browser/";
    setBrowserName(getData(NODE_ROOT + node + "name" + NODE_TEXT));
    setBrowserPlatform(getData(NODE_ROOT + node + "version" + NODE_TEXT));
    setBrowserVersion(getData(NODE_ROOT + node + "platform" + NODE_TEXT));
    node = "Hub/";
    setHubHost(getData(NODE_ROOT + node + "host" + NODE_TEXT));
    setHubPort(getData(NODE_ROOT + node + "port" + NODE_TEXT));
    setHubUserName(getData(NODE_ROOT + node + "userName" + NODE_TEXT));
    setHubUserAccessKey(getData(NODE_ROOT + node + "userAccessKey" + NODE_TEXT));
    setHubParentTunnel(getData(NODE_ROOT + node + "parentTunnel" + NODE_TEXT));
    setHubTunnelIdentifier(getData(NODE_ROOT + node + "tunnelIdentifier" + NODE_TEXT));
  }

  public Document getDocument() {
    return document;
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getBrowserName() {
    return browserName;
  }

  public void setBrowserName(String browserName) {
    this.browserName = browserName;
  }

  public String getBrowserVersion() {
    return browserVersion;
  }

  public void setBrowserVersion(String browserVersion) {
    this.browserVersion = browserVersion;
  }

  public String getBrowserPlatform() {
    return browserPlatform;
  }

  public void setBrowserPlatform(String browserPlatform) {
    this.browserPlatform = browserPlatform;
  }

  public String getHubHost() {
    return hubHost;
  }

  public void setHubHost(String hubHost) {
    this.hubHost = hubHost;
  }

  public String getHubPort() {
    return hubPort;
  }

  public void setHubPort(String hubPort) {
    this.hubPort = hubPort;
  }

  public String getHubUserName() {
    return hubUserName;
  }

  public void setHubUserName(String hubUserName) {
    this.hubUserName = hubUserName;
  }

  public String getHubUserAccessKey() {
    return hubUserAccessKey;
  }

  public void setHubUserAccessKey(String hubUserAccessKey) {
    this.hubUserAccessKey = hubUserAccessKey;
  }

  public String getHubParentTunnel() {
    return hubParentTunnel;
  }

  public void setHubParentTunnel(String hubParentTunnel) {
    this.hubParentTunnel = hubParentTunnel;
  }

  public String getHubTunnelIdentifier() {
    return hubTunnelIdentifier;
  }

  public void setHubTunnelIdentifier(String hubTunnelIdentifier) {
    this.hubTunnelIdentifier = hubTunnelIdentifier;
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
    String value = "";
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
      return value;
    }
    if (nodeList.getLength() == 0) {
      LOG.debug("{}", JavaHelpers.getCurrentMethodName() + "No Data Found For:[" + xPath + "]");
      return value;
    }
    if (JavaHelpers.hasValue(nodeList.item(0).getNodeValue())) {
      value = nodeList.item(0).getNodeValue();
    }
    // if (xPath.toLowerCase().contains("password"))
    // {
    // LOG.debug("{} Records Found For:[{}] using Value[***Password***]", nodeList.getLength(),
    // xPath);
    // } else
    // {
    // LOG.debug("{} Records Found For:[{}] using Value:[{}]", nodeList.getLength(), xPath, value);
    // }
    return value;
  }
}
