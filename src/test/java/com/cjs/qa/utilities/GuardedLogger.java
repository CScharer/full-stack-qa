package com.cjs.qa.utilities;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

/**
 * Wrapper class for Logger that automatically guards all log statements to satisfy
 * PMD.GuardLogStatement rule.
 *
 * <p>This class wraps a Logger instance and adds guard checks before each logging operation,
 * eliminating the need for manual guard checks or @SuppressWarnings annotations.
 *
 * <p>Usage: Replace: private static final Logger LOG = LogManager.getLogger(MyClass.class); With:
 * private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(MyClass.class));
 *
 * <p>All existing LOG.debug(), LOG.info(), etc. calls will work without modification.
 *
 * <p>Note: PMD's GuardLogStatement rule may still flag calls to GuardedLogger methods because it
 * doesn't recognize that GuardedLogger already performs guard checks. This is a limitation of PMD's
 * static analysis. The guards are present at runtime, ensuring no unnecessary string formatting
 * occurs when logging is disabled.
 */
@SuppressWarnings("PMD.GuardLogStatement")
public final class GuardedLogger {

  private final Logger logger;

  /**
   * Creates a new GuardedLogger wrapping the given Logger.
   *
   * @param logger the Logger instance to wrap
   */
  public GuardedLogger(final Logger logger) {
    this.logger = logger;
  }

  // ========== DEBUG methods ==========

  public void debug(final String message) {
    if (logger.isDebugEnabled()) {
      logger.debug(message);
    }
  }

  public void debug(final String message, final Object... params) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, params);
    }
  }

  public void debug(final String message, final Object param) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, param);
    }
  }

  public void debug(final String message, final Object p0, final Object p1) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, p0, p1);
    }
  }

  public void debug(final String message, final Object p0, final Object p1, final Object p2) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, p0, p1, p2);
    }
  }

  public void debug(
      final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, p0, p1, p2, p3);
    }
  }

  public void debug(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, p0, p1, p2, p3, p4);
    }
  }

  public void debug(final String message, final Throwable throwable) {
    if (logger.isDebugEnabled()) {
      logger.debug(message, throwable);
    }
  }

  /** Debug with just a Throwable (no message). Handles pattern: LOG.debug(exception) */
  public void debug(final Throwable throwable) {
    if (logger.isDebugEnabled()) {
      logger.debug(throwable);
    }
  }

  public void debug(final Marker marker, final String message) {
    if (logger.isDebugEnabled(marker)) {
      logger.debug(marker, message);
    }
  }

  public void debug(final Marker marker, final String message, final Object... params) {
    if (logger.isDebugEnabled(marker)) {
      logger.debug(marker, message, params);
    }
  }

  public void debug(final Marker marker, final String message, final Throwable throwable) {
    if (logger.isDebugEnabled(marker)) {
      logger.debug(marker, message, throwable);
    }
  }

  // ========== INFO methods ==========

  public void info(final String message) {
    if (logger.isInfoEnabled()) {
      logger.info(message);
    }
  }

  public void info(final String message, final Object... params) {
    if (logger.isInfoEnabled()) {
      logger.info(message, params);
    }
  }

  public void info(final String message, final Object param) {
    if (logger.isInfoEnabled()) {
      logger.info(message, param);
    }
  }

  public void info(final String message, final Object p0, final Object p1) {
    if (logger.isInfoEnabled()) {
      logger.info(message, p0, p1);
    }
  }

  public void info(final String message, final Object p0, final Object p1, final Object p2) {
    if (logger.isInfoEnabled()) {
      logger.info(message, p0, p1, p2);
    }
  }

  public void info(
      final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    if (logger.isInfoEnabled()) {
      logger.info(message, p0, p1, p2, p3);
    }
  }

  public void info(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4) {
    if (logger.isInfoEnabled()) {
      logger.info(message, p0, p1, p2, p3, p4);
    }
  }

  public void info(final String message, final Throwable throwable) {
    if (logger.isInfoEnabled()) {
      logger.info(message, throwable);
    }
  }

  public void info(final Marker marker, final String message) {
    if (logger.isInfoEnabled(marker)) {
      logger.info(marker, message);
    }
  }

  public void info(final Marker marker, final String message, final Object... params) {
    if (logger.isInfoEnabled(marker)) {
      logger.info(marker, message, params);
    }
  }

  public void info(final Marker marker, final String message, final Throwable throwable) {
    if (logger.isInfoEnabled(marker)) {
      logger.info(marker, message, throwable);
    }
  }

  // ========== WARN methods ==========

  public void warn(final String message) {
    if (logger.isWarnEnabled()) {
      logger.warn(message);
    }
  }

  public void warn(final String message, final Object... params) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, params);
    }
  }

  public void warn(final String message, final Object param) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, param);
    }
  }

  public void warn(final String message, final Object p0, final Object p1) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, p0, p1);
    }
  }

  public void warn(final String message, final Object p0, final Object p1, final Object p2) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, p0, p1, p2);
    }
  }

  public void warn(
      final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, p0, p1, p2, p3);
    }
  }

  public void warn(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, p0, p1, p2, p3, p4);
    }
  }

  public void warn(final String message, final Throwable throwable) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, throwable);
    }
  }

  public void warn(final Marker marker, final String message) {
    if (logger.isWarnEnabled(marker)) {
      logger.warn(marker, message);
    }
  }

  public void warn(final Marker marker, final String message, final Object... params) {
    if (logger.isWarnEnabled(marker)) {
      logger.warn(marker, message, params);
    }
  }

  public void warn(final Marker marker, final String message, final Throwable throwable) {
    if (logger.isWarnEnabled(marker)) {
      logger.warn(marker, message, throwable);
    }
  }

  // ========== ERROR methods ==========

  public void error(final String message) {
    if (logger.isErrorEnabled()) {
      logger.error(message);
    }
  }

  public void error(final String message, final Object... params) {
    if (logger.isErrorEnabled()) {
      logger.error(message, params);
    }
  }

  public void error(final String message, final Object param) {
    if (logger.isErrorEnabled()) {
      logger.error(message, param);
    }
  }

  public void error(final String message, final Object p0, final Object p1) {
    if (logger.isErrorEnabled()) {
      logger.error(message, p0, p1);
    }
  }

  public void error(final String message, final Object p0, final Object p1, final Object p2) {
    if (logger.isErrorEnabled()) {
      logger.error(message, p0, p1, p2);
    }
  }

  public void error(
      final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    if (logger.isErrorEnabled()) {
      logger.error(message, p0, p1, p2, p3);
    }
  }

  public void error(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4) {
    if (logger.isErrorEnabled()) {
      logger.error(message, p0, p1, p2, p3, p4);
    }
  }

  public void error(final String message, final Throwable throwable) {
    if (logger.isErrorEnabled()) {
      logger.error(message, throwable);
    }
  }

  /**
   * Special case: error with message, one parameter, and throwable. This handles the common
   * pattern: LOG.error("message: {}", arg, exception)
   */
  public void error(final String message, final Object param, final Throwable throwable) {
    if (logger.isErrorEnabled()) {
      logger.error(message, param, throwable);
    }
  }

  public void error(final Marker marker, final String message) {
    if (logger.isErrorEnabled(marker)) {
      logger.error(marker, message);
    }
  }

  public void error(final Marker marker, final String message, final Object... params) {
    if (logger.isErrorEnabled(marker)) {
      logger.error(marker, message, params);
    }
  }

  public void error(final Marker marker, final String message, final Throwable throwable) {
    if (logger.isErrorEnabled(marker)) {
      logger.error(marker, message, throwable);
    }
  }

  // ========== TRACE methods ==========

  public void trace(final String message) {
    if (logger.isTraceEnabled()) {
      logger.trace(message);
    }
  }

  public void trace(final String message, final Object... params) {
    if (logger.isTraceEnabled()) {
      logger.trace(message, params);
    }
  }

  public void trace(final String message, final Object param) {
    if (logger.isTraceEnabled()) {
      logger.trace(message, param);
    }
  }

  public void trace(final String message, final Throwable throwable) {
    if (logger.isTraceEnabled()) {
      logger.trace(message, throwable);
    }
  }

  public void trace(final Marker marker, final String message) {
    if (logger.isTraceEnabled(marker)) {
      logger.trace(marker, message);
    }
  }

  public void trace(final Marker marker, final String message, final Object... params) {
    if (logger.isTraceEnabled(marker)) {
      logger.trace(marker, message, params);
    }
  }

  public void trace(final Marker marker, final String message, final Throwable throwable) {
    if (logger.isTraceEnabled(marker)) {
      logger.trace(marker, message, throwable);
    }
  }

  // ========== FATAL methods ==========

  public void fatal(final String message) {
    if (logger.isFatalEnabled()) {
      logger.fatal(message);
    }
  }

  public void fatal(final String message, final Object... params) {
    if (logger.isFatalEnabled()) {
      logger.fatal(message, params);
    }
  }

  public void fatal(final String message, final Object param) {
    if (logger.isFatalEnabled()) {
      logger.fatal(message, param);
    }
  }

  public void fatal(final String message, final Throwable throwable) {
    if (logger.isFatalEnabled()) {
      logger.fatal(message, throwable);
    }
  }

  public void fatal(final Marker marker, final String message) {
    if (logger.isFatalEnabled(marker)) {
      logger.fatal(marker, message);
    }
  }

  public void fatal(final Marker marker, final String message, final Object... params) {
    if (logger.isFatalEnabled(marker)) {
      logger.fatal(marker, message, params);
    }
  }

  public void fatal(final Marker marker, final String message, final Throwable throwable) {
    if (logger.isFatalEnabled(marker)) {
      logger.fatal(marker, message, throwable);
    }
  }

  // ========== Level check methods (delegated) ==========

  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  public boolean isFatalEnabled() {
    return logger.isFatalEnabled();
  }

  /**
   * Returns the underlying Logger instance. Use with caution - direct access bypasses guard checks.
   *
   * @return the wrapped Logger instance
   */
  public Logger getLogger() {
    return logger;
  }
}
