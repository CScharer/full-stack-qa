# @SuppressWarnings Inventory

**Document Type**: Reference Document (Non-Living)  
**Last Updated**: 2026-01-24  
**Total Annotations**: 28 across 22 files

---

## Status and Summary

**Status**: ✅ **REVIEWED AND DOCUMENTED** - 28 @SuppressWarnings annotations remain across 22 files (reduced from 31)

**Summary**: After Item 2.2 review (January 16, 2026), 4 @SuppressWarnings were removed (2 bugs fixed, 2 unused code removed). All remaining 28 annotations have been reviewed and documented with explanatory comments explaining why they are necessary.

**Recent Changes (January 24, 2026)**:
- ✅ Added `HomePageTestsExample.java` - `PMD.ClassNamingConventions` (example file with intentional naming)

**Breakdown by Type**:

| Type | Count | Files | Notes |
|------|-------|-------|-------|
| `"unused"` | 7 | 7 files | All documented - required for API compatibility/Gson deserialization |
| `"unchecked"` | 3 | 3 files | All documented - necessary type casts from external APIs |
| `"rawtypes"` | 3 | 3 files | All documented - required by TestNG/Apache POI APIs |
| `"deprecation"` | 3 | 3 files | All documented - Commons CSV library limitation |
| `PMD.SingularField` | 3 | 1 file | All documented - design choice for performance |
| `PMD.ClassNamingConventions` | 1 | 1 file | All documented - example file with intentional naming |
| `PMD.DoNotExtendJavaLangThrowable` | 2 | 2 files | All documented - custom exception design |
| `PMD.GuardLogStatement` | 1 | 1 file | All documented - wrapper class design |
| `PMD.UnnecessaryImport` | 1 | 1 file | All documented - wildcard import for many classes |
| `java:S2068` (SonarQube) | 1 | 1 file | All documented - intentional placeholder |

**Files with @SuppressWarnings** (22 total):

1. **PMD Violations** (7 annotations) - ✅ All documented:
   - `src/test/java/com/cjs/qa/utilities/GuardedLogger.java` - `PMD.GuardLogStatement` (intentional - wrapper class handles guards internally)
   - `src/test/java/com/cjs/qa/core/QAException.java` - `PMD.DoNotExtendJavaLangThrowable` (custom exception design - documented)
   - `src/test/java/com/cjs/qa/utilities/QALogger.java` - `PMD.DoNotExtendJavaLangThrowable` (custom logging exception design - documented)
   - `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` - `PMD.SingularField` (3x - design choice for performance - documented)
   - `src/test/java/com/cjs/qa/ym/xml/objects/DataSet.java` - `PMD.UnnecessaryImport` (wildcard import used for many classes - documented)
   - `src/test/java/com/cjs/qa/junit/tests/HomePageTestsExample.java` - `PMD.ClassNamingConventions` (example file with intentional naming - documented)

2. **Java Compiler Warnings** (20 annotations) - ✅ All documented:
   - **"unused"** (7 annotations):
     - `AIHelper.java` (4x) - No-arg constructors for Gson deserialization (documented)
     - `GTWebinarDataTests.java` (1x) - Parameter reserved for future use (documented)
     - `DailyPollQuizPages.java` (1x) - Field reserved for future use (documented)
     - `PageObjectGenerator.java` (1x) - Method reserved for future use (documented)
     - `Processes.java` (1x) - Logger field reserved for future logging (documented)
     - `XML.java` (1x) - Constructor parameter reserved for future implementation (documented)
     - ~~`ISelenium.java`~~ - ✅ **REMOVED** (unused code removed)
     - ~~`SOAP.java`~~ - ✅ **REMOVED** (unused code removed)
   - **"unchecked"** (3 annotations):
     - `Page.java` (1x) - Type cast from executeJavaScript() (documented)
     - `SeleniumWebDriver.java` (1x) - Type cast for Chrome capabilities (documented)
     - `JavaHelpers.java` (1x) - Type cast in CollectionUtils.disjunction() (documented)
     - ~~`EveryoneSocial.java` (2x)~~ - ✅ **REMOVED** (bugs fixed: findElement → findElements)
   - **"rawtypes"** (3 annotations):
     - `GlobalRetryListener.java` (1x) - TestNG API limitation (documented)
     - `XLS.java` (1x) - Apache POI API limitation (documented)
     - `XLSX.java` (1x) - Apache POI API limitation (documented)
   - **"deprecation"** (3 annotations):
     - `SystemProcesses.java` (1x) - Commons CSV library limitation (documented in Item 2.1)
     - `CSVDataProvider.java` (1x) - Commons CSV library limitation (documented in Item 2.1)
     - `YMDataTests.java` (1x) - Commons CSV library limitation (documented in Item 2.1)
   - **SonarQube** (1 annotation):
     - `Atlassian.java` - `java:S2068` (intentional placeholder, not a credential - documented)

**Action Items**:
- [x] Audit all "unused" warnings to determine if they're intentional ✅ **COMPLETED**
- [x] Review and fix "unchecked" warnings where type safety can be improved ✅ **COMPLETED** (2 bugs fixed)
- [x] Review and fix "rawtypes" warnings where generics can be added ✅ **COMPLETED** (documented - API limitations)
- [x] Plan migration for "deprecation" warnings ✅ **COMPLETED** (documented - library limitation)
- [x] Review PMD.SingularField suppressions in XlsReader.java ✅ **COMPLETED** (documented)
- [x] Document intentional suppressions with comments explaining why ✅ **COMPLETED**

**Note**: The `PMD.GuardLogStatement` suppression in `GuardedLogger.java` is intentional and correct - it's the wrapper class that handles guards internally.

---

## Legend

### Java Compiler Warnings

| Type | Description | Typical Use Case |
|------|-------------|------------------|
| `"unused"` | Suppresses warnings about unused variables, parameters, or methods | Parameters required by interfaces/APIs but not used in implementation, fields reserved for future use, constructors for serialization |
| `"unchecked"` | Suppresses warnings about unchecked type conversions | Generic type casts that cannot be verified at compile time (e.g., casting to `List<String>` from raw types) |
| `"rawtypes"` | Suppresses warnings about raw type usage | Using generic types without type parameters (e.g., `List` instead of `List<String>`) |
| `"deprecation"` | Suppresses warnings about deprecated API usage | Using deprecated methods/classes that are still required for compatibility |

### PMD Rule Suppressions

| Type | Description | Typical Use Case |
|------|-------------|------------------|
| `PMD.ClassNamingConventions` | Suppresses PMD rule about class naming conventions | Example/documentation files that intentionally don't match Test* naming pattern |
| `PMD.GuardLogStatement` | Suppresses PMD rule requiring log statements to check log level before logging | Wrapper classes (like GuardedLogger) that handle guards internally |
| `PMD.DoNotExtendJavaLangThrowable` | Suppresses PMD rule against extending Throwable directly | Custom exception classes that extend Throwable for specific design reasons |
| `PMD.SingularField` | Suppresses PMD rule about fields that could be local variables | Fields that are reused across multiple methods as temporary variables |
| `PMD.UnnecessaryImport` | Suppresses PMD rule about unnecessary wildcard imports | Wildcard imports that are actually used for many classes from a package |

### SonarQube Rule Suppressions

| Type | Description | Typical Use Case |
|------|-------------|------------------|
| `java:S2068` | Suppresses SonarQube rule about hard-coded credentials | Intentional placeholder values that are not actual credentials |

---

## Complete Inventory Table

| # | File | Line | Type | Context | Notes |
|---|------|------|------|---------|-------|
| 1 | `src/test/java/com/cjs/qa/ym/xml/objects/DataSet.java` | 12 | `PMD.UnnecessaryImport` | Class-level | Wildcard import used for many classes from dataset package |
| 2 | `src/test/java/com/cjs/qa/utilities/GuardedLogger.java` | 23 | `PMD.GuardLogStatement` | Class-level | Intentional - wrapper class handles guards internally |
| 3 | `src/test/java/com/cjs/qa/core/QAException.java` | 10 | `PMD.DoNotExtendJavaLangThrowable` | Class-level | Custom exception design - extends Throwable |
| 4 | `src/test/java/com/cjs/qa/utilities/QALogger.java` | 5 | `PMD.DoNotExtendJavaLangThrowable` | Class-level | Custom logging exception design - extends Throwable |
| 5 | `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` | 44 | `PMD.SingularField` | Field: `sheet` | Reused across multiple methods as temporary variable |
| 6 | `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` | 47 | `PMD.SingularField` | Field: `row` | Reused across multiple methods as temporary variable |
| 7 | `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` | 50 | `PMD.SingularField` | Field: `cell` | Reused across multiple methods as temporary variable |
| 8 | `src/test/java/com/cjs/qa/junit/tests/HomePageTestsExample.java` | 24 | `PMD.ClassNamingConventions` | Class-level | Example file with intentional naming (doesn't match Test* pattern) |
| 9 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 159 | `"unused"` | Constructor: `ChatCompletionRequest()` | No-arg constructor for Gson deserialization |
| 9 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 196 | `"unused"` | Constructor: `ChatCompletionResponse()` | No-arg constructor for Gson deserialization |
| 10 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 213 | `"unused"` | Constructor: `Choice()` | No-arg constructor for Gson deserialization |
| 11 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 229 | `"unused"` | Constructor: `Message()` | No-arg constructor for Gson deserialization |
| 12 | `src/test/java/com/cjs/qa/selenium/Page.java` | 688 | `"unchecked"` | Variable: `bounds` | Type cast: `(List<String>) executeJavaScript(...)` |
| 13 | `src/test/java/com/cjs/qa/utilities/GlobalRetryListener.java` | 29 | `"rawtypes"` | Method parameter: `testClass` | Raw type in TestNG listener interface |
| 14 | `src/test/java/com/cjs/qa/utilities/PageObjectGenerator.java` | 756 | `"unused"` | Method: `getInputType()` | Reserved for future use |
| 15 | `src/test/java/com/cjs/qa/utilities/SystemProcesses.java` | 274 | `"deprecation"` | Variable: `format` | CSVFormat.builder().build() deprecated but still required |
| 16 | `src/test/java/com/cjs/qa/utilities/Processes.java` | 15 | `"unused"` | Field: `LOG` | Reserved for future logging - documented |
| 17 | `src/test/java/com/cjs/qa/utilities/CSVDataProvider.java` | 62 | `"deprecation"` | Variable: `formatWithHeader` | CSVFormat.builder().build() deprecated but still required - documented |
| 18 | `src/test/java/com/cjs/qa/utilities/JavaHelpers.java` | 168 | `"unchecked"` | Return type | Type cast in CollectionUtils.disjunction() - documented |
| 19 | `src/test/java/com/cjs/qa/atlassian/Atlassian.java` | 81 | `java:S2068` | Variable: `password` | Intentional placeholder (not a real credential) - documented |
| 20 | `src/test/java/com/cjs/qa/utilities/XML.java` | 77 | `"unused"` | Constructor parameter: `xml` | Parameter reserved for future implementation - documented |
| 21 | `src/test/java/com/cjs/qa/selenium/SeleniumWebDriver.java` | 387 | `"unchecked"` | Variable: `chromeCapabilities` | Type cast: `(Map<String, Object>) capabilities.getCapability(...)` - documented |
| 22 | `src/test/java/com/cjs/qa/microsoft/pages/DailyPollQuizPages.java` | 37 | `"unused"` | Field: `answersNeeded` | Reserved for future use - documented |
| 23 | `src/test/java/com/cjs/qa/microsoft/excel/xls/XLS.java` | 83 | `"rawtypes"` | Variable: `drawing` | Apache POI API uses raw types - documented |
| 24 | `src/test/java/com/cjs/qa/microsoft/excel/xlsx/XLSX.java` | 82 | `"rawtypes"` | Variable: `drawing` | Apache POI API uses raw types - documented |
| 25 | `src/test/java/com/cjs/qa/gt/GTWebinarDataTests.java` | 64 | `"unused"` | Method parameter: `dateTimeFrom` | Parameter reserved for future implementation - documented |
| 26 | `src/test/java/com/cjs/qa/ym/YMDataTests.java` | 1138 | `"deprecation"` | Variable: `format` | CSVFormat.builder().build() deprecated but still required - documented |
| ~~27~~ | ~~`src/test/java/com/cjs/qa/selenium/ISelenium.java`~~ | ~~86~~ | ~~`"unused"`~~ | ~~Variable: `screenshot`~~ | ✅ **REMOVED** - Unused code removed |
| ~~28~~ | ~~`src/test/java/com/cjs/qa/soap/SOAP.java`~~ | ~~125~~ | ~~`"unused"`~~ | ~~Variable: `soapMessageString`~~ | ✅ **REMOVED** - Unused code removed |
| ~~29~~ | ~~`src/test/java/com/cjs/qa/everyonesocial/EveryoneSocial.java`~~ | ~~99~~ | ~~`"unchecked"`~~ | ~~Variable: `webElementListShare`~~ | ✅ **REMOVED** - Bug fixed (findElement → findElements) |
| ~~30~~ | ~~`src/test/java/com/cjs/qa/everyonesocial/EveryoneSocial.java`~~ | ~~108~~ | ~~`"unchecked"`~~ | ~~Variable: `result`~~ | ✅ **REMOVED** - Bug fixed (findElement → findElements) |

---

## Summary Statistics

### By Type

| Type | Count | Percentage |
|------|-------|------------|
| `"unused"` | 7 | 25.9% |
| `"unchecked"` | 3 | 11.1% |
| `"rawtypes"` | 3 | 11.1% |
| `"deprecation"` | 3 | 11.1% |
| `PMD.SingularField` | 3 | 11.1% |
| `PMD.ClassNamingConventions` | 1 | 3.6% |
| `PMD.DoNotExtendJavaLangThrowable` | 2 | 7.1% |
| `PMD.GuardLogStatement` | 1 | 3.6% |
| `PMD.UnnecessaryImport` | 1 | 3.6% |
| `java:S2068` (SonarQube) | 1 | 3.6% |
| **Total** | **28** | **100%** |

### By Category

| Category | Count | Files |
|----------|-------|-------|
| **Java Compiler Warnings** | 20 | 13 files |
| **PMD Rule Suppressions** | 7 | 5 files |
| **SonarQube Rule Suppressions** | 1 | 1 file |
| **Total** | **28** | **22 files** |

### By File (Files with Multiple Annotations)

| File | Count | Types |
|------|-------|-------|
| `AIHelper.java` | 4 | `"unused"` (4x) |
| `XlsReader.java` | 3 | `PMD.SingularField` (3x) |

---

## Detailed Breakdown by Type

### "unused" (7 annotations) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses warnings about unused variables, parameters, or methods.

**Files**:
1. `AIHelper.java` (4x) - No-arg constructors for Gson deserialization ✅ **Documented**
2. `GTWebinarDataTests.java` (1x) - Method parameter reserved for future use ✅ **Documented**
3. `DailyPollQuizPages.java` (1x) - Field reserved for future use ✅ **Documented**
4. `PageObjectGenerator.java` (1x) - Method reserved for future use ✅ **Documented**
5. `Processes.java` (1x) - Logger field reserved for future logging ✅ **Documented**
6. `XML.java` (1x) - Constructor parameter reserved for future implementation ✅ **Documented**
7. ~~`ISelenium.java` (1x)~~ - ✅ **REMOVED** (unused code removed)
8. ~~`SOAP.java` (1x)~~ - ✅ **REMOVED** (unused code removed)

**Status**: ✅ All remaining suppressions are intentional and documented. All are required for API compatibility or future use.

---

### "unchecked" (3 annotations) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses warnings about unchecked type conversions.

**Files**:
1. `Page.java` (1x) - Type cast from `executeJavaScript()` return value ✅ **Documented**
2. `SeleniumWebDriver.java` (1x) - Type cast for Chrome capabilities Map ✅ **Documented**
3. `JavaHelpers.java` (1x) - Type cast in CollectionUtils.disjunction() ✅ **Documented**
4. ~~`EveryoneSocial.java` (2x)~~ - ✅ **REMOVED** (bugs fixed: findElement → findElements)

**Status**: ✅ All remaining suppressions are necessary type casts from external APIs and are documented. The bugs in EveryoneSocial.java were fixed.

---

### "rawtypes" (3 annotations) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses warnings about raw type usage (generic types without type parameters).

**Files**:
1. `GlobalRetryListener.java` (1x) - TestNG listener interface uses raw types ✅ **Documented**
2. `XLS.java` (1x) - Apache POI API uses raw types for Drawing ✅ **Documented**
3. `XLSX.java` (1x) - Apache POI API uses raw types for Drawing ✅ **Documented**

**Status**: ✅ All suppressions are due to external API limitations (TestNG, Apache POI) and are documented. Cannot be fixed without API changes.

---

### "deprecation" (3 annotations) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses warnings about deprecated API usage.

**Files**:
1. `SystemProcesses.java` (1x) - CSVFormat.builder().build() deprecated ✅ **Documented**
2. `CSVDataProvider.java` (1x) - CSVFormat.builder().build() deprecated ✅ **Documented**
3. `YMDataTests.java` (1x) - CSVFormat.builder().build() deprecated ✅ **Documented**

**Status**: ✅ All suppressions are documented. The `.build()` method is deprecated in Commons CSV 1.14.1+ but still required by the library. See Item 2.1 for details.

---

### PMD.SingularField (3 annotations) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses PMD rule about fields that could be local variables.

**Files**:
1. `XlsReader.java` (3x) - Fields `sheet`, `row`, and `cell` reused across methods ✅ **Documented**

**Status**: ✅ All suppressions are documented. These fields are intentionally reused as temporary variables across multiple methods to avoid repeated lookups, improving performance. This is a design choice.

---

### PMD.ClassNamingConventions (1 annotation) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses PMD rule about class naming conventions (classes should match `^Test.*$|^[A-Z][a-zA-Z0-9]*Test(s|Case)?$` pattern).

**Files**:
1. `HomePageTestsExample.java` (1x) - Example file with intentional naming ✅ **Documented**

**Status**: ✅ Suppression is documented. This is an example file demonstrating EnvironmentConfig usage. The class name intentionally doesn't match the Test* pattern because it's an example/documentation file, not a standard test class.

---

### PMD.DoNotExtendJavaLangThrowable (2 annotations) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses PMD rule against extending Throwable directly.

**Files**:
1. `QAException.java` (1x) - Custom exception design ✅ **Documented**
2. `QALogger.java` (1x) - Custom logging exception design ✅ **Documented**

**Status**: ✅ All suppressions are documented. These are intentional design choices for the QA framework. Documentation added explaining the custom exception design.

---

### PMD.GuardLogStatement (1 annotation) ✅ **REVIEWED AND DOCUMENTED**

**Purpose**: Suppresses PMD rule requiring log statements to check log level before logging.

**Files**:
1. `GuardedLogger.java` (1x) - Intentional - this wrapper class handles guards internally ✅ **Documented**

**Status**: ✅ Suppression is documented. This is correct and intentional. The GuardedLogger class performs guard checks internally, so the suppression is appropriate.

---

### PMD.UnnecessaryImport (1 annotation)

**Purpose**: Suppresses PMD rule about unnecessary wildcard imports.

**Files**:
1. `DataSet.java` (1x) - Wildcard import used for many classes from dataset package

**Recommendation**: This is intentional. The wildcard import is used for many classes (StrEmail, StrAddress1, StrCity, etc.). Suppressing avoids listing all individual imports.

---

### java:S2068 (SonarQube) (1 annotation)

**Purpose**: Suppresses SonarQube rule about hard-coded credentials.

**Files**:
1. `Atlassian.java` (1x) - Intentional placeholder (not a real credential)

**Recommendation**: This is correct. The code explicitly uses "PLACEHOLDER_NOT_A_REAL_PASSWORD" as a placeholder value, not an actual credential. The suppression is appropriate.

---

## Recommendations

### ✅ Completed (January 16, 2026)

1. ✅ **Review "unused" warnings** (11 → 7 annotations):
   - ✅ Verified all remaining are needed for API compatibility
   - ✅ Removed 2 unused code instances (ISelenium.java, SOAP.java)
   - ✅ Documented all intentional unused items with comments

2. ✅ **Review "unchecked" warnings** (5 → 3 annotations):
   - ✅ Fixed 2 bugs in EveryoneSocial.java (findElement → findElements)
   - ✅ Documented all remaining necessary type casts

3. ✅ **Review "deprecation" warnings** (3 annotations):
   - ✅ All documented - Commons CSV library limitation
   - ✅ See Item 2.1 for details

4. ✅ **Review "rawtypes" warnings** (3 annotations):
   - ✅ All documented - External API limitations (TestNG, Apache POI)

5. ✅ **Document intentional suppressions**:
   - ✅ All PMD suppressions documented
   - ✅ All Java compiler warnings documented
   - ✅ All SonarQube suppressions documented

### Status

**All recommendations completed.** All remaining 27 @SuppressWarnings annotations are legitimate, necessary, and documented with explanatory comments.

---

## Notes

- **Total Annotations**: 28 (reduced from 31 on January 16, 2026, added 1 on January 24, 2026)
- **Total Files**: 22 (reduced from 23 on January 16, 2026, added 1 on January 24, 2026)
- **Most Common Type**: `"unused"` (7 annotations, 25.0%)
- **Files with Multiple Annotations**: 2 files (AIHelper.java: 4, XlsReader.java: 3)
- **Removed Annotations**: 4 (2 bugs fixed, 2 unused code removed)
- **Added Annotations**: 1 (`HomePageTestsExample.java` - PMD.ClassNamingConventions)
- **Documentation Status**: ✅ All 28 remaining annotations are documented

**Recent Updates (January 24, 2026)**:
- ✅ Added `HomePageTestsExample.java` - `PMD.ClassNamingConventions` (example file with intentional naming)

---

**Document Status**: Reference document - updated January 24, 2026  
**Note**: This document is the authoritative source for @SuppressWarnings information. All annotations have been reviewed and documented. The related section has been removed from `docs/work/20260103_REMAINING_WORK_SUMMARY.md` and consolidated here.
