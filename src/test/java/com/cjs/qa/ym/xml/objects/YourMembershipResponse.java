package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "YourMembershipResponse")
public class YourMembershipResponse {

  @XmlElement(name = "ErrCode")
  private EventsAllSearch errCode;

  public EventsAllSearch getErrCode() {
    return errCode;
  }

  @XmlElement(name = "extendedErrorInfo")
  private EventsAllSearch extendedErrorInfo;

  public EventsAllSearch getExtendedErrorInfo() {
    return extendedErrorInfo;
  }

  @XmlElement(name = "Events.All.Search")
  private EventsAllSearch eventsAllSearch;

  public EventsAllSearch getEventsAllSearch() {
    return eventsAllSearch;
  }

  @XmlElement(name = "Events.Event.Get")
  private EventsEventGet eventsEventGet;

  public EventsEventGet getEventsEventGet() {
    return eventsEventGet;
  }

  @XmlElement(name = "Sa.Events.Event.Registration.Get")
  private SaEventsEventRegistrationGet saEventsEventRegistrationGet;

  public SaEventsEventRegistrationGet getSaEventsEventRegistrationGet() {
    return saEventsEventRegistrationGet;
  }
}
