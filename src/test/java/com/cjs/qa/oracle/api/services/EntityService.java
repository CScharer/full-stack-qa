package com.cjs.qa.oracle.api.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.api.WebService;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.Encoder;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class EntityService extends WebService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(EntityService.class));

  private final String baseAPIEntity =
      "https://EntityAPI."
          + OracleConstants.API_BASE
          + ".net/"
          + Environment.getEnvironment()
          + "/"
          + OracleConstants.API_ORG_VERSION_MAJOR
          + "/"
          + OracleConstants.API_ORG_VERSION_MINOR;
  private static final Encoder ENCODER = new Encoder("");

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param personId
   * @return
   */
  public Map<String, String> addMember(
      String ssoUserTokenId, String organizationId, String personId) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{PersonID: " + personId + "}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Member/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param orgTypes
   * @return
   */
  public Map<String, String> addOrgType(
      String ssoUserTokenId, String organizationId, String orgTypes) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{OrgTypes:[" + orgTypes + "],}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/OrgType/";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param countyID
   * @return
   */
  public Map<String, String> addWorkArea(
      String ssoUserTokenId, String organizationId, String countyID) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{WorkAreas:[{CountyId: " + countyID + ",}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/WorkArea/";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param workTypes
   * @return
   */
  public Map<String, String> addWorkType(
      String ssoUserTokenId, String organizationId, String workTypes) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{WorkTypes: [" + workTypes + "]}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/WorkType/";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param numberAddressesToAdd
   * @return
   */
  public Map<String, String> apiCreateAddressDynamic(
      String ssoUserTokenId, String organizationId, int numberAddressesToAdd) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    for (int i = 0; i < numberAddressesToAdd; i++) {
      final Map<String, String> oAddresses = getOrgAddressesDynamic();
      final Map<String, String> oContactPhones = getContactDynamic();
      createAddressNotPrimary(ssoUserTokenId, organizationId, oAddresses, oContactPhones);
    }
    return map;
  }

  public Map<String, String> apiCreateProjectDynamic(
      String ssoUserTokenId, String organizationId, int numberOfProject) {
    final Map<String, String> map = new HashMap<>();
    for (int i = 0; i < numberOfProject; i++) {
      final Map<String, String> oProject = getOrgProjectDynamic();
      map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
      final String apiRequest =
          "{Name: "
              + Constants.QUOTE_DOUBLE
              + oProject.get("Name")
              + Constants.QUOTE_DOUBLE
              + ","
              + "ContractValue: "
              + oProject.get("ContractValue")
              + ","
              + "DateCompleted: "
              + Constants.QUOTE_DOUBLE
              + oProject.get("DateCompleted")
              + Constants.QUOTE_DOUBLE
              + ","
              + "Description: "
              + Constants.QUOTE_DOUBLE
              + oProject.get("Description")
              + Constants.QUOTE_DOUBLE
              + ","
              + "ImageShortkeys: ["
              + Constants.QUOTE_DOUBLE
              + ""
              + oProject.get("Imageshortkey")
              + Constants.QUOTE_DOUBLE
              + "],"
              + "LeedRating: "
              + oProject.get("LEEDRating")
              + ","
              + "Location: "
              + Constants.QUOTE_DOUBLE
              + oProject.get("Location")
              + Constants.QUOTE_DOUBLE
              + ","
              + "Owner: "
              + Constants.QUOTE_DOUBLE
              + oProject.get("Owner")
              + Constants.QUOTE_DOUBLE
              + ","
              + "WorkTypes: ["
              + oProject.get("ProjectTypes")
              + "],}";
      final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Project/";
      final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
      map.put("JSON", json);
      Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    }
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param oAddresses
   * @param oContactPhones
   * @return
   */
  public Map<String, String> createAddress(
      String ssoUserTokenId,
      String organizationId,
      Map<String, String> oAddresses,
      Map<String, String> oContactPhones) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
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
            + "AddressName: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("AddressName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "City: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("City")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PartyId: "
            + organizationId
            + ","
            + "Country: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oAddresses.get("Country")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PostalCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("PostalCode")
            + Constants.QUOTE_DOUBLE
            + ","
            + "State: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("State")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + oAddresses.get("IsPrimary")
            + ","
            + "Imageshortkey: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Imageshortkey")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Latitude: "
            + oAddresses.get("Latitude")
            + ","
            + "Longitude: "
            + oAddresses.get("Longitude")
            + ","
            + "Types:"
            + oAddresses.get("Types")
            + ","
            + "IsPrimary: "
            + oContactPhones.get("IsPrimary")
            + ","
            + "Phones:["
            + "{PhoneNumber: "
            + Constants.QUOTE_DOUBLE
            + oContactPhones.get("PhoneNumber")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PhoneType: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContactPhones.get("PhoneType")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + oContactPhones.get("IsPrimary")
            + ",}"
            + ",],},";
    final String requestURL = baseAPIEntity + "/Address/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  public Map<String, String> createAddressNotPrimary(
      String ssoUserTokenId,
      String organizationId,
      Map<String, String> oAddresses,
      Map<String, String> oContactPhones) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
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
            + "AddressName: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("AddressName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "City: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("City")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PartyId: "
            + organizationId
            + ","
            + "Country: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oAddresses.get("Country")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PostalCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("PostalCode")
            + Constants.QUOTE_DOUBLE
            + ","
            + "State: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("State")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + false
            + ","
            + "Imageshortkey: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oAddresses.get("Imageshortkey")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Latitude: "
            + oAddresses.get("Latitude")
            + ","
            + "Longitude: "
            + oAddresses.get("Longitude")
            + ","
            + "Types:"
            + oAddresses.get("Types")
            + ","
            + "IsPrimary: "
            + false
            + ","
            + "Phones:["
            + "{PhoneNumber: "
            + Constants.QUOTE_DOUBLE
            + oContactPhones.get("PhoneNumber")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PhoneType: "
            + Constants.QUOTE_DOUBLE
            + oContactPhones.get("PhoneType")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + oContactPhones.get("IsPrimary")
            + ",}"
            + ",],},";
    final String requestURL = baseAPIEntity + "/Address/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param agency
   * @param awardedDate
   * @param name
   * @return
   */
  public Map<String, String> createAward(
      String ssoUserTokenId,
      String organizationId,
      String agency,
      String awardedDate,
      String name) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Agency: "
            + Constants.QUOTE_DOUBLE
            + agency
            + Constants.QUOTE_DOUBLE
            + ","
            + "AwardedDate: "
            + Constants.QUOTE_DOUBLE
            + awardedDate
            + Constants.QUOTE_DOUBLE
            + ","
            + "Name: "
            + Constants.QUOTE_DOUBLE
            + ""
            + name
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Award/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param name
   * @param status
   * @param certificateNumber
   * @param expirationDate
   * @param issuingAgency
   * @return
   */
  public Map<String, String> createCertification(
      String ssoUserTokenId,
      String organizationId,
      String name,
      String status,
      String certificateNumber,
      String expirationDate,
      String issuingAgency) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Name: "
            + Constants.QUOTE_DOUBLE
            + name
            + Constants.QUOTE_DOUBLE
            + ","
            + "Status: "
            + Constants.QUOTE_DOUBLE
            + status
            + Constants.QUOTE_DOUBLE
            + ","
            + "CertificateNumber: "
            + Constants.QUOTE_DOUBLE
            + ""
            + certificateNumber
            + Constants.QUOTE_DOUBLE
            + ","
            + "ExpirationDate: "
            + Constants.QUOTE_DOUBLE
            + expirationDate
            + Constants.QUOTE_DOUBLE
            + ","
            + "IssuingAgency: "
            + Constants.QUOTE_DOUBLE
            + ""
            + issuingAgency
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Certification/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @return
   */
  public Map<String, String> createContact(
      String ssoUserTokenId, String organizationId, Map<String, String> oContact) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Addresses:[{ Address1: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("Address1")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Address2: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContact.get("Address2")
            + Constants.QUOTE_DOUBLE
            + ","
            + "AddressName: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("AddressName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "City: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("City")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Country: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("Country")
            + Constants.QUOTE_DOUBLE
            + ","
            + "isPrimary: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("IsPrimary")
            + Constants.QUOTE_DOUBLE
            + ","
            + "State: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("State")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Latitude: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("Latitude")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Longitude: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("Longitude")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PostalCode: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("PostalCode")
            + Constants.QUOTE_DOUBLE
            + ","
            + "ImageShortKey: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContact.get("Imageshortkey")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Types: "
            + oContact.get("Types")
            + ",},],"
            + "Email: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContact.get("eMail")
            + Constants.QUOTE_DOUBLE
            + ","
            + "FirstName: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("FirstName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "LastName: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContact.get("LastName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Phones: "
            + "["
            + "{"
            + "PhoneNumber: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContact.get("PhoneNumber")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PhoneType: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("PhoneType")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("IsPrimary")
            + Constants.QUOTE_DOUBLE
            + ","
            + "},],"
            + "Title: "
            + Constants.QUOTE_DOUBLE
            + oContact.get("Title")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Types: [1,6],},";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Contact/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param licenseName
   * @param licenseExpiration
   * @param licenseIssuer
   * @param licenseNumber
   * @return
   */
  public Map<String, String> createLicense(
      String ssoUserTokenId,
      String organizationId,
      String licenseName,
      String licenseExpiration,
      String licenseIssuer,
      String licenseNumber) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{LicenseName: "
            + Constants.QUOTE_DOUBLE
            + licenseName
            + Constants.QUOTE_DOUBLE
            + ","
            + "LicenseExpiration: "
            + Constants.QUOTE_DOUBLE
            + licenseExpiration
            + Constants.QUOTE_DOUBLE
            + ","
            + "LicenseIssuer: "
            + Constants.QUOTE_DOUBLE
            + licenseIssuer
            + Constants.QUOTE_DOUBLE
            + ","
            + "LicenseNumber: "
            + Constants.QUOTE_DOUBLE
            + licenseNumber
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/License/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param membershipName
   * @return
   */
  public Map<String, String> createMembership(
      String ssoUserTokenId, String organizationId, String membershipName) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{MembershipName: "
            + Constants.QUOTE_DOUBLE
            + membershipName
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Membership/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
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
  public String createOrg(
      String ssoUserTokenId,
      String eMail,
      Map<String, String> oAddresses,
      Map<String, String> oContactPhones,
      Map<String, String> oPhones,
      Map<String, String> oCompanyInfo) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String name = eMail.substring(0, eMail.indexOf(CJSConstants.MAILDOMAIN_GMAIL) - 1);
    final String linkedInURL = ENCODER.getDecodedValue(name, "");
    String organizationId = null;
    final String organizationName = OracleDynamicVariables.getORGName(name);
    oCompanyInfo.put("CompanyName", organizationName);
    oCompanyInfo.put("ContactEmail", eMail);
    oCompanyInfo.put("ContactFirstName", OracleDynamicVariables.getFirstName(name));
    oCompanyInfo.put("ContactLastName", OracleDynamicVariables.getLastName(name));
    final String apiRequest =
        "{Addresses:["
            + "{Address1: "
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
            + "AddressName: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("AddressName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "City: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("City")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Country: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Country")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + "true"
            + ","
            + "Latitude: "
            + oAddresses.get("Latitude")
            + ","
            + "Longitude: "
            + oAddresses.get("Longitude")
            + ","
            + "PostalCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("PostalCode")
            + Constants.QUOTE_DOUBLE
            + ","
            + "State: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("State")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Imageshortkey: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Imageshortkey")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Types:"
            + oAddresses.get("Types")
            + ",}"
            + ",],"
            + "ContactPhones:["
            + "{PhoneNumber: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oPhones.get("PhoneNumber")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PhoneType: "
            + Constants.QUOTE_DOUBLE
            + oPhones.get("PhoneType")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + oPhones.get("IsPrimary")
            + ",}"
            + ",],"
            + "Phones:["
            + "{PhoneNumber: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oContactPhones.get("PhoneNumber")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PhoneType: "
            + Constants.QUOTE_DOUBLE
            + oContactPhones.get("PhoneType")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + oContactPhones.get("IsPrimary")
            + ",}"
            + ",],"
            + "CompanyName:"
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("CompanyName").toString()
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
            + "ContactTitle: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("ContactTitle").toString()
            + Constants.QUOTE_DOUBLE
            + ","
            + "TaxId: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("TaxId").toString()
            + Constants.QUOTE_DOUBLE
            + ","
            + "YearFounded: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("YearFounded")
            + Constants.QUOTE_DOUBLE
            + ","
            + "FacebookURL: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("FacebookURL")
            + Constants.QUOTE_DOUBLE
            + ","
            + "LinkedInURL: "
            + Constants.QUOTE_DOUBLE
            + "https://www.linkedin"
            + IExtension.COM
            + "/profile/view?id="
            + linkedInURL
            + Constants.QUOTE_DOUBLE
            + ","
            + "NumberOfEmployeesID: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("NumberOfEmployeesID")
            + Constants.QUOTE_DOUBLE
            + ","
            + "NumberOfOffices: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("NumberOfOffices")
            + Constants.QUOTE_DOUBLE
            + ","
            + "ProfileText: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("ProfileText")
            + Constants.QUOTE_DOUBLE
            + ","
            + "TwitterHandle: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("TwitterHandle")
            + Constants.QUOTE_DOUBLE
            + ","
            + "AnnualVolume: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oCompanyInfo.get("AnnualVolume")
            + Constants.QUOTE_DOUBLE
            + ","
            + "AvgContractAmt: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("AvgContractAmt")
            + Constants.QUOTE_DOUBLE
            + ","
            + "BondingAggregateContractLimit: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("BondingAggregateContractLimit")
            + Constants.QUOTE_DOUBLE
            + ","
            + "BondingSingleContractLimit: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("BondingSingleContractLimit")
            + Constants.QUOTE_DOUBLE
            + ","
            + "CurrentEmr: "
            + oCompanyInfo.get("CurrentEmr")
            + ","
            + "DoingBusinessAs:"
            + oCompanyInfo.get("DoingBusinessAs")
            + ","
            + "LeedLevel: "
            + oCompanyInfo.get("LeedLevel")
            + ","
            + "TradeCodes:"
            + oCompanyInfo.get("TradeCodes")
            + ","
            + "MainEmail: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("MainEmail")
            + Constants.QUOTE_DOUBLE
            + ","
            + "WebsiteURL: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("WebsiteURL")
            + Constants.QUOTE_DOUBLE
            + ","
            + "HasBIMExperience: "
            + oCompanyInfo.get("HasBIMExperience")
            + ",}";
    final String requestURL = baseAPIEntity + "/Organization";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    organizationId =
        json.substring(
            (json.indexOf("body" + Constants.QUOTE_DOUBLE + ":")
                    + "body"
                    + Constants.QUOTE_DOUBLE
                    + ":")
                .length());
    organizationId = organizationId.substring(0, organizationId.indexOf(","));
    LOG.debug("oAddresses:" + oAddresses);
    LOG.debug("oContactPhones:" + oContactPhones);
    LOG.debug("oPhones:" + oPhones);
    LOG.debug("oCompanyInfo:" + oCompanyInfo);
    LOG.debug("Organization ID:[" + organizationId + "]");
    Assertions.assertNotNull("Organization ID:[" + organizationId + "]", organizationId);
    return organizationId;
  }

  public String createOrgBarebones(
      String ssoUserTokenId,
      String eMail,
      Map<String, String> oCompanyInfo,
      Map<String, String> oAddresses) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String name = eMail.substring(0, eMail.indexOf(CJSConstants.MAILDOMAIN_GMAIL) - 1);
    String organizationId = null;
    final String organizationName = OracleDynamicVariables.getORGName(name);
    oCompanyInfo.put("CompanyName", organizationName);
    oAddresses.put("IsPrimary", "true");
    // String apiRequest = "{CompanyName: " + Constants.QUOTE_DOUBLE +
    // oCompanyInfo.get("CompanyName").toString() + Constants.QUOTE_DOUBLE +
    // "}";
    final String apiRequest =
        "{"
            + "Addresses: ["
            + "{"
            + "Address1: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Address1")
            + Constants.QUOTE_DOUBLE
            + ","
            + "City: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("City")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Country: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Country")
            + Constants.QUOTE_DOUBLE
            + ","
            + "PostalCode: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("PostalCode")
            + Constants.QUOTE_DOUBLE
            + ","
            + "State: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("State")
            + Constants.QUOTE_DOUBLE
            + ","
            + "Types: "
            + "["
            + "1"
            + "],"
            + "Address2: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("Address2")
            + Constants.QUOTE_DOUBLE
            + ","
            + "AddressName: "
            + Constants.QUOTE_DOUBLE
            + oAddresses.get("AddressName")
            + Constants.QUOTE_DOUBLE
            + ","
            + "ImageShortkey: "
            + Constants.QUOTE_DOUBLE
            + ""
            + oAddresses.get("Imageshortkey")
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + oAddresses.get("IsPrimary")
            + ","
            + "Latitude: "
            + oAddresses.get("Latitude")
            + ","
            + "Longitude: "
            + oAddresses.get("Longitude")
            + ","
            + "},"
            + "],"
            + "CompanyName: "
            + Constants.QUOTE_DOUBLE
            + oCompanyInfo.get("CompanyName").toString()
            + Constants.QUOTE_DOUBLE
            + "},";
    final String requestURL = baseAPIEntity + "/Organization";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    organizationId =
        json.substring(
            (json.indexOf("body" + Constants.QUOTE_DOUBLE + ":")
                    + "body"
                    + Constants.QUOTE_DOUBLE
                    + ":")
                .length());
    organizationId = organizationId.substring(0, organizationId.indexOf(","));
    LOG.debug("Organization ID:[" + organizationId + "]");
    Assertions.assertNotNull("Organization ID:[" + organizationId + "]", organizationId);
    return organizationId;
  }

  /**
   * @param ssoUserTokenId
   * @param sPartyId
   * @param phoneNumber
   * @param phoneType
   * @param isPrimary
   * @param sExtension
   * @return
   */
  public Map<String, String> createPhone(
      String ssoUserTokenId,
      String sPartyId,
      String phoneNumber,
      String phoneType,
      String isPrimary,
      String sExtension) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{PartyId: "
            + sPartyId
            + ","
            + "PhoneNumber: "
            + Constants.QUOTE_DOUBLE
            + phoneNumber
            + Constants.QUOTE_DOUBLE
            + ","
            + "PhoneType: "
            + Constants.QUOTE_DOUBLE
            + phoneType
            + Constants.QUOTE_DOUBLE
            + ","
            + "IsPrimary: "
            + isPrimary
            + ","
            + "Extension: "
            + Constants.QUOTE_DOUBLE
            + sExtension
            + Constants.QUOTE_DOUBLE
            + ",}";
    final String requestURL = baseAPIEntity + "/Phone/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param name
   * @param sContractValue
   * @param sDateCompleted
   * @param sDescription
   * @param sImageShortkeys
   * @param sLeedRating
   * @param sLocation
   * @param sOwner
   * @param workTypes
   * @return
   */
  public Map<String, String> createProject(
      String ssoUserTokenId,
      String organizationId,
      String name,
      String sContractValue,
      String sDateCompleted,
      String sDescription,
      String sImageShortkeys,
      String sLeedRating,
      String sLocation,
      String sOwner,
      String workTypes) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Name: "
            + Constants.QUOTE_DOUBLE
            + name
            + Constants.QUOTE_DOUBLE
            + ","
            + "ContractValue: "
            + sContractValue
            + ","
            + "DateCompleted: "
            + Constants.QUOTE_DOUBLE
            + sDateCompleted
            + Constants.QUOTE_DOUBLE
            + ","
            + "Description: "
            + Constants.QUOTE_DOUBLE
            + sDescription
            + Constants.QUOTE_DOUBLE
            + ","
            + "ImageShortkeys: ["
            + Constants.QUOTE_DOUBLE
            + sImageShortkeys
            + Constants.QUOTE_DOUBLE
            + "],"
            + "LeedRating: "
            + sLeedRating
            + ","
            + "Location: "
            + Constants.QUOTE_DOUBLE
            + ""
            + sLocation
            + Constants.QUOTE_DOUBLE
            + ","
            + "Owner: "
            + Constants.QUOTE_DOUBLE
            + sOwner
            + Constants.QUOTE_DOUBLE
            + ","
            + "WorkTypes: ["
            + workTypes
            + "],}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Project/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param sOrganizationToId
   * @param sRelationshipTypeId
   * @return
   */
  public Map<String, String> createRelationship(
      String ssoUserTokenId,
      String organizationId,
      String sOrganizationToId,
      String sRelationshipTypeId) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{OrganizationToId: "
            + sOrganizationToId
            + ","
            + "RelationshipTypeId: "
            + sRelationshipTypeId
            + "}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/Relationship/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationId
   * @param sShopTypes
   * @return
   */
  public Map<String, String> createShopType(
      String ssoUserTokenId, String organizationId, String sShopTypes) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{ShopTypes:[" + sShopTypes + "],}";
    final String requestURL = baseAPIEntity + "/Organization/" + organizationId + "/ShopType/";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param address1
   * @param address2
   * @param addressName
   * @param city
   * @param country
   * @param isPrimary
   * @param latitude
   * @param longitude
   * @param postalCode
   * @param state
   * @param imageShortKey
   * @param types
   * @param eMail
   * @param firstName
   * @param lastName
   * @param phoneNumber
   * @param phoneType
   * @param title
   * @return
   */
  public Map<String, String> getContact(
      String address1,
      String address2,
      String addressName,
      String city,
      String country,
      String isPrimary,
      String latitude,
      String longitude,
      String postalCode,
      String state,
      String imageShortKey,
      String types,
      String eMail,
      String firstName,
      String lastName,
      String phoneNumber,
      String phoneExtension,
      String phoneType,
      String title) {
    final Map<String, String> oContact = new HashMap<>();
    oContact.put("Address1", address1);
    oContact.put("Address2", address2);
    oContact.put("AddressName", addressName);
    oContact.put("City", city);
    oContact.put("Country", country);
    oContact.put("IsPrimary", isPrimary);
    oContact.put("Latitude", latitude);
    oContact.put("Longitude", longitude);
    oContact.put("PostalCode", postalCode);
    oContact.put("State", state);
    oContact.put("Imageshortkey", imageShortKey);
    oContact.put("Types", types);
    oContact.put("eMail", eMail);
    oContact.put("FirstName", firstName);
    oContact.put("LastName", lastName);
    oContact.put("PhoneNumber", phoneNumber);
    oContact.put("Extension", phoneExtension);
    oContact.put("PhoneType", phoneType);
    oContact.put("Title", title);
    return oContact;
  }

  public Map<String, String> getContactDynamic() {
    Map<String, String> map = new HashMap<>();
    final String name = ENCODER.getEncodedValue("");
    final String address1 = name + " SUITE 1";
    final String address2 = name + " AVE 2";
    final String addressName = OracleDynamicVariables.getCONName(name);
    final String city = "WEST DES MOINES";
    final String country = "USA";
    final String isPrimary = "true";
    final String state = "IA";
    final String latitude = "41.575011";
    final String longitude = "93.709212";
    final String postalCode = "50266";
    // ⚠️ REVIEW NEEDED: Hardcoded value "imageshortkey" - Is this a sensitive key/token?
    // This is not a password, bu a placeholder for a real password.
    final String imageShortKey = "imageshortkey";
    final String types = "[1]";
    final String eMail = name + CJSConstants.MAILDOMAIN_GMAIL;
    final String firstName = OracleDynamicVariables.getFirstName(name);
    final String lastName = OracleDynamicVariables.getLastName(name);
    final String phoneNumber = getRandomPhoneNumber();
    final String phoneExtension = "55555";
    final String phoneType = "Cell";
    final String title = "TITLE QAT" + name;
    map =
        getContact(
            address1,
            address2,
            addressName,
            city,
            country,
            isPrimary,
            latitude,
            longitude,
            postalCode,
            state,
            imageShortKey,
            types,
            eMail,
            firstName,
            lastName,
            phoneNumber,
            phoneExtension,
            phoneType,
            title);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getContactDynamic", map);
    }
    return map;
  }

  public String getCurrentYearMinusYears(int years) {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy");
    final Date date = new Date();
    return String.valueOf(Integer.valueOf(dateFormat.format(date)) - years);
  }

  /**
   * @param address1
   * @param address2
   * @param addressName
   * @param city
   * @param country
   * @param isPrimary
   * @param latitude
   * @param longitude
   * @param postalCode
   * @param state
   * @param imageShortKey
   * @return
   */
  public Map<String, String> getOrgAddresses(
      String address1,
      String address2,
      String addressName,
      String city,
      String country,
      String isPrimary,
      String latitude,
      String longitude,
      String postalCode,
      String state,
      String imageShortKey) {
    final Map<String, String> oOrgAddresses = new HashMap<>();
    oOrgAddresses.put("Address1", address1);
    oOrgAddresses.put("Address2", address2);
    oOrgAddresses.put("AddressName", addressName);
    oOrgAddresses.put("City", city);
    oOrgAddresses.put("Country", country);
    oOrgAddresses.put("IsPrimary", isPrimary);
    oOrgAddresses.put("Latitude", latitude);
    oOrgAddresses.put("Longitude", longitude);
    oOrgAddresses.put("PostalCode", postalCode);
    oOrgAddresses.put("State", state);
    oOrgAddresses.put("Imageshortkey", imageShortKey);
    oOrgAddresses.put("Types", "[1]");
    return oOrgAddresses;
  }

  public Map<String, String> getOrgAddressesDynamic() {
    Map<String, String> map = new HashMap<>();
    final String name = ENCODER.getEncodedValue("");
    final String locationName = OracleDynamicVariables.getLOCName(name);
    final String address1 = name + " SUITE 1";
    final String address2 = name + " AVE 2";
    final String city = "WEST DES MOINES";
    final String country = "USA";
    final String isPrimary = "true";
    final String state = "IA";
    final String latitude = "41.575011";
    final String longitude = "93.709212";
    final String postalCode = "50266";
    // ⚠️ REVIEW NEEDED: Hardcoded value "imageshortkey" - Is this a sensitive key/token?
    // This is not a password, bu a placeholder for a real password.
    final String imageshortkey = "imageshortkey";
    map =
        getOrgAddresses(
            address1,
            address2,
            locationName,
            city,
            country,
            isPrimary,
            latitude,
            longitude,
            postalCode,
            state,
            imageshortkey);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getOrgAddressesDynamic", map);
    }
    return map;
  }

  /**
   * @param ssoUserTokenId
   * @param organizationName
   * @return
   */
  public Map<String, String> getOrganizationCount(String ssoUserTokenId, String organizationName) {
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Name: " + Constants.QUOTE_DOUBLE + organizationName + Constants.QUOTE_DOUBLE + "}";
    final String requestURL = baseAPIEntity + "/Search/Organizations/Count";
    final String json = getAPIJSONResponse("PUT", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }

  /**
   * @param contactTitle
   * @param taxId
   * @param yearFounded
   * @param facebookURL
   * @param numberOfEmployeesId
   * @param numberOfOffices
   * @param profileText
   * @param twitterHandle
   * @param annualVolume
   * @param avgContractAmt
   * @param bondingAggregateContractLimit
   * @param bondingSingleContractLimit
   * @param currentEmr
   * @param doingBusinessAs
   * @param leedLevel
   * @param tradeCodes
   * @param mainEmail
   * @param websiteURL
   * @param hasBIMExperience
   * @return
   */
  public Map<String, String> getOrgCompanyInfo(
      String contactTitle,
      String taxId,
      String yearFounded,
      String facebookURL,
      String numberOfEmployeesId,
      String numberOfOffices,
      String profileText,
      String twitterHandle,
      String annualVolume,
      String avgContractAmt,
      String bondingAggregateContractLimit,
      String bondingSingleContractLimit,
      String currentEmr,
      String doingBusinessAs,
      String leedLevel,
      String tradeCodes,
      String mainEmail,
      String websiteURL,
      String hasBIMExperience) {
    final Map<String, String> oCompanyInfo = new HashMap<>();
    oCompanyInfo.put("ContactTitle", contactTitle);
    oCompanyInfo.put("TaxId", taxId);
    oCompanyInfo.put("YearFounded", yearFounded);
    oCompanyInfo.put("FacebookURL", facebookURL);
    oCompanyInfo.put("NumberOfEmployeesID", numberOfEmployeesId);
    oCompanyInfo.put("NumberOfOffices", numberOfOffices);
    oCompanyInfo.put("ProfileText", profileText);
    oCompanyInfo.put("TwitterHandle", twitterHandle);
    oCompanyInfo.put("AnnualVolume", annualVolume);
    oCompanyInfo.put("AvgContractAmt", avgContractAmt);
    oCompanyInfo.put("BondingAggregateContractLimit", bondingAggregateContractLimit);
    oCompanyInfo.put("BondingSingleContractLimit", bondingSingleContractLimit);
    oCompanyInfo.put("CurrentEmr", currentEmr);
    oCompanyInfo.put("DoingBusinessAs", doingBusinessAs);
    oCompanyInfo.put("LeedLevel", leedLevel);
    oCompanyInfo.put("TradeCodes", tradeCodes);
    oCompanyInfo.put("MainEmail", mainEmail);
    oCompanyInfo.put("WebsiteURL", websiteURL);
    oCompanyInfo.put("HasBIMExperience", hasBIMExperience);
    return oCompanyInfo;
  }

  public Map<String, String> getOrgCompanyInfoDynamic() {
    Map<String, String> map = new HashMap<>();
    final String contactTitle = "QA AUTOMATION";
    final String taxId = getRandomTaxId();
    final String yearFounded = getCurrentYearMinusYears(50);
    final String facebookURL = "https://www.facebook" + IExtension.COM + "/TexturaCorporation";
    final String numberOfEmployeesId = "1";
    final String numberOfOffices = getRandomNumberString(1, 24);
    final String profileText = "PROFILE TEXT " + DateHelpersTests.getTimeStamp();
    final String twitterHandle = "@TexturaCorpAutomation";
    final String annualVolume = getRandomNumberString(1, 499);
    final String avgContractAmt = getRandomNumberString(1, 499);
    final String bondingAggregateContractLimit = getRandomNumberString(1, 499);
    final String bondingSingleContractLimit = getRandomNumberString(1, 499);
    final String currentEmr = "1.1";
    final String doingBusinessAs =
        "["
            + Constants.QUOTE_DOUBLE
            + "Submittal Exchange"
            + Constants.QUOTE_DOUBLE
            + ", "
            + Constants.QUOTE_DOUBLE
            + "Textura"
            + Constants.QUOTE_DOUBLE
            + "]";
    final String leedLevel = "1";
    final String tradeCodes = "[7321, 7359, 7499]";
    final String mainEmail = "companyemail@TexturaCorp" + IExtension.COM;
    final String websiteURL = "www.companywebsite" + IExtension.COM;
    final String hasBIMExperience = "false";
    map =
        getOrgCompanyInfo(
            contactTitle,
            taxId,
            yearFounded,
            facebookURL,
            numberOfEmployeesId,
            numberOfOffices,
            profileText,
            twitterHandle,
            annualVolume,
            avgContractAmt,
            bondingAggregateContractLimit,
            bondingSingleContractLimit,
            currentEmr,
            doingBusinessAs,
            leedLevel,
            tradeCodes,
            mainEmail,
            websiteURL,
            hasBIMExperience);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getOrgCompanyInfoDynamic", map);
    }
    return map;
  }

  /**
   * @param phoneNumber
   * @param phoneType
   * @param isPrimary
   * @return
   */
  public Map<String, String> getOrgContactPhones(
      String phoneNumber, String phoneType, String isPrimary) {
    final Map<String, String> oPhones = new HashMap<>();
    oPhones.put("PhoneNumber", phoneNumber);
    oPhones.put("PhoneType", phoneType);
    oPhones.put("IsPrimary", isPrimary);
    return oPhones;
  }

  public Map<String, String> getOrgContactPhonesDynamic() {
    Map<String, String> map = new HashMap<>();
    final String phoneNumber = getRandomPhoneNumber();
    final String phoneType = "Work";
    final String isPrimary = "true";
    map = getOrgContactPhones(phoneNumber, phoneType, isPrimary);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getOrgContactPhonesDynamic", map);
    }
    return map;
  }

  /**
   * @param address1
   * @param address2
   * @param addressName
   * @param city
   * @param country
   * @param isPrimary
   * @param latitude
   * @param longitude
   * @param postalCode
   * @param state
   * @param imageShortKey
   * @param firstName
   * @param lastName
   * @param eMail
   * @param phoneNum
   * @param phoneType
   * @param Title
   * @param types
   * @return
   */
  public Map<String, String> getOrgContacts(
      String address1,
      String address2,
      String addressName,
      String city,
      String country,
      String isPrimary,
      String latitude,
      String longitude,
      String postalCode,
      String state,
      String imageShortKey,
      String firstName,
      String lastName,
      String eMail,
      String phoneNum,
      String phoneType,
      String title,
      String types) {
    final Map<String, String> oOrgContacts = new HashMap<>();
    oOrgContacts.put("Address1", address1);
    oOrgContacts.put("Address2", address2);
    oOrgContacts.put("AddressName", addressName);
    oOrgContacts.put("City", city);
    oOrgContacts.put("Country", country);
    oOrgContacts.put("IsPrimary", isPrimary);
    oOrgContacts.put("Latitude", latitude);
    oOrgContacts.put("Longitude", longitude);
    oOrgContacts.put("PostalCode", postalCode);
    oOrgContacts.put("State", state);
    oOrgContacts.put("Imageshortkey", imageShortKey);
    oOrgContacts.put("Types", types);
    oOrgContacts.put("FirstName", firstName);
    oOrgContacts.put("LastName", lastName);
    oOrgContacts.put("eMail", eMail);
    oOrgContacts.put("PhoneNumber", phoneNum);
    oOrgContacts.put("PhoneType", phoneType);
    oOrgContacts.put("Title", title);
    return oOrgContacts;
  }

  public Map<String, String> getOrgContactsDynamic() {
    Map<String, String> map = new HashMap<>();
    final String name = ENCODER.getEncodedValue("");
    final String address1 = name + " SUITE 1";
    final String address2 = name + " AVE 2";
    final String locationName = OracleDynamicVariables.getLOCName(name);
    final String city = "West Des Moines";
    final String country = "USA";
    final String isPrimary = "true";
    final String state = "IA";
    final String latitude = "41.575011";
    final String longitude = "93.709212";
    final String postalCode = "50266";
    // ⚠️ REVIEW NEEDED: Hardcoded value "imageshortkey" - Is this a sensitive key/token?
    // This is not a password, bu a placeholder for a real password.
    final String imageshortkey = "imageshortkey";
    final String types = "[1]";
    final String eMail = "tester@yopmail" + IExtension.COM;
    final String firstName = OracleDynamicVariables.getFirstName(name);
    final String lastName = OracleDynamicVariables.getLastName(name);
    final String phoneNum = "515-326-1919";
    final String phoneType = "Cell";
    final String title = "TITLE QAT" + name;
    map =
        getOrgContacts(
            address1,
            address2,
            locationName,
            city,
            country,
            isPrimary,
            latitude,
            longitude,
            postalCode,
            state,
            imageshortkey,
            firstName,
            lastName,
            eMail,
            phoneNum,
            phoneType,
            title,
            types);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getOrgContactsDynamic", map);
    }
    return map;
  }

  /**
   * @param phoneNumber
   * @param phoneType
   * @param isPrimary
   * @return
   */
  public Map<String, String> getOrgPhones(String phoneNumber, String phoneType, String isPrimary) {
    final Map<String, String> oPhones = new HashMap<>();
    oPhones.put("PhoneNumber", phoneNumber);
    oPhones.put("PhoneType", phoneType);
    oPhones.put("IsPrimary", isPrimary);
    return oPhones;
  }

  public Map<String, String> getOrgPhonesDynamic() {
    Map<String, String> map = new HashMap<>();
    final String phoneNumber = getRandomPhoneNumber();
    final String phoneType = "Work";
    final String isPrimary = "false";
    map = getOrgPhones(phoneNumber, phoneType, isPrimary);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getOrgPhonesDynamic", map);
    }
    return map;
  }

  /**
   * @param name
   * @param contractValue
   * @param date
   * @param description
   * @param imageShortKey
   * @param LEEDRating
   * @param location
   * @param owner
   * @param workTypes
   * @return
   */
  public Map<String, String> getOrgProject(
      String name,
      String contractValue,
      String date,
      String description,
      String imageShortKey,
      String leedRating,
      String location,
      String owner,
      String projectTypes) {
    final Map<String, String> oOrgProject = new HashMap<>();
    oOrgProject.put("Name", name);
    oOrgProject.put("ContractValue", contractValue);
    oOrgProject.put("DateCompleted", date);
    oOrgProject.put("Description", description);
    oOrgProject.put("Imageshortkey", imageShortKey);
    oOrgProject.put("LEEDRating", leedRating);
    oOrgProject.put("Location", location);
    oOrgProject.put("Owner", owner);
    oOrgProject.put("ProjectTypes", projectTypes);
    return oOrgProject;
  }

  public Map<String, String> getOrgProjectDynamic() {
    Map<String, String> map = new HashMap<>();
    final String dynamicName = ENCODER.getEncodedValue("");
    final String name = OracleDynamicVariables.getPROName(dynamicName);
    final String contractValue = "10000";
    final String date = "Aug 2015";
    final String description = "QA Testing";
    // ⚠️ REVIEW NEEDED: Hardcoded value "imageshortkey" - Is this a sensitive key/token?
    // This is not a password, bu a placeholder for a real password.
    final String imageShortKey = "imageshortkey";
    final String leedRating = "2";
    final String location = OracleDynamicVariables.getLOCName(dynamicName);
    final String owner = OracleDynamicVariables.getOWNName(dynamicName);
    final String projectTypes = "1";
    map =
        getOrgProject(
            name,
            contractValue,
            date,
            description,
            imageShortKey,
            leedRating,
            location,
            owner,
            projectTypes);
    if (OracleConstants.MAPPING) {
      JavaHelpers.mapSysOut("getOrgProjectDynamic", map);
    }
    return map;
  }

  public String getRandomNumberString(int miniumumNumber, int maximumNumber) {
    int iRandomNumber = (int) (Math.random() * maximumNumber);
    iRandomNumber += miniumumNumber;
    return String.valueOf(iRandomNumber);
  }

  public String getRandomPhoneNumber() {
    // String.format("%03d", Integer.valueOf(getRandomNumberString(1,
    // 999)));
    final String s01 = "515";
    final String s02 = String.format("%03d", Integer.valueOf(getRandomNumberString(1, 999)));
    final String s03 = String.format("%04d", Integer.valueOf(getRandomNumberString(1, 9999)));
    return s01 + "-" + s02 + "-" + s03;
  }

  public String getRandomTaxId() {
    final String s01 = String.format("%03d", Integer.valueOf(getRandomNumberString(1, 999)));
    final String s02 = String.format("%02d", Integer.valueOf(getRandomNumberString(1, 99)));
    final String s03 = String.format("%04d", Integer.valueOf(getRandomNumberString(1, 9999)));
    return s01 + "-" + s02 + "-" + s03;
  }
}
