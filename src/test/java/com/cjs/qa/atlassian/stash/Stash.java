package com.cjs.qa.atlassian.stash;

import com.cjs.qa.atlassian.Atlassian;

public class Stash {

  private String url = "stash" + Atlassian.getDomain();

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
