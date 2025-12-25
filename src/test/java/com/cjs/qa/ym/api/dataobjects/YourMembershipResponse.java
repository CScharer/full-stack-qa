package com.cjs.qa.ym.api.dataobjects;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "YourMembershipResponse")
public class YourMembershipResponse {

  @XmlElement(name = "ErrCode")
  private String errCode;

  @XmlElement(name = "ExtendedErrorInfo")
  private String extendedErrorInfo;

  @XmlElement(name = "Results")
  private Results results;

  @XmlAnyElement private Object otherStuff;

  public String getErrCode() {
    return errCode;
  }

  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }

  public String getExtendedErrorInfo() {
    return extendedErrorInfo;
  }

  public void setExtendedErrorInfo(String extendedErrorInfo) {
    this.extendedErrorInfo = extendedErrorInfo;
  }

  public Results getResults() {
    return results;
  }

  public void setResults(Results results) {
    this.results = results;
  }

  public Object getOtherStuff() {
    return otherStuff;
  }

  public void setOtherStuff(Object otherStuff) {
    this.otherStuff = otherStuff;
  }
}
