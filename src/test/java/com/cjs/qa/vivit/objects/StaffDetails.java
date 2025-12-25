package com.cjs.qa.vivit.objects;

public class StaffDetails {

  private String url;
  private String webSiteMemberID;
  private String nameTitle;
  private String eMail;
  private String phone;
  private String profile;

  public StaffDetails(String url) {
    this.url = url;
    this.webSiteMemberID = getUrl().substring(getUrl().indexOf("=") + 1);
  }

  public String geteMail() {
    return eMail;
  }

  public String getNameTitle() {
    return nameTitle;
  }

  public String getPhone() {
    return phone;
  }

  public String getProfile() {
    return profile;
  }

  public String getUrl() {
    return url;
  }

  public String getWebSiteMemberID() {
    return webSiteMemberID;
  }

  public void seteMail(String eMail) {
    this.eMail = eMail;
  }

  public void setNameTitle(String nameTitle) {
    this.nameTitle = nameTitle;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setWebSiteMemberID(String webSiteMemberID) {
    this.webSiteMemberID = webSiteMemberID;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("URL: [" + getUrl() + "], ");
    stringBuilder.append("Web Site Member ID: [" + getWebSiteMemberID() + "], ");
    stringBuilder.append("Name Title: [" + getNameTitle() + "], ");
    stringBuilder.append("eMail: [" + geteMail() + "], ");
    stringBuilder.append("Phone: [" + getPhone() + "], ");
    stringBuilder.append("Profile: [" + getProfile() + "]");
    return stringBuilder.toString();
  }
}
