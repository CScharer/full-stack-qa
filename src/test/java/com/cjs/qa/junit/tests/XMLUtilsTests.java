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
    final String xml1 = getXML1();
    if (xml1 == null) {
      throw new QAException("Failed to read xml1.xml - file not found or empty");
    }
    XML.xmlAssertEqual(xml1, xml1);
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
      final String xml1 = getXML1();
      if (xml1 == null) {
        throw new QAException("Failed to read xml1.xml - file not found or empty");
      }
      XML.xmlAssertEquals(xml1, xml1);
    } catch (final Exception e) {
      throw new QAException("Error trying to assertXMLEquals", e);
    }
  }

  @Test
  public void formatPretty() throws QAException {
    final String xml1 = getXML1();
    final String xml2 = getXML2();
    if (xml1 == null) {
      throw new QAException("Failed to read xml1.xml - file not found or empty");
    }
    if (xml2 == null) {
      throw new QAException("Failed to read xml2.xml - file not found or empty");
    }
    LOG.info("XML1 formatted: {}", XML.formatPretty(xml1));
    LOG.info("XML2 formatted: {}", XML.formatPretty(xml2));
  }

  @Test
  public void fromStringToCanonicalXML() throws QAException {
    final String xml1 = getXML1();
    final String xml2 = getXML2();
    if (xml1 == null) {
      throw new QAException("Failed to read xml1.xml - file not found or empty");
    }
    if (xml2 == null) {
      throw new QAException("Failed to read xml2.xml - file not found or empty");
    }
    LOG.info("XML1 canonical: {}", XML.fromStringToCanonical(xml1));
    LOG.info("XML2 canonical: {}", XML.fromStringToCanonical(xml2));
  }

  private String getXML1() {
    return FSOTests.fileReadAll(Constants.PATH_FILES_XML + "xml1" + IExtension.XML);
  }

  private String getXML2() {
    return FSOTests.fileReadAll(Constants.PATH_FILES_XML + "xml2" + IExtension.XML);
  }
}
