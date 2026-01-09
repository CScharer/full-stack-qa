*** Settings ***
Documentation     Page Object Model for the Home Page
Resource          ${CURDIR}${/}Common.robot

*** Variables ***
# Selectors - use data-qa attributes from frontend (Sidebar.tsx, app/page.tsx)
${HOME_PAGE_SIDEBAR}                  css:[data-qa="sidebar"]
${HOME_PAGE_SIDEBAR_TITLE}            css:[data-qa="sidebar-title"]
${HOME_PAGE_SIDEBAR_NAVIGATION}       css:[data-qa="sidebar-navigation"]
${HOME_PAGE_SIDEBAR_NAV_HOME}         css:[data-qa="sidebar-nav-home"]
${HOME_PAGE_APPLICATIONS_CARD}        css:[data-qa="sidebar-nav-applications"]
${HOME_PAGE_COMPANIES_CARD}           css:[data-qa="sidebar-nav-companies"]
${HOME_PAGE_CONTACTS_CARD}            css:[data-qa="sidebar-nav-contacts"]
${HOME_PAGE_CLIENTS_CARD}             css:[data-qa="sidebar-nav-clients"]
${HOME_PAGE_NOTES_CARD}               css:[data-qa="sidebar-nav-notes"]
${HOME_PAGE_JOB_SEARCH_SITES_CARD}    css:[data-qa="sidebar-nav-job-search-sites"]

*** Keywords ***
Navigate To Home Page
    [Documentation]    Navigate to the home page
    Navigate To Page    /

Verify Home Page Loaded
    [Documentation]    Verify home page has loaded successfully
    Verify Page Title Contains    Job Search Application
    Verify Page Loaded

Verify Sidebar Visible
    [Documentation]    Verify sidebar is visible
    Wait Until Element Is Visible    ${HOME_PAGE_SIDEBAR}    timeout=${SHORT_TIMEOUT}

Verify Navigation Title
    [Documentation]    Verify navigation title contains expected text
    [Arguments]    ${expected_text}=Navigation
    Wait Until Element Is Visible    ${HOME_PAGE_SIDEBAR_TITLE}    timeout=${SHORT_TIMEOUT}
    Element Should Contain    ${HOME_PAGE_SIDEBAR_TITLE}    ${expected_text}

Verify Navigation Elements Present
    [Documentation]    Verify navigation elements are present and visible
    Wait Until Element Is Visible    ${HOME_PAGE_SIDEBAR_NAVIGATION}    timeout=${SHORT_TIMEOUT}
    Wait Until Element Is Visible    ${HOME_PAGE_SIDEBAR_NAV_HOME}    timeout=${SHORT_TIMEOUT}

Click Applications Card
    [Documentation]    Click the Applications navigation card
    Click Element    ${HOME_PAGE_APPLICATIONS_CARD}

Click Companies Card
    [Documentation]    Click the Companies navigation card
    Click Element    ${HOME_PAGE_COMPANIES_CARD}

Click Contacts Card
    [Documentation]    Click the Contacts navigation card
    Click Element    ${HOME_PAGE_CONTACTS_CARD}

Click Clients Card
    [Documentation]    Click the Clients navigation card
    Click Element    ${HOME_PAGE_CLIENTS_CARD}

Click Notes Card
    [Documentation]    Click the Notes navigation card
    Click Element    ${HOME_PAGE_NOTES_CARD}

Click Job Search Sites Card
    [Documentation]    Click the Job Search Sites navigation card
    Click Element    ${HOME_PAGE_JOB_SEARCH_SITES_CARD}

Verify All Navigation Cards Visible
    [Documentation]    Verify all navigation cards are visible
    Wait Until Element Is Visible    ${HOME_PAGE_APPLICATIONS_CARD}    timeout=${SHORT_TIMEOUT}
    Wait Until Element Is Visible    ${HOME_PAGE_COMPANIES_CARD}    timeout=${SHORT_TIMEOUT}
    Wait Until Element Is Visible    ${HOME_PAGE_CONTACTS_CARD}    timeout=${SHORT_TIMEOUT}
    Wait Until Element Is Visible    ${HOME_PAGE_CLIENTS_CARD}    timeout=${SHORT_TIMEOUT}
    Wait Until Element Is Visible    ${HOME_PAGE_NOTES_CARD}    timeout=${SHORT_TIMEOUT}
    Wait Until Element Is Visible    ${HOME_PAGE_JOB_SEARCH_SITES_CARD}    timeout=${SHORT_TIMEOUT}
