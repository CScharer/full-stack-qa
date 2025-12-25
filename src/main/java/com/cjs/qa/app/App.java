package com.cjs.qa.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class App {

  private App() {
    // Utility class - prevent instantiation
  }

  public static void main(String[] args) {
    Logger logger = LogManager.getRootLogger();
    logger.debug("Hello World!");
  }
}
