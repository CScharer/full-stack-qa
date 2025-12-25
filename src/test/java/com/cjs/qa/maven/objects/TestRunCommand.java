package com.cjs.qa.maven.objects;

public class TestRunCommand {

  private String command = "mvn clean test";
  private String argLine = "\"-Xms64m -Xmx128m\"";
  private Boolean failIfNoTests = false;
  private String className = "";
  private String methodName = "";
  private String tagIDs = "\"@\"";

  public TestRunCommand(String className, String methodName) {
    this.className = className;
    this.methodName = methodName;
  }

  public String getArgLine() {
    return argLine;
  }

  public String getClassName() {
    return className;
  }

  public String getCommand() {
    return command;
  }

  public Boolean getFailIfNoTests() {
    return failIfNoTests;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getTagIDs() {
    return tagIDs;
  }

  public void setArgLine(String argLine) {
    this.argLine = argLine;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public void setFailIfNoTests(Boolean failIfNoTests) {
    this.failIfNoTests = failIfNoTests;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public void setTagIDs(String tagIDs) {
    this.tagIDs = tagIDs;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    // mvn clean test
    stringBuilder.append(getCommand() + " ");
    // -DargLine="-Xms64m -Xmx128m"
    stringBuilder.append("-DargLine=" + getArgLine() + " ");
    // -DfailIfNoTests=false
    stringBuilder.append("-DfailIfNoTests=" + getFailIfNoTests() + " ");
    // -Dtest=com.cjs.qa.junit.tests.Scenarios#Google
    stringBuilder.append("-Dtest=" + getClassName() + "#");
    stringBuilder.append(getMethodName() + " ");
    // -Dtag.ids="@"
    stringBuilder.append("-Dtag.ids=" + getTagIDs());
    return stringBuilder.toString();
  }
}
