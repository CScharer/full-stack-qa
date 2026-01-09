# Robot Framework Page Object Model (POM) Migration Guide

**Date Created**: 2026-01-09  
**Status**: 📋 Migration Planning Document  
**Purpose**: Guide for converting Robot Framework tests to use Page Object Model pattern  
**Current State**: Tests use direct selectors and keywords in test files  
**Target State**: All tests use Page Object Model pattern via Resource files

---

## 📋 Executive Summary

This document outlines the strategy and step-by-step process for migrating Robot Framework tests from the current direct selector approach to the **Page Object Model (POM)** pattern using Robot Framework's Resource files. This migration will improve test maintainability, reusability, and readability.

### Benefits of Page Object Model in Robot Framework

- ✅ **Maintainability**: Selectors and keywords defined in Resource files
- ✅ **Reusability**: Page Object resources can be shared across multiple test files
- ✅ **Readability**: Tests focus on behavior, not implementation details
- ✅ **Scalability**: Easier to add new tests as application grows
- ✅ **Team Collaboration**: Clear separation of concerns
- ✅ **Robot Framework Native**: Uses built-in Resource file mechanism

---

## 🎯 Current State Analysis

### Current Test Structure

**Location**: `src/test/robot/HomePageTests.robot`

```robot
*** Settings ***
Documentation     Simple test for the HomePage
Library           SeleniumLibrary
Library           BuiltIn
Library           OperatingSystem
Test Setup        Setup WebDriver And Open Browser
Test Teardown     Close Browser

*** Variables ***
${BASE_URL}       http://localhost:3003

*** Test Cases ***
Home Page Should Load
    [Documentation]    Test that the home page loads
    [Tags]    homepage    smoke
    
    Wait Until Page Contains    Job Search    timeout=10s
    ${title}=    Get Title
    Should Contain    ${title}    Job Search Application
    Wait Until Element Is Visible    tag:body    timeout=5s

Home Page Should Display Navigation Panel
    [Documentation]    Test that the navigation panel is visible
    [Tags]    homepage    navigation
    
    Wait Until Element Is Visible    css:[data-qa="sidebar"]    timeout=5s
    Wait Until Element Is Visible    css:[data-qa="sidebar-title"]    timeout=5s
    Element Should Contain    css:[data-qa="sidebar-title"]    Navigation
    Wait Until Element Is Visible    css:[data-qa="sidebar-navigation"]    timeout=5s
    Wait Until Element Is Visible    css:[data-qa="sidebar-nav-home"]    timeout=5s

*** Keywords ***
Setup WebDriver And Open Browser
    # Setup code...
    Open Browser    ${BASE_URL}    browser=chrome
    Maximize Browser Window
```

### Issues with Current Approach

1. **Selectors scattered**: Each test file contains its own selectors
2. **Keywords duplicated**: Same keywords repeated across multiple test files
3. **Hard to maintain**: UI changes require updating multiple files
4. **Mixed concerns**: Test logic mixed with page interaction details
5. **No reusability**: Cannot easily share page objects across test suites

---

## 🔄 Target Architecture

### Directory Structure

```
src/test/robot/
├── HomePageTests.robot          # Test files (behavior-focused)
├── ApplicationsTests.robot
├── CompaniesTests.robot
├── resources/                    # NEW: Page Object Resources
│   ├── Common.robot             # Common keywords and variables
│   ├── HomePage.robot           # HomePage Page Object
│   ├── ApplicationsPage.robot   # ApplicationsPage Page Object
│   ├── CompaniesPage.robot      # CompaniesPage Page Object
│   └── NavigationComponent.robot # Shared navigation component
├── WebDriverManager.py
└── README.md
```

---

## 📝 Step-by-Step Migration Process

### Step 1: Create Resources Directory Structure

**Action**: Create the directory structure for page object resources.

```bash
mkdir -p src/test/robot/resources
```

---

### Step 2: Create Common Resource File

**File**: `src/test/robot/resources/Common.robot`

**Purpose**: Common keywords, variables, and setup/teardown logic shared across all tests.

```robot
*** Settings ***
Documentation     Common keywords and variables for all tests
Library           SeleniumLibrary
Library           BuiltIn
Library           OperatingSystem
Library           ${CURDIR}${/}..${/}WebDriverManager.py

*** Variables ***
${BASE_URL}       http://localhost:3003
${SELENIUM_REMOTE_URL}    ${EMPTY}
${BROWSER}        chrome
${TIMEOUT}        10s
${SHORT_TIMEOUT}  5s

*** Keywords ***
Setup WebDriver And Open Browser
    [Documentation]    Sets up WebDriver and opens browser
    ${remote_url_env}=    Get Environment Variable    SELENIUM_REMOTE_URL    default=${EMPTY}
    ${remote_url}=    Set Variable If    '${SELENIUM_REMOTE_URL}' != '' and '${SELENIUM_REMOTE_URL}' != '${EMPTY}'    ${SELENIUM_REMOTE_URL}    ${remote_url_env}
    ${has_remote}=    Evaluate    '${remote_url}' != '' and '${remote_url}' != '${EMPTY}' and '${remote_url}' != 'None'
    
    Run Keyword If    not ${has_remote}    WebDriverManager.Setup Chromedriver
    
    ${base_url}=    Set Variable If    '${BASE_URL}' != ''    ${BASE_URL}    http://localhost:3003
    
    Run Keyword If    ${has_remote}
    ...    Open Browser    ${base_url}    browser=${BROWSER}    remote_url=${remote_url}
    ...    ELSE
    ...    Open Browser    ${base_url}    browser=${BROWSER}
    
    Maximize Browser Window
    Set Selenium Implicit Wait    ${SHORT_TIMEOUT}
    Set Selenium Timeout    ${SHORT_TIMEOUT}
    Wait Until Page Contains    Job Search    timeout=${TIMEOUT}
    Sleep    2s

Close Browser And Cleanup
    [Documentation]    Closes browser and performs cleanup
    Close Browser

Navigate To Page
    [Documentation]    Navigate to a specific page
    [Arguments]    ${path}=${EMPTY}
    ${url}=    Set Variable If    '${path}' == '${EMPTY}'    ${BASE_URL}    ${BASE_URL}${path}
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
```

---

### Step 3: Create HomePage Page Object Resource

**File**: `src/test/robot/resources/HomePage.robot`

**Purpose**: Encapsulate all HomePage selectors and keywords.

```robot
*** Settings ***
Documentation     Page Object Model for the Home Page
Resource          Common.robot

*** Variables ***
# Selectors
${HOME_PAGE_SIDEBAR}              css:[data-qa="sidebar"]
${HOME_PAGE_SIDEBAR_TITLE}        css:[data-qa="sidebar-title"]
${HOME_PAGE_SIDEBAR_NAVIGATION}   css:[data-qa="sidebar-navigation"]
${HOME_PAGE_SIDEBAR_NAV_HOME}     css:[data-qa="sidebar-nav-home"]
${HOME_PAGE_APPLICATIONS_CARD}   css:a[href="/applications"]
${HOME_PAGE_COMPANIES_CARD}      css:a[href="/companies"]
${HOME_PAGE_CONTACTS_CARD}       css:a[href="/contacts"]
${HOME_PAGE_CLIENTS_CARD}        css:a[href="/clients"]
${HOME_PAGE_NOTES_CARD}          css:a[href="/notes"]
${HOME_PAGE_JOB_SEARCH_SITES_CARD}    css:a[href="/job-search-sites"]

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
```

---

### Step 4: Update Test File to Use Page Object Resource

**File**: `src/test/robot/HomePageTests.robot` (Updated)

**Before** (Direct selectors):
```robot
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

*** Test Cases ***
Home Page Should Load
    [Documentation]    Test that the home page loads
    [Tags]    homepage    smoke
    
    Wait Until Page Contains    Job Search    timeout=10s
    ${title}=    Get Title
    Should Contain    ${title}    Job Search Application
    Wait Until Element Is Visible    tag:body    timeout=5s

Home Page Should Display Navigation Panel
    [Documentation]    Test that the navigation panel is visible
    [Tags]    homepage    navigation
    
    Wait Until Element Is Visible    css:[data-qa="sidebar"]    timeout=5s
    Wait Until Element Is Visible    css:[data-qa="sidebar-title"]    timeout=5s
    Element Should Contain    css:[data-qa="sidebar-title"]    Navigation
    Wait Until Element Is Visible    css:[data-qa="sidebar-navigation"]    timeout=5s
    Wait Until Element Is Visible    css:[data-qa="sidebar-nav-home"]    timeout=5s

*** Keywords ***
Setup WebDriver And Open Browser
    # ... setup code ...
```

**After** (Using Page Object Resource):
```robot
*** Settings ***
Documentation     Simple test for the HomePage
Resource          resources/Common.robot
Resource          resources/HomePage.robot
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
```

---

### Step 5: Create Additional Page Object Resources (As Needed)

#### ApplicationsPage Example

**File**: `src/test/robot/resources/ApplicationsPage.robot`

```robot
*** Settings ***
Documentation     Page Object Model for the Applications Page
Resource          Common.robot

*** Variables ***
# Selectors
${APPLICATIONS_PAGE_TITLE}              css:h1.h2, css:h2
${APPLICATIONS_NEW_BUTTON}             css:a[href="/applications/new"], css:button:has-text("Add")
${APPLICATIONS_TABLE}                  css:table.table
${APPLICATIONS_EMPTY_STATE}            xpath://text()[contains(., 'No applications found')]

*** Keywords ***
Navigate To Applications Page
    [Documentation]    Navigate to the applications page
    Navigate To Page    /applications

Verify Applications Page Loaded
    [Documentation]    Verify applications page has loaded
    Wait Until Element Is Visible    ${APPLICATIONS_PAGE_TITLE}    timeout=${SHORT_TIMEOUT}
    Element Should Contain    ${APPLICATIONS_PAGE_TITLE}    Applications

Click New Application Button
    [Documentation]    Click the button to create a new application
    Click Element    ${APPLICATIONS_NEW_BUTTON}

Verify Applications Table Visible
    [Documentation]    Verify the applications table is visible
    Wait Until Element Is Visible    ${APPLICATIONS_TABLE}    timeout=${SHORT_TIMEOUT}

Verify Empty State
    [Documentation]    Verify empty state message is displayed
    Wait Until Element Is Visible    ${APPLICATIONS_EMPTY_STATE}    timeout=${SHORT_TIMEOUT}

Get Application Row
    [Documentation]    Get locator for application row by position name
    [Arguments]    ${position}
    [Return]    xpath://table//tbody//tr[contains(., '${position}')]

Click Application
    [Documentation]    Click on an application by position name
    [Arguments]    ${position}
    ${row}=    Get Application Row    ${position}
    Click Element    ${row}//a[1]
```

---

## 🔧 Best Practices

### 1. Selector Strategy

**Use data-qa attributes** (already in use):
```robot
${HOME_PAGE_SIDEBAR}    css:[data-qa="sidebar"]
```

**CSS selectors** (when data-qa not available):
```robot
${HOME_PAGE_TITLE}    css:h1.display-5, css:h1.display-4, css:h1
```

**XPath selectors** (for complex queries):
```robot
${APPLICATION_ROW}    xpath://table//tbody//tr[contains(., '${position}')]
```

### 2. Keyword Naming Conventions

- **Actions**: Use verbs (`Click`, `Navigate`, `Fill`, `Select`)
  - `Click Applications Card`
  - `Fill Application Form`
  - `Select Option`

- **Verifications**: Use `Verify` prefix
  - `Verify Home Page Loaded`
  - `Verify Sidebar Visible`
  - `Verify Navigation Title`

- **Getters**: Use `Get` prefix
  - `Get Application Row`
  - `Get Page Title`

### 3. Resource File Structure

```robot
*** Settings ***
Documentation     Page Object description
Resource          Common.robot    # Import common keywords

*** Variables ***
# Page-specific selectors
${SELECTOR_1}    css:...
${SELECTOR_2}    xpath:...

*** Keywords ***
# Navigation keywords
Navigate To Page Name
    # ...

# Action keywords
Click Button Name
    # ...

# Verification keywords
Verify Something
    # ...
```

### 4. Variable Naming Conventions

**Page-specific variables**: Use page prefix
```robot
${HOME_PAGE_SIDEBAR}              # HomePage specific
${APPLICATIONS_PAGE_TITLE}        # ApplicationsPage specific
```

**Common variables**: No prefix
```robot
${BASE_URL}                       # Common across all pages
${TIMEOUT}                        # Common timeout value
```

### 5. Keyword Documentation

**Always document keywords**:
```robot
Navigate To Home Page
    [Documentation]    Navigate to the home page
    # Implementation
```

**Document arguments**:
```robot
Verify Navigation Title
    [Documentation]    Verify navigation title contains expected text
    [Arguments]    ${expected_text}=Navigation
    # Implementation
```

**Document return values**:
```robot
Get Application Row
    [Documentation]    Get locator for application row by position name
    [Arguments]    ${position}
    [Return]    xpath://table//tbody//tr[contains(., '${position}')]
```

### 6. Waiting Strategies

**Explicit waits** (preferred):
```robot
Wait Until Element Is Visible    ${SELECTOR}    timeout=${TIMEOUT}
Wait Until Page Contains    ${text}    timeout=${TIMEOUT}
```

**Avoid hard-coded waits**:
```robot
# ❌ Bad
Sleep    5s

# ✅ Good
Wait Until Element Is Visible    ${SELECTOR}    timeout=5s
```

### 7. Resource File Organization

**Import order**:
1. Common.robot (always first)
2. Component resources (if any)
3. Page-specific resources

```robot
*** Settings ***
Resource          resources/Common.robot
Resource          resources/NavigationComponent.robot
Resource          resources/HomePage.robot
```

---

## 📊 Migration Checklist

### Phase 1: Setup (Foundation)
- [ ] Create `src/test/robot/resources/` directory
- [ ] Create `Common.robot` with common keywords and variables
- [ ] Move shared setup/teardown keywords to `Common.robot`

### Phase 2: Core Pages
- [ ] Create `HomePage.robot` resource file
- [ ] Migrate `HomePageTests.robot` to use `HomePage.robot`
- [ ] Test migrated tests pass

### Phase 3: Additional Pages (As Needed)
- [ ] Create `ApplicationsPage.robot`
- [ ] Create `CompaniesPage.robot`
- [ ] Create `ContactsPage.robot`
- [ ] Create `ClientsPage.robot`
- [ ] Create `NotesPage.robot`
- [ ] Create `JobSearchSitesPage.robot`

### Phase 4: Component Resources (Optional)
- [ ] Create `NavigationComponent.robot` for shared navigation
- [ ] Create `FormComponent.robot` for shared form elements
- [ ] Refactor page objects to use components

### Phase 5: Documentation & Cleanup
- [ ] Update test documentation
- [ ] Add documentation to all keywords
- [ ] Review and refactor for consistency
- [ ] Remove duplicate code

---

## 🎓 Example: Complete Migration

### Before Migration

**Test File**: `HomePageTests.robot`
```robot
*** Test Cases ***
Home Page Should Navigate To Applications
    [Documentation]    Test navigation to applications page
    [Tags]    homepage    navigation
    
    Go To    ${BASE_URL}
    Wait Until Page Contains    Job Search    timeout=10s
    Wait Until Element Is Visible    css:[data-qa="sidebar"]    timeout=5s
    Click Element    css:a[href="/applications"]
    Wait Until Page Contains    Applications    timeout=10s
    Element Should Contain    css:h1.h2, css:h2    Applications
```

### After Migration

**Page Object Resource**: `resources/HomePage.robot`
```robot
*** Variables ***
${HOME_PAGE_SIDEBAR}              css:[data-qa="sidebar"]
${HOME_PAGE_APPLICATIONS_CARD}   css:a[href="/applications"]

*** Keywords ***
Navigate To Home Page
    Navigate To Page    /

Verify Sidebar Visible
    Wait Until Element Is Visible    ${HOME_PAGE_SIDEBAR}    timeout=${SHORT_TIMEOUT}

Click Applications Card
    Click Element    ${HOME_PAGE_APPLICATIONS_CARD}
```

**Page Object Resource**: `resources/ApplicationsPage.robot`
```robot
*** Variables ***
${APPLICATIONS_PAGE_TITLE}    css:h1.h2, css:h2

*** Keywords ***
Verify Applications Page Loaded
    Wait Until Page Contains    Applications    timeout=${TIMEOUT}
    Element Should Contain    ${APPLICATIONS_PAGE_TITLE}    Applications
```

**Test File**: `HomePageTests.robot` (Updated)
```robot
*** Settings ***
Resource          resources/Common.robot
Resource          resources/HomePage.robot
Resource          resources/ApplicationsPage.robot

*** Test Cases ***
Home Page Should Navigate To Applications
    [Documentation]    Test navigation to applications page
    [Tags]    homepage    navigation
    
    Navigate To Home Page
    Verify Sidebar Visible
    Click Applications Card
    Verify Applications Page Loaded
```

---

## 🔍 Troubleshooting

### Common Issues

**Issue**: Resource file not found
```robot
# Error: Resource file 'resources/HomePage.robot' not found
```

**Solution**: Ensure correct path relative to test file:
```robot
# From src/test/robot/HomePageTests.robot
Resource          resources/HomePage.robot

# Or use absolute path from project root
Resource          ${CURDIR}${/}..${/}..${/}resources${/}HomePage.robot
```

**Issue**: Variable not found
```robot
# Error: Variable '${HOME_PAGE_SIDEBAR}' not found
```

**Solution**: Ensure Resource file is imported in Settings section:
```robot
*** Settings ***
Resource          resources/HomePage.robot
```

**Issue**: Keyword not found
```robot
# Error: No keyword with name 'Navigate To Home Page' found
```

**Solution**: Ensure keyword exists in imported Resource file and spelling matches exactly.

**Issue**: Circular dependency
```robot
# Error: Circular resource file import detected
```

**Solution**: Avoid circular imports. Use Common.robot for shared functionality.

---

## 📚 Additional Resources

- [Robot Framework User Guide](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html)
- [Robot Framework Resource Files](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#resource-files)
- [Page Object Model Pattern](https://martinfowler.com/bliki/PageObject.html)
- [SeleniumLibrary Documentation](https://robotframework.org/SeleniumLibrary/SeleniumLibrary.html)

---

## ✅ Success Criteria

Migration is complete when:

1. ✅ All test files use Resource files instead of direct selectors
2. ✅ Selectors are centralized in Resource files
3. ✅ Tests are more readable and focused on behavior
4. ✅ Resource files follow consistent naming conventions
5. ✅ All tests pass after migration
6. ✅ Code is maintainable and scalable
7. ✅ Common keywords are shared via Common.robot

---

## 🔄 Integration with Existing Tests

### API Tests

API tests can also benefit from Resource files:

**File**: `resources/APICommon.robot`
```robot
*** Settings ***
Documentation     Common API keywords
Library           RequestsLibrary
Library           Collections

*** Variables ***
${API_BASE_URL}    https://jsonplaceholder.typicode.com

*** Keywords ***
Create API Session
    [Documentation]    Create a session for API calls
    [Arguments]    ${session_name}=api    ${base_url}=${API_BASE_URL}
    Create Session    ${session_name}    ${base_url}

Get API Response
    [Documentation]    Make GET request and return response
    [Arguments]    ${session_name}=api    ${endpoint}=/
    ${response}=    GET On Session    ${session_name}    ${endpoint}
    [Return]    ${response}
```

**Usage in Test**:
```robot
*** Settings ***
Resource          resources/APICommon.robot

*** Test Cases ***
Get Posts API Test
    Create API Session
    ${response}=    Get API Response    endpoint=/posts/1
    Should Be Equal As Strings    ${response.status_code}    200
```

---

**Last Updated**: 2026-01-09  
**Document Location**: `docs/work/20260109_ROBOT_FRAMEWORK_POM_MIGRATION_GUIDE.md`  
**Status**: 📋 Ready for Implementation
