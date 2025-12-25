package com.cjs.qa.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import io.cucumber.datatable.DataTable;

// import java.util.Optional;

public class Processes {

  @SuppressWarnings("unused")
  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(Processes.class)); // Reserved for future logging

  public static final String NO_RESULTS =
      "INFO: No tasks are running which match the specified criteria.";
  private List<ProcessUtil> processList = new ArrayList<>();

  public Processes(String command) throws Exception {
    // Note: Command must not include headers.
    // CommandLineTests.TASKLIST + " /fo:csv /nh /fi \"
    // executeCommand now throws Exception instead of Throwable
    String data = CommandLineTests.executeCommand(command);
    if (!data.contains(NO_RESULTS)) {
      List<String> records = Arrays.asList(data.split(Constants.NL));
      for (String recordRaw : records) {
        // if (indexRecord > 1)
        // {
        String record = recordRaw.replaceAll(" +", " ");
        // String[] columns = record.split(" ");
        String[] columns = record.split("\",\"");
        String imageName = columns[0].replaceAll("\"", "");
        String pid = columns[1].replaceAll("\"", "");
        String sessionName = columns[2].replaceAll("\"", "");
        String sessionNumber = columns[3].replaceAll("\"", "");
        // String memUsage = columns[4] + " " + columns[5];
        String memUsage = columns[4].replaceAll("\"", "");
        processList.add(new ProcessUtil(imageName, pid, sessionName, sessionNumber, memUsage));
        // }
      }
    }
  }

  public List<ProcessUtil> getProcessList() {
    return processList;
  }

  public void setProcessList(List<ProcessUtil> processList) {
    this.processList = processList;
  }

  public DataTable toDataTable() {
    DataTable dataTable = null;
    List<List<String>> dataTableList = new ArrayList<>();
    dataTableList.add(Arrays.asList("Image Name", "PID", "Session Name", "Session#", "Mem Usage"));
    if (!getProcessList().isEmpty()) {
      for (ProcessUtil processUtil : getProcessList()) {
        String imageName = "\"" + processUtil.getImageName() + "\"";
        String pid = "\"" + processUtil.getPid() + "\"";
        String sessionName = "\"" + processUtil.getSessionName() + "\"";
        String sessionNumber = "\"" + processUtil.getSessionNumber() + "\"";
        String memUsage = "\"" + processUtil.getMemUsage() + "\"";
        dataTableList.add(Arrays.asList(imageName, pid, sessionName, sessionNumber, memUsage));
      }
    }
    dataTable = DataTable.create(dataTableList);
    return dataTable;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (getProcessList().isEmpty()) {
      stringBuilder.append(NO_RESULTS);
    } else {
      stringBuilder.append(ProcessUtil.HEADINGS.replaceAll(Constants.DELIMETER_LIST, ", "));
      for (ProcessUtil processUtil : getProcessList()) {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(Constants.NEWLINE);
        }
        stringBuilder.append("[" + processUtil.getImageName() + "]");
        stringBuilder.append("[" + processUtil.getPid() + "]");
        stringBuilder.append("[" + processUtil.getSessionName() + "]");
        stringBuilder.append("[" + processUtil.getSessionNumber() + "]");
        stringBuilder.append("[" + processUtil.getMemUsage() + "]");
      }
    }
    return stringBuilder.toString();
  }
  //
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
