# @SuppressWarnings Inventory

**Document Type**: Reference Document (Non-Living)  
**Last Updated**: 2025-12-27  
**Total Annotations**: 31 across 23 files

---

## Status and Summary

**Status**: ⚠️ **TO REVIEW** - 31 @SuppressWarnings annotations remain across 23 files

**Summary**: After the GuardedLogger migration, there are still 31 @SuppressWarnings annotations remaining. These should be reviewed to determine if they can be resolved via code changes or if they are legitimate suppressions.

**Breakdown by Type**:

| Type | Count | Files | Notes |
|------|-------|-------|-------|
| `"unused"` | 11 | 8 files | Unused variables/parameters - may be intentional for API compatibility |
| `"unchecked"` | 5 | 4 files | Generic type warnings - may need type safety improvements |
| `"rawtypes"` | 3 | 3 files | Raw type usage - may need generic type improvements |
| `"deprecation"` | 3 | 3 files | Deprecated API usage - may need migration to newer APIs |
| `PMD.SingularField` | 3 | 1 file | XlsReader.java - design choice (fields could be local variables) |
| `PMD.DoNotExtendJavaLangThrowable` | 2 | 2 files | QAException.java, QALogger.java - custom exception design |
| `PMD.GuardLogStatement` | 1 | 1 file | GuardedLogger.java - intentional (wrapper class) |
| `PMD.UnnecessaryImport` | 1 | 1 file | DataSet.java - wildcard import used for many classes |
| `java:S2068` (SonarQube) | 1 | 1 file | Atlassian.java - intentional placeholder (not a credential) |

**Files with @SuppressWarnings** (23 total):

1. **PMD Violations** (6 annotations):
   - `src/test/java/com/cjs/qa/utilities/GuardedLogger.java` - `PMD.GuardLogStatement` (intentional - wrapper class)
   - `src/test/java/com/cjs/qa/core/QAException.java` - `PMD.DoNotExtendJavaLangThrowable` (custom exception design)
   - `src/test/java/com/cjs/qa/utilities/QALogger.java` - `PMD.DoNotExtendJavaLangThrowable` (custom logging exception design)
   - `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` - `PMD.SingularField` (3x - design choice)
   - `src/test/java/com/cjs/qa/ym/xml/objects/DataSet.java` - `PMD.UnnecessaryImport` (wildcard import used for many classes)

2. **Java Compiler Warnings** (24 annotations):
   - **"unused"** (11 annotations):
     - `AIHelper.java` (4x)
     - `GTWebinarDataTests.java` (1x)
     - `DailyPollQuizPages.java` (1x)
     - `ISelenium.java` (1x)
     - `SOAP.java` (1x)
     - `PageObjectGenerator.java` (1x)
     - `Processes.java` (1x)
     - `XML.java` (1x)
   - **"unchecked"** (5 annotations):
     - `EveryonesSocial.java` (2x)
     - `Page.java` (1x)
     - `SeleniumWebDriver.java` (1x)
     - `JavaHelpers.java` (1x)
   - **"rawtypes"** (3 annotations):
     - `GlobalRetryListener.java` (1x)
     - `XLS.java` (1x)
     - `XLSX.java` (1x)
   - **"deprecation"** (3 annotations):
     - `SystemProcesses.java` (1x)
     - `CSVDataProvider.java` (1x)
     - `YMDataTests.java` (1x)
   - **SonarQube** (1 annotation):
     - `Atlassian.java` - `java:S2068` (intentional placeholder, not a credential - see code comments)

**Action Items**:
- [ ] Audit all "unused" warnings to determine if they're intentional
- [ ] Review and fix "unchecked" warnings where type safety can be improved
- [ ] Review and fix "rawtypes" warnings where generics can be added
- [ ] Plan migration for "deprecation" warnings
- [ ] Review PMD.SingularField suppressions in XlsReader.java
- [ ] Document intentional suppressions with comments explaining why

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
| 1 | `src/test/java/com/cjs/qa/ym/xml/objects/DataSet.java` | 13 | `PMD.UnnecessaryImport` | Class-level | Wildcard import used for many classes from dataset package |
| 2 | `src/test/java/com/cjs/qa/utilities/GuardedLogger.java` | 23 | `PMD.GuardLogStatement` | Class-level | Intentional - wrapper class handles guards internally |
| 3 | `src/test/java/com/cjs/qa/core/QAException.java` | 10 | `PMD.DoNotExtendJavaLangThrowable` | Class-level | Custom exception design - extends Throwable |
| 4 | `src/test/java/com/cjs/qa/utilities/QALogger.java` | 5 | `PMD.DoNotExtendJavaLangThrowable` | Class-level | Custom logging exception design - extends Throwable |
| 5 | `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` | 44 | `PMD.SingularField` | Field: `sheet` | Reused across multiple methods as temporary variable |
| 6 | `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` | 47 | `PMD.SingularField` | Field: `row` | Reused across multiple methods as temporary variable |
| 7 | `src/test/java/com/cjs/qa/microsoft/utilities/XlsReader.java` | 50 | `PMD.SingularField` | Field: `cell` | Reused across multiple methods as temporary variable |
| 8 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 159 | `"unused"` | Constructor: `ChatCompletionRequest()` | No-arg constructor for Gson deserialization |
| 9 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 196 | `"unused"` | Constructor: `ChatCompletionResponse()` | No-arg constructor for Gson deserialization |
| 10 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 213 | `"unused"` | Constructor: `Choice()` | No-arg constructor for Gson deserialization |
| 11 | `src/test/java/com/cjs/qa/utilities/AIHelper.java` | 229 | `"unused"` | Constructor: `Message()` | No-arg constructor for Gson deserialization |
| 12 | `src/test/java/com/cjs/qa/selenium/Page.java` | 688 | `"unchecked"` | Variable: `bounds` | Type cast: `(List<String>) executeJavaScript(...)` |
| 13 | `src/test/java/com/cjs/qa/utilities/GlobalRetryListener.java` | 29 | `"rawtypes"` | Method parameter: `testClass` | Raw type in TestNG listener interface |
| 14 | `src/test/java/com/cjs/qa/utilities/PageObjectGenerator.java` | 756 | `"unused"` | Method: `getInputType()` | Reserved for future use |
| 15 | `src/test/java/com/cjs/qa/utilities/SystemProcesses.java` | 274 | `"deprecation"` | Variable: `format` | CSVFormat.builder().build() deprecated but still required |
| 16 | `src/test/java/com/cjs/qa/selenium/ISelenium.java` | 86 | `"unused"` | Variable: `screenshot` | Screenshot captured but not used (commented out usage) |
| 17 | `src/test/java/com/cjs/qa/utilities/Processes.java` | 15 | `"unused"` | Field: `LOG` | Reserved for future logging |
| 18 | `src/test/java/com/cjs/qa/utilities/CSVDataProvider.java` | 62 | `"deprecation"` | Variable: `formatWithHeader` | CSVFormat.builder().build() deprecated but still required |
| 19 | `src/test/java/com/cjs/qa/soap/SOAP.java` | 125 | `"unused"` | Variable: `soapMessageString` | Variable created but not used |
| 20 | `src/test/java/com/cjs/qa/utilities/JavaHelpers.java` | 168 | `"unchecked"` | Return type | Type cast in CollectionUtils.disjunction() |
| 21 | `src/test/java/com/cjs/qa/atlassian/Atlassian.java` | 81 | `java:S2068` | Variable: `password` | Intentional placeholder (not a real credential) |
| 22 | `src/test/java/com/cjs/qa/utilities/XML.java` | 77 | `"unused"` | Constructor parameter: `xml` | Parameter reserved for future implementation |
| 23 | `src/test/java/com/cjs/qa/selenium/SeleniumWebDriver.java` | 385 | `"unchecked"` | Variable: `chromeCapabilities` | Type cast: `(Map<String, Object>) capabilities.getCapability(...)` |
| 24 | `src/test/java/com/cjs/qa/microsoft/pages/DailyPollQuizPages.java` | 37 | `"unused"` | Field: `answersNeeded` | Reserved for future use |
| 25 | `src/test/java/com/cjs/qa/microsoft/excel/xls/XLS.java` | 83 | `"rawtypes"` | Variable: `drawing` | Apache POI API uses raw types |
| 26 | `src/test/java/com/cjs/qa/microsoft/excel/xlsx/XLSX.java` | 82 | `"rawtypes"` | Variable: `drawing` | Apache POI API uses raw types |
| 27 | `src/test/java/com/cjs/qa/gt/GTWebinarDataTests.java` | 64 | `"unused"` | Method parameter: `dateTimeFrom` | Parameter reserved for future implementation |
| 28 | `src/test/java/com/cjs/qa/everyonesocial/EveryoneSocial.java` | 99 | `"unchecked"` | Variable: `webElementListShare` | Type cast: `(List<WebElement>) getWebDriver().findElement(...)` |
| 29 | `src/test/java/com/cjs/qa/everyonesocial/EveryoneSocial.java` | 108 | `"unchecked"` | Variable: `result` | Type cast: `(List<WebElement>) getWebDriver().findElement(...)` |
| 30 | `src/test/java/com/cjs/qa/ym/YMDataTests.java` | 1135 | `"deprecation"` | Variable: `format` | CSVFormat.builder().build() deprecated but still required |

---

## Summary Statistics

### By Type

| Type | Count | Percentage |
|------|-------|------------|
| `"unused"` | 11 | 35.5% |
| `"unchecked"` | 5 | 16.1% |
| `"rawtypes"` | 3 | 9.7% |
| `"deprecation"` | 3 | 9.7% |
| `PMD.SingularField` | 3 | 9.7% |
| `PMD.DoNotExtendJavaLangThrowable` | 2 | 6.5% |
| `PMD.GuardLogStatement` | 1 | 3.2% |
| `PMD.UnnecessaryImport` | 1 | 3.2% |
| `java:S2068` (SonarQube) | 1 | 3.2% |
| **Total** | **31** | **100%** |

### By Category

| Category | Count | Files |
|----------|-------|-------|
| **Java Compiler Warnings** | 24 | 15 files |
| **PMD Rule Suppressions** | 6 | 4 files |
| **SonarQube Rule Suppressions** | 1 | 1 file |
| **Total** | **31** | **23 files** |

### By File (Files with Multiple Annotations)

| File | Count | Types |
|------|-------|-------|
| `AIHelper.java` | 4 | `"unused"` (4x) |
| `XlsReader.java` | 3 | `PMD.SingularField` (3x) |
| `EveryoneSocial.java` | 2 | `"unchecked"` (2x) |

---

## Detailed Breakdown by Type

### "unused" (11 annotations)

**Purpose**: Suppresses warnings about unused variables, parameters, or methods.

**Files**:
1. `AIHelper.java` (4x) - No-arg constructors for Gson deserialization
2. `GTWebinarDataTests.java` (1x) - Method parameter reserved for future use
3. `DailyPollQuizPages.java` (1x) - Field reserved for future use
4. `ISelenium.java` (1x) - Screenshot variable (commented out usage)
5. `SOAP.java` (1x) - Variable created but not used
6. `PageObjectGenerator.java` (1x) - Method reserved for future use
7. `Processes.java` (1x) - Logger field reserved for future logging
8. `XML.java` (1x) - Constructor parameter reserved for future implementation

**Recommendation**: Review each to determine if they're truly intentional or can be removed/refactored.

---

### "unchecked" (5 annotations)

**Purpose**: Suppresses warnings about unchecked type conversions.

**Files**:
1. `Page.java` (1x) - Type cast from `executeJavaScript()` return value
2. `SeleniumWebDriver.java` (1x) - Type cast for Chrome capabilities Map
3. `JavaHelpers.java` (1x) - Type cast in CollectionUtils.disjunction()
4. `EveryoneSocial.java` (2x) - Type casts for WebElement lists from findElement()

**Recommendation**: Consider adding proper generic types or using type-safe alternatives where possible.

---

### "rawtypes" (3 annotations)

**Purpose**: Suppresses warnings about raw type usage (generic types without type parameters).

**Files**:
1. `GlobalRetryListener.java` (1x) - TestNG listener interface uses raw types
2. `XLS.java` (1x) - Apache POI API uses raw types for Drawing
3. `XLSX.java` (1x) - Apache POI API uses raw types for Drawing

**Recommendation**: These are due to external API limitations (TestNG, Apache POI). May not be fixable without API changes.

---

### "deprecation" (3 annotations)

**Purpose**: Suppresses warnings about deprecated API usage.

**Files**:
1. `SystemProcesses.java` (1x) - CSVFormat.builder().build() deprecated
2. `CSVDataProvider.java` (1x) - CSVFormat.builder().build() deprecated
3. `YMDataTests.java` (1x) - CSVFormat.builder().build() deprecated

**Recommendation**: Plan migration to newer Commons CSV API (CSVParser.parse() is recommended).

---

### PMD.SingularField (3 annotations)

**Purpose**: Suppresses PMD rule about fields that could be local variables.

**Files**:
1. `XlsReader.java` (3x) - Fields `sheet`, `row`, and `cell` reused across methods

**Recommendation**: Review design - these fields are intentionally reused as temporary variables across multiple methods. This is a design choice.

---

### PMD.DoNotExtendJavaLangThrowable (2 annotations)

**Purpose**: Suppresses PMD rule against extending Throwable directly.

**Files**:
1. `QAException.java` (1x) - Custom exception design
2. `QALogger.java` (1x) - Custom logging exception design

**Recommendation**: These are intentional design choices. Consider documenting why Throwable is extended instead of Exception.

---

### PMD.GuardLogStatement (1 annotation)

**Purpose**: Suppresses PMD rule requiring log statements to check log level before logging.

**Files**:
1. `GuardedLogger.java` (1x) - Intentional - this wrapper class handles guards internally

**Recommendation**: This is correct and intentional. The GuardedLogger class performs guard checks internally, so the suppression is appropriate.

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

### High Priority

1. **Review "unused" warnings** (11 annotations):
   - Verify if unused parameters/fields are truly needed for API compatibility
   - Consider removing or refactoring if not needed
   - Document intentional unused items with comments

2. **Review "unchecked" warnings** (5 annotations):
   - Evaluate if type safety can be improved
   - Consider using type-safe alternatives where possible
   - Document why unchecked casts are necessary

### Medium Priority

3. **Plan "deprecation" migration** (3 annotations):
   - All three are for Commons CSV API
   - Plan migration to `CSVParser.parse()` as recommended
   - Test thoroughly after migration

4. **Review "rawtypes" warnings** (3 annotations):
   - Two are due to Apache POI API limitations (may not be fixable)
   - One is due to TestNG listener interface (may not be fixable)
   - Document why raw types are necessary

### Low Priority

5. **Document intentional suppressions**:
   - PMD.GuardLogStatement (GuardedLogger.java) - Already documented
   - PMD.DoNotExtendJavaLangThrowable (QAException.java, QALogger.java) - Consider adding design rationale
   - PMD.SingularField (XlsReader.java) - Already has comments explaining design choice
   - PMD.UnnecessaryImport (DataSet.java) - Already has comment explaining rationale
   - java:S2068 (Atlassian.java) - Already has extensive comments

---

## Notes

- **Total Annotations**: 31
- **Total Files**: 23
- **Most Common Type**: `"unused"` (11 annotations, 35.5%)
- **Files with Multiple Annotations**: 3 files (AIHelper.java: 4, XlsReader.java: 3, EveryoneSocial.java: 2)

---

**Document Status**: Reference document - not actively maintained  
**Note**: This document is the authoritative source for @SuppressWarnings information. The related section has been removed from `docs/work/20251225_REMAINING_WORK_SUMMARY.md` and consolidated here.
