package com.cjs.qa.utilities;

import org.apache.logging.log4j.LogManager;

@SuppressWarnings("PMD.DoNotExtendJavaLangThrowable") // Custom logging exception design
public class QALogger extends Throwable {

  private static final long serialVersionUID = 1L;
  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(QALogger.class));
  private static final GuardedLogger LOGGER = new GuardedLogger(LogManager.getRootLogger());

  public QALogger(Exception e) {
    LOG.debug(e);
    LOGGER.debug(e);
  }

  public QALogger(String message, Exception e) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(message + Constants.NEWLINE + e);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(message + Constants.NEWLINE + e);
    }
  }

  public QALogger(String message) {
    LOG.debug(message);
    LOGGER.debug(message);
  }
}
