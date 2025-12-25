package com.cjs.qa.ym.api.dataobjects;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Sa.Events.Event.Registration.Get")
public class Registration {

  @XmlElement(name = "Status")
  private String status;

  @XmlElement(name = "EventName")
  private String eventName;

  @XmlElement(name = "FirstName")
  private String firstName;

  @XmlElement(name = "LastName")
  private String lastName;

  @XmlElement(name = "BadgeNumber")
  private String badgeNumber;

  @XmlElement(name = "RegistrationID")
  private String registrationID;

  @XmlElement(name = "DateRegistered")
  private String dateRegistered;

  @XmlElement(name = "Attended")
  private String attended;

  @XmlElement(name = "OrderID")
  private String orderID;

  @XmlElement(name = "MemberID")
  private String memberID;

  @XmlElement(name = "InvoiceID")
  private String invoiceID;

  @XmlElement(name = "IsPrimaryRegistrant")
  private String isPrimaryRegistrant;

  @XmlElement(name = "PrimaryRegistrantRegistrationID")
  private String primaryRegistrantRegistrationID;

  @XmlElement(name = "PrimaryRegistrantMemberID")
  private String primaryRegistrantMemberID;

  @XmlElement(name = "AttendeeType")
  private String attendeeType;

  @XmlElement(name = "DataSet")
  private List<DataSet> dataSet;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getBadgeNumber() {
    return badgeNumber;
  }

  public void setBadgeNumber(String badgeNumber) {
    this.badgeNumber = badgeNumber;
  }

  public String getRegistrationID() {
    return registrationID;
  }

  public void setRegistrationID(String registrationID) {
    this.registrationID = registrationID;
  }

  public String getDateRegistered() {
    return dateRegistered;
  }

  public void setDateRegistered(String dateRegistered) {
    this.dateRegistered = dateRegistered;
  }

  public String getAttended() {
    return attended;
  }

  public void setAttended(String attended) {
    this.attended = attended;
  }

  public String getOrderID() {
    return orderID;
  }

  public void setOrderID(String orderID) {
    this.orderID = orderID;
  }

  public String getMemberID() {
    return memberID;
  }

  public void setMemberID(String memberID) {
    this.memberID = memberID;
  }

  public String getInvoiceID() {
    return invoiceID;
  }

  public void setInvoiceID(String invoiceID) {
    this.invoiceID = invoiceID;
  }

  public String getIsPrimaryRegistrant() {
    return isPrimaryRegistrant;
  }

  public void setIsPrimaryRegistrant(String isPrimaryRegistrant) {
    this.isPrimaryRegistrant = isPrimaryRegistrant;
  }

  public String getPrimaryRegistrantRegistrationID() {
    return primaryRegistrantRegistrationID;
  }

  public void setPrimaryRegistrantRegistrationID(String primaryRegistrantRegistrationID) {
    this.primaryRegistrantRegistrationID = primaryRegistrantRegistrationID;
  }

  public String getPrimaryRegistrantMemberID() {
    return primaryRegistrantMemberID;
  }

  public void setPrimaryRegistrantMemberID(String primaryRegistrantMemberID) {
    this.primaryRegistrantMemberID = primaryRegistrantMemberID;
  }

  public String getAttendeeType() {
    return attendeeType;
  }

  public void setAttendeeType(String attendeeType) {
    this.attendeeType = attendeeType;
  }

  public List<DataSet> getDataSet() {
    return dataSet;
  }

  public void setDataSet(List<DataSet> dataSet) {
    this.dataSet = dataSet;
  }
}
