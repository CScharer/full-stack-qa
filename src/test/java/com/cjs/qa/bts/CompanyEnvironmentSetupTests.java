package com.cjs.qa.bts;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.Environment;
import com.cjs.qa.saucelabs.SauceUtils;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.JavaHelpers;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class CompanyEnvironmentSetupTests extends Environment {

  private CompanyEnvironmentData companyEnvironmentData = null;
  private String userName = null;
  private String userPasword = null;
  private String browserName = null;
  private String browserVersion = null;
  private String browserPlatform = null;
  private String hubHost = null;
  private String hubPort = null;
  private String hubUserName = null;
  private String hubUserAccessKey = null;
  private String hubParentTunnel = null;
  private String hubTunnelIdentifier = null;

  @Test
  public void testCompanyEnvironmentData() throws Exception {
    clearSystemData();
    // System.setProperty("userName", "CScharer");
    // System.setProperty("userPasword", "Test");
    // System.setProperty("hubHost", SauceUtils.DEFAULT_HOST);
    System.setProperty("hubHost", "Bamboo");
    setCompanyEnvironmentData(new CompanyEnvironmentData());
  }

  public CompanyEnvironmentData getCompanyEnvironmentData() {
    return companyEnvironmentData;
  }

  public void setCompanyEnvironmentData(CompanyEnvironmentData companyEnvironmentData) {
    this.companyEnvironmentData = companyEnvironmentData;
    setSystemData();
    // User Data
    if (!JavaHelpers.hasValue(getUserPassword())) {
      setUserName("");
      setUserPassword("");
      sysOut(
          "The User Password has not been set so the User Name and User Password have"
              + " been removed.");
    }
    if (JavaHelpers.hasValue(getUserName())) {
      getCompanyEnvironmentData().setUserName(getUserName());
    }
    if (JavaHelpers.hasValue(getUserPassword())) {
      getCompanyEnvironmentData().setUserPassword(getUserPassword());
    }
    // Browser Data
    if (JavaHelpers.hasValue(getBrowserName())) {
      getCompanyEnvironmentData().setBrowserName(getBrowserName());
    } else if (JavaHelpers.hasValue(getCompanyEnvironmentData().getBrowserName())) {
      // Browser name already set - no action needed
      assert true; // No-op statement to satisfy PMD
    } else {
      getCompanyEnvironmentData().setBrowserName(ISelenium.BROWSER_DEFAULT);
      sysOut(
          "The Browser has not been set so it will be set to use the"
              + " ISelenium.BROWSER_DEFAULT.");
    }
    if (JavaHelpers.hasValue(getBrowserVersion())) {
      getCompanyEnvironmentData().setBrowserVersion(getBrowserVersion());
    } else if (JavaHelpers.hasValue(getCompanyEnvironmentData().getBrowserVersion())) {
      // Browser version already set - no action needed
      assert true; // No-op statement to satisfy PMD
    } else if (JavaHelpers.hasValue(getBrowserPlatform())) {
      getCompanyEnvironmentData().setBrowserPlatform(getBrowserPlatform());
    }
    // Hub Data
    if (JavaHelpers.hasValue(getHubHost())) {
      getCompanyEnvironmentData().setHubHost(getHubHost());
    } else if (JavaHelpers.hasValue(getCompanyEnvironmentData().getHubHost())) {
      // Hub host already set - no action needed
      assert true; // No-op statement to satisfy PMD
    } else {
      getCompanyEnvironmentData().setHubHost("");
    }
    switch (getCompanyEnvironmentData().getHubHost()) {
      case "Bamboo":
        getCompanyEnvironmentData().setHubPort("");
        getCompanyEnvironmentData().setHubUserName("");
        getCompanyEnvironmentData().setHubUserAccessKey("");
        getCompanyEnvironmentData().setHubParentTunnel("");
        getCompanyEnvironmentData().setHubTunnelIdentifier("");
        break;
      default:
        if (JavaHelpers.hasValue(getHubHost())) {
          getCompanyEnvironmentData().setHubHost(getHubHost());
        }
        // Set the Access Key automatically for the default HubUserName.
        if (JavaHelpers.hasValue(getHubUserName())) {
          getCompanyEnvironmentData().setHubUserName(getHubUserName());
          if (SauceUtils.DEFAULT_HOST.equalsIgnoreCase(
              getCompanyEnvironmentData().getHubUserName())) {
            getCompanyEnvironmentData().setHubUserAccessKey(SauceUtils.DEFAULT_ACCESS_KEY);
          }
        }
        boolean clearDataHub = false;
        if (JavaHelpers.hasValue(getHubUserAccessKey())) {
          getCompanyEnvironmentData().setHubUserAccessKey(getHubUserAccessKey());
        } else {
          if (JavaHelpers.hasValue(getHubUserName())) {
            getCompanyEnvironmentData().setHubUserName(getHubUserName());
            if (SauceUtils.DEFAULT_HOST.equalsIgnoreCase(
                getCompanyEnvironmentData().getHubUserName())) {
              setHubUserAccessKey(SauceUtils.DEFAULT_ACCESS_KEY);
              setHubParentTunnel(SauceUtils.DEFAULT_PARENT_TUNNEL);
              setHubTunnelIdentifier(SauceUtils.DEFAULT_TUNNEL_IDENTIFIER);
            }
          } else {
            clearDataHub = true;
          }
        }
        if (clearDataHub) {
          setHubPort("");
          setHubUserName("");
          setHubUserAccessKey("");
          setHubParentTunnel("");
          setHubTunnelIdentifier("");
          sysOut(
              "The Hub User Name or Hub User Access Key has not been set so the Hub"
                  + " Data has been removed.");
        }
        if (JavaHelpers.hasValue(getHubPort())) {
          getCompanyEnvironmentData().setHubPort(getHubPort());
        }
        if (JavaHelpers.hasValue(getHubUserName())) {
          getCompanyEnvironmentData().setHubUserName(getHubUserName());
        }
        if (JavaHelpers.hasValue(getHubUserAccessKey())) {
          getCompanyEnvironmentData().setHubUserAccessKey(getHubUserAccessKey());
        }
        if (JavaHelpers.hasValue(getHubParentTunnel())) {
          getCompanyEnvironmentData().setHubParentTunnel(getHubParentTunnel());
        }
        if (JavaHelpers.hasValue(getHubTunnelIdentifier())) {
          getCompanyEnvironmentData().setHubTunnelIdentifier(getHubTunnelIdentifier());
        }
        break;
    }
    sysOut(toString());
  }

  private void clearSystemData() {
    System.setProperty("userName", "");
    System.setProperty("userPassword", "");
    System.setProperty("browserName", "");
    System.setProperty("browserVersion", "");
    System.setProperty("browserPlatform", "");
    System.setProperty("hubHost", "");
    System.setProperty("hubPort", "");
    System.setProperty("hubUserName", "");
    System.setProperty("hubUserAccessKey", "");
    System.setProperty("hubParentTunnel", "");
    System.setProperty("hubTunnelIdentifier", "");
  }

  private void setSystemData() {
    setUserName(System.getProperty("userName"));
    setUserPassword(System.getProperty("userPassword"));
    setBrowserName(System.getProperty("browserName"));
    setBrowserVersion(System.getProperty("browserVersion"));
    setBrowserPlatform(System.getProperty("browserPlatform"));
    setHubHost(System.getProperty("hubHost"));
    setHubPort(System.getProperty("hubPort"));
    setHubUserName(System.getProperty("hubUserName"));
    setHubUserAccessKey(System.getProperty("hubUserAccessKey"));
    setHubParentTunnel(System.getProperty("hubParentTunnel"));
    setHubTunnelIdentifier(System.getProperty("hubTunnelIdentifier"));
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPassword() {
    return userPasword;
  }

  public void setUserPassword(String userPassword) {
    this.userPasword = userPassword;
  }

  public String getBrowserName() {
    return browserName;
  }

  public void setBrowserName(String browserName) {
    this.browserName = browserName;
  }

  public String getBrowserVersion() {
    return browserVersion;
  }

  public void setBrowserVersion(String browserVersion) {
    this.browserVersion = browserVersion;
  }

  public String getBrowserPlatform() {
    return browserPlatform;
  }

  public void setBrowserPlatform(String browserPlatform) {
    this.browserPlatform = browserPlatform;
  }

  public String getHubHost() {
    return hubHost;
  }

  public void setHubHost(String hubHost) {
    this.hubHost = hubHost;
  }

  public String getHubPort() {
    return hubPort;
  }

  public void setHubPort(String hubPort) {
    this.hubPort = hubPort;
  }

  public String getHubUserName() {
    return hubUserName;
  }

  public void setHubUserName(String hubUserName) {
    this.hubUserName = hubUserName;
  }

  public String getHubUserAccessKey() {
    return hubUserAccessKey;
  }

  public void setHubUserAccessKey(String hubUserAccessKey) {
    this.hubUserAccessKey = hubUserAccessKey;
  }

  public String getHubParentTunnel() {
    return hubParentTunnel;
  }

  public void setHubParentTunnel(String hubParentTunnel) {
    this.hubParentTunnel = hubParentTunnel;
  }

  public String getHubTunnelIdentifier() {
    return hubTunnelIdentifier;
  }

  public void setHubTunnelIdentifier(String hubTunnelIdentifier) {
    this.hubTunnelIdentifier = hubTunnelIdentifier;
  }

  @Override
  public String toString() {
    CompanyEnvironmentData cED = getCompanyEnvironmentData();
    String cNL = Constants.NEWLINE;
    StringBuilder stringBuilder = new StringBuilder(cNL);
    // User Data
    stringBuilder.append("User Name:[" + cED.getUserName() + "]" + cNL);
    if (cED.getUserPassword().isEmpty()) {
      stringBuilder.append("User Password:[" + cED.getUserPassword() + "]" + cNL);
    } else {
      stringBuilder.append("User Password:[********]" + cNL);
    }
    // Browser Data
    stringBuilder.append("Browser Name:[" + cED.getBrowserName() + "]" + cNL);
    stringBuilder.append("Browser Version:[" + cED.getBrowserVersion() + "]" + cNL);
    stringBuilder.append("Browser Platform:[" + cED.getBrowserPlatform() + "]" + cNL);
    // Hub Data
    stringBuilder.append("Hub Host:[" + cED.getHubHost() + "]" + cNL);
    stringBuilder.append("Hub Port:[" + cED.getHubPort() + "]" + cNL);
    stringBuilder.append("Hub User Name:[" + cED.getHubUserName() + "]" + cNL);
    if (cED.getHubUserAccessKey().isEmpty()) {
      stringBuilder.append("Hub User Access Key:[" + cED.getHubUserAccessKey() + "]" + cNL);
    } else {
      stringBuilder.append("Hub User Access Key:[********]" + cNL);
    }
    stringBuilder.append("Hub Parent Tunnel:[" + cED.getHubParentTunnel() + "]" + cNL);
    stringBuilder.append("Hub Tunnel Identifier:[" + cED.getHubTunnelIdentifier() + "]");
    return stringBuilder.toString();
  }
}
