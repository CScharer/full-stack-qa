package com.cjs.qa.utilities;

import java.util.Arrays;
import java.util.List;

public class ProcessUtil {

  public static final String HEADINGS = "Image Name;PID;Session Name;Session#;Mem Usage";
  public static final List<String> HEADING_LIST =
      Arrays.asList(HEADINGS.split(Constants.DELIMETER_LIST));
  private String imageName = null;
  private String pid = null;
  private String sessionName = null;
  private String sessionNumber = null;
  private String memUsage = null;

  public ProcessUtil(
      String imageName, String pid, String sessionName, String sessionNumber, String memUsage) {
    this.imageName = imageName;
    this.pid = pid;
    this.sessionName = sessionName;
    this.sessionNumber = sessionNumber;
    this.memUsage = memUsage;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getSessionName() {
    return sessionName;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public String getSessionNumber() {
    return sessionNumber;
  }

  public void setSessionNumber(String sessionNumber) {
    this.sessionNumber = sessionNumber;
  }

  public String getMemUsage() {
    return memUsage;
  }

  public void setMemUsage(String memUsage) {
    this.memUsage = memUsage;
  }

  // public static void main(String[] args)
  // {
  // ProcessHandle.allProcesses().forEach(process ->
  // LOG.debug("{}", processDetails(process));
  // }
  //
  // private static String processDetails(ProcessHandle process)
  // {
  // return String.format("%8d %8s %10s %26s %-40s", process.pid(),
  // text(process.parent().map(ProcessHandle::pid)),
  // text(process.info().user()), text(process.info().startInstant()),
  // text(process.info().commandLine()));
  // }
  //
  // private static String text(Optional<?> optional)
  // {
  // return optional.map(Object::toString).orElse("-");
  // }
}
