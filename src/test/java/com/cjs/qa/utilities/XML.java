package com.cjs.qa.utilities;

// import static org.apache.xml.security.Init.init;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;

public class XML {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(XML.class));
  public static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
  public static final XPath XPATH = XPATH_FACTORY.newXPath();
  public static final String ENCLOSURE_DELIMETER = "/";
  public static final String ENCLOSURE_LEFT = "<";
  public static final String ENCLOSURE_RIGHT = ">";
  public static final String VERSION = "1.0";
  // "UTF-8";
  public static final String ENCODING = System.getProperty("file.encoding");
  public static final String HEADING_INFO =
      ENCLOSURE_LEFT
          + "?xml version="
          + Constants.QUOTE_DOUBLE
          + VERSION
          + Constants.QUOTE_DOUBLE
          + " encoding="
          + Constants.QUOTE_DOUBLE
          + ENCODING
          + Constants.QUOTE_DOUBLE
          + "?"
          + ENCLOSURE_RIGHT;
  public static final String XPATH_ROOT_NODE = "";
  private static int nodeElementCount = 0;

  // static
  // {
  // init();
  // }
  //
  @SuppressWarnings("unused")
  public XML(String xml) {
    // Parameter reserved for future implementation
    LOG.debug("XPathConstants.NODESET: {}", XPathConstants.NODESET.toString());
    final String xpath = "";
    try {
      XPATH.compile(xpath);
    } catch (final Exception e) {
      LOG.error("Error in XML operation", e);
    }
  }

  /**
   * @param xml
   * @return
   * @throws Exception
   */
  public static Document createDocument(File xml) throws Exception {
    Document document = null;
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    // Disable external entity expansion to prevent XXE attacks
    documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    documentBuilderFactory.setFeature(
        "http://xml.org/sax/features/external-general-entities", false);
    documentBuilderFactory.setFeature(
        "http://xml.org/sax/features/external-parameter-entities", false);
    documentBuilderFactory.setFeature(
        "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    documentBuilderFactory.setExpandEntityReferences(false);
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    document = documentBuilder.parse(xml);
    // read this -
    // http://stackoverflow" + IExtension.COM +
    // "/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
    document.getDocumentElement().normalize();
    return document;
  }

  /**
   * @param xml
   * @return
   * @throws Exception
   */
  public static Document createDocument(String xml) throws Exception {
    Document document = null;
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    // Disable external entity expansion to prevent XXE attacks
    documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    documentBuilderFactory.setFeature(
        "http://xml.org/sax/features/external-general-entities", false);
    documentBuilderFactory.setFeature(
        "http://xml.org/sax/features/external-parameter-entities", false);
    documentBuilderFactory.setFeature(
        "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    documentBuilderFactory.setExpandEntityReferences(false);
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    try (StringReader stringReader = new StringReader(xml)) {
      document = documentBuilder.parse(new InputSource(stringReader));
    }
    document.getDocumentElement().normalize();
    return document;
  }

  /**
   * @param xml
   * @return
   */
  public String encloseXML(String nodeRoot, String xml) {
    final String heading = HEADING_INFO;
    return Environment.noPMDUselessParentheses(
        heading
            + ENCLOSURE_LEFT
            + nodeRoot
            + ENCLOSURE_RIGHT
            + xml
            + ENCLOSURE_LEFT
            + nodeRoot
            + ENCLOSURE_DELIMETER
            + ENCLOSURE_RIGHT);
  }

  /**
   * @param file
   * @param tagName
   * @param elementTagName
   * @return
   * @throws Exception
   */
  public static String getNode(File file, String tagName, String elementTagName) throws Exception {
    String value = null;
    final Document document = createDocument(file);
    final NodeList nodeList = document.getElementsByTagName(tagName);
    // sysOut("----------------------------");
    for (int index = 0; index < nodeList.getLength(); index++) {
      final Node node = nodeList.item(index);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        value = element.getElementsByTagName(elementTagName).item(0).getTextContent();
      }
    }
    return value;
  }

  /**
   * @param xml
   * @param tagName
   * @param elementTagName
   * @return
   * @throws Exception
   */
  public static String getNode(String xml, String tagName, String elementTagName) throws Exception {
    String value = null;
    final Document document = createDocument(xml);
    final NodeList nodeList = document.getElementsByTagName(tagName);
    // sysOut("----------------------------");
    for (int index = 0; index < nodeList.getLength(); index++) {
      final Node node = nodeList.item(index);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        value = element.getElementsByTagName(elementTagName).item(0).getTextContent();
      }
    }
    return value;
  }

  /**
   * This uses an XPath expression with an XPathFactory to compile the results of the xPath. This is
   * very useful when traversing the DOM.
   *
   * @param xml
   * @param xPath
   * @return
   * @throws Exception
   */
  public static NodeList getNodeList(String xml, String xPath) throws Exception {
    final Document document = createDocument(xml);
    final XPathExpression xpathExpression = XPATH.compile(xPath);
    return (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
  }

  /**
   * @param xml
   * @param tagName
   * @param elementTagName
   * @return
   * @throws Exception
   */
  public static List<String> getNodes(String xml, String tagName, String elementTagName)
      throws Exception {
    final List<String> list = new ArrayList<>();
    String value = null;
    final Document document = createDocument(xml);
    final NodeList nodeList = document.getElementsByTagName(tagName);
    // sysOut("----------------------------");
    for (int index = 0; index < nodeList.getLength(); index++) {
      final Node node = nodeList.item(index);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        final int elementCount = element.getElementsByTagName(elementTagName).getLength();
        LOG.debug("elementCount: [{}]", elementCount);
        for (int indexElement = 0; indexElement < elementCount; indexElement++) {
          value = element.getElementsByTagName(elementTagName).item(indexElement).getTextContent();
          list.add(value);
        }
      }
    }
    return list;
  }

  /**
   * @param xml
   * @param tagName
   * @param mapEvent
   * @return
   * @throws Exception
   */
  public static Map<String, String> getNodes(
      String xml, String tagName, Map<String, String> mapEvent) throws Exception {
    String value = null;
    final Document document = createDocument(xml);
    final NodeList nodeList = document.getElementsByTagName(tagName);
    for (int index = 0; index < nodeList.getLength(); index++) {
      final Node node = nodeList.item(index);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        for (final String key : mapEvent.keySet()) {
          final String elementTagName = key;
          value = element.getElementsByTagName(elementTagName).item(0).getTextContent();
          mapEvent.put(key, value);
        }
      }
    }
    return mapEvent;
  }

  /**
   * @param node
   * @param rootDescendant
   * @param descendants
   * @return
   */
  public static String getNodeDescendantNames(
      Node node, String rootDescendant, String descendants) {
    if (descendants == null) {
      descendants = "";
    }
    String parent = "";
    do {
      final Node nodeParent = node.getParentNode();
      if (nodeParent == null) {
        break;
      }
      parent = nodeParent.getNodeName();
      if (parent.equals(rootDescendant)) {
        break;
      }
      if (descendants.isEmpty()) {
        descendants += getNodeDescendantNames(nodeParent, rootDescendant, parent);
        return descendants;
      } else {
        descendants += "~" + getNodeDescendantNames(nodeParent, rootDescendant, parent);
        return descendants;
      }
    } while (!"InsurancePolicyXML".equals(parent));
    return descendants;
  }

  /**
   * @param node
   * @param nodeName
   */
  public static void getSiblingNodes(Node node, String nodeName) {
    LOG.debug("nodeName: [{}]", nodeName);
    Node nodeSibling = null;
    do {
      nodeSibling = node.getNextSibling();
      nodeName = node.getNodeName();
      LOG.debug("nodeName: [{}]", nodeName);
      node = nodeSibling;
    } while (nodeSibling != null);
  }

  public static StringBuilder getWrappedField(
      StringBuilder stringBuilder, String fieldName, String fieldValue) {
    stringBuilder.append(ENCLOSURE_LEFT + fieldName + ENCLOSURE_RIGHT);
    // stringBuilder.append(fieldValue);
    stringBuilder.append(HTML.convertStringToHTML(fieldValue));
    stringBuilder.append(ENCLOSURE_LEFT + ENCLOSURE_DELIMETER + fieldName + ENCLOSURE_RIGHT);
    return stringBuilder;
  }

  /**
   * @param node
   * @param nodeName
   */
  public static void traverseNodes(Node node, String nodeName) {
    String nodeNameT = "";
    // if (nodeName.isEmpty())
    // {
    nodeNameT = node.getNodeName();
    // } else
    // {
    // nodeNameT = nodeName + ":" + node.getNodeName();
    // }
    LOG.debug("nodeNameT:[" + nodeNameT + "]");
    final NodeList nodeList = node.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      final Node currentNode = nodeList.item(i);
      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
        nodeElementCount++;
        final Element element = (Element) currentNode;
        // final String elementName = element.getNodeName();
        // String elementValue = element.getNodeValue();
        // elementValue = element.getTextContent();
        // LOG.debug(nodeName + ":" + elementName + "=" +
        // elementValue);
        // LOG.debug("nodeNameT:[{}], elementName:[{}], elementValue:[{}]", nodeNameT, elementName,
        // elementValue);
        // getSiblingNodes(element, nodeNameT);
        traverseNodes(element, nodeNameT);
      }
    }
  }

  /**
   * @param xml
   * @param rootTagName
   */
  public static void parseNodes(String xml, String rootTagName) {
    try {
      final Document document = createDocument(xml);
      final NodeList nodeList = document.getElementsByTagName(rootTagName);
      for (int index = 0; index < nodeList.getLength(); index++) {
        final Node node = nodeList.item(index);
        traverseNodes(node, "");
      }
      LOG.debug("nodeElementCount: [{}]", nodeElementCount);
    } catch (final Exception e) {
      LOG.error("Error in XML operation", e);
    }
  }

  /**
   * @param xml
   * @param tagName
   * @return
   * @throws Exception
   */
  public static String getTag(String xml, String tagName) throws Exception {
    String tagValue = null;
    final Document document = createDocument(xml);
    final NodeList nodeList = document.getElementsByTagName(tagName);
    final Node node = nodeList.item(0);
    final Element element = (Element) node;
    tagValue = element.getTextContent();
    // final String elementName = element.getNodeName();
    // LOG.debug("elementName:[{}], tagValue:[{}]", elementName, tagValue);
    return tagValue;
  }

  /**
   * @param xml - The xml string.
   * @return - Formatted xml string without tabs.
   */
  public static String formatNotPretty(String xml) {
    return xml.replaceAll("><", ">" + Constants.NEWLINE + "<");
  }

  /**
   * @param xml - The xml string.
   * @return - Formatted xml string with tabs.
   * @throws QAException
   */
  public static String formatPretty(String xml) throws QAException {
    if (xml.contains("&")) {
      xml = xml.replaceAll("&&", "&");
      xml = xml.replaceAll("&amp;", "&");
      xml = xml.replaceAll("&", "&amp;");
    }
    Element element;
    // Added to resolve Cursor breakage.
    InputSource inputSource = new InputSource("");
    try (StringReader stringReader = new StringReader(xml)) {
      inputSource = new InputSource(stringReader);
      final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      // Disable external entity expansion to prevent XXE attacks
      documentBuilderFactory.setFeature(
          "http://apache.org/xml/features/disallow-doctype-decl", true);
      documentBuilderFactory.setFeature(
          "http://xml.org/sax/features/external-general-entities", false);
      documentBuilderFactory.setFeature(
          "http://xml.org/sax/features/external-parameter-entities", false);
      documentBuilderFactory.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      documentBuilderFactory.setExpandEntityReferences(false);
      element = documentBuilderFactory.newDocumentBuilder().parse(inputSource).getDocumentElement();
    } catch (SAXException | IOException | ParserConfigurationException e) {
      throw new QAException(
          "DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(" + inputSource + ")",
          e);
    }
    final Boolean keepDeclaration = xml.startsWith("<?xml");
    DOMImplementationRegistry domImplementationRegistry;
    try {
      domImplementationRegistry = DOMImplementationRegistry.newInstance();
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | ClassCastException e) {
      throw new QAException("DOMImplementationRegistry.newInstance();", e);
    }
    final DOMImplementationLS domImplementationLS =
        (DOMImplementationLS) domImplementationRegistry.getDOMImplementation("LS");
    final LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
    lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
    lsSerializer.getDomConfig().setParameter("xml-declaration", keepDeclaration);
    xml = lsSerializer.writeToString(element);
    xml = xml.replace("UTF-16", ENCODING);
    return xml;
  }

  /**
   * @param xml
   * @return
   * @throws QAException
   */
  public static String fromStringToCanonical(String xml) throws QAException {
    Canonicalizer canonicalizer;
    try {
      canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
    } catch (final InvalidCanonicalizerException e) {
      throw new QAException(
          "Canonicalizer.getInstance(" + Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS + ");", e);
    }
    try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
      canonicalizer.canonicalize(xml.getBytes(), baos, false);
      return new String(baos.toByteArray());
    } catch (CanonicalizationException
        | IOException
        | org.apache.xml.security.parser.XMLParserException e) {
      throw new QAException("canonicalizer.canonicalize(xml.getBytes(), baos, false);", e);
    }
  }

  /**
   * @param filePathXSL
   * @param filePathInput
   * @param filePathOutput
   */
  public static void transformFromFile(
      String filePathXSL, String filePathInput, String filePathOutput) {
    LOG.info("Creating [{}] From [{}] Using [{}]", filePathOutput, filePathInput, filePathXSL);
    final StreamSource streamSourceXSL = new StreamSource(new File(filePathXSL));
    final StreamSource streamSourceIn = new StreamSource(new File(filePathInput));
    final StreamResult streamResultOut = new StreamResult(new File(filePathOutput));
    final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer(streamSourceXSL);
      try {
        transformer.transform(streamSourceIn, streamResultOut);
        LOG.info("Created [{}] From [{}] Using [{}]", filePathOutput, filePathInput, filePathXSL);
      } catch (final TransformerException e) {
        e.printStackTrace();
      }
    } catch (final TransformerConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param xsl
   * @param input
   * @param filePathOutput
   */
  public static void transformFromString(String xsl, String input, String filePathOutput) {
    LOG.info("Creating [{}] using string inputs", filePathOutput);
    try (StringReader xslReader = new StringReader(xsl);
        StringReader inputReader = new StringReader(input)) {
      final StreamSource streamSourceXSL = new StreamSource(xslReader);
      final StreamSource streamSourceIn = new StreamSource(inputReader);
      final StreamResult streamResultOut = new StreamResult(new File(filePathOutput));
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer;
      try {
        transformer = transformerFactory.newTransformer(streamSourceXSL);
        try {
          transformer.transform(streamSourceIn, streamResultOut);
          LOG.info("Created [{}] using string inputs", filePathOutput);
        } catch (final TransformerException e) {
          e.printStackTrace();
        }
      } catch (final TransformerConfigurationException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @param xmlExpected
   * @param xmlActual
   * @throws QAException
   */
  public static void xmlAssertEqual(String xmlExpected, String xmlActual) throws QAException {
    final String canonicalExpected = formatPretty(fromStringToCanonical(xmlExpected));
    final String canonicalActual = formatPretty(fromStringToCanonical(xmlActual));
    assertEquals(canonicalExpected, canonicalActual);
  }

  /**
   * @param expectedXML
   * @param actualXML
   * @throws QAException
   */
  public static void xmlAssertEquals(String expectedXML, String actualXML) throws QAException {
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    DetailedDiff detailedDiff;
    try {
      detailedDiff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));
    } catch (SAXException | IOException e) {
      throw new QAException(
          "new DetailedDiff(XMLUnit.compareXML(" + expectedXML + ", " + actualXML + "));", e);
    }
    final List<?> detailedDiffList = detailedDiff.getAllDifferences();
    for (final Object detailedDifference : detailedDiffList) {
      LOG.debug("XML Difference: {}", detailedDifference.toString());
    }
    // Assert.assertEquals("Differences found: " + detailedDiff.toString(),
    // 0, detailedDiffList.size());
    assertEquals("Differences found: " + detailedDiff.toString(), 0, detailedDiffList.size());
  }
}
