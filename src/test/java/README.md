# ⚠️ CRITICAL: Protected Test Code Directory

**🚨 DO NOT MODIFY WITHOUT EXPLICIT APPROVAL 🚨**

---

## 🔒 Protection Status

This directory (`src/test/java/`) contains **protected legacy test code** that requires **special handling**.

---

## 📋 Important Information

### Current State
- **Contains**: Legacy test code that has been updated over time
- **Usage Status**: **Most code is NOT currently used** in active test execution
- **Purpose**: Preserved for potential future incorporation into test suites
- **Value**: Historical code that may be valuable for future test development

### ⚠️ Critical Rules

**🚨 MANDATORY - NEVER SKIP THESE STEPS:**

1. **✅ VERIFICATION REQUIRED - TWICE**
   - ❌ **NEVER** modify, delete, or refactor code in this directory without explicit user approval
   - ✅ **ALWAYS** verify with the user **at least TWO times** before making any changes:
     - First verification: Before starting any work
     - Second verification: Before committing any changes
   - ✅ **ALWAYS** explain what changes you plan to make and why
   - ✅ **ALWAYS** wait for explicit approval before proceeding

2. **✅ Preservation Priority**
   - ❌ **NEVER** delete files from this directory
   - ❌ **NEVER** refactor without approval
   - ❌ **NEVER** remove "unused" code without explicit confirmation
   - ✅ **ALWAYS** preserve existing code structure
   - ✅ **ALWAYS** ask before making changes, even if code appears unused

3. **✅ Change Process**
   - Step 1: Identify what needs to change
   - Step 2: **Ask user for approval** (first verification)
   - Step 3: Explain the changes and impact
   - Step 4: Wait for explicit approval
   - Step 5: Make changes (if approved)
   - Step 6: **Ask user for approval again** (second verification) before committing
   - Step 7: Wait for explicit approval before committing

---

## 📝 Examples of Protected Operations

### ❌ DO NOT DO (Without Approval):
- Delete any files
- Refactor code structure
- Remove "unused" imports or methods
- Modernize code style
- Update dependencies used by this code
- Rename classes or packages
- Consolidate duplicate code
- Remove commented-out code
- Update deprecated API usage

### ✅ ALLOWED (With Explicit Approval):
- Fix compilation errors (with approval)
- Add new test files (with approval)
- Update test data (with approval)
- Fix critical bugs (with approval)

---

## 🔍 How to Identify Protected Code

**All code in this directory is protected:**
- `src/test/java/com/cjs/qa/**/*.java` - **ALL FILES**
- Subdirectories:
  - `google/`
  - `microsoft/`
  - `linkedin/`
  - `vivit/`
  - `bts/`
  - `core/`
  - `selenium/`
  - `utilities/`
  - And all other subdirectories

---

## 📚 Related Documentation

- **[AI_WORKFLOW_RULES.md](../../../docs/process/AI_WORKFLOW_RULES.md)** - Contains detailed rules about this directory
- **[QUICK_REFERENCE.md](../../../docs/process/QUICK_REFERENCE.md)** - Quick reference for critical rules

---

## ⚠️ Reminder

**This code may not be actively used, but it is PRESERVED for future use. Do not assume it's safe to modify or delete.**

**When in doubt: ASK. Always verify twice before making changes.**

---

**Last Updated**: 2025-12-20  
**Status**: 🔒 **PROTECTED - DO NOT MODIFY WITHOUT APPROVAL**
