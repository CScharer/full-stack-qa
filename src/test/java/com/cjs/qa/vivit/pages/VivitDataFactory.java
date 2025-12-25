package com.cjs.qa.vivit.pages;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.VivitTables;

import io.cucumber.datatable.DataTable;

public class VivitDataFactory {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(VivitDataFactory.class));
  private static final String LABEL_EMAIL_ADDRESS = "Email_Address";

  public DataTable getBio(String columnName, String columnValue) {
    LOG.debug("***ClassMethodDebug***: [{}]", JavaHelpers.getCurrentClassMethodDebugName());
    JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
    final StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(
        JDBCConstants.SELECT_ALL_FROM + "[" + VivitTables.DOM_VIVIT_MEMBERS + "] ");
    switch (columnName) {
      case VivitDataTests.LABEL_WEB_SITE_MEMBER_ID:
      case "Username":
      case LABEL_EMAIL_ADDRESS:
      case "Email_Address_Alternate":
        sqlStringBuilder.append(
            JDBCConstants.WHERE + "LOWER([" + columnName + "])=LOWER('" + columnValue + "');");
        break;
      default:
        sqlStringBuilder.append(
            JDBCConstants.WHERE + "[" + VivitDataTests.LABEL_WEB_SITE_MEMBER_ID + "]='10969660';");
        break;
    }
    final String sql = sqlStringBuilder.toString();
    final List<Map<String, String>> memberData = jdbc.queryResultsString(sql, false);
    final Map<String, String> mapData = memberData.get(0);
    final DataTable dataTable =
        Convert.fromListToDataTable(
            Arrays.asList(
                Arrays.asList("Email", mapData.get(LABEL_EMAIL_ADDRESS)),
                Arrays.asList("Email Confirm", mapData.get(LABEL_EMAIL_ADDRESS)),
                Arrays.asList("Gender", mapData.get("Gender")),
                Arrays.asList("Title", mapData.get("Member_Name_Title")),
                Arrays.asList("First Name", mapData.get("First_Name")),
                Arrays.asList("Middle Name", mapData.get("Middle_Name")),
                Arrays.asList("Last Name", mapData.get("Last_Name")),
                Arrays.asList("Suffix Name", mapData.get("Member_Name_Suffix")),
                Arrays.asList("Nickname", mapData.get("Nickname")),
                Arrays.asList("Maiden Name", mapData.get("Maiden_Name")),
                Arrays.asList("Birthday", mapData.get("Birthdate")),
                Arrays.asList("Personal URL", mapData.get("Personal_Website")),
                Arrays.asList("Phone Home Fax Area Code", mapData.get("Employer_Phone_Area_Code")),
                Arrays.asList("Phone Home Fax", mapData.get("Employer_Phone")),
                Arrays.asList("Email Alternate", mapData.get("Email_Address_Alternate")),
                Arrays.asList(
                    "Alternate IM Username",
                    CJSConstants.EMAIL_ADDRESS_MSN.substring(
                        0, CJSConstants.EMAIL_ADDRESS_MSN.indexOf('@'))),
                Arrays.asList("Alternate IM Type", "// Windows Live Messenger"),
                Arrays.asList("Employer Name", mapData.get("Employer_Name")),
                Arrays.asList("Employer Own", mapData.get("Employer_Name")),
                Arrays.asList("Employer Title", mapData.get("Professional_Title")),
                Arrays.asList(
                    "Employer URL", "https://www.linkedin" + IExtension.COM + "/in/cscharer"),
                Arrays.asList("Employer Address1", mapData.get("Employer_Address_Line1")),
                Arrays.asList("Employer Address2", mapData.get("Employer_Address_Line2")),
                Arrays.asList("Employer City", mapData.get("Employer_City")),
                Arrays.asList("Employer Country", "// " + mapData.get("Employer_Country")),
                Arrays.asList("Employer State", "// " + mapData.get("Employer_State_Abbrev")),
                Arrays.asList("Employer Zip", mapData.get("Employer_Postal_Code")),
                Arrays.asList("Employer Phone Area Code", mapData.get("Employer_Phone_Area_Code")),
                Arrays.asList("Employer Phone", mapData.get("Employer_Phone")),
                Arrays.asList("Employer Fax Area Code", mapData.get("Employer_Fax_Area_Code")),
                Arrays.asList("Employer Fax", mapData.get("Employer_Fax")),
                Arrays.asList("Employer Continent", "// " + mapData.get("Continent")),
                Arrays.asList(
                    "Used HP Years", "// " + mapData.get("Years_Micro_Focus_Products_Used"))));
    jdbc.close();
    LOG.debug("DataTable: {}", dataTable.toString());
    return dataTable;
  }
}
