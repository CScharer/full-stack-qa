package com.cjs.qa.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.DisabledRetryAnalyzer;

/**
 * Global Retry Listener for TestNG
 *
 * <p>Automatically applies RetryAnalyzer to all test methods globally, eliminating the need to add
 * `retryAnalyzer = RetryAnalyzer.class` to each @Test annotation.
 *
 * <p>This listener is configured in TestNG suite XML files to enable global retries for all tests.
 *
 * <p>Usage: Add this listener to your TestNG suite XML file:
 *
 * <pre>{@code
 * <listeners>
 *     <listener class-name="com.cjs.qa.utilities.GlobalRetryListener"/>
 * </listener>
 * }</pre>
 */
public class GlobalRetryListener implements IAnnotationTransformer {

  // TestNG IAnnotationTransformer interface requires raw types for Class, Constructor, Method
  // This is a limitation of the TestNG API, not our code
  @Override
  @SuppressWarnings("rawtypes")
  public void transform(
      ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
    // Only apply retry analyzer if one is not already specified
    // This allows individual tests to override with their own retry analyzer if needed
    // getRetryAnalyzerClass() returns DisabledRetryAnalyzer.class if no retry analyzer is set
    if (annotation.getRetryAnalyzerClass() == DisabledRetryAnalyzer.class) {
      annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
  }
}
