package com.cjs.qa.bts;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cjs.qa.core.QAException;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.XML;

public class BTSConvertDatabaseToXMLTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BTSConvertDatabaseToXMLTests.class));

  @Test
  public void testCompanyEnvironmentData() throws Exception {
    final BTSCompanyEnvironmentData btsCompanyEnvironmentData =
        new BTSCompanyEnvironmentData("aic", "int");
    LOG.debug("{}", btsCompanyEnvironmentData.getCompanyAbbreviation());
    LOG.debug("{}", btsCompanyEnvironmentData.getEnvironment());
    LOG.debug("{}", btsCompanyEnvironmentData.getCompanyName());
    LOG.debug("{}", btsCompanyEnvironmentData.getCompanyNumber());
    LOG.debug("{}", btsCompanyEnvironmentData.getFilenetSplit());
    LOG.debug("{}", btsCompanyEnvironmentData.getServiceAccount());
    LOG.debug("{}", btsCompanyEnvironmentData.getServiceAccountPassword());
    LOG.debug("{}", btsCompanyEnvironmentData.getServiceAccountPasswordJava());
    LOG.debug("{}", btsCompanyEnvironmentData.getEnvironmentURL());
    LOG.debug("{}", btsCompanyEnvironmentData.getDatabase());
    LOG.debug("{}", btsCompanyEnvironmentData.getDatabaseInstance());
    LOG.debug("{}", btsCompanyEnvironmentData.getDatabaseServer());
    LOG.debug("{}", btsCompanyEnvironmentData.getDatabasePortNumber());
  }

  @Test
  public void convertDBToXML() throws Exception {
    try {
      StringBuilder stringBuilder = new StringBuilder(XML.HEADING_INFO);
      stringBuilder.append(
          XML.ENCLOSURE_LEFT + BTSCompanyEnvironmentData.FILE_NAME + XML.ENCLOSURE_RIGHT);
      JDBC jdbc = new JDBC("", "qatools");
      String sql = JDBCConstants.SELECT_ALL_FROM + "[tblCompany] ORDER BY [Abbreviation]";
      List<Map<String, String>> companyList = jdbc.queryResultsString(sql, true);
      for (int companyIndex = 1; companyIndex < companyList.size(); companyIndex++) {
        Map<String, String> company = companyList.get(companyIndex);
        LOG.debug("company:[[{}]", company + "]");
        String companyAbbreviation = (String) company.get("Abbreviation");
        stringBuilder.append(
            XML.ENCLOSURE_LEFT
                + companyAbbreviation.toLowerCase(Locale.ENGLISH)
                + XML.ENCLOSURE_RIGHT);
        stringBuilder = getWrappedField(stringBuilder, company, "Name");
        stringBuilder = getWrappedField(stringBuilder, company, "Number");
        stringBuilder = getWrappedField(stringBuilder, company, "FilenetSplit");
        sql =
            JDBCConstants.SELECT_ALL_FROM
                + "[tblDOM_PSTAR_Service_Accounts] "
                + JDBCConstants.WHERE
                + "[Abbreviation]='"
                + companyAbbreviation
                + "'";
        List<Map<String, String>> serviceAccountList = jdbc.queryResultsString(sql, true);
        for (int serviceAccountIndex = 1;
            serviceAccountIndex < serviceAccountList.size();
            serviceAccountIndex++) {
          Map<String, String> serviceAccount = serviceAccountList.get(serviceAccountIndex);
          LOG.debug("serviceAccount:[[{}]", serviceAccount + "]");
          stringBuilder = getWrappedField(stringBuilder, serviceAccount, "Service_Account");
          stringBuilder = getWrappedField(stringBuilder, serviceAccount, "Password");
          stringBuilder = getWrappedField(stringBuilder, serviceAccount, "Password_Java");
          sql =
              JDBCConstants.SELECT_ALL_FROM
                  + "[tblEnvironments] "
                  + JDBCConstants.WHERE
                  + "[Abbreviation]='"
                  + companyAbbreviation
                  + "'";
          List<Map<String, String>> environmentList = jdbc.queryResultsString(sql, true);
          stringBuilder.append(XML.ENCLOSURE_LEFT + "Environment" + XML.ENCLOSURE_RIGHT);
          for (int environmentIndex = 1;
              environmentIndex < environmentList.size();
              environmentIndex++) {
            Map<String, String> environment = environmentList.get(environmentIndex);
            String environmentTag = (String) environment.get("Environment");
            LOG.debug("environment:[[{}]", environment + "]");
            stringBuilder.append(
                XML.ENCLOSURE_LEFT
                    + environmentTag.toLowerCase(Locale.ENGLISH)
                    + XML.ENCLOSURE_RIGHT);
            stringBuilder = getWrappedField(stringBuilder, environment, "URL");
            sql =
                JDBCConstants.SELECT_ALL_FROM
                    + "[tblDOM_DBPolicy] "
                    + JDBCConstants.WHERE
                    + "[Company]='"
                    + companyAbbreviation
                    + "' "
                    + JDBCConstants.AND
                    + "[Environment]='"
                    + environmentTag
                    + "'";
            List<Map<String, String>> dbPolicyList = jdbc.queryResultsString(sql, true);
            for (int dbPolicyIndex = 1; dbPolicyIndex < dbPolicyList.size(); dbPolicyIndex++) {
              Map<String, String> dbPolicy = dbPolicyList.get(dbPolicyIndex);
              LOG.debug("dbPolicy:[[{}]", dbPolicy + "]");
              stringBuilder = getWrappedField(stringBuilder, dbPolicy, "Server");
              stringBuilder = getWrappedField(stringBuilder, dbPolicy, "PortNumber");
              stringBuilder = getWrappedField(stringBuilder, dbPolicy, "Instance");
              stringBuilder = getWrappedField(stringBuilder, dbPolicy, "Database");
            }
            stringBuilder.append(
                XML.ENCLOSURE_LEFT
                    + XML.ENCLOSURE_DELIMETER
                    + environmentTag.toLowerCase(Locale.ENGLISH)
                    + XML.ENCLOSURE_RIGHT);
          }
          stringBuilder.append(
              XML.ENCLOSURE_LEFT + XML.ENCLOSURE_DELIMETER + "Environment" + XML.ENCLOSURE_RIGHT);
        }
        stringBuilder.append(
            XML.ENCLOSURE_LEFT
                + XML.ENCLOSURE_DELIMETER
                + companyAbbreviation.toLowerCase(Locale.ENGLISH)
                + XML.ENCLOSURE_RIGHT);
      }
      stringBuilder.append(
          XML.ENCLOSURE_LEFT
              + XML.ENCLOSURE_DELIMETER
              + BTSCompanyEnvironmentData.FILE_NAME
              + XML.ENCLOSURE_RIGHT);
      String xml = stringBuilder.toString();
      LOG.debug("xml:[[{}]", xml + "]");
      // xml = HTML.convertStringToHTML(xml);
      LOG.debug("xml:[[{}]", xml + "]");
      xml = XML.formatPretty(xml);
      FSOTests.fileWrite(BTSCompanyEnvironmentData.getEnvironmentsFilePathName(), xml, false);
    } catch (Exception | QAException e) {
      e.printStackTrace();
    }
  }

  private StringBuilder getWrappedField(
      StringBuilder stringBuilder, Map<String, String> map, String fieldName) {
    String fieldValue = map.get(fieldName);
    stringBuilder.append(XML.getWrappedField(stringBuilder, fieldName, fieldValue));
    return stringBuilder;
  }

  private StringBuilder getQueryDataOuterJoin() {
    // When developed this was not supported by sqlite.
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT);
    stringBuilder.append("[c].[Abbreviation],");
    stringBuilder.append("[c].[Name],");
    stringBuilder.append("[c].[Number],");
    stringBuilder.append("[c].[FilenetSplit],");
    stringBuilder.append("[a].[Service_Account],");
    stringBuilder.append("[a].[Password],");
    stringBuilder.append("[a].[Password_Java],");
    stringBuilder.append("[e].[Environment],");
    stringBuilder.append("[e].[URL],");
    stringBuilder.append("[d].[Server],");
    stringBuilder.append("[d].[PortNumber],");
    stringBuilder.append("[d].[Instance],");
    stringBuilder.append("[d].[Database] ");
    stringBuilder.append(JDBCConstants.FROM + "[tblCompany] [c] ");
    stringBuilder.append(JDBCConstants.OUTER_JOIN + "[tblDOM_PSTAR_Service_Accounts] [a] ");
    stringBuilder.append(JDBCConstants.ON + "[c].[Abbreviation]=[a].[Abbreviation] ");
    stringBuilder.append(JDBCConstants.OUTER_JOIN + "[tblEnvironments] [e] ");
    stringBuilder.append(JDBCConstants.ON + "[a].[Abbreviation]=[e].[Abbreviation] ");
    stringBuilder.append(JDBCConstants.OUTER_JOIN + "[tblDOM_DBPolicy] [d] ");
    stringBuilder.append(JDBCConstants.ON + "[e].[Abbreviation]=[d].[Company];");
    return stringBuilder;
  }

  @Test
  public void readFromXML() throws Exception {
    String filePathName = BTSCompanyEnvironmentData.getEnvironmentsFilePathName();
    String xml = FSOTests.fileReadAll(filePathName);
    Document document = XML.createDocument(xml);
    String xPath = BTSCompanyEnvironmentData.NODE_COMPANY + BTSCompanyEnvironmentData.NODE_TEXT;
    XPathExpression xpathExpression = XML.XPATH.compile(xPath);
    NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
    LOG.debug("nodeList.getLength():[{}]", nodeList.getLength());
    JDBC jdbc = new JDBC("", "qatools");
    String sql =
        JDBCConstants.SELECT
            + "[Abbreviation],[Environment] "
            + JDBCConstants.FROM
            + "[tblEnvironments]";
    LOG.debug("sql:[[{}]", sql + "]");
    List<Map<String, String>> queryList = jdbc.queryResultsString(sql, false);
    sql = getQueryDataOuterJoin().toString();
    for (Map<String, String> map : queryList) {
      LOG.debug("map:[[{}]", map + "]");
      String companyAbbreviation = map.get("Abbreviation").toLowerCase(Locale.ENGLISH);
      final String companyEnvironment = map.get("Abbreviation").toLowerCase(Locale.ENGLISH);
      xPath =
          BTSCompanyEnvironmentData.NODE_COMPANY
              + "/"
              + companyAbbreviation
              + "/Name"
              + BTSCompanyEnvironmentData.NODE_TEXT;
      List<String> listCompany = Arrays.asList("Name", "Number", "FilenetSplit");
      List<String> listServiceAccount =
          Arrays.asList("Service_Account", "Password", "Password_Java");
      List<String> listEnvironment = Arrays.asList("URL");
      for (String company : listCompany) {
        xPath =
            BTSCompanyEnvironmentData.NODE_COMPANY
                + "/"
                + companyAbbreviation
                + "/"
                + company
                + BTSCompanyEnvironmentData.NODE_TEXT;
        writeInformation(document, xPath);
      }
      for (String serviceAccount : listServiceAccount) {
        xPath =
            BTSCompanyEnvironmentData.NODE_COMPANY
                + "/"
                + companyAbbreviation
                + "/"
                + serviceAccount
                + BTSCompanyEnvironmentData.NODE_TEXT;
        writeInformation(document, xPath);
      }
      for (String environment : listEnvironment) {
        xPath =
            BTSCompanyEnvironmentData.NODE_COMPANY
                + "/"
                + companyAbbreviation
                + "/Environment/"
                + companyEnvironment
                + "/"
                + environment
                + BTSCompanyEnvironmentData.NODE_TEXT;
        writeInformation(document, xPath);
      }
      final List<String> listDatabase =
          Arrays.asList("Server", "PortNumber", "Instance", "Database");
      for (String database : listDatabase) {
        xPath =
            BTSCompanyEnvironmentData.NODE_COMPANY
                + "/"
                + companyAbbreviation
                + "/Environment/"
                + companyEnvironment
                + "/"
                + database
                + BTSCompanyEnvironmentData.NODE_TEXT;
        writeInformation(document, xPath);
      }
      writeInformation(document, xPath);
    }
    LOG.debug("queryList.size():[{}]", queryList.size());
  }

  private void writeInformation(Document document, String xPath) throws XPathExpressionException {
    XPathExpression xpathExpression = XML.XPATH.compile(xPath);
    NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
    if (nodeList.getLength() == 0) {
      LOG.debug("No Data Found For:[[{}]", xPath + "]");
      return;
    }
    LOG.debug("{} Records Found For:[{}]", nodeList.getLength(), xPath);
    for (int index = 0; index < nodeList.getLength(); index++) {
      LOG.debug("{}", nodeList.item(index).getNodeValue());
    }
  }
}
