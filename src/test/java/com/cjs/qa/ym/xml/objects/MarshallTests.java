package com.cjs.qa.ym.xml.objects;

import java.io.File;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.XML;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class MarshallTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(MarshallTests.class));
  private static final String FILE_PATH_NAME_XML =
      "C:\\Workspace\\Data\\Vivit\\Data\\Events\\temp.xml";
  private static final String FILE_PATH_NAME_NEW_XML =
      "C:\\Workspace\\Data\\Vivit\\Data\\Events\\tempNew.xml";

  @Test
  public void testAll() throws JAXBException {
    testEventsAllSearch();
    testEventsEventsGet();
    testSaEventsEventRegistrationGet();
  }

  @Test
  public void testEventsAllSearch() throws JAXBException {
    String filePathName = "C:\\Workspace\\Data\\Vivit\\Data\\Events\\101.xml";
    YourMembershipResponse yourMembershipResponse = yourMembershipResponseUnmarshall(filePathName);
    LOG.debug("{}", yourMembershipResponse.toString());
    LOG.debug("{}", yourMembershipResponse.getEventsAllSearch().toString());
    LOG.debug("{}", yourMembershipResponse.getEventsAllSearch().getResults().toString());
    for (int eventsIndex = 0;
        eventsIndex < yourMembershipResponse.getEventsAllSearch().getResults().getItem().size();
        eventsIndex++) {
      Item item =
          yourMembershipResponse.getEventsAllSearch().getResults().getItem().get(eventsIndex);
      LOG.debug("Item Index ({})", eventsIndex);
      LOG.debug("{}", item.toString());
    }
    yourMembershipResponseMarshall(yourMembershipResponse, FILE_PATH_NAME_NEW_XML);
  }

  @Test
  public void testEventsEventsGet() throws JAXBException {
    String filePathName = "C:\\Workspace\\Data\\Vivit\\Data\\Events\\EventInformation\\1209865.xml";
    YourMembershipResponse yourMembershipResponse = yourMembershipResponseUnmarshall(filePathName);
    LOG.debug("{}", yourMembershipResponse.toString());
    LOG.debug("{}", yourMembershipResponse.getEventsEventGet().toString());
    yourMembershipResponseMarshall(yourMembershipResponse, FILE_PATH_NAME_NEW_XML);
  }

  @Test
  public void testSaEventsEventRegistrationGet() throws JAXBException {
    String filePathName =
        "C:\\Workspace\\Data\\Vivit\\Data\\Events\\Registration\\49170B25-A49F-46F7-98CB-3DA7042D4BB1.xml";
    YourMembershipResponse yourMembershipResponse = yourMembershipResponseUnmarshall(filePathName);
    LOG.debug("{}", yourMembershipResponse.getSaEventsEventRegistrationGet().toString());
    String xml = yourMembershipResponseMarshall(yourMembershipResponse, FILE_PATH_NAME_NEW_XML);
    LOG.debug("TESTING XML CREATION[{}]", Constants.NEWLINE + xml);
    LOG.info(
        "Getting the Phone Number:"
            + yourMembershipResponse
                .getSaEventsEventRegistrationGet()
                .getDataSet()
                .getPhone()
                .getValue());
  }

  public YourMembershipResponse yourMembershipResponseUnmarshall(String filePathName)
      throws JAXBException {
    createFixedXMLFile(filePathName);
    JAXBContext jaxbContext = JAXBContext.newInstance(YourMembershipResponse.class);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    File file = new File(FILE_PATH_NAME_XML);
    return (YourMembershipResponse) unmarshaller.unmarshal(file);
  }

  public String yourMembershipResponseMarshall(
      YourMembershipResponse yourMembershipResponse, String filePathName) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(YourMembershipResponse.class);
    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    String xml = null;
    if (filePathName != null) {
      marshaller.marshal(yourMembershipResponse, new File(filePathName));
    }
    //        xml = FSOTests.fileReadAll(filePathName);
    //        marshaller.marshal(yourMembershipResponse, System.out);
    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(yourMembershipResponse, stringWriter);
    xml = stringWriter.toString();
    return xml;
  }

  private void createFixedXMLFile(String filePathName) {
    String fixedXML = FSOTests.fileReadAll(filePathName);
    fixedXML = fixedXML.replace("Cp1252", XML.ENCODING);
    fixedXML = fixedXML.replace(XML.HEADING_INFO, XML.HEADING_INFO + Constants.NEWLINE);
    FSOTests.fileWrite(FILE_PATH_NAME_XML, fixedXML, false);
  }
}
