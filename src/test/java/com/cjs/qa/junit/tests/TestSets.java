package com.cjs.qa.junit.tests;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class TestSets {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(TestSets.class));

  @Test
  public void emptyTest() {
    LOG.debug(
        "getCurrentPackageClassMethodDebugName: [{}]",
        JavaHelpers.getCurrentPackageClassMethodDebugName());
    LOG.debug(
        "getCurrentPackageClassMethodName: [{}]", JavaHelpers.getCurrentPackageClassMethodName());
    LOG.debug("getCurrentPackageClassName: [{}]", JavaHelpers.getCurrentPackageClassName());
    LOG.debug("getCurrentPackageName: [{}]", JavaHelpers.getCurrentPackageName());
    LOG.debug("getCurrentClassMethodDebugName: [{}]", JavaHelpers.getCurrentClassMethodDebugName());
    LOG.debug("getCurrentClassMethodName: [{}]", JavaHelpers.getCurrentClassMethodName());
    LOG.debug("getCurrentClassName: [{}]", JavaHelpers.getCurrentClassName());
    LOG.debug("getCurrentMethodDebugName: [{}]", JavaHelpers.getCurrentMethodDebugName());
    LOG.debug("getCurrentMethodName: [{}]", JavaHelpers.getCurrentMethodName());
  }
}
