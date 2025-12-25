package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class BiUnderwritingPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(BiUnderwritingPage.class));

  public BiUnderwritingPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DECLARATIONS
  private static final By editSICCode = By.id("SIC_CD");
  private static final By buttonSICCodeLookup = By.id("SIC_CD-lookup");
  private static final By dropdownRatingTier = By.id("RTG_TIER");
  private static final By dropdownPrimaryClassCode = By.id("PRIM_CLS");
  private static final By editBusinessGroup = By.id("BUS_GRP");
  private static final By dropdownBusinessGroupLookup = By.id("BUS_GRP-lookup");
  private static final By dropdownProfitCenter = By.id("PCC");
  private static final By dropdownNationalAccountsPolicyOrigination = By.id("NTNL_ACCT");
  private static final By dropdownSpecialUnderwritingProgram = By.id("POL_PRG");
  private static final By editDunsNumber = By.id("DUNS_NUM1");
  private static final By dropdownStarAccount = By.id("STAR_ACCT");
  private static final By editOtherLegalEntity = By.id("ETY_OTH_DESC1");
  private static final By dropdownSourceOfBusiness = By.id("SRC_BUS");
  private static final By dropdownPrimaryPolicySymbol = By.id("PRIM_POL_SYM");
  private static final By checkboxPolicyMinimumBypassed = By.id("MIN_PREM");
  private static final By editPolicyMinimumPremiumBypassedCommercialAuto = By.id("AUTO_MIN_PREM");
  private static final By checkboxPublicUtilitiesCommissionFiling = By.id("PUB_UTIL_COMM_FIL");
  private static final By dropdownSafetyDividendGroup = By.id("SAFE_GRP");
  private static final By editUnemploymentNumber = By.id("UIAN");
  private static final By checkboxFacultiveReinsuranceApplies = By.id("FAC_REINS_IND");
  private static final By checkboxExemptCommercialAccount = By.id("EXMPT_COM_ACCT");
  private static final By editAgencysInsuredIdentification = By.id("AGCY_INSD_ID");
  private static final By dropdownNumberOfDaysQuoteIsValid = By.id("NUM_DAYS1");
  private static final By checkboxPackageMod = By.id("ASSGN_PKG_MOD_POL_LVL");
  private static final By editDateQuoteReceivedByCompany = By.id("CO_RCVD_DT");
  private static final By editDateUnderwritingReviewCompleted = By.id("UDWR_RVW_DT");
  private static final By dropdownRolloverPriorCarrierCode = By.id("ROLL_PRI_CARR_CD");
  private static final By checkboxMoPepNonCancel = By.id("NON_CANC_GUAR");
  private static final By dropdownPolicyConsideredForTransferToSmallBusiness =
      By.id("SM_BUS_UNT_CONV");
  private static final By editPolicyNumberForAuditCrossReference = By.id("AUDT_CRS_REF_POL_NUM");
  private static final By editOriginalAgencyCode = By.id("ORIG_AGCY_CD");
  private static final By editPremiumFromAplusConvertedPolicy = By.id("APLUS_PREM");
  private static final By editRenewalOfPolicyNumber = By.id("RNL_POL_NUM");
  private static final By dropdownDoesEmploymentPracticesLiabilityInsuranceApply =
      By.id("EPLI_IND");
  private static final By editEnterEmploymentPracticesLiabilityRetroactiveDate =
      By.id("EPLI_RETRO_DT");
  private static final By checkboxRerateEmploymentPracticesLiabilityOnAmmendment =
      By.id("EPLI_RRT_IND");
  private static final By editKentuckyCollectionFeeTotalPolicyPremium = By.id("KY_CLC_FEE_TOPREM");
  private static final By editKentuckyTaxTotalPolicyPremium = By.id("KY_TAX_TOPREM");
  private static final By editTotalEmploymentPracticesLiabilityPremium = By.id("EPLI_TOPREM");
  private static final By editTotalEPLISupplementalExtendedReportingPremium =
      By.id("EPLI_RPTNG_TOPREM");
  private static final By checkboxRapidRenewal = By.id("RPD_RNL");
  private static final By editSupportingPolicies = By.id("SUP_POL");
  private static final By buttonFrame = By.id("underwriting-title");
  private static final String pageTitle = "BI_UnderwritingPage";

  private String getPageTitle() {
    return pageTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  // METHODS GET
  public String getEditSICCode() {
    return getEdit(editSICCode);
  }

  public String getDropdownRatingTier() {
    return getDropdown(dropdownRatingTier);
  }

  public String getDropdownPrimaryClassCode() {
    return getDropdown(dropdownPrimaryClassCode);
  }

  public String getEditBusinessGroup() {
    return getEdit(editBusinessGroup);
  }

  public String getDropdownBusinessGroupLookup() {
    return getDropdown(dropdownBusinessGroupLookup);
  }

  public String getDropdownProfitCenter() {
    return getDropdown(dropdownProfitCenter);
  }

  public String getDropdownNationalAccountsPolicyOrigination() {
    return getDropdown(dropdownNationalAccountsPolicyOrigination);
  }

  public String getDropdownSpecialUnderwritingProgram() {
    return getDropdown(dropdownSpecialUnderwritingProgram);
  }

  public String getEditDunsNumber() {
    return getEdit(editDunsNumber);
  }

  public String getDropdownStarAccount() {
    return getDropdown(dropdownStarAccount);
  }

  public String getEditOtherLegalEntity() {
    return getEdit(editOtherLegalEntity);
  }

  public String getDropdownSourceOfBusiness() {
    return getDropdown(dropdownSourceOfBusiness);
  }

  public String getDropdownPrimaryPolicySymbol() {
    return getDropdown(dropdownPrimaryPolicySymbol);
  }

  public String getCheckboxPolicyMinimumBypassed() {
    return getCheckbox(checkboxPolicyMinimumBypassed);
  }

  public String getEditPolicyMinimumPremiumBypassedCommercialAuto() {
    return getEdit(editPolicyMinimumPremiumBypassedCommercialAuto);
  }

  public String getCheckboxPublicUtilitiesCommissionFiling() {
    return getCheckbox(checkboxPublicUtilitiesCommissionFiling);
  }

  public String getDropdownSafetyDividendGroup() {
    return getDropdown(dropdownSafetyDividendGroup);
  }

  public String getEditUnemploymentNumber() {
    return getEdit(editUnemploymentNumber);
  }

  public String getCheckboxFacultiveReinsuranceApplies() {
    return getCheckbox(checkboxFacultiveReinsuranceApplies);
  }

  public String getCheckboxExemptCommercialAccount() {
    return getCheckbox(checkboxExemptCommercialAccount);
  }

  public String getEditAgencysInsuredIdentification() {
    return getEdit(editAgencysInsuredIdentification);
  }

  public String getDropdownNumberOfDaysQuoteIsValid() {
    return getDropdown(dropdownNumberOfDaysQuoteIsValid);
  }

  public String getCheckboxPackageMod() {
    return getCheckbox(checkboxPackageMod);
  }

  public String getEditDateQuoteReceivedByCompany() {
    return getEdit(editDateQuoteReceivedByCompany);
  }

  public String getEditDateUnderwritingReviewCompleted() {
    return getEdit(editDateUnderwritingReviewCompleted);
  }

  public String getDropdownRolloverPriorCarrierCode() {
    return getDropdown(dropdownRolloverPriorCarrierCode);
  }

  public String getCheckboxMoPepNonCancel() {
    return getCheckbox(checkboxMoPepNonCancel);
  }

  public String getDropdownPolicyConsideredForTransferToSmallBusiness() {
    return getDropdown(dropdownPolicyConsideredForTransferToSmallBusiness);
  }

  public String getEditPolicyNumberForAuditCrossReference() {
    return getEdit(editPolicyNumberForAuditCrossReference);
  }

  public String getEditOriginalAgencyCode() {
    return getEdit(editOriginalAgencyCode);
  }

  public String getEditPremiumFromAplusConvertedPolicy() {
    return getEdit(editPremiumFromAplusConvertedPolicy);
  }

  public String getEditRenewalOfPolicyNumber() {
    return getEdit(editRenewalOfPolicyNumber);
  }

  public String getDropdownDoesEmploymentPracticesLiabilityInsuranceApply() {
    return getDropdown(dropdownDoesEmploymentPracticesLiabilityInsuranceApply);
  }

  public String getEditEnterEmploymentPracticesLiabilityRetroactiveDate() {
    return getEdit(editEnterEmploymentPracticesLiabilityRetroactiveDate);
  }

  public String getCheckboxRerateEmploymentPracticesLiabilityOnAmmendment() {
    return getCheckbox(checkboxRerateEmploymentPracticesLiabilityOnAmmendment);
  }

  public String getEditKentuckyCollectionFeeTotalPolicyPremium() {
    return getEdit(editKentuckyCollectionFeeTotalPolicyPremium);
  }

  public String getEditKentuckyTaxTotalPolicyPremium() {
    return getEdit(editKentuckyTaxTotalPolicyPremium);
  }

  public String getEditTotalEmploymentPracticesLiabilityPremium() {
    return getEdit(editTotalEmploymentPracticesLiabilityPremium);
  }

  public String getEditTotalEPLISupplementalExtendedReportingPremium() {
    return getEdit(editTotalEPLISupplementalExtendedReportingPremium);
  }

  public String getCheckboxRapidRenewal() {
    return getCheckbox(checkboxRapidRenewal);
  }

  public String getEditSupportingPolicies() {
    return getEdit(editSupportingPolicies);
  }

  // METHODS SET
  public void setEditSICCode(String value) {
    setEdit(editSICCode, value);
  }

  public void clickButtonSICCodeLookup() {
    clickObject(buttonSICCodeLookup);
  }

  public void selectDropdownRatingTier(String value) {
    selectDropdown(dropdownRatingTier, value);
  }

  public void selectDropdownPrimaryClassCode(String value) {
    selectDropdown(dropdownPrimaryClassCode, value);
  }

  public void setEditBusinessGroup(String value) {
    setEdit(editBusinessGroup, value);
  }

  public void selectDropdownBusinessGroupLookup(String value) {
    selectDropdown(dropdownBusinessGroupLookup, value);
  }

  public void selectDropdownProfitCenter(String value) {
    selectDropdown(dropdownProfitCenter, value);
  }

  public void selectDropdownNationalAccountsPolicyOrigination(String value) {
    selectDropdown(dropdownNationalAccountsPolicyOrigination, value);
  }

  public void selectDropdownSpecialUnderwritingProgram(String value) {
    selectDropdown(dropdownSpecialUnderwritingProgram, value);
  }

  public void setEditDunsNumber(String value) {
    setEdit(editDunsNumber, value);
  }

  public void selectDropdownStarAccount(String value) {
    selectDropdown(dropdownStarAccount, value);
  }

  public void setEditOtherLegalEntity(String value) {
    setEdit(editOtherLegalEntity, value);
  }

  public void selectDropdownSourceOfBusiness(String value) {
    selectDropdown(dropdownSourceOfBusiness, value);
  }

  public void selectDropdownPrimaryPolicySymbol(String value) {
    selectDropdown(dropdownPrimaryPolicySymbol, value);
  }

  public void toggleCheckboxPolicyMinimumBypassed() {
    toggleCheckbox(checkboxPolicyMinimumBypassed);
  }

  public void setCheckboxPolicyMinimumBypassed(String value) {
    setCheckbox(checkboxPolicyMinimumBypassed, value);
  }

  public void setEditPolicyMinimumPremiumBypassedCommercialAuto(String value) {
    setEdit(editPolicyMinimumPremiumBypassedCommercialAuto, value);
  }

  public void toggleCheckboxPublicUtilitiesCommissionFiling() {
    toggleCheckbox(checkboxPublicUtilitiesCommissionFiling);
  }

  public void setCheckboxPublicUtilitiesCommissionFiling(String value) {
    setCheckbox(checkboxPublicUtilitiesCommissionFiling, value);
  }

  public void selectDropdownSafetyDividendGroup(String value) {
    selectDropdown(dropdownSafetyDividendGroup, value);
  }

  public void setEditUnemploymentNumber(String value) {
    setEdit(editUnemploymentNumber, value);
  }

  public void toggleCheckboxFacultiveReinsuranceApplies() {
    toggleCheckbox(checkboxFacultiveReinsuranceApplies);
  }

  public void setCheckboxFacultiveReinsuranceApplies(String value) {
    setCheckbox(checkboxFacultiveReinsuranceApplies, value);
  }

  public void toggleCheckboxExemptCommercialAccount() {
    toggleCheckbox(checkboxExemptCommercialAccount);
  }

  public void setCheckboxExemptCommercialAccount(String value) {
    setCheckbox(checkboxExemptCommercialAccount, value);
  }

  public void setEditAgencysInsuredIdentification(String value) {
    setEdit(editAgencysInsuredIdentification, value);
  }

  public void selectDropdownNumberOfDaysQuoteIsValid(String value) {
    selectDropdown(dropdownNumberOfDaysQuoteIsValid, value);
  }

  public void toggleCheckboxPackageMod() {
    toggleCheckbox(checkboxPackageMod);
  }

  public void setCheckboxPackageMod(String value) {
    setCheckbox(checkboxPackageMod, value);
  }

  public void setEditDateQuoteReceivedByCompany(String value) {
    setEdit(editDateQuoteReceivedByCompany, value);
  }

  public void setEditDateUnderwritingReviewCompleted(String value) {
    setEdit(editDateUnderwritingReviewCompleted, value);
  }

  public void selectDropdownRolloverPriorCarrierCode(String value) {
    selectDropdown(dropdownRolloverPriorCarrierCode, value);
  }

  public void toggleCheckboxMoPepNonCancel() {
    toggleCheckbox(checkboxMoPepNonCancel);
  }

  public void setCheckboxMoPepNonCancel(String value) {
    setCheckbox(checkboxMoPepNonCancel, value);
  }

  public void selectDropdownPolicyConsideredForTransferToSmallBusiness(String value) {
    selectDropdown(dropdownPolicyConsideredForTransferToSmallBusiness, value);
  }

  public void setEditPolicyNumberForAuditCrossReference(String value) {
    setEdit(editPolicyNumberForAuditCrossReference, value);
  }

  public void setEditOriginalAgencyCode(String value) {
    setEdit(editOriginalAgencyCode, value);
  }

  public void setEditPremiumFromAplusConvertedPolicy(String value) {
    setEdit(editPremiumFromAplusConvertedPolicy, value);
  }

  public void setEditRenewalOfPolicyNumber(String value) {
    setEdit(editRenewalOfPolicyNumber, value);
  }

  public void selectDropdownDoesEmploymentPracticesLiabilityInsuranceApply(String value) {
    selectDropdown(dropdownDoesEmploymentPracticesLiabilityInsuranceApply, value);
  }

  public void setEditEnterEmploymentPracticesLiabilityRetroactiveDate(String value) {
    setEdit(editEnterEmploymentPracticesLiabilityRetroactiveDate, value);
  }

  public void toggleCheckboxRerateEmploymentPracticesLiabilityOnAmmendment() {
    toggleCheckbox(checkboxRerateEmploymentPracticesLiabilityOnAmmendment);
  }

  public void setCheckboxRerateEmploymentPracticesLiabilityOnAmmendment(String value) {
    setCheckbox(checkboxRerateEmploymentPracticesLiabilityOnAmmendment, value);
  }

  public void setEditKentuckyCollectionFeeTotalPolicyPremium(String value) {
    setEdit(editKentuckyCollectionFeeTotalPolicyPremium, value);
  }

  public void setEditKentuckyTaxTotalPolicyPremium(String value) {
    setEdit(editKentuckyTaxTotalPolicyPremium, value);
  }

  public void setEditTotalEmploymentPracticesLiabilityPremium(String value) {
    setEdit(editTotalEmploymentPracticesLiabilityPremium, value);
  }

  public void setEditTotalEPLISupplementalExtendedReportingPremium(String value) {
    setEdit(editTotalEPLISupplementalExtendedReportingPremium, value);
  }

  public void toggleCheckboxRapidRenewal() {
    toggleCheckbox(checkboxRapidRenewal);
  }

  public void setCheckboxRapidRenewal(String value) {
    setCheckbox(checkboxRapidRenewal, value);
  }

  public void setEditSupportingPolicies(String value) {
    setEdit(editSupportingPolicies, value);
  }

  public void toggleFrame() {
    clickObject(buttonFrame);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("({Field}{}, {Value}{});", field, value);
        }
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "sic code":
            setEditSICCode(value);
            break;
          case "rating tier":
            selectDropdownRatingTier(value);
            break;
          case "primary class code":
            selectDropdownPrimaryClassCode(value);
            break;
          case "business group":
            setEditBusinessGroup(value);
            break;
          case "business group lookup":
            selectDropdownBusinessGroupLookup(value);
            break;
          case "profit center":
            selectDropdownProfitCenter(value);
            break;
          case "national accounts policy origination":
            selectDropdownNationalAccountsPolicyOrigination(value);
            break;
          case "special underwriting program":
            selectDropdownSpecialUnderwritingProgram(value);
            break;
          case "duns number":
            setEditDunsNumber(value);
            break;
          case "star account":
            selectDropdownStarAccount(value);
            break;
          case "other legal entity":
            setEditOtherLegalEntity(value);
            break;
          case "source of business":
            selectDropdownSourceOfBusiness(value);
            break;
          case "primary policy symbol":
            selectDropdownPrimaryPolicySymbol(value);
            break;
          case "policy minimum bypassed":
            setCheckboxPolicyMinimumBypassed(value);
            break;
          case "policyminimumpremiumbypassedcommercialauto":
            setEditPolicyMinimumPremiumBypassedCommercialAuto(value);
            break;
          case "public utilities commission filing":
            setCheckboxPublicUtilitiesCommissionFiling(value);
            break;
          case "safety dividend group":
            selectDropdownSafetyDividendGroup(value);
            break;
          case "unemployment number":
            setEditUnemploymentNumber(value);
            break;
          case "facultive reinsurance applies":
            setCheckboxFacultiveReinsuranceApplies(value);
            break;
          case "exempt commercial account":
            setCheckboxExemptCommercialAccount(value);
            break;
          case "agencys insured identification":
            setEditAgencysInsuredIdentification(value);
            break;
          case "number of days quote is valid":
            selectDropdownNumberOfDaysQuoteIsValid(value);
            break;
          case "package mod":
            setCheckboxPackageMod(value);
            break;
          case "date quote received by company":
            setEditDateQuoteReceivedByCompany(value);
            break;
          case "date underwriting review completed":
            setEditDateUnderwritingReviewCompleted(value);
            break;
          case "rollover prior carrier code":
            selectDropdownRolloverPriorCarrierCode(value);
            break;
          case "mo pep non cancel":
            setCheckboxMoPepNonCancel(value);
            break;
          case "policy considered for transfer to small business":
            selectDropdownPolicyConsideredForTransferToSmallBusiness(value);
            break;
          case "policy number for audit cross reference":
            setEditPolicyNumberForAuditCrossReference(value);
            break;
          case "original agency code":
            setEditOriginalAgencyCode(value);
            break;
          case "premium from aplus converted policy":
            setEditPremiumFromAplusConvertedPolicy(value);
            break;
          case "renewal of policy number":
            setEditRenewalOfPolicyNumber(value);
            break;
          case "does employment practices liability insurance apply":
            selectDropdownDoesEmploymentPracticesLiabilityInsuranceApply(value);
            break;
          case "enter employment practices liability retroactive date":
            setEditEnterEmploymentPracticesLiabilityRetroactiveDate(value);
            break;
          case "rerate employment practices liability on ammendment":
            setCheckboxRerateEmploymentPracticesLiabilityOnAmmendment(value);
            break;
          case "kentucky collection fee total policy premium":
            setEditKentuckyCollectionFeeTotalPolicyPremium(value);
            break;
          case "kentucky tax total policy premium":
            setEditKentuckyTaxTotalPolicyPremium(value);
            break;
          case "total employment practices liability premium":
            setEditTotalEmploymentPracticesLiabilityPremium(value);
            break;
          case "total epli supplemental extended reporting premium":
            setEditTotalEPLISupplementalExtendedReportingPremium(value);
            break;
          case "rapid renewal":
            setCheckboxRapidRenewal(value);
            break;
          case "supporting policies":
            setEditSupportingPolicies(value);
            break;
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable table) {
    final Map<String, String> expected = new HashMap<>();
    final Map<String, String> actual = new HashMap<>();
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "sic code":
            value = getEditSICCode();
            break;
          case "rating tier":
            value = getDropdownRatingTier();
            break;
          case "primary class code":
            value = getDropdownPrimaryClassCode();
            break;
          case "business group":
            value = getEditBusinessGroup();
            break;
          case "business group lookup":
            value = getDropdownBusinessGroupLookup();
            break;
          case "profit center":
            value = getDropdownProfitCenter();
            break;
          case "national accounts policy origination":
            value = getDropdownNationalAccountsPolicyOrigination();
            break;
          case "special underwriting program":
            value = getDropdownSpecialUnderwritingProgram();
            break;
          case "duns number":
            value = getEditDunsNumber();
            break;
          case "star account":
            value = getDropdownStarAccount();
            break;
          case "other legal entity":
            value = getEditOtherLegalEntity();
            break;
          case "source of business":
            value = getDropdownSourceOfBusiness();
            break;
          case "primary policy symbol":
            value = getDropdownPrimaryPolicySymbol();
            break;
          case "policy minimum bypassed":
            value = getCheckboxPolicyMinimumBypassed();
            break;
          case "policyminimumpremiumbypassedcommercialauto":
            value = getEditPolicyMinimumPremiumBypassedCommercialAuto();
            break;
          case "public utilities commission filing":
            value = getCheckboxPublicUtilitiesCommissionFiling();
            break;
          case "safety dividend group":
            value = getDropdownSafetyDividendGroup();
            break;
          case "unemployment number":
            value = getEditUnemploymentNumber();
            break;
          case "facultive reinsurance applies":
            value = getCheckboxFacultiveReinsuranceApplies();
            break;
          case "exempt commercial account":
            value = getCheckboxExemptCommercialAccount();
            break;
          case "agencys insured identification":
            value = getEditAgencysInsuredIdentification();
            break;
          case "number of days quote is valid":
            value = getDropdownNumberOfDaysQuoteIsValid();
            break;
          case "package mod":
            value = getCheckboxPackageMod();
            break;
          case "date quote received by company":
            value = getEditDateQuoteReceivedByCompany();
            break;
          case "date underwriting review completed":
            value = getEditDateUnderwritingReviewCompleted();
            break;
          case "rollover prior carrier code":
            value = getDropdownRolloverPriorCarrierCode();
            break;
          case "mo pep non cancel":
            value = getCheckboxMoPepNonCancel();
            break;
          case "policy considered for transfer to small business":
            value = getDropdownPolicyConsideredForTransferToSmallBusiness();
            break;
          case "policy number for audit cross reference":
            value = getEditPolicyNumberForAuditCrossReference();
            break;
          case "original agency code":
            value = getEditOriginalAgencyCode();
            break;
          case "premium from aplus converted policy":
            value = getEditPremiumFromAplusConvertedPolicy();
            break;
          case "renewal of policy number":
            value = getEditRenewalOfPolicyNumber();
            break;
          case "does employment practices liability insurance apply":
            value = getDropdownDoesEmploymentPracticesLiabilityInsuranceApply();
            break;
          case "enter employment practices liability retroactive date":
            value = getEditEnterEmploymentPracticesLiabilityRetroactiveDate();
            break;
          case "rerate employment practices liability on ammendment":
            value = getCheckboxRerateEmploymentPracticesLiabilityOnAmmendment();
            break;
          case "kentucky collection fee total policy premium":
            value = getEditKentuckyCollectionFeeTotalPolicyPremium();
            break;
          case "kentucky tax total policy premium":
            value = getEditKentuckyTaxTotalPolicyPremium();
            break;
          case "total employment practices liability premium":
            value = getEditTotalEmploymentPracticesLiabilityPremium();
            break;
          case "total epli supplemental extended reporting premium":
            value = getEditTotalEPLISupplementalExtendedReportingPremium();
            break;
          case "rapid renewal":
            value = getCheckboxRapidRenewal();
            break;
          case "supporting policies":
            value = getEditSupportingPolicies();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.debug("Value: {}", value);
            break;
        }
        actual.put(field, value);
      }
    }
    Assert.assertSame(getPageTitle() + " validatePage", expected, actual);
  }
}
