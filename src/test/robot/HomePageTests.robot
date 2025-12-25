*** Settings ***
Documentation     Simple test for the HomePage
Library           SeleniumLibrary
Library           BuiltIn
Library           OperatingSystem
Library           ${CURDIR}${/}WebDriverManager.py
Test Setup        Setup WebDriver And Open Browser
Test Teardown     Close Browser

*** Variables ***
${BASE_URL}       http://localhost:3003
${SELENIUM_REMOTE_URL}    ${EMPTY}

*** Test Cases ***
Home Page Should Load
    [Documentation]    Test that the home page loads
    [Tags]    homepage    smoke
    
    # Wait for page to be ready
    Wait Until Page Contains    Job Search    timeout=20s
    
    # Verify the page title in browser tab
    ${title}=    Get Title
    Should Contain    ${title}    Job Search Application
    
    # Verify page has loaded - use tag: prefix for body element
    Wait Until Element Is Visible    tag:body    timeout=10s

Home Page Should Display Navigation Panel
    [Documentation]    Test that the navigation panel is visible
    [Tags]    homepage    navigation
    
    # Verify sidebar is visible - use css: prefix for CSS selectors
    Wait Until Element Is Visible    css:[data-qa="sidebar"]    timeout=10s
    
    # Verify navigation title is visible and contains "Navigation"
    Wait Until Element Is Visible    css:[data-qa="sidebar-title"]    timeout=10s
    Element Should Contain    css:[data-qa="sidebar-title"]    Navigation
    
    # Verify navigation elements are present
    Wait Until Element Is Visible    css:[data-qa="sidebar-navigation"]    timeout=10s
    Wait Until Element Is Visible    css:[data-qa="sidebar-nav-home"]    timeout=10s

*** Keywords ***
Setup WebDriver And Open Browser
    # Get SELENIUM_REMOTE_URL from environment variable if not set as Robot variable
    ${remote_url_env}=    Get Environment Variable    SELENIUM_REMOTE_URL    default=${EMPTY}
    ${remote_url}=    Set Variable If    '${SELENIUM_REMOTE_URL}' != '' and '${SELENIUM_REMOTE_URL}' != '${EMPTY}'    ${SELENIUM_REMOTE_URL}    ${remote_url_env}
    ${has_remote}=    Evaluate    '${remote_url}' != '' and '${remote_url}' != '${EMPTY}' and '${remote_url}' != 'None'
    
    # Only set up ChromeDriver for local execution (not remote grid)
    # This automatically downloads and sets up ChromeDriver if needed
    Run Keyword If    not ${has_remote}    WebDriverManager.Setup Chromedriver
    
    # Get base URL from Robot variable (set via --variable or default)
    # BASE_URL is passed from CI or uses default from Variables section
    ${base_url}=    Set Variable If    '${BASE_URL}' != ''    ${BASE_URL}    http://localhost:3003
    
    # Use remote WebDriver if SELENIUM_REMOTE_URL is set and not empty
    # Otherwise, ChromeDriver is now in PATH, so SeleniumLibrary will find it automatically
    Run Keyword If    ${has_remote}
    ...    Open Browser    ${base_url}    browser=chrome    remote_url=${remote_url}
    ...    ELSE
    ...    Open Browser    ${base_url}    browser=chrome
    
    Maximize Browser Window
    Set Selenium Implicit Wait    15s
    Set Selenium Timeout    15s
    # Wait for page to fully load
    Wait Until Page Contains    Job Search    timeout=20s
    Sleep    2s    # Additional wait for any dynamic content
