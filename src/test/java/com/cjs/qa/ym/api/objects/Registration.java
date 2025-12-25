package com.cjs.qa.ym.api.objects;

public class Registration {

  private String status;
  private String eventName;
  private String firstName;
  private String lastName;
  private String badgeNumber;
  private String registrationID;
  private String dateRegister;
  private String attended;
  private String orderID;
  private String memberID;
  private String invoiceID;
  private String isPrimaryRegistrant;
  private String primaryRegistrantRegistrationID;
  private String primaryRegistrantMemberID;
  private String attendeeType;
  private DataSet dataSet;

  public Registration() {
    // Empty
  }

  public Registration(
      String status,
      String eventName,
      String firstName,
      String lastName,
      String badgeNumber,
      String registrationID,
      String dateRegister,
      String attended,
      String orderID,
      String memberID,
      String invoiceID,
      String isPrimaryRegistrant,
      String primaryRegistrantRegistrationID,
      String primaryRegistrantMemberID,
      String attendeeType) {
    this.status = status;
    this.eventName = eventName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.badgeNumber = badgeNumber;
    this.registrationID = registrationID;
    this.dateRegister = dateRegister;
    this.attended = attended;
    this.orderID = orderID;
    this.memberID = memberID;
    this.invoiceID = invoiceID;
    this.isPrimaryRegistrant = isPrimaryRegistrant;
    this.primaryRegistrantRegistrationID = primaryRegistrantRegistrationID;
    this.primaryRegistrantMemberID = primaryRegistrantMemberID;
    this.attendeeType = attendeeType;
  }

  public String getAttended() {
    return attended;
  }

  public String getAttendeeType() {
    return attendeeType;
  }

  public String getBadgeNumber() {
    return badgeNumber;
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  public String getDateRegister() {
    return dateRegister;
  }

  public String getEventName() {
    return eventName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getInvoiceID() {
    return invoiceID;
  }

  public String getIsPrimaryRegistrant() {
    return isPrimaryRegistrant;
  }

  public String getLastName() {
    return lastName;
  }

  public String getMemberID() {
    return memberID;
  }

  public String getOrderID() {
    return orderID;
  }

  public String getPrimaryRegistrantMemberID() {
    return primaryRegistrantMemberID;
  }

  public String getPrimaryRegistrantRegistrationID() {
    return primaryRegistrantRegistrationID;
  }

  public String getRegistrationID() {
    return registrationID;
  }

  public String getStatus() {
    return status;
  }

  public void setAttended(String attended) {
    this.attended = attended;
  }

  public void setAttendeeType(String attendeeType) {
    this.attendeeType = attendeeType;
  }

  public void setBadgeNumber(String badgeNumber) {
    this.badgeNumber = badgeNumber;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  public void setDateRegister(String dateRegister) {
    this.dateRegister = dateRegister;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setInvoiceID(String invoiceID) {
    this.invoiceID = invoiceID;
  }

  public void setIsPrimaryRegistrant(String isPrimaryRegistrant) {
    this.isPrimaryRegistrant = isPrimaryRegistrant;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setMemberID(String memberID) {
    this.memberID = memberID;
  }

  public void setOrderID(String orderID) {
    this.orderID = orderID;
  }

  public void setPrimaryRegistrantMemberID(String primaryRegistrantMemberID) {
    this.primaryRegistrantMemberID = primaryRegistrantMemberID;
  }

  public void setPrimaryRegistrantRegistrationID(String primaryRegistrantRegistrationID) {
    this.primaryRegistrantRegistrationID = primaryRegistrantRegistrationID;
  }

  public void setRegistrationID(String registrationID) {
    this.registrationID = registrationID;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
