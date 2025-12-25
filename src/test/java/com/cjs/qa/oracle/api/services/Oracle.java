package com.cjs.qa.oracle.api.services;

import com.cjs.qa.core.api.WebService;

public class Oracle extends WebService {

  private String browser;
  private WebService apiPage;
  private OracleDynamicVariables oracleDynamicVariables;

  public Oracle() {
    // API = new API(Environment, driver);
    oracleDynamicVariables = new OracleDynamicVariables();
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public WebService getApiPage() {
    return apiPage;
  }

  public void setApiPage(WebService apiPage) {
    this.apiPage = apiPage;
  }

  public OracleDynamicVariables getOracleDynamicVariables() {
    return oracleDynamicVariables;
  }

  public void setOracleDynamicVariables(OracleDynamicVariables oracleDynamicVariables) {
    this.oracleDynamicVariables = oracleDynamicVariables;
  }
}
