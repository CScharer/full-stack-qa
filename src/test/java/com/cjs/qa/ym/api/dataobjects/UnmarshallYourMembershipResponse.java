package com.cjs.qa.ym.api.dataobjects;

import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Node;

import com.cjs.qa.core.QAException;
import com.cjs.qa.soap.SOAP;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class UnmarshallYourMembershipResponse {

  public YourMembershipResponse getFromFile(String fileName) throws QAException {
    final SOAP soap = new SOAP();
    SOAPMessage soapMessage;
    try {
      soapMessage = soap.getSoapMessageFromFile(fileName);
    } catch (Exception e) {
      throw new QAException("Error Getting XML From File.", e);
    }
    return get(soapMessage);
  }

  public YourMembershipResponse getFromString(String xml) throws QAException {
    final SOAP soap = new SOAP();
    SOAPMessage soapMessage;
    try {
      soapMessage = soap.getSoapMessageFromString(xml);
    } catch (Exception e) {
      throw new QAException("Error Getting XML From String.", e);
    }
    return get(soapMessage);
  }

  private YourMembershipResponse get(SOAPMessage soapMessage) throws QAException {
    YourMembershipResponse yourMembershipResponse = null;
    try {
      // Iterator pattern is optimal for SOAP XML parsing
      for (final Iterator<jakarta.xml.soap.Node> bodyIterator =
              soapMessage.getSOAPBody().getChildElements();
          bodyIterator.hasNext(); ) {

        final SOAPElement soapBody = (SOAPElement) bodyIterator.next();
        for (final Iterator<jakarta.xml.soap.Node> dataIterator = soapBody.getChildElements();
            dataIterator.hasNext(); ) {

          final SOAPElement dataElement = (SOAPElement) dataIterator.next();
          final Node node = dataElement.getFirstChild();
          JAXBContext context;
          try {
            context = JAXBContext.newInstance(YourMembershipResponse.class);
          } catch (final JAXBException e) {
            throw new QAException(
                "Error Creating (context ="
                    + " JAXBContext.newInstance(YourMembershipResponse.class);).",
                e);
          }
          Unmarshaller unmarshaller;
          try {
            unmarshaller = context.createUnmarshaller();
          } catch (final JAXBException e) {
            throw new QAException(
                "Error Creating (unmarshaller = context.createUnmarshaller();).", e);
          }
          try {
            yourMembershipResponse = (YourMembershipResponse) unmarshaller.unmarshal(node);
          } catch (final JAXBException e) {
            throw new QAException(
                "Error Creating (yourMembershipResponse ="
                    + " (YourMembershipResponse) unmarshaller.unmarshal(node);).",
                e);
          }
        }
      }
    } catch (final SOAPException e) {
      throw new QAException(
          "Error In (for (final Iterator<SOAPElement> bodyIterator ="
              + " soapMessage.getSOAPBody().getChildElements();"
              + " bodyIterator.hasNext();)).",
          e);
    }
    return yourMembershipResponse;
  }
}
