package com.cjs.qa.utilities;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.Environment;

public class CommandLineTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(CommandLineTests.class));

  // Windows-specific commands
  public static final String TASKKILL = "taskkill /f /im ";
  public static final String TASKLIST = "tasklist";

  // Cross-platform process name normalization
  private static String normalizeProcessName(String processName) {
    if (processName == null) {
      return null;
    }
    // On Mac/Linux, remove .exe extension if present
    if (!Constants.IS_WINDOWS && processName.endsWith(".exe")) {
      return processName.substring(0, processName.length() - 4);
    }
    return processName;
  }

  @Test
  public void testCommandLine() throws IOException {
    String processName = Constants.IS_WINDOWS ? "chrome.exe" : "chrome";
    LOG.info("Chrome is running: [{}]", isProcessRunning(processName));
  }

  @Test
  public void testGetJpsProcesses() throws Throwable {
    final String surefireBooter = "surefirebooter";
    List<String> jpsProcessList = new ArrayList<>();
    Map<String, String> jpsProcessMap = new HashMap<>();
    jpsProcessList = getJpsProcessesList(surefireBooter);
    LOG.debug("jpsProcessList: {}", jpsProcessList.toString());
    jpsProcessList = getJpsProcessesList(null);
    LOG.debug("jpsProcessList: {}", jpsProcessList.toString());
    jpsProcessMap = getJpsProcessesMap(null);
    LOG.debug("jpsProcessMap: {}", jpsProcessMap.toString());
    jpsProcessMap = getJpsProcessesMap(surefireBooter);
    LOG.debug("jpsProcessMap: {}", jpsProcessMap.toString());
    for (Entry<String, String> entry : jpsProcessMap.entrySet()) {
      String pid = entry.getKey();
      String processName = entry.getValue();
      String imageName = null;
      String command;
      if (Constants.IS_WINDOWS) {
        command = TASKLIST + " /fo:csv /nh /fi \"pid eq " + pid + "\"";
      } else {
        // Mac/Linux: Use ps with PID
        command = "ps -p " + pid + " -o pid,comm,args";
      }
      try {
        Processes processes = new Processes(command);
        if (!processes.getProcessList().isEmpty()) {
          imageName = processes.getProcessList().get(0).getImageName();
        }
      } catch (Exception e) {
        LOG.warn("Failed to get process info for PID {}: {}", pid, e.getMessage());
      }
      LOG.debug("pid: [{}], processName: [{}], imageName: [{}]", pid, processName, imageName);
    }
  }

  public Map<String, String> getJpsProcessesMap(String jpsProcessName) throws Throwable {
    Map<String, String> jpsProcessMap = new HashMap<>();
    List<String> jpsProcessList = getJpsProcessesList(jpsProcessName);
    for (String jpsProcess : jpsProcessList) {
      String[] jpsProcessArray = jpsProcess.split(" ");
      String pid = jpsProcessArray[0];
      String processName = null;
      if (jpsProcessArray.length > 1) {
        processName = jpsProcessArray[1];
      }
      jpsProcessMap.put(pid, processName);
    }
    return jpsProcessMap;
  }

  public List<String> getJpsProcessesList(String jpsProcessName) throws Throwable {
    List<String> jpsProcessListNew = new ArrayList<>();
    // Cross-platform jps command
    String command = Constants.IS_WINDOWS ? "cmd /C jps" : "jps";
    final Map<String, String> mapProcess = runProcess(command, true);
    String status = mapProcess.get("status");
    if ("0".equals(status)) {
      String processes = mapProcess.get("lines").toString();
      List<String> jpsProcessList = Arrays.asList(processes.split(Constants.NEWLINE));
      for (String jpsProcess : jpsProcessList) {
        if (JavaHelpers.hasValue(jpsProcessName)) {
          if (jpsProcess.indexOf(jpsProcessName) != -1) {
            jpsProcessListNew.add(jpsProcess);
          }
        } else {
          jpsProcessListNew.add(jpsProcess);
        }
      }
    }
    return jpsProcessListNew;
  }

  @Test
  public void testProcesses() throws Throwable {
    Processes processes = null;
    String command = "";

    if (Constants.IS_WINDOWS) {
      // Windows-specific tasklist commands
      command = TASKLIST + " /fo:csv /nh /fi \"imagename eq chrome.exe\"";
      LOG.debug("command: [{}]", command);
      processes = new Processes(command);
      LOG.debug("Processes: {}", processes.toString());
      command =
          TASKLIST + " /fo:csv /nh /fi \"windowtitle eq Selenium-Grid2-Hub_CSCHARER-LAPTOP*\"";
      LOG.debug("command: [{}]", command);
      processes = new Processes(command);
      LOG.debug("Processes: {}", processes.toString());
      command =
          TASKLIST
              + " /fo:csv /nh /fi \"windowtitle eq"
              + " Selenium-Grid2-Node_CSCHARER-LAPTOP*\"";
      LOG.debug("command: [{}]", command);
      processes = new Processes(command);
      LOG.debug("Processes: {}", processes.toString());
      command = TASKLIST + " /fo:csv /nh /fi \"windowtitle eq Selenium-Grid2-*\"";
      LOG.debug("command: [{}]", command);
      processes = new Processes(command);
      LOG.debug("Processes: {}", processes.toString());
      command = TASKLIST + " /fo:csv /nh /fi \"windowtitle eq Selenium-Grid3-*\"";
      LOG.debug("command: [{}]", command);
      processes = new Processes(command);
      LOG.debug("Processes: {}", processes.toString());
    } else {
      // Mac/Linux: Use ps command with similar filtering
      // Note: Window title filtering is Windows-specific, so we'll use process name filtering
      command = "ps -eo pid,comm,args | grep -i chrome | head -5";
      LOG.debug("command: [{}] (Mac/Linux)", command);
      try {
        processes = new Processes(command);
        LOG.debug("Processes: {}", processes.toString());
      } catch (Exception e) {
        LOG.warn("Process listing failed (expected on Mac/Linux): {}", e.getMessage());
      }
      // Additional ps commands for testing
      command = "ps -eo pid,comm | head -10";
      LOG.debug("command: [{}] (Mac/Linux)", command);
      try {
        processes = new Processes(command);
        LOG.debug("Processes: {}", processes.toString());
      } catch (Exception e) {
        LOG.warn("Process listing failed: {}", e.getMessage());
      }
    }
  }

  public static String executeCommand(String command) throws Exception {
    final StringBuilder stringBuilder = new StringBuilder();
    try {
      // Handle Windows shell commands (cmd /C ...) and simple commands
      ProcessBuilder processBuilder;
      if (Constants.IS_WINDOWS
          && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
        // Windows shell command: split into ["cmd", "/C", "rest of command"]
        String[] parts = command.split("\\s+", 3);
        if (parts.length == 3) {
          processBuilder = new ProcessBuilder(parts[0], parts[1], parts[2]);
        } else {
          processBuilder = new ProcessBuilder(command.split("\\s+"));
        }
      } else if (!Constants.IS_WINDOWS
          && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
        // On Mac/Linux, strip "cmd /C" prefix and execute directly
        String actualCommand = command.replaceFirst("(?i)cmd /C ", "").trim();
        processBuilder = new ProcessBuilder("/bin/sh", "-c", actualCommand);
      } else {
        // Simple command - split by spaces
        processBuilder = new ProcessBuilder(command.split("\\s+"));
      }
      Process process = processBuilder.start();
      try (InputStream inputStream = process.getInputStream()) {
        stringBuilder.append(printLines(inputStream));
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return stringBuilder.toString();
  }

  private static String getInputStream(InputStream inputStream) {
    final Scanner scanner =
        new Scanner(inputStream, "UTF-8").useDelimiter(Constants.BACKSLASH + "A");
    final String string = scanner.hasNext() ? scanner.next() : "";
    scanner.close();
    return string;
  }

  public static boolean isProcessRunning(String processName) throws IOException {
    String normalizedName = normalizeProcessName(processName);
    if (Constants.IS_WINDOWS) {
      final ProcessBuilder processBuilder = new ProcessBuilder(TASKLIST + ".exe");
      final Process process = processBuilder.start();
      final String tasksList = getInputStream(process.getInputStream()).toLowerCase(Locale.ENGLISH);
      return tasksList.contains(normalizedName.toLowerCase(Locale.ENGLISH));
    } else {
      // Mac/Linux: Use ps command
      try {
        ProcessBuilder processBuilder = new ProcessBuilder("ps", "aux");
        Process process = processBuilder.start();
        final String processes =
            getInputStream(process.getInputStream()).toLowerCase(Locale.ENGLISH);
        return processes.contains(normalizedName.toLowerCase(Locale.ENGLISH));
      } catch (IOException e) {
        LOG.warn("Failed to check process on Mac/Linux: {}", e.getMessage());
        return false;
      }
    }
  }

  public static boolean isProcessRunningNoException(String processRunning) {
    String normalizedName = normalizeProcessName(processRunning);
    Process process;
    try {
      ProcessBuilder processBuilder;
      if (Constants.IS_WINDOWS) {
        processBuilder = new ProcessBuilder(TASKLIST);
      } else {
        // Mac/Linux: Use ps command
        processBuilder = new ProcessBuilder("ps", "aux");
      }
      process = processBuilder.start();
      try (BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          line = line.toLowerCase(Locale.ENGLISH);
          if (line.contains(normalizedName.toLowerCase(Locale.ENGLISH))) {
            if (Environment.isLogAll()) {
              LOG.debug("Output line: {}", line);
            }
            return true;
          }
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
      LOG.error("Error in command execution", e);
    }
    return false;
  }

  public static void killProcess(String processRunning) throws Exception {
    String command;
    if (Constants.IS_WINDOWS) {
      command = TASKKILL + processRunning;
    } else {
      // Mac/Linux: Use killall or pkill
      String normalizedName = normalizeProcessName(processRunning);
      command = "killall " + normalizedName;
    }
    if (Environment.isLogAll()) {
      LOG.debug("command: [{}]", command);
    }
    ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
    processBuilder.start();
  }

  private static String printLines(InputStream inputStream) throws Exception {
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    final StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(Constants.NEWLINE);
      }
      stringBuilder.append(line);
    }
    return stringBuilder.toString();
  }

  public static void runApplication(String applicationFilePath) throws IOException {
    final File application = new File(applicationFilePath);
    final String applicationName = application.getName();
    if (!isProcessRunning(applicationName)) {
      Desktop.getDesktop().open(application);
    }
  }

  public static int runProcess(String command) throws Exception {
    // Handle Windows shell commands (cmd /C ...) and simple commands
    ProcessBuilder processBuilder;
    if (Constants.IS_WINDOWS && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
      // Windows shell command: split into ["cmd", "/C", "rest of command"]
      String[] parts = command.split("\\s+", 3);
      if (parts.length == 3) {
        processBuilder = new ProcessBuilder(parts[0], parts[1], parts[2]);
      } else {
        processBuilder = new ProcessBuilder(command.split("\\s+"));
      }
    } else if (!Constants.IS_WINDOWS
        && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
      // On Mac/Linux, strip "cmd /C" prefix and execute directly
      String actualCommand = command.replaceFirst("(?i)cmd /C ", "").trim();
      processBuilder = new ProcessBuilder("/bin/sh", "-c", actualCommand);
    } else {
      processBuilder = new ProcessBuilder(command.split("\\s+"));
    }
    final Process process = processBuilder.start();
    printLines(process.getInputStream());
    process.waitFor();
    return process.exitValue();
  }

  public static Map<String, String> runProcess(String command, boolean wait) throws Exception {
    // Handle Windows shell commands (cmd /C ...) and simple commands
    ProcessBuilder processBuilder;
    if (Constants.IS_WINDOWS && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
      // Windows shell command: split into ["cmd", "/C", "rest of command"]
      String[] parts = command.split("\\s+", 3);
      if (parts.length == 3) {
        processBuilder = new ProcessBuilder(parts[0], parts[1], parts[2]);
      } else {
        processBuilder = new ProcessBuilder(command.split("\\s+"));
      }
    } else if (!Constants.IS_WINDOWS
        && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
      // On Mac/Linux, strip "cmd /C" prefix and execute directly
      String actualCommand = command.replaceFirst("(?i)cmd /C ", "").trim();
      processBuilder = new ProcessBuilder("/bin/sh", "-c", actualCommand);
    } else {
      processBuilder = new ProcessBuilder(command.split("\\s+"));
    }
    Process process = processBuilder.start();
    final Map<String, String> mapProcess = new HashMap<>();
    final String lines = printLines(process.getInputStream());
    process.waitFor();
    final int status = process.exitValue();
    mapProcess.put("lines", lines);
    mapProcess.put("status", String.valueOf(status));
    return mapProcess;
  }

  public static void runProcessNoWait(String command) throws Exception {
    LOG.debug("command: [{}]", command);
    // Handle Windows shell commands (cmd /C ...) and simple commands
    ProcessBuilder processBuilder;
    if (Constants.IS_WINDOWS && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
      // Windows shell command: split into ["cmd", "/C", "rest of command"]
      String[] parts = command.split("\\s+", 3);
      if (parts.length == 3) {
        processBuilder = new ProcessBuilder(parts[0], parts[1], parts[2]);
      } else {
        processBuilder = new ProcessBuilder(command.split("\\s+"));
      }
    } else if (!Constants.IS_WINDOWS
        && (command.startsWith("cmd /C ") || command.startsWith("cmd /c "))) {
      // On Mac/Linux, strip "cmd /C" prefix and execute directly
      String actualCommand = command.replaceFirst("(?i)cmd /C ", "").trim();
      processBuilder = new ProcessBuilder("/bin/sh", "-c", actualCommand);
    } else {
      processBuilder = new ProcessBuilder(command.split("\\s+"));
    }
    processBuilder.start();
  }
}
