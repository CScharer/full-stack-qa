package com.cjs.qa.atlassian.crowd;

import com.cjs.qa.atlassian.Atlassian;

public class Crowd {

  private String url = "crowd" + Atlassian.getDomain();

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
