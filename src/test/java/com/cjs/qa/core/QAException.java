package com.cjs.qa.core;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

// PMD: DoNotExtendJavaLangThrowable is intentionally suppressed
// This is a custom exception design that extends Throwable for specific QA framework requirements
@SuppressWarnings("PMD.DoNotExtendJavaLangThrowable")
public class QAException extends Throwable {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(QAException.class));

  public static final String ERROR = "***ERROR***:";
  private static final long serialVersionUID = 1L;
  private static final AtomicReference<String> QA_ERROR_MESSAGE = new AtomicReference<>();

  public QAException(Throwable throwable) {
    throwable(throwable);
  }

  public QAException(String message) {
    QA_ERROR_MESSAGE.set(message); // Intentional static state for last error message
    LOG.error("{}:{}", ERROR + JavaHelpers.getCallingMethodName(), message);
  }

  public QAException(String message, Throwable throwable) {
    LOG.error("{}:{}", ERROR + JavaHelpers.getCallingMethodName(), message);
    QA_ERROR_MESSAGE.set(message); // Intentional static state for last error message
    throwable(throwable);
  }

  public void throwable(Throwable throwable) {
    throwable.printStackTrace();
  }

  public static String getQaErrorMessage() {
    return QA_ERROR_MESSAGE.get();
  }

  public void setQaErrorMessage(String qaErrorMessage) {
    QA_ERROR_MESSAGE.set(qaErrorMessage);
  }
}
