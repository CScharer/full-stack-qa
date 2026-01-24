package com.cjs.qa.junit.tests;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.cjs.qa.bitcoin.Bitcoin;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.maven.objects.TestRunCommand;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitDataTests;

public class BitcoinTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BitcoinTests.class));

  private String mavenCommand;
  private JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);

  @BeforeEach
  void beforeTestSetup(TestInfo testInfo) throws Throwable {
    LOG.debug(
        "{}], TestName: [{}]",
        Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName(),
        getTestName(testInfo));
    // mavenCommand = "mvn clean test " + getDArgLine() + "
    // -DfailIfNoTests=false -Dtest=" + this.getClass().getName() + "#" +
    // getTestName() + " -Dtags=" + Constants.QUOTE_DOUBLE + "@" +
    // Constants.QUOTE_DOUBLE
    mavenCommand = new TestRunCommand(this.getClass().getName(), getTestName(testInfo)).toString();
  }

  @AfterEach
  void afterTestTeardown(TestInfo testInfo) {
    LOG.debug(
        "{}], TestName: [{}]",
        Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName(),
        getTestName(testInfo));
  }

  private String getTestName(TestInfo testInfo) {
    return testInfo.getTestMethod().map(Method::getName).orElse("Unknown");
  }

  public JDBC getJdbc() {
    return jdbc;
  }

  public void setJdbc(JDBC jdbc) {
    this.jdbc = jdbc;
  }

  @Test
  void mining(TestInfo testInfo) throws Throwable {
    LOG.debug("getCurrentMethodDebugName: [{}]", JavaHelpers.getCurrentMethodDebugName());
    LOG.info("mavenCommand: [{}]", mavenCommand);
    String urlBitcoinCurrentPrice = "https://api.coindesk.com/v1/bpi/currentprice.json";
    String command = "curl -X GET " + urlBitcoinCurrentPrice;
    try {
      Bitcoin bitcoinPrevious = null;
      do {
        StringBuilder stringBuilderSQL = new StringBuilder();
        String dateTimeStamp =
            DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME);
        Map<String, String> responseMap = CommandLineTests.runProcess(command, true);
        String response = responseMap.get("lines");
        if (JavaHelpers.hasValue(response)) {
          // LOG.debug("response:[{}]", response);
          final JSONObject jsonObjectResponse = new JSONObject(response);
          final JSONObject jsonObjectBpi = jsonObjectResponse.getJSONObject("bpi");
          final JSONObject jsonObjectUSD = jsonObjectBpi.getJSONObject("USD");
          final String rate = jsonObjectUSD.getString("rate");
          final BigDecimal rateLong = jsonObjectUSD.getBigDecimal("rate_float");
          Bitcoin bitcoinCurrent = new Bitcoin(rate, rateLong, dateTimeStamp);
          if (bitcoinPrevious != null) {
            // Java 17: Records use direct accessor methods (no 'get' prefix)
            String bitcoinRateFloatCurrent = bitcoinCurrent.rateFloat().toString();
            String bitcoinRateFloatPrevious = bitcoinPrevious.rateFloat().toString();
            bitcoinPrevious = bitcoinCurrent;
            if (!bitcoinRateFloatCurrent.equals(bitcoinRateFloatPrevious)) {
              stringBuilderSQL = appendRecord(stringBuilderSQL, bitcoinCurrent);
              LOG.info("bitcoinCurrent: [{}]", bitcoinCurrent.toString());
            }
          } else {
            bitcoinPrevious = bitcoinCurrent;
            stringBuilderSQL = appendRecord(stringBuilderSQL, bitcoinCurrent);
            LOG.info("bitcoinCurrent: [{}]", bitcoinCurrent.toString());
          }
          // Java 17: Records use direct accessor methods (no 'get' prefix)
          LOG.info("{} - rate: [${}]", dateTimeStamp, bitcoinCurrent.rate());
        } else {
          LOG.warn("{} - [No Response]", dateTimeStamp);
        }
      } while (true);
    } catch (Exception e) {
      e.printStackTrace();
      if (getJdbc() != null) {
        getJdbc().close();
      }
    }
  }

  private StringBuilder appendRecord(StringBuilder stringBuilderSQL, Bitcoin bitcoin)
      throws Throwable {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO + " [t_Bitcoin] ");
    stringBuilder.append("([DateTimeStamp],[Rate],[RateFloat]");
    stringBuilder.append(") VALUES (");
    // Java 17: Records use direct accessor methods (no 'get' prefix)
    stringBuilder.append("'" + bitcoin.dateTimeStamp() + "',");
    stringBuilder.append("'" + bitcoin.rate() + "',");
    stringBuilder.append("'" + bitcoin.rateFloat() + "');");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilderSQL.append(stringBuilder.toString());
    if (!stringBuilderSQL.toString().isEmpty()
        && stringBuilderSQL.toString().split(Constants.NEWLINE).length >= 1) {
      int recordsAffected = getJdbc().executeUpdate(stringBuilderSQL.toString(), false);
      LOG.info("recordsAffected: [{}]", recordsAffected);
      stringBuilderSQL = new StringBuilder();
    }
    return stringBuilderSQL;
  }
}
