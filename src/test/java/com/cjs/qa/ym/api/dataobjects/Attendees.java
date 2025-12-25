package com.cjs.qa.ym.api.dataobjects;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Events.Event.Attendees.Get")
public class Attendees {

  @XmlElement(name = "Attendees")
  private List<Attendees> attendees;

  public List<Attendees> getAttendees() {
    return attendees;
  }

  public void setAttendees(List<Attendees> attendees) {
    this.attendees = attendees;
  }
}
