package com.cjs.qa.core;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

public class ScenarioErrors {

  private static Map<Integer, String> errors = new HashMap<>();

  public static Map<Integer, String> getErrors() {
    return errors;
  }

  public void assertErrors(String message) {
    Assertions.assertEquals(0, getErrors().size(), message);
  }

  public void add(String error) {
    getErrors().put(Environment.getScenarioErrors().size() + 1, error);
  }

  public static void clear() {
    errors = new HashMap<>();
  }
}
