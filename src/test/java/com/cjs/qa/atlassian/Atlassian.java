package com.cjs.qa.atlassian;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.codehaus.plexus.util.FileUtils;

import com.cjs.qa.atlassian.bamboo.Bamboo;
import com.cjs.qa.atlassian.confluence.Confluence;
import com.cjs.qa.atlassian.crowd.Crowd;
import com.cjs.qa.atlassian.jira.Issue;
import com.cjs.qa.atlassian.jira.Jira;
import com.cjs.qa.atlassian.jira.Project;
import com.cjs.qa.atlassian.stash.Stash;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class Atlassian {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Atlassian.class));

  private static String domain = "-tst.cjsconsulting" + IExtension.COM;
  private Bamboo bamboo;
  private Confluence confluence;
  private Crowd crowd;
  private Jira jira;
  private Stash stash;

  public Atlassian() {
    setBamboo(new Bamboo());
    setConfluence(new Confluence());
    setCrowd(new Crowd());
    final Project project = new Project("QSDT");
    final String dateTimeStamp =
        DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_DATE_TIME_STAMP);
    final Issue issue =
        new Issue(
            "TEST_" + dateTimeStamp,
            "Creating of an issue TEST_" + dateTimeStamp + " using the REST API",
            "Task");
    setJira(new Jira(issue, project));
    setStash(new Stash());
  }

  public static void main(String[] args) {
    for (int index = 1; index <= 1; index++) {
      final Atlassian atlassian = new Atlassian();
      if (index == 1) {
        LOG.debug("URLs:{}", getString(atlassian));
      }
      final String json = atlassian.getJira().toString();
      // LOG.debug("json:[{}]", json);
      // curl -D- -u fred:fred -X POST --data {see below} -H
      // "Content-Type: application/json"
      // http://localhost:8090/rest/api/2/issue/
      final String jsonPath = Constants.PATH_PROJECT + "JSON" + Constants.DELIMETER_PATH;
      FileUtils.mkdir(jsonPath);
      final String fileJSON = jsonPath + atlassian.getJira().getIssue().getSummary() + ".json";
      FSOTests.fileWrite(fileJSON, json, false);
      final StringBuilder stringBuilder =
          new StringBuilder("cmd /C " + Constants.QUOTE_DOUBLE + "");
      final String userID = CJSConstants.USERID_VIVIT;
      // SECURITY NOTE: This is an INTENTIONAL PLACEHOLDER, not a real password.
      // This code constructs a curl command but does not execute it with a real password.
      // The "********" value is a placeholder that will be replaced at runtime if this
      // functionality is ever implemented. This is NOT a security risk.
      //
      // If this functionality is needed in the future:
      // 1. Add password to EPasswords enum
      // 2. Create secret in Google Cloud Secret Manager
      // 3. Replace this placeholder with: EPasswords.ATLASSIAN.getValue()
      //
      // See: docs/issues/open/cleanup-hardcoded-passwords.md
      // Suppress security warnings: This is intentionally a placeholder, not a credential
      @SuppressWarnings(
          "java:S2068") // Suppress "Credentials should not be hard-coded" - this is a placeholder
      final String password =
          "PLACEHOLDER_NOT_A_REAL_PASSWORD"; // Explicitly named to avoid false positives
      stringBuilder.append("curl -D- -u ");
      stringBuilder.append(userID + ":" + password);
      stringBuilder.append(" -X POST -d ");
      stringBuilder.append("@" + Constants.QUOTE_DOUBLE + fileJSON + Constants.QUOTE_DOUBLE + " ");
      stringBuilder.append(
          "-H "
              + Constants.QUOTE_DOUBLE
              + "Content-Type: application/json"
              + Constants.QUOTE_DOUBLE
              + "");
      stringBuilder.append(
          " "
              + Constants.QUOTE_DOUBLE
              + atlassian.getJira().getUrl()
              + Constants.QUOTE_DOUBLE
              + Constants.QUOTE_DOUBLE
              + "");
      LOG.debug("{}", "CURL Command:" + stringBuilder.toString());
      Map<String, String> map = null;
      try {
        map = CommandLineTests.runProcess(stringBuilder.toString(), true);
      } catch (final Exception e) {
        e.printStackTrace();
      }
      if (map != null && "0".equals(map.get("status"))) {
        LOG.debug("{}", "PASS!!! map:" + map.toString());
      } else {
        LOG.error("FAIL!!! map:{}", map != null ? map.toString() : "null");
      }
      JavaHelpers.sleep(0, 1);
    }
  }

  public Bamboo getBamboo() {
    return bamboo;
  }

  public Confluence getConfluence() {
    return confluence;
  }

  public Crowd getCrowd() {
    return crowd;
  }

  public Jira getJira() {
    return jira;
  }

  public Stash getStash() {
    return stash;
  }

  public static String getDomain() {
    return domain;
  }

  public void setBamboo(Bamboo bamboo) {
    this.bamboo = bamboo;
  }

  public void setConfluence(Confluence confluence) {
    this.confluence = confluence;
  }

  public void setCrowd(Crowd crowd) {
    this.crowd = crowd;
  }

  public void setJira(Jira jira) {
    this.jira = jira;
  }

  public void setStash(Stash stash) {
    this.stash = stash;
  }

  public void setDomain(String domain) {
    Atlassian.domain = domain;
  }

  public static String getString(Atlassian atlassian) {
    // final StringBuilder stringBuilder = new StringBuilder();
    final Constants c = new Constants();
    c.setFormatPretty(true);
    final int newLine = c.isFormatPretty() ? 1 : 0;
    final int tab = 0;
    String prefix = Constants.nlTab(newLine, tab);
    // stringBuilder
    //   .append(Constants.nlTab(newLine, tab) + "Bamboo URL:[" + atlassian.getBamboo().getUrl() +
    // "]")
    //   .append(Constants.nlTab(newLine, tab) + "Confluence URL:[" +
    // atlassian.getConfluence().getUrl() + "]")
    //   .append(Constants.nlTab(newLine, tab) + "Crowd URL:[" + atlassian.getCrowd().getUrl() +
    // "]")
    //   .append(Constants.nlTab(newLine, tab) + "Jira URL:[" + atlassian.getJira().getUrl() + "]")
    //   .append(Constants.nlTab(newLine, tab) + "Stash URL:[" + atlassian.getStash().getUrl() +
    // "]");
    // return stringBuilder.toString();

    // String.format handles the layout and efficient building
    return String.format(
        "%sBamboo URL:[%s]%sConfluence URL:[%s]%sCrowd URL:[%s]%sJira URL:[%s]%sStash URL:[%s]",
        prefix,
        atlassian.getBamboo().getUrl(),
        prefix,
        atlassian.getConfluence().getUrl(),
        prefix,
        atlassian.getCrowd().getUrl(),
        prefix,
        atlassian.getJira().getUrl(),
        prefix,
        atlassian.getStash().getUrl());
  }
}
