# Concurrent Test Data Isolation Plan

**Date**: January 17, 2026  
**Status**: 📋 Planning  
**Goal**: Ensure wizard tests from multiple frameworks can run simultaneously without data conflicts

---

## 📋 Executive Summary

When wizard tests from multiple frameworks (Cypress, Playwright, Robot Framework, Java/Selenide, Java/Selenium) run concurrently, they currently use the same test data pattern with `Date.now()` timestamps. This can cause conflicts if tests execute at the same millisecond or if data validation relies on unique identifiers.

This document outlines strategies and implementation plans to ensure each framework generates unique test data that won't conflict with other frameworks running simultaneously.

---

## 🔍 Problem Analysis

### Current Test Data Pattern

**Cypress & Playwright** currently use:
```typescript
// Company
name: `Test Company ${Date.now()}`

// Contact
first_name: `TestFirst${Date.now()}`
last_name: `TestLast${Date.now()}`

// Client
name: `Test Client ${Date.now()}`
```

### Conflict Scenarios

1. **Timestamp Collision**
   - Multiple frameworks call `Date.now()` in the same millisecond
   - Result: Identical test data values
   - Impact: If tests accidentally create data (instead of canceling), database unique constraints could fail

2. **API Verification Conflicts**
   - Tests verify entity counts haven't changed
   - If two frameworks use same timestamp and both accidentally create data, counts will be off
   - Impact: False test failures

3. **Database Query Conflicts**
   - Tests query for entities with specific names
   - If names collide, queries may return wrong results
   - Impact: Incorrect test assertions

4. **Parallel Execution in CI/CD**
   - CI/CD pipelines may run multiple framework tests in parallel
   - All frameworks use same database/API
   - Impact: High probability of conflicts

### Current Test Behavior

**Important Note**: The wizard tests are designed to **cancel** forms, not submit them. However:
- Tests still populate form fields with data
- If a test fails or is interrupted, data might be created
- Defensive programming requires unique data even for canceled forms
- Future tests might actually create data

---

## 🎯 Solution Strategies

### Strategy 1: Framework Prefix (Recommended)

**Approach**: Prefix all test data with framework identifier

**Format**: `{FRAMEWORK}_{BASE_DATA}_{TIMESTAMP}`

**Examples**:
- Cypress: `Cypress_Test Company_1737123456789`
- Playwright: `Playwright_Test Company_1737123456789`
- Robot Framework: `Robot_Test Company_1737123456789`
- Java/Selenide: `Selenide_Test Company_1737123456789`
- Java/Selenium: `Selenium_Test Company_1737123456789`

**Pros**:
- ✅ Simple to implement
- ✅ Easy to identify which framework created data (if needed)
- ✅ Low collision probability (framework + timestamp)
- ✅ Human-readable in logs/debugging

**Cons**:
- ⚠️ Slightly longer test data strings
- ⚠️ Need to standardize framework names

---

### Strategy 2: UUID-Based Unique IDs

**Approach**: Use UUIDs or similar unique identifiers

**Format**: `{BASE_DATA}_{UUID}`

**Examples**:
- `Test Company_a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- `TestFirst_123e4567-e89b-12d3-a456-426614174000`

**Pros**:
- ✅ Guaranteed uniqueness
- ✅ No collision risk
- ✅ Standard approach

**Cons**:
- ⚠️ Very long strings
- ⚠️ Not human-readable
- ⚠️ Harder to debug
- ⚠️ May exceed field length limits

---

### Strategy 3: Process ID + Timestamp

**Approach**: Combine process ID with timestamp

**Format**: `{BASE_DATA}_{PID}_{TIMESTAMP}`

**Examples**:
- `Test Company_12345_1737123456789`
- `TestFirst_67890_1737123456789`

**Pros**:
- ✅ Good uniqueness (process ID + timestamp)
- ✅ Shorter than UUID
- ✅ Still readable

**Cons**:
- ⚠️ Process IDs can be reused
- ⚠️ Still small collision risk if same PID + same millisecond

---

### Strategy 4: Test Run ID + Timestamp

**Approach**: Generate unique test run ID at suite start, use throughout

**Format**: `{BASE_DATA}_{RUN_ID}_{TIMESTAMP}`

**Examples**:
- `Test Company_RUN_abc123_1737123456789`
- `TestFirst_RUN_abc123_1737123456789`

**Pros**:
- ✅ Unique per test run
- ✅ All tests in same run share same ID (good for grouping)
- ✅ Good for debugging (can filter by run ID)

**Cons**:
- ⚠️ Need to generate and store run ID
- ⚠️ Slightly more complex implementation

---

### Strategy 5: Hybrid Approach (Recommended for Production)

**Approach**: Framework prefix + Process/Thread ID + Timestamp + Random suffix

**Format**: `{FRAMEWORK}_{BASE_DATA}_{PID}_{TIMESTAMP}_{RANDOM}`

**Examples**:
- `Cypress_Test Company_12345_1737123456789_a7b3`
- `Playwright_Test Company_67890_1737123456789_c9d1`

**Pros**:
- ✅ Maximum uniqueness
- ✅ Framework identification
- ✅ Process identification
- ✅ Timestamp for ordering
- ✅ Random suffix for extra safety

**Cons**:
- ⚠️ Longest strings
- ⚠️ Most complex implementation

---

## ✅ Recommended Solution: Framework Prefix + UUID4

**Selected Strategy**: **Framework Prefix + UUID4 (without dashes)**

**Rationale**:
- ✅ **Guaranteed uniqueness** - UUID4 provides cryptographic randomness
- ✅ **No collision risk** - UUID4 has 122 bits of randomness (2^122 possible values)
- ✅ **Framework identification** - Prefix allows easy identification of test data source
- ✅ **Shorter than full UUID** - Remove dashes to save 4 characters
- ✅ **Standard approach** - UUID4 is widely supported across all frameworks
- ✅ **No timestamp dependency** - Works even if system clocks are out of sync

**Format**: `{FRAMEWORK}_{BASE_DATA}_{UUID4}`

**UUID4 Format**: 32 hexadecimal characters (UUID without dashes)

**Examples**:
- `Cypress_Test Company_a1b2c3d4e5f6789012345678901234ab`
- `Playwright_Test Company_b2c3d4e5f6789012345678901234abcd`
- `Robot_Test Company_c3d4e5f6789012345678901234abcdef12`
- `Selenide_Test Company_d4e5f6789012345678901234abcdef1234`
- `Selenium_Test Company_e5f6789012345678901234abcdef123456`

---

## 🛠️ Implementation Plan

### Phase 1: Create Test Data Generation Utilities

#### 1.1 Cypress Utility

**File**: `cypress/cypress/support/test-data-generator.ts`

```typescript
/**
 * Test Data Generator for Cypress
 * Generates unique test data with framework prefix and UUID4
 */

const FRAMEWORK_PREFIX = 'Cypress';

/**
 * Generate UUID4 without dashes
 */
function generateUUID4(): string {
  // Use crypto.randomUUID() if available (Node.js 14.17.0+)
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID().replace(/-/g, '');
  }
  // Fallback for older environments
  return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

/**
 * Generate unique company name
 */
export function generateCompanyName(): string {
  return `${FRAMEWORK_PREFIX}_Test Company_${generateUUID4()}`;
}

/**
 * Generate unique contact names
 */
export function generateContactNames(): { first_name: string; last_name: string } {
  const uuid = generateUUID4();
  return {
    first_name: `${FRAMEWORK_PREFIX}_TestFirst_${uuid}`,
    last_name: `${FRAMEWORK_PREFIX}_TestLast_${uuid}`,
  };
}

/**
 * Generate unique client name
 */
export function generateClientName(): string {
  return `${FRAMEWORK_PREFIX}_Test Client_${generateUUID4()}`;
}
```

#### 1.2 Playwright Utility

**File**: `playwright/tests/utils/test-data-generator.ts`

```typescript
/**
 * Test Data Generator for Playwright
 * Generates unique test data with framework prefix and UUID4
 */

import { randomUUID } from 'crypto';

const FRAMEWORK_PREFIX = 'Playwright';

/**
 * Generate UUID4 without dashes
 */
function generateUUID4(): string {
  return randomUUID().replace(/-/g, '');
}

/**
 * Generate unique company name
 */
export function generateCompanyName(): string {
  return `${FRAMEWORK_PREFIX}_Test Company_${generateUUID4()}`;
}

/**
 * Generate unique contact names
 */
export function generateContactNames(): { first_name: string; last_name: string } {
  const uuid = generateUUID4();
  return {
    first_name: `${FRAMEWORK_PREFIX}_TestFirst_${uuid}`,
    last_name: `${FRAMEWORK_PREFIX}_TestLast_${uuid}`,
  };
}

/**
 * Generate unique client name
 */
export function generateClientName(): string {
  return `${FRAMEWORK_PREFIX}_Test Client_${generateUUID4()}`;
}
```

#### 1.3 Robot Framework Utility

**File**: `src/test/robot/resources/TestDataGenerator.py`

```python
"""
Test Data Generator for Robot Framework
Generates unique test data with framework prefix and UUID4
"""
import uuid

FRAMEWORK_PREFIX = 'Robot'

def generate_uuid4():
    """Generate UUID4 without dashes"""
    return uuid.uuid4().hex

def generate_company_name():
    """Generate unique company name"""
    return f"{FRAMEWORK_PREFIX}_Test Company_{generate_uuid4()}"

def generate_contact_names():
    """Generate unique contact names"""
    uuid_str = generate_uuid4()
    return {
        'first_name': f"{FRAMEWORK_PREFIX}_TestFirst_{uuid_str}",
        'last_name': f"{FRAMEWORK_PREFIX}_TestLast_{uuid_str}"
    }

def generate_client_name():
    """Generate unique client name"""
    return f"{FRAMEWORK_PREFIX}_Test Client_{generate_uuid4()}"
```

#### 1.4 Java/Selenide Utility

**File**: `src/test/java/com/cjs/qa/junit/utilities/TestDataGenerator.java`

```java
package com.cjs.qa.junit.utilities;

import java.util.UUID;

/**
 * Test Data Generator for Java/Selenide
 * Generates unique test data with framework prefix and UUID4
 */
public class TestDataGenerator {
    private static final String FRAMEWORK_PREFIX = "Selenide";

    /**
     * Generate UUID4 without dashes
     */
    private static String generateUUID4() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate unique company name
     */
    public static String generateCompanyName() {
        return FRAMEWORK_PREFIX + "_Test Company_" + generateUUID4();
    }

    /**
     * Generate unique contact names
     */
    public static ContactNames generateContactNames() {
        String uuid = generateUUID4();
        return new ContactNames(
            FRAMEWORK_PREFIX + "_TestFirst_" + uuid,
            FRAMEWORK_PREFIX + "_TestLast_" + uuid
        );
    }

    /**
     * Generate unique client name
     */
    public static String generateClientName() {
        return FRAMEWORK_PREFIX + "_Test Client_" + generateUUID4();
    }

    /**
     * Contact names holder class
     */
    public static class ContactNames {
        public final String first_name;
        public final String last_name;

        public ContactNames(String first_name, String last_name) {
            this.first_name = first_name;
            this.last_name = last_name;
        }
    }
}
```

#### 1.5 Java/Selenium Utility

**File**: `src/test/java/com/cjs/qa/junit/utilities/selenium/TestDataGenerator.java`

```java
package com.cjs.qa.junit.utilities.selenium;

import java.util.UUID;

/**
 * Test Data Generator for Java/Selenium
 * Generates unique test data with framework prefix and UUID4
 */
public class TestDataGenerator {
    private static final String FRAMEWORK_PREFIX = "Selenium";

    /**
     * Generate UUID4 without dashes
     */
    private static String generateUUID4() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate unique company name
     */
    public static String generateCompanyName() {
        return FRAMEWORK_PREFIX + "_Test Company_" + generateUUID4();
    }

    /**
     * Generate unique contact names
     */
    public static ContactNames generateContactNames() {
        String uuid = generateUUID4();
        return new ContactNames(
            FRAMEWORK_PREFIX + "_TestFirst_" + uuid,
            FRAMEWORK_PREFIX + "_TestLast_" + uuid
        );
    }

    /**
     * Generate unique client name
     */
    public static String generateClientName() {
        return FRAMEWORK_PREFIX + "_Test Client_" + generateUUID4();
    }

    /**
     * Contact names holder class
     */
    public static class ContactNames {
        public final String first_name;
        public final String last_name;

        public ContactNames(String first_name, String last_name) {
            this.first_name = first_name;
            this.last_name = last_name;
        }
    }
}
```

---

### Phase 2: Update Existing Tests

#### 2.1 Update Cypress Tests

**File**: `cypress/cypress/e2e/wizard.cy.ts`

**Before**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  // ...
};
```

**After**:
```typescript
import { generateCompanyName, generateContactNames, generateClientName } from '../support/test-data-generator';

// In test_companies
const testData = {
  name: generateCompanyName(),
  // ... rest of data
};

// In test_contacts
const contactNames = generateContactNames();
const testData = {
  first_name: contactNames.first_name,
  last_name: contactNames.last_name,
  // ... rest of data
};

// In test_clients
const testData = {
  name: generateClientName(),
};
```

#### 2.2 Update Playwright Tests

**File**: `playwright/tests/wizard.spec.ts`

**Before**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  // ...
};
```

**After**:
```typescript
import { generateCompanyName, generateContactNames, generateClientName } from './utils/test-data-generator';

// In test_companies
const testData = {
  name: generateCompanyName(),
  // ... rest of data
};

// In test_contacts
const contactNames = generateContactNames();
const testData = {
  first_name: contactNames.first_name,
  last_name: contactNames.last_name,
  // ... rest of data
};

// In test_clients
const testData = {
  name: generateClientName(),
};
```

---

### Phase 3: Implement in New Frameworks

#### 3.1 Robot Framework Tests

**File**: `src/test/robot/WizardTests.robot`

```robot
*** Settings ***
Library    TestDataGenerator

*** Test Cases ***
test_companies
    ${company_name}=    Generate Company Name
    # Use ${company_name} in test data

test_contacts
    ${contact_names}=    Generate Contact Names
    # Use ${contact_names}[first_name] and ${contact_names}[last_name]

test_clients
    ${client_name}=    Generate Client Name
    # Use ${client_name} in test data
```

#### 3.2 Java/Selenide Tests

**File**: `src/test/java/com/cjs/qa/junit/tests/WizardTests.java`

```java
import com.cjs.qa.junit.utilities.TestDataGenerator;

@Test
public void test_companies() {
    String companyName = TestDataGenerator.generateCompanyName();
    // Use companyName in test data
}

@Test
public void test_contacts() {
    TestDataGenerator.ContactNames names = TestDataGenerator.generateContactNames();
    // Use names.first_name and names.last_name in test data
}

@Test
public void test_clients() {
    String clientName = TestDataGenerator.generateClientName();
    // Use clientName in test data
}
```

#### 3.3 Java/Selenium Tests

**File**: `src/test/java/com/cjs/qa/junit/tests/WizardTestsSelenium.java`

```java
import com.cjs.qa.junit.utilities.selenium.TestDataGenerator;

@Test
public void test_companies() {
    String companyName = TestDataGenerator.generateCompanyName();
    // Use companyName in test data
}

@Test
public void test_contacts() {
    TestDataGenerator.ContactNames names = TestDataGenerator.generateContactNames();
    // Use names.first_name and names.last_name in test data
}

@Test
public void test_clients() {
    String clientName = TestDataGenerator.generateClientName();
    // Use clientName in test data
}
```

---

## 📊 Framework Prefix Standardization

### Standard Framework Names

| Framework | Prefix | Notes |
|-----------|--------|-------|
| Cypress | `Cypress` | TypeScript framework |
| Playwright | `Playwright` | TypeScript framework |
| Robot Framework | `Robot` | Python-based framework |
| Java/Selenide | `Selenide` | Java framework using Selenide |
| Java/Selenium | `Selenium` | Java framework using direct WebDriver |

### Prefix Rules

1. **Capitalization**: First letter uppercase, rest lowercase
2. **No Spaces**: Use single word or camelCase
3. **Consistent**: Same prefix across all test data generators
4. **Documented**: Document in each utility file

---

## 🔍 Collision Probability Analysis

### Current Approach (Date.now() only)

**Collision Risk**: High
- If 5 frameworks run simultaneously
- All call `Date.now()` in same millisecond
- Probability of collision: **~100%** if same millisecond

### Framework Prefix + UUID4 Approach

**Collision Risk**: Extremely Low (Practically Zero)
- Framework prefix provides first level of isolation
- UUID4 provides cryptographic randomness (122 bits)
- Probability of collision: **~5.3 × 10^-37** (practically impossible)

**Calculation**:
- UUID4 has 122 bits of randomness (2^122 possible values)
- Even with 5 frameworks running simultaneously
- Collision probability: `5 / (2^122) ≈ 5.3 × 10^-37`
- For comparison: Probability of winning Powerball lottery is ~1 in 292 million (3.4 × 10^-9)

**Conclusion**: UUID4 provides **guaranteed uniqueness** for practical purposes. Framework prefix adds identification but isn't strictly necessary for uniqueness (though it's useful for debugging).

---

## ✅ Implementation Checklist

### Phase 1: Create Utilities
- [ ] Create `cypress/cypress/support/test-data-generator.ts`
- [ ] Create `playwright/tests/utils/test-data-generator.ts`
- [ ] Create `src/test/robot/resources/TestDataGenerator.py`
- [ ] Create `src/test/java/com/cjs/qa/junit/utilities/TestDataGenerator.java` (Selenide)
- [ ] Create `src/test/java/com/cjs/qa/junit/utilities/selenium/TestDataGenerator.java` (Selenium)
- [ ] Test each utility independently

### Phase 2: Update Existing Tests
- [ ] Update `cypress/cypress/e2e/wizard.cy.ts` (3 test cases)
- [ ] Update `playwright/tests/wizard.spec.ts` (3 test cases)
- [ ] Verify tests still pass
- [ ] Verify test data format is correct

### Phase 3: Implement in New Frameworks
- [ ] Use utilities in Robot Framework wizard tests
- [ ] Use utilities in Java/Selenide wizard tests
- [ ] Use utilities in Java/Selenium wizard tests
- [ ] Verify all tests generate unique data

### Phase 4: Validation
- [ ] Run all frameworks simultaneously
- [ ] Verify no data conflicts occur
- [ ] Verify test data is unique across frameworks
- [ ] Verify tests pass when run concurrently
- [ ] Document framework prefixes

### Phase 5: Documentation
- [ ] Update framework READMEs with test data generation info
- [ ] Document framework prefixes
- [ ] Create cross-framework test data guide
- [ ] Update main project README

---

## 🚨 Edge Cases and Considerations

### Edge Case 1: Same Framework, Multiple Test Runs

**Scenario**: Multiple test runs of same framework in parallel  
**Solution**: UUID4 ensures uniqueness even within same framework (2^122 possible values)

### Edge Case 2: Clock Synchronization Issues

**Scenario**: Different machines with slightly different clocks  
**Solution**: UUID4 doesn't depend on timestamps, so clock sync is not an issue

### Edge Case 3: Test Data Length Limits

**Scenario**: Database or form field has length limits  
**Solution**: 
- Monitor test data length
- Truncate if necessary (keep prefix + suffix, truncate base)
- Document maximum lengths

### Edge Case 4: Test Data in Logs

**Scenario**: Long test data strings in logs  
**Solution**: 
- Logs will show framework prefix (good for debugging)
- Consider truncating in logs if too verbose

### Edge Case 5: Test Data Cleanup

**Scenario**: If tests accidentally create data, need to clean up  
**Solution**: 
- Use framework prefix to identify test data
- Create cleanup scripts that filter by prefix
- Document cleanup procedures

---

## 📝 Test Data Format Examples

### Company Name
```
Cypress_Test Company_a1b2c3d4e5f6789012345678901234ab
Playwright_Test Company_b2c3d4e5f6789012345678901234abcd
Robot_Test Company_c3d4e5f6789012345678901234abcdef12
Selenide_Test Company_d4e5f6789012345678901234abcdef1234
Selenium_Test Company_e5f6789012345678901234abcdef123456
```

### Contact First Name
```
Cypress_TestFirst_a1b2c3d4e5f6789012345678901234ab
Playwright_TestFirst_b2c3d4e5f6789012345678901234abcd
Robot_TestFirst_c3d4e5f6789012345678901234abcdef12
Selenide_TestFirst_d4e5f6789012345678901234abcdef1234
Selenium_TestFirst_e5f6789012345678901234abcdef123456
```

### Contact Last Name
```
Cypress_TestLast_a1b2c3d4e5f6789012345678901234ab
Playwright_TestLast_b2c3d4e5f6789012345678901234abcd
Robot_TestLast_c3d4e5f6789012345678901234abcdef12
Selenide_TestLast_d4e5f6789012345678901234abcdef1234
Selenium_TestLast_e5f6789012345678901234abcdef123456
```

### Client Name
```
Cypress_Test Client_a1b2c3d4e5f6789012345678901234ab
Playwright_Test Client_b2c3d4e5f6789012345678901234abcd
Robot_Test Client_c3d4e5f6789012345678901234abcdef12
Selenide_Test Client_d4e5f6789012345678901234abcdef1234
Selenium_Test Client_e5f6789012345678901234abcdef123456
```

---

## 🔢 Handling Different Data Types

The wizard tests currently use **string fields** for unique identifiers (name, first_name, last_name). However, forms may also contain **numeric** and **boolean** fields that need unique values when running tests concurrently.

### String Fields (Current Implementation)

**Fields**: `name`, `first_name`, `last_name`, `title`, `address`, `city`, `state`, `zip`, `country`, `job_type`, `linkedin`, `contact_type`

**Approach**: Use framework prefix + UUID4
- ✅ Already implemented in test data generators
- ✅ Guaranteed uniqueness
- ✅ Framework identification

**Example**:
```typescript
name: generateCompanyName() // "Cypress_Test Company_a1b2c3d4..."
```

### Numeric Fields

**Fields**: `company_id`, `application_id`, `client_id`, `zip` (if stored as number)

**Approach**: Use framework-specific base number + random offset

**Implementation Strategy**:
1. **Framework Base Numbers** (to avoid collisions):
   - Cypress: 1000000
   - Playwright: 2000000
   - Robot: 3000000
   - Selenide: 4000000
   - Selenium: 5000000

2. **Random Offset**: Add random number (0-999999) to base

3. **Format**: `{FRAMEWORK_BASE} + {RANDOM_OFFSET}`

**Example Implementation**:
```typescript
// Cypress
function generateCompanyId(): number {
  const base = 1000000; // Cypress base
  const offset = Math.floor(Math.random() * 1000000); // 0-999999
  return base + offset;
}

// Playwright
function generateCompanyId(): number {
  const base = 2000000; // Playwright base
  const offset = Math.floor(Math.random() * 1000000);
  return base + offset;
}
```

**Pros**:
- ✅ Numeric values stay within reasonable ranges
- ✅ Framework identification (by number range)
- ✅ Low collision probability (1 million possible values per framework)

**Cons**:
- ⚠️ Not guaranteed unique (small collision risk)
- ⚠️ Need to coordinate base numbers across frameworks

**Alternative**: Use UUID4 converted to numeric (hash or first 8 hex chars as number)
```typescript
function generateNumericId(): number {
  const uuid = generateUUID4();
  // Use first 8 hex characters as number (0-4294967295)
  return parseInt(uuid.substring(0, 8), 16);
}
```

### Boolean Fields

**Fields**: `is_primary` (stored as integer 0/1 in database)

**Approach**: Use deterministic or random boolean values

**Options**:

1. **Fixed Value** (if uniqueness not required):
   ```typescript
   is_primary: 0  // or 1
   ```

2. **Framework-Specific Pattern** (if uniqueness needed):
   ```typescript
   // Cypress: always 0, Playwright: always 1, etc.
   is_primary: FRAMEWORK_INDEX % 2  // 0 or 1 based on framework
   ```

3. **Random Boolean** (if variety needed):
   ```typescript
   is_primary: Math.random() < 0.5 ? 0 : 1
   ```

**Recommendation**: For wizard tests (which cancel forms), use **fixed values** since boolean fields don't need uniqueness for canceled forms.

### Date/DateTime Fields

**Fields**: `created_on`, `modified_on` (usually auto-generated by database)

**Approach**: Let database handle these automatically, or use current timestamp

**Note**: Wizard tests cancel forms, so dates are typically not set. If needed:
```typescript
created_on: new Date().toISOString()
```

### Summary Table

| Field Type | Current Usage | Uniqueness Strategy | Example |
|------------|---------------|---------------------|---------|
| **String** (name, first_name, etc.) | ✅ Used in wizard tests | Framework prefix + UUID4 | `Cypress_Test Company_a1b2c3d4...` |
| **Numeric** (company_id, etc.) | ⚠️ Not currently used in wizard tests | Framework base + random offset | `1000000 + random(0-999999)` |
| **Boolean** (is_primary) | ⚠️ Not currently used in wizard tests | Fixed value or framework pattern | `0` or `1` |
| **Date/DateTime** | ⚠️ Not currently used in wizard tests | Current timestamp or auto-generated | `new Date().toISOString()` |

### Future Considerations

If wizard tests are extended to actually **create** data (not just cancel), consider:

1. **Numeric ID Generation Utility**:
   ```typescript
   export function generateNumericId(framework: string): number {
     const bases: Record<string, number> = {
       'Cypress': 1000000,
       'Playwright': 2000000,
       'Robot': 3000000,
       'Selenide': 4000000,
       'Selenium': 5000000,
     };
     const base = bases[framework] || 0;
     return base + Math.floor(Math.random() * 1000000);
   }
   ```

2. **Boolean Value Generator**:
   ```typescript
   export function generateBoolean(framework: string): number {
     // Use framework index to determine boolean value
     const frameworkIndex = ['Cypress', 'Playwright', 'Robot', 'Selenide', 'Selenium'].indexOf(framework);
     return frameworkIndex % 2; // 0 or 1
   }
   ```

3. **Extended Test Data Generator**:
   ```typescript
   export interface CompleteTestData {
     // String fields
     name: string;
     first_name?: string;
     last_name?: string;
     
     // Numeric fields (if needed)
     company_id?: number;
     application_id?: number;
     
     // Boolean fields (if needed)
     is_primary?: number;
   }
   ```

---

## 🔄 Migration Strategy

### Step 1: Create Utilities (No Breaking Changes)
- Create all test data generator utilities
- Don't change existing tests yet
- Test utilities independently

### Step 2: Update One Framework at a Time
- Start with Cypress (easiest to test)
- Update tests to use new utilities
- Verify tests pass
- Then update Playwright
- Then update new frameworks as they're created

### Step 3: Parallel Execution Testing
- Run Cypress and Playwright simultaneously
- Verify no conflicts
- Add more frameworks as they're implemented
- Document any issues

### Step 4: CI/CD Integration
- Update CI/CD pipelines to run tests in parallel
- Monitor for conflicts
- Adjust if needed

---

## 🎯 Success Criteria

1. ✅ All frameworks have test data generator utilities
2. ✅ All wizard tests use unique test data generators
3. ✅ Test data includes framework prefix
4. ✅ Test data includes timestamp + random suffix
5. ✅ All tests pass when run individually
6. ✅ All tests pass when run concurrently
7. ✅ No data conflicts occur in parallel execution
8. ✅ Test data is identifiable by framework prefix
9. ✅ Documentation updated

---

## 🔗 Related Documents

- `docs/work/20260117_WIZARD_TESTS_IMPLEMENTATION_PLAN.md` - Wizard tests implementation
- `docs/work/20260117_TEST_DATA_CENTRALIZATION_PLAN.md` - Test data centralization (separate work)
- `cypress/cypress/e2e/wizard.cy.ts` - Cypress wizard tests (current)
- `playwright/tests/wizard.spec.ts` - Playwright wizard tests (current)

---

## 📊 Comparison Table

| Approach | Uniqueness | Readability | Implementation Complexity | Collision Risk |
|----------|-----------|-------------|--------------------------|----------------|
| **Date.now() only** | Low | High | Low | High |
| **Framework Prefix** | Medium | High | Low | Low |
| **UUID** | Very High | Low | Medium | Very Low |
| **Process ID + Timestamp** | Medium | Medium | Medium | Low |
| **Test Run ID + Timestamp** | High | Medium | High | Very Low |
| **Hybrid (Recommended)** | Very High | Medium | Medium | Very Low |

---

**Last Updated**: January 17, 2026  
**Status**: Ready for Review
