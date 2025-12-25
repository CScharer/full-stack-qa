package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Events.All.Search")
public class EventsAllSearch {

  @XmlElement(name = "Results")
  private Results results;

  public Results getResults() {
    return results;
  }
}
