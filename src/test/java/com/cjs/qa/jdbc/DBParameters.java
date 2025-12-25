package com.cjs.qa.jdbc;

import java.util.Locale;

import com.cjs.qa.utilities.Constants;

public class DBParameters {

  private String type;
  private String jdbcDriver;
  private String urlPrefix;
  private String server;
  private String port;
  private String name;
  private String user;
  private String password;
  private String memory;
  private String connectionString;

  public DBParameters(String type) {
    setType(type.toUpperCase(Locale.ENGLISH));
    final EDBDriver eDBDriver = EDBDriver.fromString(getType());
    setJdbcDriver(eDBDriver.getJdbcDriver());
    setUrlPrefix(eDBDriver.getUrlPrefix());
  }

  public String getConnectionString() {
    return connectionString;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getPort() {
    return port;
  }

  public String getServer() {
    return server;
  }

  public String getJdbcDriver() {
    return jdbcDriver;
  }

  public String getMemory() {
    return memory;
  }

  public String getPassword() {
    return password;
  }

  public String getUrlPrefix() {
    return urlPrefix;
  }

  public String getUser() {
    return user;
  }

  public void setConnectionString(String connectionString) {
    this.connectionString = connectionString;
  }

  private void setType(String type) {
    this.type = type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void setServer(String server) {
    this.server = server;
  }

  private void setJdbcDriver(String jdbcDriver) {
    this.jdbcDriver = jdbcDriver;
  }

  public void setMemory(String memory) {
    this.memory = memory;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUrlPrefix(String urlPrefix) {
    this.urlPrefix = urlPrefix;
  }

  public void setUser(String user) {
    this.user = user;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Type:[" + getType() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("JDBC Driver:[" + getJdbcDriver() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("URL Prefix:[" + getUrlPrefix() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Server:[" + getServer() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Port:[" + getPort() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Name:[" + getName() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("User:[" + getUser() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Password:[" + getPassword() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Memory:[" + getMemory() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Connection String:[" + getConnectionString() + "]");
    return stringBuilder.toString();
  }
}
