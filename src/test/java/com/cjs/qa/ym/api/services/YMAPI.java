package com.cjs.qa.ym.api.services;

import com.cjs.qa.ym.api.namespace.AuthNamespace;
import com.cjs.qa.ym.api.namespace.ConvertNamespace;
import com.cjs.qa.ym.api.namespace.EventsNamespace;
import com.cjs.qa.ym.api.namespace.FeedsNamespace;
import com.cjs.qa.ym.api.namespace.MemberNamespace;
import com.cjs.qa.ym.api.namespace.MembersNamespace;
import com.cjs.qa.ym.api.namespace.PeopleNamespace;
import com.cjs.qa.ym.api.namespace.SaAuthNamespace;
import com.cjs.qa.ym.api.namespace.SaCertificationsNamespace;
import com.cjs.qa.ym.api.namespace.SaCommerceNamespace;
import com.cjs.qa.ym.api.namespace.SaEventsNamespace;
import com.cjs.qa.ym.api.namespace.SaExportNamespace;
import com.cjs.qa.ym.api.namespace.SaFinanceNamespace;
import com.cjs.qa.ym.api.namespace.SaGroupsNamespace;
import com.cjs.qa.ym.api.namespace.SaMemberNamespace;
import com.cjs.qa.ym.api.namespace.SaMembersNamespace;
import com.cjs.qa.ym.api.namespace.SaNonMembersNamespace;
import com.cjs.qa.ym.api.namespace.SaPeopleNamespace;
import com.cjs.qa.ym.api.namespace.SessionNamespace;

public class YMAPI {

  private AuthNamespace authNamespace;
  private ConvertNamespace convertNamespace;
  private EventsNamespace eventsNamespace;
  private FeedsNamespace feedsNamespace;
  private MemberNamespace memberNamespace;
  private MembersNamespace membersNamespace;
  private PeopleNamespace peopleNamespace;
  private SaAuthNamespace saAuthNamespace;
  private SaCertificationsNamespace saCertificationsNamespace;
  private SaCommerceNamespace saCommerceNamespace;
  private SaEventsNamespace saEventsNamespace;
  private SaExportNamespace saExportNamespace;
  private SaFinanceNamespace saFinanceNamespace;
  private SaGroupsNamespace saGroupsNamespace;
  private SaMemberNamespace saMemberNamespace;
  private SaMembersNamespace saMembersNamespace;
  private SaNonMembersNamespace saNonMembersNamespace;
  private SaPeopleNamespace saPeopleNamespace;
  private SessionNamespace sessionNamespace;

  public AuthNamespace getAuthNamespace() {
    return authNamespace;
  }

  public ConvertNamespace getConvertNamespace() {
    return convertNamespace;
  }

  public EventsNamespace getEventsNamespace() {
    return eventsNamespace;
  }

  public FeedsNamespace getFeedsNamespace() {
    return feedsNamespace;
  }

  public MemberNamespace getMemberNamespace() {
    return memberNamespace;
  }

  public MembersNamespace getMembersNamespace() {
    return membersNamespace;
  }

  public PeopleNamespace getPeopleNamespace() {
    return peopleNamespace;
  }

  public SaAuthNamespace getSaAuthNamespace() {
    return saAuthNamespace;
  }

  public SaCertificationsNamespace getSaCertificationsNamespace() {
    return saCertificationsNamespace;
  }

  public SaCommerceNamespace getSaCommerceNamespace() {
    return saCommerceNamespace;
  }

  public SaEventsNamespace getSaEventsNamespace() {
    return saEventsNamespace;
  }

  public SaExportNamespace getSaExportNamespace() {
    return saExportNamespace;
  }

  public SaFinanceNamespace getSaFinanceNamespace() {
    return saFinanceNamespace;
  }

  public SaGroupsNamespace getSaGroupsNamespace() {
    return saGroupsNamespace;
  }

  public SaMemberNamespace getSaMemberNamespace() {
    return saMemberNamespace;
  }

  public SaMembersNamespace getSaMembersNamespace() {
    return saMembersNamespace;
  }

  public SaNonMembersNamespace getSaNonMembersNamespace() {
    return saNonMembersNamespace;
  }

  public SaPeopleNamespace getSaPeopleNamespace() {
    return saPeopleNamespace;
  }

  public SessionNamespace getSessionNamespace() {
    return sessionNamespace;
  }

  public YMAPI() throws Throwable {
    authNamespace = new AuthNamespace();
    convertNamespace = new ConvertNamespace();
    eventsNamespace = new EventsNamespace();
    feedsNamespace = new FeedsNamespace();
    memberNamespace = new MemberNamespace();
    membersNamespace = new MembersNamespace();
    peopleNamespace = new PeopleNamespace();
    saAuthNamespace = new SaAuthNamespace();
    saCertificationsNamespace = new SaCertificationsNamespace();
    saCommerceNamespace = new SaCommerceNamespace();
    saEventsNamespace = new SaEventsNamespace();
    saExportNamespace = new SaExportNamespace();
    saFinanceNamespace = new SaFinanceNamespace();
    saGroupsNamespace = new SaGroupsNamespace();
    saMemberNamespace = new SaMemberNamespace();
    saMembersNamespace = new SaMembersNamespace();
    saNonMembersNamespace = new SaNonMembersNamespace();
    saPeopleNamespace = new SaPeopleNamespace();
    sessionNamespace = new SessionNamespace();
  }

  public static final String LABEL_CALL_METHOD_PREFIX = "<Call Method=";
  public static final String LABEL_CALL_METHOD_SUFFIX = "</Call>";
}
