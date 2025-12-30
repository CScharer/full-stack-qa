# Outdated Dependencies - Update Review

**Date Created**: 2025-12-30  
**Status**: ✅ **Verification Complete**  
**Purpose**: List all dependencies that are NOT current/stable and require updates

---

## 🚀 Quick Summary

### Total Outdated Dependencies: **10**

#### High Priority (PATCH Updates - Low Risk)
1. **Maven Compiler Plugin**: 3.13.0 → 3.14.1
2. **HTMLUnit**: 4.20.0 → 4.21.0
3. **JSON**: 20250517 → 20251224
4. **Next.js**: 16.1.0 → 16.1.1
5. **@tanstack/react-query**: 5.90.12 → 5.90.16
6. **eslint-config-next**: 16.1.0 → 16.1.1
7. **jsdom**: 27.3.0 → 27.4.0
8. **Requests (Python)**: 2.32.4 → 2.32.5
9. **aiosqlite**: 0.22.0 → 0.22.1

#### Medium Priority (MINOR Update - Review Required)
1. **Rhino**: 1.7.14.1 → 1.9.0 (Review changelog for breaking changes)

### Recommended Action
- **PATCH updates**: Can be applied immediately (low risk)
- **MINOR updates**: Review changelog before updating (Rhino 1.7 → 1.9)

---

## 🔑 Status Legend

- **Current Version**: Version currently in use in the project
- **Latest Stable**: Most recent stable version available
- **Update Type**: 
  - `PATCH` = Bug fixes only (low risk)
  - `MINOR` = New features, backward compatible (medium risk)
  - `MAJOR` = Breaking changes possible (high risk, review required)

---

## 📦 Java/Maven Dependencies (pom.xml)

### Build Tools - OUTDATED

| Dependency | Current Version | Latest Stable | Update Type | Status | Notes |
|------------|----------------|---------------|-------------|--------|-------|
| **Maven Compiler Plugin** | 3.13.0 | 3.14.1 | PATCH | ⚠️ **OUTDATED** | Update available: 3.13.0 → 3.14.1 |

### Utilities & Libraries - OUTDATED

| Dependency | Current Version | Latest Stable | Update Type | Status | Notes |
|------------|----------------|---------------|-------------|--------|-------|
| **HTMLUnit** | 4.20.0 | 4.21.0 | PATCH | ⚠️ **OUTDATED** | Update available: 4.20.0 → 4.21.0 |
| **JSON** | 20250517 | 20251224 | PATCH | ⚠️ **OUTDATED** | Update available: 20250517 → 20251224 |
| **Rhino** | 1.7.14.1 | 1.9.0 | MINOR | ⚠️ **OUTDATED** | Update available: 1.7.14.1 → 1.9.0 (review changelog for breaking changes) |

---

## 📦 Node.js Dependencies

### Frontend Project (frontend/package.json) - OUTDATED ⚠️

| Dependency | Current Version | Latest Stable | Update Type | Status | Notes |
|------------|----------------|---------------|-------------|--------|-------|
| **Next.js** | 16.1.0 | 16.1.1 | PATCH | ⚠️ **OUTDATED** | Update available: 16.1.0 → 16.1.1 |
| **@tanstack/react-query** | 5.90.12 | 5.90.16 | PATCH | ⚠️ **OUTDATED** | Update available: 5.90.12 → 5.90.16 |
| **eslint-config-next** | 16.1.0 | 16.1.1 | PATCH | ⚠️ **OUTDATED** | Update available: 16.1.0 → 16.1.1 |
| **jsdom** | 27.3.0 | 27.4.0 | PATCH | ⚠️ **OUTDATED** | Update available: 27.3.0 → 27.4.0 |

---

## 🐍 Python Dependencies

### Backend (backend/requirements.txt) - OUTDATED ⚠️

| Dependency | Current Version | Latest Stable | Update Type | Status | Notes |
|------------|----------------|---------------|-------------|--------|-------|
| **aiosqlite** | >=0.22.0 | 0.22.1 | PATCH | ⚠️ **OUTDATED** | Update available: 0.22.0 → 0.22.1 (pip list shows 0.22.1 available) |

**Note**: Other backend dependencies use `>=` version constraints and will auto-update on install. Run `pip install -r backend/requirements.txt --upgrade` to update to latest compatible versions.

### Performance Testing (requirements.txt) - OUTDATED ⚠️

| Dependency | Current Version | Latest Stable | Update Type | Status | Notes |
|------------|----------------|---------------|-------------|--------|-------|
| **Requests** | 2.32.4 | 2.32.5 | PATCH | ⚠️ **OUTDATED** | Update available: 2.32.4 → 2.32.5 |

---

## 📋 Update Instructions

### Java/Maven Dependencies

1. **Maven Compiler Plugin**:
   ```xml
   <!-- Update in pom.xml -->
   <maven-compiler-plugin.version>3.14.1</maven-compiler-plugin.version>
   ```

2. **HTMLUnit**:
   ```xml
   <!-- Update in pom.xml -->
   <htmlunit.version>4.21.0</htmlunit.version>
   ```

3. **JSON**:
   ```xml
   <!-- Update in pom.xml -->
   <json.version>20251224</json.version>
   ```

4. **Rhino**:
   ```xml
   <!-- Update in pom.xml (review changelog first) -->
   <rhino.version>1.9.0</rhino.version>
   ```

### Node.js Dependencies (Frontend)

```bash
cd frontend
npm install next@16.1.1 @tanstack/react-query@5.90.16 eslint-config-next@16.1.1 jsdom@27.4.0
```

### Python Dependencies

1. **Backend (aiosqlite)**:
   ```bash
   cd backend
   pip install --upgrade aiosqlite==0.22.1
   # Or update requirements.txt to: aiosqlite>=0.22.1
   ```

2. **Performance Testing (Requests)**:
   ```bash
   pip install --upgrade requests==2.32.5
   # Or update requirements.txt to: requests==2.32.5
   ```

---

## 📝 Next Steps

1. **Review MINOR updates** (Rhino 1.7 → 1.9):
   - Check changelog for breaking changes
   - Test thoroughly after update

2. **Apply PATCH updates**:
   - Create feature branch
   - Apply updates incrementally
   - Test locally
   - Commit and create PR

3. **Verify updates**:
   - Run test suite
   - Check for any deprecation warnings
   - Verify functionality

---

## 📅 Document Maintenance

- **Created**: 2025-12-30
- **Last Updated**: 2025-12-30 (Verification Complete)
- **Next Review**: 2026-01-30 (Monthly recommended)
- **Maintainer**: Development Team

**Verification Status**: ✅ **COMPLETE** - All dependencies verified against package repositories

**Note**: This document only lists outdated dependencies requiring action. All current/up-to-date dependencies have been removed and are tracked in `docs/process/VERSION_TRACKING.md`.
