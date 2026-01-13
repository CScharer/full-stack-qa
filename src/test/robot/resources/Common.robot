*** Settings ***
Documentation     Common keywords and variables for all Robot Framework tests
Library           SeleniumLibrary
Library           BuiltIn
Library           OperatingSystem
Library           ${CURDIR}${/}..${/}WebDriverManager.py
Library           ${CURDIR}${/}ConfigHelper.py

*** Variables ***
${BASE_URL}               ${EMPTY}    # Will be set from shared config
${SELENIUM_REMOTE_URL}    ${EMPTY}
${BROWSER}                chrome
${TIMEOUT}                10s
${SHORT_TIMEOUT}          5s

*** Keywords ***
Setup WebDriver And Open Browser
    [Documentation]    Sets up WebDriver and opens browser to base URL
    # Get SELENIUM_REMOTE_URL from environment variable if not set as Robot variable
    ${remote_url_env}=    Get Environment Variable    SELENIUM_REMOTE_URL    default=${EMPTY}
    ${remote_url}=    Set Variable If    '${SELENIUM_REMOTE_URL}' != '' and '${SELENIUM_REMOTE_URL}' != '${EMPTY}'    ${SELENIUM_REMOTE_URL}    ${remote_url_env}
    ${has_remote}=    Evaluate    '${remote_url}' != '' and '${remote_url}' != '${EMPTY}' and '${remote_url}' != 'None'

    # Only set up ChromeDriver for local execution (not remote grid)
    Run Keyword If    not ${has_remote}    WebDriverManager.Setup Chromedriver

    # Get base URL from shared config (config/environments.json)
    # Priority: 1) BASE_URL env var, 2) BASE_URL Robot variable, 3) Shared config based on ENVIRONMENT
    ${base_url_env}=    Get Environment Variable    BASE_URL    default=${EMPTY}
    ${base_url}=    Run Keyword If    '${base_url_env}' != '' and '${base_url_env}' != '${EMPTY}'    Set Variable    ${base_url_env}
    ...    ELSE IF    '${BASE_URL}' != '' and '${BASE_URL}' != '${EMPTY}'    Set Variable    ${BASE_URL}
    ...    ELSE    Get Base Url From Shared Config

    # Use remote WebDriver if SELENIUM_REMOTE_URL is set, otherwise use local Chrome
    Run Keyword If    ${has_remote}
    ...    Open Browser    ${base_url}    browser=${BROWSER}    remote_url=${remote_url}
    ...    ELSE
    ...    Open Browser    ${base_url}    browser=${BROWSER}

    Maximize Browser Window
    Set Selenium Implicit Wait    ${SHORT_TIMEOUT}
    Set Selenium Timeout    ${SHORT_TIMEOUT}
    # Wait for page to fully load
    Wait Until Page Contains    Job Search    timeout=${TIMEOUT}
    Sleep    2s    # Additional wait for any dynamic content

Close Browser And Cleanup
    [Documentation]    Closes browser and performs cleanup
    Close Browser

Navigate To Page
    [Documentation]    Navigate to a specific page
    [Arguments]    ${path}=${EMPTY}
    ${url}=    Set Variable If    '${path}' == '' or '${path}' == '${EMPTY}'    ${BASE_URL}    ${BASE_URL}${path}
    Go To    ${url}
    Wait Until Page Contains    Job Search    timeout=${TIMEOUT}

Verify Page Title Contains
    [Documentation]    Verify page title contains expected text
    [Arguments]    ${expected_text}
    ${title}=    Get Title
    Should Contain    ${title}    ${expected_text}

Verify Page Loaded
    [Documentation]    Verify page has loaded by checking body element
    Wait Until Element Is Visible    tag:body    timeout=${SHORT_TIMEOUT}

Get Base Url From Shared Config
    [Documentation]    Get base URL from shared config/environments.json based on ENVIRONMENT
    [Return]    Base URL from shared config
    ${environment}=    Get Environment Variable    ENVIRONMENT    default=dev
    ${base_url}=    ConfigHelper.Get Base Url For Robot    ${environment}
    [Return]    ${base_url}
