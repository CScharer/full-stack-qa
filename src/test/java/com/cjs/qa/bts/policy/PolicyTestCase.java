package com.cjs.qa.bts.policy;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.utilities.GuardedLogger;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public final class PolicyTestCase {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(PolicyTestCase.class));

  private PolicyTestCase() {
    // Private constructor
  }

  @Test
  public void mainTest() {
    // main(String[] args)
    final Policy policy = new Policy("123456789-10");
    LOG.debug("{}", policy.getPolicy());
    LOG.debug("{}", policy.getPolicyNumber());
    LOG.debug("{}", policy.getSequenceNumber());
  }
}
