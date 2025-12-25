package com.cjs.qa.junit.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.junit.runner.Description;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class RTestReporter {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RTestReporter.class));

  private List<Map<String, Map<String, String>>> listMapTests = new ArrayList<>();
  private Map<String, Integer> mapCounts = new HashMap<>();
  private List<String> listStatus =
      Arrays.asList("Failed;Finished;Skipped;Starting;Succeeded".split(Constants.NEWLINE));

  public RTestReporter() {
    setMapCounts(new HashMap<>());
    for (final String status : getListStatus()) {
      mapCounts.put(status, 0);
    }
  }

  public void addStepStatus(String stepName, Description description, String status) {
    final Map<String, String> mapStatus = new HashMap<>();
    mapStatus.put(description.toString(), status);
    mapCounts.put(status, mapCounts.get(status) + 1);
  }

  public List<String> getListStatus() {
    return listStatus;
  }

  public Map<String, Integer> getMapCounts() {
    return mapCounts;
  }

  public void reportAll() {
    for (int indexTest = 0; indexTest < getListMapTests().size(); indexTest++) {
      final Map<String, Map<String, String>> mapTest = getListMapTests().get(indexTest);
      for (final String keyTest : mapTest.keySet()) {
        final Map<String, String> mapSteps = mapTest.get(keyTest);
        for (final String keyStep : mapSteps.keySet()) {
          final String value = mapSteps.get(keyStep);
          LOG.debug("{};[{}]", keyStep, value);
        }
      }
    }

    int count = 0;
    for (final Entry<String, Integer> entry : mapCounts.entrySet()) {
      switch (entry.getKey()) {
        case "Finished":
        case "Starting":
          break;
        default:
          count += entry.getValue();
          break;
      }
      LOG.debug("{}:[{}]", entry.getKey(), entry.getValue());
    }
    LOG.debug("Count Tests:[[{}]", count + "]");
  }

  public void setListStatus(List<String> listStatus) {
    this.listStatus = listStatus;
  }

  public void setMapCounts(Map<String, Integer> mapCounts) {
    this.mapCounts = mapCounts;
  }

  public void addTest(String testName) {
    // TODO Auto-generated method stub
  }

  public void reportTest(String testName) {
    // TODO Auto-generated method stub
  }

  public List<Map<String, Map<String, String>>> getListMapTests() {
    return listMapTests;
  }

  public void setListMapTests(List<Map<String, Map<String, String>>> listMapTests) {
    this.listMapTests = listMapTests;
  }
}
