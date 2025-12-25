package com.cjs.qa.gt.api.services;

import com.cjs.qa.gt.api.namespace.webinar.AuthNamespace;
import com.cjs.qa.gt.api.namespace.webinar.RegistrantsNamespace;
import com.cjs.qa.gt.api.namespace.webinar.WebinarsNamespace;

public class GTWebinarAPI {

  private AuthNamespace authNamespace;
  private WebinarsNamespace webinarsNamespace;
  private RegistrantsNamespace registrantsNamespace;

  public GTWebinarAPI() throws Throwable {
    authNamespace = new AuthNamespace();
    webinarsNamespace = new WebinarsNamespace();
    registrantsNamespace = new RegistrantsNamespace();
  }

  public AuthNamespace getAuthNamespace() {
    return authNamespace;
  }

  public WebinarsNamespace getWebinarsNamespace() {
    return webinarsNamespace;
  }

  public RegistrantsNamespace getRegistrantsNamespace() {
    return registrantsNamespace;
  }
}
