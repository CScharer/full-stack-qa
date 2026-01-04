package com.cjs.qa.utilities;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.cjs.qa.core.QAException;

/**
 * Utility class for retrying Selenium Grid connections with exponential backoff.
 *
 * <p>This class provides intelligent retry logic for Grid connections, distinguishing between
 * transient errors (which should be retried) and permanent errors (which should fail fast).
 *
 * <p>Features:
 *
 * <ul>
 *   <li>Exponential backoff with configurable base delay and max delay
 *   <li>Jitter to prevent thundering herd problem
 *   <li>Error categorization (transient vs. permanent)
 *   <li>Configurable retry attempts and timeouts
 *   <li>Detailed logging of retry attempts
 * </ul>
 */
public final class RetryableGridConnection {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RetryableGridConnection.class));

  private static final int DEFAULT_MAX_RETRIES = 5;
  private static final long DEFAULT_BASE_DELAY_MS = 1000L;
  private static final long DEFAULT_MAX_DELAY_MS = 10000L;
  private static final long DEFAULT_TIMEOUT_MS = 30000L;
  private static final double JITTER_FACTOR = 0.1; // 10% jitter

  private static final Random random = new Random();

  private RetryableGridConnection() {
    // Utility class - do not instantiate
  }

  /**
   * Connects to Selenium Grid with retry logic and exponential backoff.
   *
   * @param gridUrl The Grid hub URL
   * @param capabilities The desired capabilities for the WebDriver
   * @return A connected RemoteWebDriver instance
   * @throws QAException if connection fails after all retry attempts
   */
  public static RemoteWebDriver connectWithRetry(String gridUrl, Capabilities capabilities)
      throws QAException {
    int maxRetries = getMaxRetries();
    long timeoutMs = getRetryTimeout();
    long startTime = System.currentTimeMillis();

    if (LOG.isInfoEnabled()) {
      LOG.info("Attempting to connect to Grid at {} with retry logic", gridUrl);
      LOG.info("Max retries: {}, Timeout: {}ms", maxRetries, timeoutMs);
    }

    Exception lastException = null;
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      // Check timeout
      long elapsed = System.currentTimeMillis() - startTime;
      if (elapsed > timeoutMs) {
        String errorMessage =
            String.format(
                "Connection timeout after %dms (attempt %d/%d)", elapsed, attempt, maxRetries);
        LOG.error(errorMessage);
        throw new QAException(errorMessage, lastException);
      }

      try {
        if (LOG.isInfoEnabled()) {
          LOG.info("Connection attempt {}/{} to Grid at {}", attempt, maxRetries, gridUrl);
        }

        RemoteWebDriver driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), capabilities);

        if (driver != null) {
          long totalTime = System.currentTimeMillis() - startTime;
          LOG.info(
              "✅ Successfully connected to Grid on attempt {}/{} (took {}ms)",
              attempt,
              maxRetries,
              totalTime);
          return driver;
        }
      } catch (Exception e) {
        lastException = e;

        // Check if error is permanent (don't retry)
        if (!isTransientError(e)) {
          String errorMessage =
              String.format(
                  "Permanent error on attempt %d/%d: %s - %s",
                  attempt, maxRetries, e.getClass().getSimpleName(), e.getMessage());
          LOG.error(errorMessage);
          throw new QAException(errorMessage, e);
        }

        // Calculate backoff delay
        long backoffDelay = calculateBackoff(attempt);

        if (LOG.isWarnEnabled()) {
          LOG.warn(
              "Transient error on attempt {}/{}: {} - {}. Retrying in {}ms...",
              attempt,
              maxRetries,
              e.getClass().getSimpleName(),
              e.getMessage(),
              backoffDelay);
        }

        // Sleep with backoff
        try {
          Thread.sleep(backoffDelay);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          QAException interruptedException = new QAException("Retry interrupted");
          interruptedException.initCause(ie);
          throw interruptedException;
        }
      }
    }

    // All retries exhausted
    long totalTime = System.currentTimeMillis() - startTime;
    String errorMessage =
        String.format(
            "Failed to connect to Grid after %d attempts (total time: %dms): %s",
            maxRetries,
            totalTime,
            lastException != null ? lastException.getMessage() : "Unknown error");
    LOG.error(errorMessage);
    throw new QAException(errorMessage, lastException);
  }

  /**
   * Determines if an exception represents a transient error that should be retried.
   *
   * @param e The exception to check
   * @return true if the error is transient and should be retried, false otherwise
   */
  public static boolean isTransientError(Exception e) {
    if (e == null) {
      return false;
    }

    // Check exception type
    Class<?> exceptionClass = e.getClass();

    // Transient errors (should retry)
    if (ConnectException.class.isAssignableFrom(exceptionClass)
        || SocketException.class.isAssignableFrom(exceptionClass)
        || SocketTimeoutException.class.isAssignableFrom(exceptionClass)
        || java.util.concurrent.TimeoutException.class.isAssignableFrom(exceptionClass)
        || org.openqa.selenium.TimeoutException.class.isAssignableFrom(exceptionClass)) {
      return true;
    }

    // Check exception message for transient error patterns
    String message = e.getMessage();
    if (message != null) {
      String lowerMessage = message.toLowerCase(Locale.ROOT);
      if (lowerMessage.contains("connection refused")
          || lowerMessage.contains("connection reset")
          || lowerMessage.contains("timeout")
          || lowerMessage.contains("temporarily unavailable")
          || lowerMessage.contains("service unavailable")
          || lowerMessage.contains("503")
          || lowerMessage.contains("502")
          || lowerMessage.contains("504")) {
        return true;
      }
    }

    // Permanent errors (don't retry)
    if (UnknownHostException.class.isAssignableFrom(exceptionClass)
        || java.net.MalformedURLException.class.isAssignableFrom(exceptionClass)
        || IllegalArgumentException.class.isAssignableFrom(exceptionClass)
        || org.openqa.selenium.SessionNotCreatedException.class.isAssignableFrom(exceptionClass)) {
      return false;
    }

    // Check message for permanent error patterns
    if (message != null) {
      String lowerMessage = message.toLowerCase(Locale.ROOT);
      if (lowerMessage.contains("version mismatch")
          || lowerMessage.contains("authentication")
          || lowerMessage.contains("unauthorized")
          || lowerMessage.contains("forbidden")
          || lowerMessage.contains("invalid capabilities")
          || lowerMessage.contains("malformed url")
          || lowerMessage.contains("unknown host")) {
        return false;
      }
    }

    // Default: treat as transient (conservative approach)
    return true;
  }

  /**
   * Calculates exponential backoff delay with jitter.
   *
   * @param attempt The current attempt number (1-based)
   * @return The delay in milliseconds
   */
  public static long calculateBackoff(int attempt) {
    long baseDelay = getRetryBaseDelay();
    long maxDelay = getRetryMaxDelay();

    // Exponential backoff: baseDelay * 2^(attempt-1)
    long delay = baseDelay * (1L << (attempt - 1));

    // Cap at max delay
    delay = Math.min(delay, maxDelay);

    // Add jitter (±10%)
    long jitter = (long) (delay * JITTER_FACTOR);
    long jitterAmount = (random.nextLong() % (2 * jitter + 1)) - jitter;

    delay = delay + jitterAmount;

    // Ensure minimum delay of 100ms
    return Math.max(delay, 100L);
  }

  /**
   * Gets the maximum number of retry attempts from configuration.
   *
   * @return Maximum retry attempts
   */
  public static int getMaxRetries() {
    String envValue = System.getenv("SELENIUM_GRID_MAX_RETRIES");
    if (envValue != null && !envValue.isEmpty()) {
      try {
        return Integer.parseInt(envValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid SELENIUM_GRID_MAX_RETRIES value: {}. Using default: {}",
            envValue,
            DEFAULT_MAX_RETRIES);
      }
    }

    String propValue = System.getProperty("selenium.grid.max.retries");
    if (propValue != null && !propValue.isEmpty()) {
      try {
        return Integer.parseInt(propValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid selenium.grid.max.retries value: {}. Using default: {}",
            propValue,
            DEFAULT_MAX_RETRIES);
      }
    }

    return DEFAULT_MAX_RETRIES;
  }

  /**
   * Gets the base delay for exponential backoff from configuration.
   *
   * @return Base delay in milliseconds
   */
  public static long getRetryBaseDelay() {
    String envValue = System.getenv("SELENIUM_GRID_RETRY_BASE_DELAY_MS");
    if (envValue != null && !envValue.isEmpty()) {
      try {
        return Long.parseLong(envValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid SELENIUM_GRID_RETRY_BASE_DELAY_MS value: {}. Using default: {}ms",
            envValue,
            DEFAULT_BASE_DELAY_MS);
      }
    }

    String propValue = System.getProperty("selenium.grid.retry.base.delay.ms");
    if (propValue != null && !propValue.isEmpty()) {
      try {
        return Long.parseLong(propValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid selenium.grid.retry.base.delay.ms value: {}. Using default: {}ms",
            propValue,
            DEFAULT_BASE_DELAY_MS);
      }
    }

    return DEFAULT_BASE_DELAY_MS;
  }

  /**
   * Gets the maximum delay for exponential backoff from configuration.
   *
   * @return Maximum delay in milliseconds
   */
  public static long getRetryMaxDelay() {
    String envValue = System.getenv("SELENIUM_GRID_RETRY_MAX_DELAY_MS");
    if (envValue != null && !envValue.isEmpty()) {
      try {
        return Long.parseLong(envValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid SELENIUM_GRID_RETRY_MAX_DELAY_MS value: {}. Using default: {}ms",
            envValue,
            DEFAULT_MAX_DELAY_MS);
      }
    }

    String propValue = System.getProperty("selenium.grid.retry.max.delay.ms");
    if (propValue != null && !propValue.isEmpty()) {
      try {
        return Long.parseLong(propValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid selenium.grid.retry.max.delay.ms value: {}. Using default: {}ms",
            propValue,
            DEFAULT_MAX_DELAY_MS);
      }
    }

    return DEFAULT_MAX_DELAY_MS;
  }

  /**
   * Gets the total timeout for all retry attempts from configuration.
   *
   * @return Timeout in milliseconds
   */
  public static long getRetryTimeout() {
    String envValue = System.getenv("SELENIUM_GRID_RETRY_TIMEOUT_MS");
    if (envValue != null && !envValue.isEmpty()) {
      try {
        return Long.parseLong(envValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid SELENIUM_GRID_RETRY_TIMEOUT_MS value: {}. Using default: {}ms",
            envValue,
            DEFAULT_TIMEOUT_MS);
      }
    }

    String propValue = System.getProperty("selenium.grid.retry.timeout.ms");
    if (propValue != null && !propValue.isEmpty()) {
      try {
        return Long.parseLong(propValue);
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid selenium.grid.retry.timeout.ms value: {}. Using default: {}ms",
            propValue,
            DEFAULT_TIMEOUT_MS);
      }
    }

    return DEFAULT_TIMEOUT_MS;
  }
}
