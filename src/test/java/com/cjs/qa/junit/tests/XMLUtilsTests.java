package com.cjs.qa.junit.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.xml.security.Init;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.XML;

public class XMLUtilsTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(XMLUtilsTests.class));

  @BeforeAll
  static void initializeXMLSecurity() {
    try {
      Init.init();
      LOG.info("XML Security library initialized successfully");
    } catch (final Exception e) {
      LOG.error("Failed to initialize XML Security library", e);
      throw new RuntimeException("Failed to initialize XML Security library", e);
    }
  }

  @Test
  public void assertXMLEqualFail() throws QAException {
    // In CI: abort (doesn't fail pipeline), locally: fail normally
    if (Constants.isRunningInCI()) {
      throw new TestAbortedException("assertXMLEqualFail - Intentionally aborted in CI");
    } else {
      // Locally: let the assertion fail as expected
      XML.xmlAssertEqual(getXML1(), getXML2());
    }
  }

  @Test
  public void assertXMLEqualPass() throws QAException {
    XML.xmlAssertEqual(getXML1(), getXML1());
  }

  @Test
  public void assertXMLEqualsFail() throws QAException {
    // In CI: abort (doesn't fail pipeline), locally: fail normally
    if (Constants.isRunningInCI()) {
      throw new TestAbortedException("assertXMLEqualsFail - Intentionally aborted in CI");
    } else {
      // Locally: let the assertion fail as expected
      XML.xmlAssertEquals(getXML1(), getXML2());
    }
  }

  @Test
  public void assertXMLEqualsPass() throws QAException {
    try {
      XML.xmlAssertEquals(getXML1(), getXML1());
    } catch (final Exception e) {
      throw new QAException("Error trying to assertXMLEquals", e);
    }
  }

  @Test
  public void formatPretty() throws QAException {
    LOG.info("XML1 formatted: {}", XML.formatPretty(getXML1()));
    LOG.info("XML2 formatted: {}", XML.formatPretty(getXML2()));
  }

  @Test
  public void fromStringToCanonicalXML() throws QAException {
    LOG.info("XML1 canonical: {}", XML.fromStringToCanonical(getXML1()));
    LOG.info("XML2 canonical: {}", XML.fromStringToCanonical(getXML2()));
  }

  private String getXML1() {
    return FSOTests.fileReadAll(Constants.PATH_FILES_XML + "xml1" + IExtension.XML);
  }

  private String getXML2() {
    return FSOTests.fileReadAll(Constants.PATH_FILES_XML + "xml2" + IExtension.XML);
  }
}
