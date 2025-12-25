package com.cjs.qa.ym.xml.objects;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Results")
public class Results {

  @XmlElement(name = "Item")
  private List<Item> item;

  public List<Item> getItem() {
    return item;
  }
}
