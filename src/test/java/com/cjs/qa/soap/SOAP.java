package com.cjs.qa.soap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.XML;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class SOAP {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(SOAP.class));

  private String xml = null;

  public SOAPMessage getSoapMessageFromFile(String filePathName) throws IOException, SOAPException {
    String input = "";
    final StringBuilder stringBuilder = new StringBuilder();
    try (FileReader fileReader = new FileReader(new File(filePathName));
        BufferedReader bufferedReader = new BufferedReader(fileReader)) {
      while ((input = bufferedReader.readLine()) != null) {
        stringBuilder.append(input);
      }
    }
    return getSoapMessageFromString(stringBuilder.toString());
  }

  public SOAPMessage getSoapMessageFromString(String xml) throws IOException, SOAPException {
    final MessageFactory messageFactory = MessageFactory.newInstance();
    return messageFactory.createMessage(
        new MimeHeaders(), new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
  }

  public String getAPIXMLRequest(SOAPMessage soapMessage) throws QAException {
    setXml(getSOAPMessageValue(soapMessage, false));
    return getXml();
  }

  public String getAPIXMLResponse(String url, SOAPMessage soapMessage) throws Throwable {
    getAPIXMLRequest(soapMessage);
    setXml(null);
    writeSOAPMessageToOutputStream(soapMessage);
    SOAPConnectionFactory soapConnectionFactory;
    try {
      soapConnectionFactory = SOAPConnectionFactory.newInstance();
      try (SOAPConnection soapConnection = soapConnectionFactory.createConnection()) {
        final SOAPMessage soapMessageResponse = soapConnection.call(soapMessage, url);
        setXml(getSOAPMessageValue(soapMessageResponse, false));
      }
    } catch (SOAPException | UnsupportedOperationException e) {
      throw new QAException("Error getting Soap Response.", e);
    }
    if (getXml().indexOf("<faultstring>") != -1) {
      final String faultString = XML.getTag(getXml(), "faultstring");
      final String message = "Soap Error:[" + faultString + "]";
      LOG.debug("{}", message);
    }
    return getXml();
  }

  public String getSOAPMessageValue(SOAPMessage soapMessage, boolean maskPassword)
      throws QAException {
    setXml(null);
    final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer();
      final Source source = soapMessage.getSOAPPart().getContent();
      final StringWriter stringWriter = new StringWriter();
      final StreamResult streamResult = new StreamResult(stringWriter);
      transformer.transform(source, streamResult);
      final StringBuffer stringBuffer = stringWriter.getBuffer();
      setXml(stringBuffer.toString());
    } catch (SOAPException | TransformerException e) {
      throw new QAException("Error getting Soap Message Value.", e);
    }
    setXml(xmlReplaceSpecialCharacters(getXml(), true));
    if (maskPassword) {
      String prePW = "<password>";
      String postPW = "</password>";
      if (getXml().contains(prePW)) {
        final String prePassword = getXml().substring(0, getXml().indexOf(prePW) + prePW.length());
        final String postPassword = getXml().substring(getXml().indexOf(postPW), getXml().length());
        setXml(prePassword + "**********" + postPassword);
      }
    }
    setXml(getXml().replaceAll(Constants.NEWLINE + Constants.NEWLINE, Constants.NEWLINE));
    return getXml();
  }

  public String removeCDATATags(String xml, String tag) {
    xml = xml.replace("<![CDATA[<" + tag + ">", "<" + tag + ">" + ")");
    xml = xml.replace("</" + tag + ">]]>", "</" + tag + ">");
    return xml;
  }

  public void writeSOAPMessageToOutputStream(SOAPMessage soapMessage) throws QAException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      soapMessage.writeTo(byteArrayOutputStream);
      @SuppressWarnings("unused")
      final String soapMessageString = byteArrayOutputStream.toString();
      String soapMessageValue = getSOAPMessageValue(soapMessage, true);
      soapMessageValue =
          soapMessageValue.replaceAll(Constants.NEWLINE + Constants.NEWLINE, Constants.NEWLINE);
      soapMessageValue = soapMessageValue.replaceAll(Constants.NL + Constants.NL, Constants.NL);
      soapMessageValue = soapMessageValue.replaceAll(Constants.CR + Constants.CR, Constants.CR);
      LOG.debug("{}{}", Constants.NEWLINE + Constants.TAB, soapMessageValue);
    } catch (IOException | SOAPException e) {
      throw new QAException("Error getting Writing Soap Message to Output Stream.", e);
    }
  }

  public String xmlReplaceSpecialCharacters(String xml, boolean convertToReadable) {
    final List<String> charactersXML =
        Arrays.asList("&lt;", "&gt;", "&apos;", "&quot;", "&amp;", "&#xD;");
    final List<String> charactersHTML = Arrays.asList("<", ">", "'", "\"", "&", "");
    for (int index = 0; index < charactersXML.size(); index++) {
      String replacee = "";
      String replacer = "";
      if (convertToReadable) {
        replacee = charactersXML.get(index);
        replacer = charactersHTML.get(index);
      } else {
        replacee = charactersHTML.get(index);
        replacer = charactersXML.get(index);
      }
      if (xml.contains(replacee)) {
        xml = xml.replaceAll(replacee, replacer);
        if (convertToReadable) {
          final String replaceeSpecial = replacee.replace("&", "&amp;");
          if (xml.contains(replaceeSpecial)) {
            xml = xml.replaceAll(replaceeSpecial, replacee);
          }
        }
      }
    }
    return xml;
  }

  private String getXml() {
    return xml;
  }

  private void setXml(String xml) {
    this.xml = xml;
  }
}
