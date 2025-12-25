# 📊 Data-Driven Testing Guide

**Last Updated**: 2025-11-14  
**Status**: Active

---

## Overview

This framework now supports **data-driven testing** using external data files (Excel, JSON, CSV). This allows you to:

- ✅ **Separate test data from test code** - Update test data without code changes
- ✅ **Run same test with multiple data sets** - Increase test coverage easily
- ✅ **Maintain test data in familiar formats** - Excel, JSON, or CSV files
- ✅ **Reduce code duplication** - One test method, multiple data sets

---

## Available Data Providers

### 1. ExcelDataProvider

Reads test data from Excel files (`.xlsx`, `.xls`).

**Location**: `src/test/java/com/cjs/qa/utilities/ExcelDataProvider.java`

**Usage**:

```java
import com.cjs.qa.utilities.ExcelDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests {
    
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return ExcelDataProvider.readExcel(
            "test-data/login-scenarios.xlsx",
            "Sheet1",      // Sheet name
            true           // Skip header row
        );
    }
    
    @Test(dataProvider = "loginData")
    public void testLogin(String email, String password, String expectedResult) {
        // Test implementation
        loginPage.login(email, password);
        Assert.assertEquals(loginPage.getResult(), expectedResult);
    }
}
```

**Excel File Format** (`test-data/login-scenarios.xlsx`):

| email | password | expectedResult |
|-------|----------|----------------|
| user1@test.com | password1 | success |
| user2@test.com | password2 | success |
| invalid@test.com | wrongpass | failure |

**Features**:
- Supports multiple sheets
- Automatically handles different data types (strings, numbers, dates, booleans)
- Skips empty rows
- Can skip header row

---

### 2. JSONDataProvider

Reads test data from JSON files.

**Location**: `src/test/java/com/cjs/qa/utilities/JSONDataProvider.java`

**Usage**:

```java
import com.cjs.qa.utilities.JSONDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchTests {
    
    @DataProvider(name = "searchData")
    public Object[][] getSearchData() {
        return JSONDataProvider.readJSONArray(
            "test-data/search-queries.json",
            "queries"  // Key name for the array
        );
    }
    
    @Test(dataProvider = "searchData")
    public void testSearch(String searchTerm, boolean shouldSucceed) {
        // Test implementation
        searchPage.search(searchTerm);
        if (shouldSucceed) {
            Assert.assertTrue(searchPage.hasResults());
        }
    }
}
```

**JSON File Format** (`test-data/search-queries.json`):

```json
{
  "queries": [
    {
      "searchTerm": "Selenium WebDriver",
      "shouldSucceed": true
    },
    {
      "searchTerm": "TestNG Framework",
      "shouldSucceed": true
    },
    {
      "searchTerm": "",
      "shouldSucceed": false
    }
  ]
}
```

**Features**:
- Supports array-based and object-based JSON
- Automatically converts JSON types to Java types
- Handles nested structures

---

### 3. CSVDataProvider

Reads test data from CSV files.

**Location**: `src/test/java/com/cjs/qa/utilities/CSVDataProvider.java`

**Usage**:

```java
import com.cjs.qa.utilities.CSVDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UserTests {
    
    @DataProvider(name = "userData")
    public Object[][] getUserData() {
        return CSVDataProvider.readCSV(
            "test-data/users.csv",
            true  // Skip header row
        );
    }
    
    @Test(dataProvider = "userData")
    public void testUser(String email, String password, String expectedResult) {
        // Test implementation
    }
}
```

**CSV File Format** (`test-data/users.csv`):

```csv
email,password,expectedResult,description
user1@test.com,password1,success,Valid user 1
user2@test.com,password2,success,Valid user 2
invalid@test.com,wrongpassword,failure,Invalid credentials
```

**Features**:
- Simple, human-readable format
- Supports header row detection
- Easy to edit in Excel or text editors

---

## File Location

All test data files should be placed in:

```
src/test/resources/test-data/
```

**Examples**:
- `src/test/resources/test-data/login-scenarios.json`
- `src/test/resources/test-data/search-queries.json`
- `src/test/resources/test-data/users.csv`
- `src/test/resources/test-data/login-scenarios.xlsx`

---

## Real Example: Updated DataDrivenTests

The `DataDrivenTests.java` class has been updated to use `JSONDataProvider`:

**Before** (hardcoded data):
```java
@DataProvider(name = "searchQueries")
public Object[][] searchQueriesProvider() {
    return new Object[][] {
        {"Selenium WebDriver", true},
        {"TestNG Framework", true},
        // ... more hardcoded values
    };
}
```

**After** (external data file):
```java
@DataProvider(name = "searchQueries")
public Object[][] searchQueriesProvider() {
    return JSONDataProvider.readJSONArray(
        "test-data/search-queries.json",
        "queries"
    );
}
```

**Benefits**:
- ✅ Test data can be updated without code changes
- ✅ Non-developers can update test data
- ✅ Easier to add new test cases

---

## Best Practices

### 1. Organize Test Data by Feature

```
test-data/
  ├── login/
  │   ├── valid-users.json
  │   ├── invalid-users.json
  │   └── edge-cases.csv
  ├── search/
  │   ├── search-queries.json
  │   └── search-filters.xlsx
  └── registration/
      └── registration-data.csv
```

### 2. Use Descriptive File Names

- ✅ `login-valid-users.json`
- ✅ `search-queries.json`
- ❌ `data1.json`
- ❌ `test.json`

### 3. Include Header Rows (CSV/Excel)

Always include header rows to make data self-documenting:

```csv
email,password,expectedResult,description
user1@test.com,password1,success,Valid user 1
```

### 4. Document Data Structure

Add comments or documentation explaining the data structure:

```json
{
  "_comment": "Login test scenarios - email, password, expected result",
  "loginScenarios": [
    {
      "email": "user1@test.com",
      "password": "password1",
      "expectedResult": "success"
    }
  ]
}
```

### 5. Keep Data Files in Version Control

✅ **DO**: Commit test data files to Git  
❌ **DON'T**: Include sensitive data (passwords, API keys)

For sensitive data, use:
- Google Cloud Secret Manager (already integrated)
- Environment variables
- Encrypted data files (not in this guide)

---

## Migration Guide

### Converting Existing Hardcoded DataProviders

**Step 1**: Create data file (JSON, CSV, or Excel)

**Step 2**: Update DataProvider method:

```java
// Before
@DataProvider(name = "myData")
public Object[][] getMyData() {
    return new Object[][] {
        {"value1", "value2"},
        {"value3", "value4"}
    };
}

// After
@DataProvider(name = "myData")
public Object[][] getMyData() {
    return JSONDataProvider.readJSONArray(
        "test-data/my-data.json",
        "data"
    );
}
```

**Step 3**: Test the change

```bash
./mvnw test -Dtest=MyTestClass
```

---

## Troubleshooting

### File Not Found

**Error**: `File not found: test-data/my-file.json`

**Solutions**:
1. Check file path is relative to `src/test/resources/`
2. Verify file exists in `src/test/resources/test-data/`
3. Check file name spelling (case-sensitive)

### Empty Data Array

**Error**: DataProvider returns empty array

**Solutions**:
1. Check JSON structure matches expected format
2. Verify array key name is correct
3. Check for syntax errors in JSON/CSV file

### Type Mismatch

**Error**: `ClassCastException` when reading data

**Solutions**:
1. Ensure data types match test method parameters
2. For Excel: Check cell formatting (text vs number)
3. For JSON: Verify JSON types match Java types

---

## Related Documentation

- **TestNG DataProviders**: https://testng.org/doc/documentation-main.html#parameters-dataproviders
- **Apache POI (Excel)**: https://poi.apache.org/
- **Gson (JSON)**: https://github.com/google/gson
- **Commons CSV**: https://commons.apache.org/proper/commons-csv/

---

## Examples in Codebase

- ✅ `DataDrivenTests.java` - Uses `JSONDataProvider` for search queries
- 📁 `src/test/resources/test-data/` - Sample data files
- 📁 `src/test/java/com/cjs/qa/utilities/` - Data provider utilities

---

**Questions?** Check the code examples or create a GitHub issue!

