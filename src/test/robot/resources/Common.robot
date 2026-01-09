*** Settings ***
Documentation     Common keywords and variables for all Robot Framework tests
Library           SeleniumLibrary
Library           BuiltIn
Library           OperatingSystem
Library           ${CURDIR}${/}..${/}WebDriverManager.py

*** Variables ***
${BASE_URL}               http://localhost:3003
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

    # Get base URL from Robot variable (set via --variable or default)
    ${base_url}=    Set Variable If    '${BASE_URL}' != ''    ${BASE_URL}    http://localhost:3003

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
