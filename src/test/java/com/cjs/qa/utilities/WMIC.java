package com.cjs.qa.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

public class WMIC {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(WMIC.class));
  private final WMICAlias wmicAlias = new WMICAlias();
  private String namespace = null;
  private String role = null;
  private String node = null;
  private String implevel = null;
  private String authlevel = null;
  private String locale = null;
  private String privileges = null;
  private String trace = null;
  private String record = null;
  private String interactive = null;
  private String failfast = null;
  private String user = null;
  private String password = null;
  private String output = null;
  private String append = null;
  private String aggregate = null;
  private String authority = null;
  private String command = null;

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  public String getImplevel() {
    return implevel;
  }

  public void setImplevel(String implevel) {
    this.implevel = implevel;
  }

  public String getAuthlevel() {
    return authlevel;
  }

  public void setAuthlevel(String authlevel) {
    this.authlevel = authlevel;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getPrivileges() {
    return privileges;
  }

  public void setPrivileges(String privileges) {
    this.privileges = privileges;
  }

  public String getTrace() {
    return trace;
  }

  public void setTrace(String trace) {
    this.trace = trace;
  }

  public String getRecord() {
    return record;
  }

  public void setRecord(String record) {
    this.record = record;
  }

  public String getInteractive() {
    return interactive;
  }

  public void setInteractive(String interactive) {
    this.interactive = interactive;
  }

  public String getFailfast() {
    return failfast;
  }

  public void setFailfast(String failfast) {
    this.failfast = failfast;
  }

  public WMICAlias getWmicAlias() {
    return wmicAlias;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public String getAppend() {
    return append;
  }

  public void setAppend(String append) {
    this.append = append;
  }

  public String getAggregate() {
    return aggregate;
  }

  public void setAggregate(String aggregate) {
    this.aggregate = aggregate;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }

  public String getCommand() {
    return this.command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Map<String, String> run() throws Exception {
    final String command = createCommand();
    final Map<String, String> result = new HashMap<>();
    // result = CommandLineTests.runProcess(command, true);
    result.put("command", command);
    return result;
  }

  private String getArgumentValue(String argument) {
    switch (argument) {
      case "NAMESPACE":
        return getNamespace();
      case "ROLE":
        return getRole();
      case "NODE":
        return getNode();
      case "IMPLEVEL":
        return getImplevel();
      case "AUTHLEVEL":
        return getAuthlevel();
      case "LOCALE":
        return getLocale();
      case "PRIVILEGES":
        return getPrivileges();
      case "TRACE":
        return getTrace();
      case "RECORD":
        return getRecord();
      case "INTERACTIVE":
        return getInteractive();
      case "FAILFAST":
        return getFailfast();
      case "USER":
        return getUser();
      case "PASSWORD":
        return getPassword();
      case "OUTPUT":
        return getOutput();
      case "APPEND":
        return getAppend();
      case "AGGREGATE":
        return getAggregate();
      case "AUTHORITY":
        return getAuthority();
      default:
        LOG.warn("Unknown WMIC argument: {}. Returning null.", argument);
        return null;
    }
  }

  private String createCommand() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("wmic ");
    final List<String> arguments =
        Arrays.asList(
            ("NAMESPACE;ROLE;NODE;IMPLEVEL;AUTHLEVEL;LOCALE;PRIVILEGES;TRACE;"
                    + "RECORD;INTERACTIVE;FAILFAST;USER;PASSWORD;OUTPUT;APPEND;AGGREGATE;AUTHORITY")
                .split(Constants.DELIMETER_LIST));
    for (final String argument : arguments) {
      final String argumentValue = getArgumentValue(argument);
      if (argumentValue != null) {
        stringBuilder.append("/" + argument + ":" + argumentValue + " ");
      }
    }
    final String command = "process call create " + getCommand();
    stringBuilder.append(command);
    return stringBuilder.toString();
  }

  public void main(String[] args) {
    try {
      // /?[:<BRIEF|FULL>] Usage information.
      // CLASS - Escapes to full WMI schema.
      // PATH - Escapes to full WMI object paths.
      // CONTEXT - Displays the state of all the global switches.
      // QUIT/EXIT - Exits the program.
      final WMIC wmic = new WMIC();
      final String dateTimeStamp = DateHelpersTests.getCurrentDateTime("yyyyMMdd_HHmmss");
      // wmic.setNamespace("namespace");
      wmic.setRole(Constants.DELIMETER_PATH + "root" + Constants.DELIMETER_PATH + "cli");
      wmic.setNode("BTSDESEWS08");
      wmic.setImplevel("Impersonate");
      wmic.setAuthlevel("Default");
      wmic.setLocale("MS_409");
      wmic.setPrivileges("ENABLE");
      wmic.setTrace("ON");
      wmic.setRecord(
          Constants.QUOTE_DOUBLE
              + Constants.DELIMETER_PATH
              + "wrbts"
              + Constants.DELIMETER_PATH
              + "shared"
              + Constants.DELIMETER_PATH
              + "bts QA"
              + Constants.DELIMETER_PATH
              + "QATools"
              + Constants.DELIMETER_PATH
              + "Logs"
              + Constants.DELIMETER_PATH
              + wmic.getNode()
              + "_"
              + dateTimeStamp
              + IExtension.XML
              + Constants.QUOTE_DOUBLE);
      wmic.setInteractive("OFF");
      wmic.setFailfast("ON");
      wmic.setUser("btsqa");
      wmic.setPassword("AUTO_BTSQA_PASSWORD");
      wmic.setOutput(
          Constants.QUOTE_DOUBLE
              + Constants.DELIMETER_PATH
              + "wrbts"
              + Constants.DELIMETER_PATH
              + "shared"
              + Constants.DELIMETER_PATH
              + "bts QA"
              + Constants.DELIMETER_PATH
              + "QATools"
              + Constants.DELIMETER_PATH
              + "Logs"
              + Constants.DELIMETER_PATH
              + wmic.getNode()
              + "_"
              + dateTimeStamp
              + IExtension.LOG
              + Constants.QUOTE_DOUBLE);
      wmic.setOutput("STDOUT");
      // wmic.setAppend("append");
      wmic.setAggregate("ON");
      // wmic.setAuthority("authority");
      wmic.wmicAlias.setAlias(null);
      wmic.setCommand(
          "CMD /C "
              + Constants.QUOTE_DOUBLE
              + "C:"
              + Constants.DELIMETER_PATH
              + "Users"
              + Constants.DELIMETER_PATH
              + "btsqa"
              + Constants.DELIMETER_PATH
              + "Desktop"
              + Constants.DELIMETER_PATH
              + "TEST_WMIC"
              + IExtension.BAT
              + Constants.QUOTE_DOUBLE);
      final Map<String, String> results = wmic.run();
      LOG.info("results: [{}]", results);
    } catch (final Exception e) {
      LOG.error("Error in WMIC main", e);
    }
  }
}
