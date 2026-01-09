*** Settings ***
Documentation     Simple test for the HomePage - uses Page Object Model via Resource files
Resource          ${CURDIR}${/}resources${/}Common.robot
Resource          ${CURDIR}${/}resources${/}HomePage.robot
Test Setup        Setup WebDriver And Open Browser
Test Teardown     Close Browser And Cleanup

*** Test Cases ***
Home Page Should Load
    [Documentation]    Test that the home page loads
    [Tags]    homepage    smoke

    Navigate To Home Page
    Verify Home Page Loaded

Home Page Should Display Navigation Panel
    [Documentation]    Test that the navigation panel is visible
    [Tags]    homepage    navigation

    Navigate To Home Page
    Verify Sidebar Visible
    Verify Navigation Title    Navigation
    Verify Navigation Elements Present
