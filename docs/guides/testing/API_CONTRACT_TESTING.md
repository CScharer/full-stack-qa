# 🔌 API Contract Testing Guide

**Last Updated**: 2025-11-14
**Status**: Active

---

## Overview

**API Contract Testing** validates that API responses match expected schemas. This catches breaking API changes early, before they cause test failures or production issues.

**Benefits**:
- ✅ **Catch breaking changes early** - Detect API changes immediately
- ✅ **Living documentation** - Schemas document API structure
- ✅ **Prevent regressions** - Ensure API contracts remain stable
- ✅ **Better error messages** - Schema validation provides detailed error info

---

## How It Works

1. **Define JSON Schema** - Describe expected API response structure
2. **Make API Call** - Use REST Assured to call the API
3. **Validate Response** - Check response matches schema
4. **Fail Fast** - Test fails if schema doesn't match

---

## JSON Schemas

### Schema Location

All JSON schemas are stored in:

```
src/test/resources/schemas/
```

### Available Schemas

1. **`gotowebinar-auth-response.json`**
   - Schema for GoToWebinar OAuth token response
   - Validates: `access_token`, `token_type`, `expires_in`, etc.

2. **`gotowebinar-webinar-list-response.json`**
   - Schema for GoToWebinar webinar list response
   - Validates: `webinars` array, `webinarKey`, `subject`, `times`, etc.

3. **`yourmembership-api-response.json`**
   - Schema for YourMembership API response
   - Validates: `Response.Status.Code`, `Response.Data`, etc.

---

## Usage with REST Assured

### Basic Example

```java
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class APIContractTests {

    @Test
    public void testAuthResponseSchema() {
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("grant_type", "password")
            .formParam("user_id", "username")
            .formParam("password", "password")
            .formParam("client_id", "consumer_key")
        .when()
            .post("https://api.getgo.com/oauth/v2/access_token")
        .then()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(
                "schemas/gotowebinar-auth-response.json"
            ));
    }
}
```

### With Authentication

```java
@Test
public void testWebinarListSchema() {
    String accessToken = getAccessToken(); // Your auth method

    given()
        .header("Authorization", "Bearer " + accessToken)
        .header("Accept", "application/json")
    .when()
        .get("https://api.getgo.com/G2W/rest/v2/accounts/{accountKey}/webinars", accountKey)
    .then()
        .statusCode(200)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(
            "schemas/gotowebinar-webinar-list-response.json"
        ));
}
```

---

## Creating JSON Schemas

### Schema Structure

JSON Schema uses Draft 7 format. Basic structure:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "title": "My API Response",
  "description": "Description of the API response",
  "required": ["field1", "field2"],
  "properties": {
    "field1": {
      "type": "string",
      "description": "Field description"
    },
    "field2": {
      "type": "integer",
      "minimum": 0
    }
  },
  "additionalProperties": false
}
```

### Common Field Types

**String**:
```json
{
  "type": "string",
  "minLength": 1,
  "maxLength": 100
}
```

**Integer**:
```json
{
  "type": "integer",
  "minimum": 0,
  "maximum": 100
}
```

**Array**:
```json
{
  "type": "array",
  "items": {
    "type": "string"
  }
}
```

**Object**:
```json
{
  "type": "object",
  "properties": {
    "nestedField": {
      "type": "string"
    }
  }
}
```

**Enum**:
```json
{
  "type": "string",
  "enum": ["value1", "value2", "value3"]
}
```

### Example: Complete Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "title": "User API Response",
  "description": "Response from /api/users/{id} endpoint",
  "required": ["id", "email", "name"],
  "properties": {
    "id": {
      "type": "integer",
      "description": "User ID",
      "minimum": 1
    },
    "email": {
      "type": "string",
      "format": "email",
      "description": "User email address"
    },
    "name": {
      "type": "string",
      "description": "User full name",
      "minLength": 1
    },
    "status": {
      "type": "string",
      "enum": ["active", "inactive", "pending"],
      "description": "User account status"
    },
    "roles": {
      "type": "array",
      "description": "User roles",
      "items": {
        "type": "string"
      }
    }
  },
  "additionalProperties": false
}
```

---

## Example Test Class

See `src/test/java/com/cjs/qa/junit/tests/api/APIContractTests.java` for a complete example.

**Key Points**:
- Uses REST Assured for API calls
- Validates responses against JSON schemas
- Includes Allure annotations for reporting
- Provides clear error messages on failure

---

## Best Practices

### 1. Keep Schemas Up to Date

When APIs change, update schemas immediately:

```bash
# Update schema
vim src/test/resources/schemas/my-api-response.json

# Run tests to verify
./mvnw test -Dtest=APIContractTests
```

### 2. Use Descriptive Schema Names

- ✅ `gotowebinar-auth-response.json`
- ✅ `yourmembership-user-response.json`
- ❌ `schema1.json`
- ❌ `api.json`

### 3. Document Required vs Optional Fields

Use `required` array to specify mandatory fields:

```json
{
  "required": ["id", "email"],  // These MUST be present
  "properties": {
    "id": { "type": "integer" },
    "email": { "type": "string" },
    "optionalField": { "type": "string" }  // This is optional
  }
}
```

### 4. Validate Response Status Codes

Always check HTTP status codes:

```java
.then()
    .statusCode(200)  // Verify success
    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(...));
```

### 5. Use `additionalProperties: false` for Strict Validation

Prevents unexpected fields in responses:

```json
{
  "additionalProperties": false  // Reject unknown fields
}
```

---

## Troubleshooting

### Schema Validation Fails

**Error**: `Schema validation failed: ...`

**Solutions**:
1. Check actual API response structure
2. Compare with schema definition
3. Update schema if API changed (intentionally)
4. Fix API if it deviated from contract

### Schema File Not Found

**Error**: `Schema file not found: schemas/my-schema.json`

**Solutions**:
1. Verify file exists in `src/test/resources/schemas/`
2. Check file name spelling (case-sensitive)
3. Ensure file is in classpath

### Type Mismatch

**Error**: `Expected type: string, found: integer`

**Solutions**:
1. Check actual API response type
2. Update schema to match actual response
3. Or fix API to match schema

---

## Integration with Existing Tests

### Current API Tests

The framework currently uses `HttpURLConnection` for API calls (see `REST.java`, `GTWebinarServiceTests.java`, `YMService.java`).

### Migration Path

**Option 1**: Add contract tests alongside existing tests
- Keep existing tests
- Add new contract tests using REST Assured
- Gradually migrate

**Option 2**: Migrate existing tests to REST Assured
- Replace `HttpURLConnection` with REST Assured
- Add schema validation
- Update all API tests

**Recommendation**: Start with Option 1, then gradually migrate.

---

## Dependencies

Required dependencies (already in `pom.xml`):

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.5.6</version>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>5.5.6</version>
</dependency>
```

---

## Related Documentation

- **REST Assured**: https://rest-assured.io/
- **JSON Schema**: https://json-schema.org/
- **JSON Schema Validator**: https://github.com/rest-assured/rest-assured/wiki/Usage#json-schema-validation

---

## Examples in Codebase

- ✅ `APIContractTests.java` - Example contract test class
- 📁 `src/test/resources/schemas/` - JSON schema definitions
- 📁 `src/test/java/com/cjs/qa/gt/api/` - GoToWebinar API tests
- 📁 `src/test/java/com/cjs/qa/ym/api/` - YourMembership API tests

---

**Questions?** Check the example test class or create a GitHub issue!
