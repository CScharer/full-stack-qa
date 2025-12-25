package com.cjs.qa.ym.api.dataobjects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Results")
public class Results {

  // @XmlElement(name = "Results")
  // private Results results;
  @XmlElement(name = "ResultTotal")
  private int resultTotal;

  @XmlElement(name = "Item")
  private List<Item> item = new ArrayList<>();

  public int getResultTotal() {
    return resultTotal;
  }

  public void setResultTotal(int resultTotal) {
    this.resultTotal = resultTotal;
  }

  public List<Item> getItem() {
    return item;
  }

  public void setItem(List<Item> item) {
    this.item = item;
  }
}
