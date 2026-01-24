package com.cjs.qa.junit.tests;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.maven.objects.Dependency;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class MavenTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(MavenTests.class));
  private String stringBuffer = JavaHelpers.createBufferString("*", 100, "") + Constants.NEWLINE;
  private FSOTests fso = new FSOTests();

  private String getStringBuffer() {
    return stringBuffer;
  }

  public FSOTests getFso() {
    return fso;
  }

  @Test
  public void dependencyTest() throws Exception {
    final String projectAnalyze =
        "C:"
            + Constants.DELIMETER_PATH
            + "Workspace"
            + Constants.DELIMETER_PATH
            + "Github"
            + Constants.DELIMETER_PATH
            + "cjs-app"
            + Constants.DELIMETER_PATH
            + "cjs-app-gui"
            + Constants.DELIMETER_PATH
            + "pom"
            + IExtension.XML;
    final String pathWorkspace =
        "C:"
            + Constants.DELIMETER_PATH
            + "Workspace"
            + Constants.DELIMETER_PATH
            + "Github"
            + Constants.DELIMETER_PATH;
    final String resultsPathFile =
        Constants.PATH_FILES_DATA + "MavenDependenciesResults" + IExtension.LOG;
    FSOTests.fileWrite(resultsPathFile, "", false);
    final List<Path> files = FSOTests.pathsList(pathWorkspace);
    for (final Path path : files) {
      final StringBuilder stringBuilder = new StringBuilder();
      final String filePathName = path.toString();
      if (filePathName.contains("pom" + IExtension.XML)) {
        if (filePathName.equals(projectAnalyze)) {
          stringBuilder.append(getStringBuffer());
          stringBuilder.append(newLine("PATH_WORKSPACE:" + pathWorkspace));
          stringBuilder.append(newLine("resultsPathFile:" + resultsPathFile));
          stringBuilder.append(newLine("filePathName:[" + filePathName + "]"));
          final String projectPath = path.getParent().toString();
          stringBuilder.append(newLine("projectPath:[" + projectPath + "]"));
          final String batchPathFile = Constants.PATH_TEMP + "mvn.dependecy.test" + IExtension.BAT;
          stringBuilder.append(newLine("batchPathFile:[" + batchPathFile + "]"));
          final String outputPathFile = batchPathFile.replace(IExtension.BAT, IExtension.LOG);
          stringBuilder.append(newLine("outputPathFile:[" + outputPathFile + "]"));
          LOG.info("Maven dependency analysis: {}", stringBuilder.toString());
          FSOTests.fileWrite(resultsPathFile, stringBuilder.toString(), true);
          final String results = runDependencyCheck(batchPathFile, projectPath, outputPathFile);
          LOG.info("Dependency check results: {}", results);
          FSOTests.fileWrite(resultsPathFile, results, false);
          if (FSOTests.fileExists(outputPathFile)) {
            FSOTests.fileDelete(outputPathFile);
          }
        }
      }
    }
    FSOTests.fileWrite(resultsPathFile, getStringBuffer(), true);
  }

  private String runDependencyCheck(
      String batchPathFile, String projectPath, String resultsPathFile) throws Exception {
    final StringBuilder stringBuilder = new StringBuilder(getStringBuffer());
    stringBuilder.append(newLine("Checking POM Dependencies for [" + projectPath + "]"));
    stringBuilder.append(newLine("Creating Temporary Batch File [" + batchPathFile + "]"));
    final StringBuilder stringBuilderBatch = new StringBuilder();
    stringBuilderBatch.append(newLine("cls"));
    stringBuilderBatch.append(newLine("c:"));
    stringBuilderBatch.append(newLine("cd" + Constants.DELIMETER_PATH));
    stringBuilderBatch.append(newLine("cd " + projectPath));
    stringBuilderBatch.append(
        newLine(
            "mvn dependency:analyze>"
                + Constants.QUOTE_DOUBLE
                + resultsPathFile
                + Constants.QUOTE_DOUBLE));
    stringBuilderBatch.append(newLine("exit"));
    stringBuilder.append(stringBuilderBatch.toString());
    FSOTests.fileWrite(batchPathFile, stringBuilderBatch.toString(), false);
    final String command =
        "cmd /C " + Constants.QUOTE_DOUBLE + batchPathFile + Constants.QUOTE_DOUBLE;
    stringBuilder.append(newLine("command:[" + command + "]"));
    final Map<String, String> mapResults = CommandLineTests.runProcess(command, true);
    final String status = mapResults.get("status");
    stringBuilder.append(newLine("status:" + mapResults.get("status")));
    if ("0".equals(status)) {
      final String projectFolder =
          projectPath.substring(projectPath.lastIndexOf(Constants.DELIMETER_PATH) + 1);
      stringBuilder.append(newLine(parseResultsFile(resultsPathFile, projectFolder)));
    }
    if (FSOTests.fileExists(batchPathFile)) {
      FSOTests.fileDelete(batchPathFile);
    }
    return stringBuilder.toString();
  }

  private String parseResultsFile(String resultsPathFile, String projectFolder) {
    final StringBuilder stringBuilder = new StringBuilder();
    final String infoBuildSuccess = "[INFO] BUILD SUCCESS";
    final String warningDependency = "[WARNING]    ";
    final String warningUndeclared = "[WARNING] Used undeclared dependencies found:";
    final String warningUnused = "[WARNING] Unused declared dependencies found:";
    final String results = FSOTests.fileReadAll(resultsPathFile);
    stringBuilder.append(results);
    if (results.contains(infoBuildSuccess)) {
      final String[] records = results.split(Constants.NEWLINE);
      final List<String> dependenciesUndeclaredList = new ArrayList<>();
      final List<String> dependenciesUnusedList = new ArrayList<>();
      boolean dependenciesSection = false;
      boolean undeclaredSection = false;
      boolean unusedSection = false;
      for (final String record : records) {
        if (record.contains("[INFO] --- maven-dependency-plugin:")
            && record.contains(":analyze (default-cli) @ " + projectFolder + " ---")) {
          dependenciesSection = true;
        }
        if (record.contains(
            "[INFO]"
                + " ------------------------------------------------------------------------")) {
          dependenciesSection = false;
        }
        if (dependenciesSection) {
          if (record.equals(warningUndeclared)) {
            unusedSection = false;
            undeclaredSection = true;
          }
          if (record.equals(warningUnused)) {
            unusedSection = true;
            undeclaredSection = false;
          }
          if (undeclaredSection) {
            // Add Undeclared Dependency
            if (record.contains(warningDependency) && !record.equalsIgnoreCase(warningUndeclared)) {
              final String[] dependencyArray = record.split(" ");
              final String dependency = dependencyArray[dependencyArray.length - 1];
              dependenciesUndeclaredList.add(dependency);
            }
          }
          if (unusedSection) {
            // Add Unused Dependency
            if (record.contains(warningDependency) && !record.equalsIgnoreCase(warningUnused)) {
              final String[] dependencyArray = record.split(" ");
              final String dependency = dependencyArray[dependencyArray.length - 1];
              dependenciesUnusedList.add(dependency);
            }
          }
        }
      }
      stringBuilder.append(sortAddDependenciesUndeclared(dependenciesUndeclaredList));
      stringBuilder.append(sortAddDependenciesUnused(dependenciesUnusedList));
    }
    return stringBuilder.toString();
  }

  private String sortAddDependenciesUndeclared(List<String> dependenciesList) {
    // Sort and Add Undeclared Dependences
    dependenciesList.sort(null);
    final StringBuilder stringBuilder = new StringBuilder("");
    final List<Dependency> dependenciesObjectList = new ArrayList<>();
    final StringBuilder stringBuilderP = new StringBuilder("");
    final StringBuilder stringBuilderD = new StringBuilder("");
    for (final String dependency : dependenciesList) {
      final Dependency dependencyObject = new Dependency(dependency);
      dependenciesObjectList.add(dependencyObject);
      stringBuilderP.append(Constants.TAB);
      stringBuilderP.append(newLine(dependencyObject.getVersionProperty()));
      final String dependencyOld = dependencyObject.toString();
      final String dependencyNew =
          dependencyOld.replace(dependencyObject.getVersion(), dependencyObject.getVersionTag());
      stringBuilderD.append(dependencyNew);
    }
    stringBuilder.append(
        newLine(
            "Dependency Properties (Versions) Undeclared (" + dependenciesObjectList.size() + ")"));
    stringBuilder.append(stringBuilderP.toString());
    if (dependenciesObjectList.isEmpty()) {
      stringBuilder.append(newLine("None"));
    }
    stringBuilder.append(
        newLine("Dependencies Undeclared (" + dependenciesObjectList.size() + ")"));
    stringBuilder.append(stringBuilderD.toString());
    if (dependenciesObjectList.isEmpty()) {
      stringBuilder.append(newLine("None"));
    }
    return stringBuilder.toString();
  }

  private String sortAddDependenciesUnused(List<String> dependenciesList) {
    // Sort and Add Unused Dependencies
    dependenciesList.sort(null);
    final StringBuilder stringBuilder = new StringBuilder("");
    final StringBuilder stringBuilderD = new StringBuilder("");
    final List<Dependency> dependenciesObjectList = new ArrayList<>();
    for (final String dependency : dependenciesList) {
      final Dependency dependencyObject = new Dependency(dependency);
      dependenciesObjectList.add(dependencyObject);
      stringBuilderD.append(newLine(dependencyObject.toString()));
    }
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(newLine("Dependencies Unused (" + dependenciesObjectList.size() + ")"));
    stringBuilder.append(stringBuilderD.toString());
    if (dependenciesObjectList.isEmpty()) {
      stringBuilder.append(newLine("None"));
    }
    return stringBuilder.toString();
  }

  private String newLine(String value) {
    return value + Constants.NEWLINE;
  }
}
