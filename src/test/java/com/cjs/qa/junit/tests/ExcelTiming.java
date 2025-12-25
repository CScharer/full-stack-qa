package com.cjs.qa.junit.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelTiming {

  private Map<String, Integer> methodCallsMap = new HashMap<>();
  private Map<String, Double> methodTimingMap = new HashMap<>();
  private List<String> fileList;

  public List<String> getFileList() {
    return fileList;
  }

  public Map<String, Integer> getMethodCallsMap() {
    return methodCallsMap;
  }

  public Map<String, Double> getMethodTimingMap() {
    return methodTimingMap;
  }

  public void setFileList(List<String> fileList) {
    this.fileList = fileList;
  }

  public void setMethodCallsMap(Map<String, Integer> methodCallsMap) {
    this.methodCallsMap = methodCallsMap;
  }

  public void setMethodTimingMap(Map<String, Double> methodTimingMap) {
    this.methodTimingMap = methodTimingMap;
  }
}
