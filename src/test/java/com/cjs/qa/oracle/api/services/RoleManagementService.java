package com.cjs.qa.oracle.api.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.api.WebService;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

public class RoleManagementService extends WebService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RoleManagementService.class));
  private final String baseAPIRoleManagement =
      "https://RoleManagementAPI."
          + OracleConstants.API_BASE
          + ".net/"
          + Environment.getEnvironment()
          + "/"
          + OracleConstants.API_ROL_VERSION_MAJOR
          + "/"
          + OracleConstants.API_ROL_VERSION_MINOR;

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param roleId
   * @param personIds
   * @return
   */
  public Map<String, String> assignUser(
      String ssoUserTokenId, String organizationId, String roleId, String personIds) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{PartyIds:[" + personIds + "],}";
    final String requestURL =
        baseAPIRoleManagement + "/Organization/" + organizationId + "/Role/" + roleId + "/User/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param roleName
   * @param permissionIds
   * @return
   */
  public String createRole(
      String ssoUserTokenId, String organizationId, String roleName, String permissionIds) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    String roleId = null;
    final String apiRequest =
        "{Name: "
            + Constants.QUOTE_DOUBLE
            + roleName
            + Constants.QUOTE_DOUBLE
            + ",PermissionIds:["
            + permissionIds
            + "],}";
    final String requestURL = baseAPIRoleManagement + "/Organization/" + organizationId + "/Role/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    roleId =
        json.substring(
            (json.indexOf("body" + Constants.QUOTE_DOUBLE + ":")
                    + "body"
                    + Constants.QUOTE_DOUBLE
                    + ":")
                .length());
    roleId = roleId.substring(0, roleId.indexOf(","));
    LOG.info("Role ID:[{}]", roleId);
    Assert.assertNotNull("Role ID:[" + roleId + "]", roleId);
    return roleId;
  }

  /**
   * @param ssoUserTokenId
   * @param eMail
   * @param oAddresses
   * @param oContactPhones
   * @param oPhones
   * @param oCompanyInfo
   * @return
   */
  public Map<String, String> createOrg(
      String ssoUserTokenId,
      String eMail,
      Map<String, String> oAddresses,
      Map<String, String> oContactPhones,
      Map<String, String> oPhones,
      Map<String, String> oCompanyInfo) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String name = eMail.substring(0, eMail.indexOf(CJSConstants.MAILDOMAIN_GMAIL) - 1);
    String organizationId = null;
    final String organizationName = OracleDynamicVariables.getORGName(name);
    oCompanyInfo.put("CompanyName", organizationName);
    oCompanyInfo.put("ContactEmail", eMail);
    oCompanyInfo.put("ContactFirstName", OracleDynamicVariables.getFirstName(name));
    oCompanyInfo.put("ContactLastName", OracleDynamicVariables.getLastName(name));
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut(
          "RoleManagement: " + JavaHelpers.getCurrentMethodName().toString(), oCompanyInfo);
    }
    final String apiRequest =
        "{Address1: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Address1")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Address2: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oAddresses.get("Address2")
            + Constants.QUOTE_DOUBLE
            + ","
            + "City: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("City")
            + Constants.QUOTE_DOUBLE
            + ","
            + "ContactEmail: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("ContactEmail").toString()
            + Constants.QUOTE_DOUBLE
            + ","
            + "ContactFirstName: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("ContactFirstName").toString()
            + Constants.QUOTE_DOUBLE
            + ","
            + "ContactLastName: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("ContactLastName").toString()
            + Constants.QUOTE_DOUBLE
            + ","
            + "CountryCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Country")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Name:"
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("CompanyName").toString()
            + Constants.QUOTE_DOUBLE
            + ","
            + "Phone: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oPhones.get("PhoneNumber")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PostalCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("PostalCode")
            + Constants.QUOTE_DOUBLE
            + ","
            + "StateCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("State")
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL = baseAPIRoleManagement + "/Organization";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    organizationId =
        json.substring(
            (json.indexOf("body" + Constants.QUOTE_DOUBLE + ":")
                    + "body"
                    + Constants.QUOTE_DOUBLE
                    + ":")
                .length());
    organizationId = organizationId.substring(0, organizationId.indexOf(","));
    map.put("organizationId", organizationId);
    LOG.debug("oAddresses:{}", oAddresses);
    LOG.debug("oContactPhones:{}", oContactPhones);
    LOG.debug("oPhones:{}", oPhones);
    LOG.debug("oCompanyInfo:{}", oCompanyInfo);
    LOG.info("Organization ID:[{}]", organizationId);
    Assert.assertNotNull("Organization ID:[" + organizationId + "]", organizationId);
    // return organizationId;
    return map;
  }

  public String getCreateLicenseExpirationDate(int yearsToIncrement) {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy");
    final Date date = new Date();
    // return String.valueOf(Integer.valueOf(dateFormat.format(date)) +
    // yearsToIncrement) + "-01-01";
    return "01/01/" + String.valueOf(Integer.valueOf(dateFormat.format(date)) + yearsToIncrement);
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param expirationDate
   * @param solution
   * @return
   */
  public String createLicense(
      String ssoUserTokenId, String organizationId, String expirationDate, String solution) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{ExpirationDate: "
            + Constants.QUOTE_DOUBLE
            + expirationDate
            + Constants.QUOTE_DOUBLE
            + ",Solution: "
            + Constants.QUOTE_DOUBLE
            + solution
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL =
        baseAPIRoleManagement + "/Organization/" + organizationId + "/License/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    return json;
  }
}
