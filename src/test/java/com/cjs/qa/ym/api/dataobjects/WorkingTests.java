package com.cjs.qa.ym.api.dataobjects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.Environment;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class WorkingTests extends Environment {

  @Test
  public void verification() throws Throwable {
    // YourMembershipResponse yourMembership_Response = new
    // UnmarshallEventResponse()
    // yourMembership_Response.getResults().getResultTotal();
    //
    // C:\Workspace\Data\Vivit\Data\20190316\YM\Events\1.xml
    String fileName = "C:\\Workspace\\Data\\Vivit\\Data\\20190316\\YM\\Events\\1.xml";
    final UnmarshallYourMembershipResponse unmarshallYourMembershipResponse =
        new UnmarshallYourMembershipResponse();
    final YourMembershipResponse yourMembershipResponse =
        unmarshallYourMembershipResponse.getFromFile(fileName);
    sysOut(yourMembershipResponse.getErrCode());
    Results results = yourMembershipResponse.getResults();
    int eventCount = results.getResultTotal();
    sysOut("eventCount:[" + eventCount + "]");
  }
}
