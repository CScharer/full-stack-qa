package com.cjs.qa.ym.api.dataobjects;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Attendee")
public class Attendee {

  @XmlElement(name = "RegisterID")
  private String registerID;

  @XmlElement(name = "RsvpID")
  private String rsvpID;

  @XmlElement(name = "DateRegister")
  private String dateRegister;

  @XmlElement(name = "LastName")
  private String lastName;

  @XmlElement(name = "FirstName")
  private String firstName;

  @XmlElement(name = "WebsiteID")
  private String websiteID;

  @XmlElement(name = "ID")
  private String id;

  @XmlElement(name = "RsvpResponse")
  private String rsvpResponse;

  @XmlElement(name = "DataSet")
  private List<DataSet> dataSet;

  public String getRegisterID() {
    return registerID;
  }

  public void setRegisterID(String registerID) {
    this.registerID = registerID;
  }

  public String getRsvpID() {
    return rsvpID;
  }

  public void setRsvpID(String rsvpID) {
    this.rsvpID = rsvpID;
  }

  public String getDateRegister() {
    return dateRegister;
  }

  public void setDateRegister(String dateRegister) {
    this.dateRegister = dateRegister;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getWebsiteID() {
    return websiteID;
  }

  public void setWebsiteID(String websiteID) {
    this.websiteID = websiteID;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRsvpResponse() {
    return rsvpResponse;
  }

  public void setRsvpResponse(String rsvpResponse) {
    this.rsvpResponse = rsvpResponse;
  }

  public List<DataSet> getDataSet() {
    return dataSet;
  }

  public void setDataSet(List<DataSet> dataSet) {
    this.dataSet = dataSet;
  }
}
