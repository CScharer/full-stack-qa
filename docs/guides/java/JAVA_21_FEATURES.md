# ☕ Java 21 Features Guide

**Last Updated**: 2025-11-16  
**Status**: Active  
**Java Version**: 21

---

## Overview

This guide covers **Java 21 features** and how they're being used in this codebase. Java 21 is an LTS (Long-Term Support) release with many modern language features that improve code quality, readability, and maintainability.

**Key Features**:
- ✅ **Records** - Immutable data classes
- ✅ **Switch Expressions** - More concise switch statements
- ✅ **Text Blocks** - Multi-line strings
- ✅ **Pattern Matching** - Enhanced instanceof and switch
- ✅ **Sealed Classes** - Restricted inheritance
- ✅ **Virtual Threads** - Lightweight concurrency (Preview)
- ✅ **Structured Concurrency** - Better async handling (Preview)

---

## 1. Records

**Records** are immutable data classes that automatically generate:
- Constructor
- Getters
- `equals()`, `hashCode()`, `toString()`

### Before (Traditional Class)

```java
public class User {
    private final String email;
    private final String name;
    
    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }
    
    public String getEmail() { return email; }
    public String getName() { return name; }
    
    @Override
    public boolean equals(Object o) { /* ... */ }
    @Override
    public int hashCode() { /* ... */ }
    @Override
    public String toString() { /* ... */ }
}
```

### After (Record)

```java
public record User(String email, String name) {
    // That's it! All methods generated automatically
}
```

### Usage Example

```java
// Create record
User user = new User("user@test.com", "John Doe");

// Access fields (getters generated automatically)
String email = user.email();
String name = user.name();

// Records are immutable
// user.email = "new@test.com"; // Compile error!

// equals(), hashCode(), toString() work automatically
User user2 = new User("user@test.com", "John Doe");
assert user.equals(user2); // true
```

### When to Use Records

✅ **Good for**:
- Data transfer objects (DTOs)
- Value objects
- Simple data containers
- API response models

❌ **Not suitable for**:
- Classes with mutable state
- Classes with complex behavior
- Classes that need inheritance
- JAXB/XML annotated classes (may need setters)

---

## 2. Switch Expressions

**Switch Expressions** are more concise and expression-based than traditional switch statements.

### Before (Traditional Switch)

```java
String result;
switch (status) {
    case "active":
        result = "User is active";
        break;
    case "inactive":
        result = "User is inactive";
        break;
    default:
        result = "Unknown status";
        break;
}
return result;
```

### After (Switch Expression)

```java
// Arrow syntax (simple cases)
String result = switch (status) {
    case "active" -> "User is active";
    case "inactive" -> "User is inactive";
    default -> "Unknown status";
};

// Block syntax (complex cases)
String result = switch (status) {
    case "active" -> {
        log.info("Processing active user");
        yield "User is active";
    }
    case "inactive" -> "User is inactive";
    default -> "Unknown status";
};
```

### Key Differences

1. **No break statements** - Prevents fall-through bugs
2. **Expression-based** - Can return values directly
3. **Exhaustive** - Compiler ensures all cases covered
4. **yield keyword** - Returns value from block

### Example from Codebase

**File**: `ExcelDataProvider.java`

```java
// Java 21: Switch expression
return switch (cellType) {
    case STRING -> cell.getStringCellValue().trim();
    case NUMERIC -> {
        if (DateUtil.isCellDateFormatted(cell)) {
            yield cell.getDateCellValue();
        }
        double numericValue = cell.getNumericCellValue();
        yield (numericValue == (long) numericValue) 
            ? (long) numericValue 
            : numericValue;
    }
    case BOOLEAN -> cell.getBooleanCellValue();
    case BLANK -> "";
    default -> cell.toString().trim();
};
```

---

## 3. Text Blocks

**Text Blocks** make multi-line strings much more readable.

### Before (String Concatenation)

```java
String json = "{\n" +
    "  \"name\": \"" + name + "\",\n" +
    "  \"email\": \"" + email + "\",\n" +
    "  \"age\": " + age + "\n" +
    "}";
```

### After (Text Block)

```java
String json = """
    {
      "name": "%s",
      "email": "%s",
      "age": %d
    }
    """.formatted(name, email, age);
```

### Key Features

- **Triple quotes** (`"""`) - Delimit text blocks
- **Automatic indentation** - Leading whitespace removed
- **`.formatted()`** - String formatting (replaces `String.format()`)
- **Escape sequences** - Same as regular strings

### Usage Examples

```java
// SQL query
String sql = """
    SELECT id, name, email
    FROM users
    WHERE status = 'active'
    ORDER BY name
    """;

// HTML template
String html = """
    <div class="user">
        <h1>%s</h1>
        <p>%s</p>
    </div>
    """.formatted(userName, userEmail);

// JSON
String json = """
    {
      "test": "value",
      "number": 42
    }
    """;
```

### When to Use Text Blocks

✅ **Good for**:
- SQL queries
- JSON/XML templates
- HTML templates
- Multi-line messages
- Configuration strings

---

## 4. Pattern Matching for instanceof

**Pattern Matching** simplifies type checking and casting.

### Before (Traditional instanceof)

```java
if (obj instanceof String) {
    String str = (String) obj;
    System.out.println(str.length());
}
```

### After (Pattern Matching)

```java
if (obj instanceof String str) {
    System.out.println(str.length()); // No cast needed!
}
```

### Benefits

- **No explicit cast** - Variable automatically cast
- **Scope limited** - Variable only available in true branch
- **Cleaner code** - Less boilerplate

---

## 5. Pattern Matching for switch (Standard in Java 21)

Pattern matching in switch statements (standard feature in Java 21):

```java
String result = switch (obj) {
    case String s -> "String: " + s;
    case Integer i -> "Integer: " + i;
    case null -> "Null value";
    default -> "Unknown type";
};
```

**Note**: This is a standard feature in Java 21 (was preview in Java 17-20).

---

## 6. Virtual Threads (Preview)

**Virtual Threads** provide lightweight concurrency alternative to platform threads.

```java
// Traditional thread
Thread thread = new Thread(() -> {
    System.out.println("Running on platform thread");
});

// Virtual thread (Java 21)
Thread virtual = Thread.ofVirtual()
    .name("virtual-worker")
    .start(() -> {
        System.out.println("Running on virtual thread");
    });
```

### Benefits

- **Lightweight** - Thousands of virtual threads vs hundreds of platform threads
- **Better resource usage** - Lower memory footprint
- **Easier async** - No callback hell or reactive complexity
- **Easy migration** - Can replace platform threads in many cases

### When to Use

✅ **Good for**:
- High-throughput I/O applications
- Server applications
- Async processing

❌ **Not suitable for**:
- CPU-bound operations (use platform threads)
- Small numbers of concurrent tasks

---

## 7. Sealed Classes

**Sealed Classes** restrict which classes can extend or implement them.

```java
// Define sealed interface
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
}

// Implementing classes
public final class Circle implements Shape {
    private double radius;
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

public final class Rectangle implements Shape {
    private double width, height;
    
    @Override
    public double area() {
        return width * height;
    }
}

public final class Triangle implements Shape {
    // Implementation...
}
```

### Benefits

- **Type safety** - Compiler knows all possible subtypes
- **Better pattern matching** - Exhaustive checking in switch
- **Security** - Prevent unexpected subclassing

---

## Migration Strategy

### Phase 1: Low-Risk Changes ✅
- ✅ Convert simple switch statements to switch expressions
- ✅ Use text blocks for new code
- ✅ Use pattern matching for instanceof

### Phase 2: Medium-Risk Changes
- Convert simple data classes to Records (where appropriate)
- Replace string concatenation with text blocks
- Use sealed classes for restricted inheritance

### Phase 3: Advanced Features
- Experiment with virtual threads
- Use structured concurrency (when stable)
- Advanced pattern matching with guards

---

## Examples in Codebase

### Switch Expression Example

**File**: `src/test/java/com/cjs/qa/utilities/ExcelDataProvider.java`

```java
// Converted from traditional switch to switch expression
return switch (cellType) {
    case STRING -> cell.getStringCellValue().trim();
    case NUMERIC -> { /* complex logic */ }
    case BOOLEAN -> cell.getBooleanCellValue();
    case BLANK -> "";
    default -> cell.toString().trim();
};
```

### Future Opportunities

1. **Records**: Consider converting simple DTOs like:
   - `Bitcoin.java` - Simple data class
   - `Item.java` - If not using JAXB annotations

2. **Text Blocks**: Use for:
   - SQL queries
   - JSON/XML generation
   - HTML templates
   - Multi-line log messages

3. **Switch Expressions**: Convert remaining switch statements in:
   - `Encoder.java`
   - `PageObjectGenerator.java`
   - Various page classes

4. **Virtual Threads**: Future consideration for:
   - Performance test execution
   - High-concurrency test scenarios
   - Async WebDriver operations

---

## Best Practices

### ✅ DO

1. **Use Switch Expressions** - Prefer over traditional switch statements
2. **Use Text Blocks** - For multi-line strings
3. **Use Pattern Matching** - For instanceof checks
4. **Use Records** - For simple immutable data classes
5. **Use Sealed Classes** - For restricted inheritance hierarchies

### ❌ DON'T

1. **Don't Force Records** - Not all classes should be records
2. **Don't Overuse Text Blocks** - Simple strings don't need text blocks
3. **Don't Use Preview Features in Production** - Virtual threads still in preview
4. **Don't Overuse Sealed Classes** - Only for restricted hierarchies

---

## Compilation

### Java 21 Required

Ensure your `pom.xml` specifies Java 21:

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

### Maven Compiler Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.1</version>
    <configuration>
        <release>21</release>
        <source>21</source>
        <target>21</target>
    </configuration>
</plugin>
```

### For Preview Features

If using virtual threads or other preview features:

```xml
<configuration>
    <release>21</release>
    <enablePreview>true</enablePreview>
</configuration>
```

And run with: `mvn clean compile -X`

---

## Related Documentation

- **Java 21 Release Notes**: https://openjdk.org/projects/jdk/21/
- **Records JEP 395**: https://openjdk.org/jeps/395
- **Switch Expressions JEP 361**: https://openjdk.org/jeps/361
- **Text Blocks JEP 378**: https://openjdk.org/jeps/378
- **Pattern Matching JEP 394**: https://openjdk.org/jeps/394
- **Sealed Classes JEP 409**: https://openjdk.org/jeps/409
- **Virtual Threads JEP 436**: https://openjdk.org/jeps/436

---

## Migration Checklist

- [x] Switch expression in `ExcelDataProvider.java`
- [ ] Convert remaining switch statements
- [ ] Identify Records candidates
- [ ] Replace string concatenation with text blocks
- [ ] Use pattern matching for instanceof
- [ ] Consider sealed classes for inheritance
- [ ] Evaluate virtual threads for performance tests
- [ ] Update documentation

---

**Questions?** Check the code examples or create a GitHub issue!
