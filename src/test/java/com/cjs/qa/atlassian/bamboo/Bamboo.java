package com.cjs.qa.atlassian.bamboo;

import com.cjs.qa.atlassian.Atlassian;

public class Bamboo {

  private String url = "bamboo" + Atlassian.getDomain();

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
