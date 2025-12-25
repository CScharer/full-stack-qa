package com.cjs.qa.ym.api.dataobjects;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Sa.Events.Event.Registrations.GetIDs")
public class Registrations {

  @XmlElement(name = "RegistrationID")
  private List<String> registrationID;

  public List<String> getRegistrationID() {
    return registrationID;
  }

  public void setRegistrationID(List<String> registrationID) {
    this.registrationID = registrationID;
  }
}
