# Documentation Reorganization Plan

**Created**: 2025-12-27  
**Purpose**: Plan for reorganizing root-level documentation files into appropriate subfolders  
**Status**: 📋 Planning

---

## 📋 Overview

Currently, several documentation files reside in the root `docs/` folder. This plan proposes moving them to appropriate subfolders to improve organization and maintainability.

**Goal**: Keep only essential entry-point documents (`README.md`, `NAVIGATION.md`, `QUICK_START.md`) in the root `docs/` folder, with all other documents organized in logical subfolders.

---

## 📊 Current Files to Reorganize

| File | Current Location | Purpose | Category |
|------|------------------|---------|----------|
| `DEBUGGING_PIPELINE_FAILURES.md` | `docs/` | Guide for debugging CI/CD pipeline failures locally | 🔧 Troubleshooting / 🧪 Testing |
| `DOCKER_TESTING_STATUS.md` | `docs/` | Status tracking for Docker testing setup | 🏗️ Infrastructure / 🧪 Testing |
| `INTEGRATION_TESTING.md` | `docs/` | Guide for running full-stack integration tests | 🧪 Testing |
| `LOCAL_DEVELOPMENT.md` | `docs/` | Guide for local development setup and usage | ⚙️ Setup / 🚀 Development |
| `LOCAL_TESTING_GUIDE.md` | `docs/` | Guide for running tests locally without Docker | 🧪 Testing |
| `NAVIGATION.md` | `docs/` | Documentation navigation and structure guide | 📚 Meta-Documentation |
| `QUICK_START.md` | `docs/` | Quick reference for starting the application | ⚙️ Setup / 🚀 Getting Started |
| `TROUBLESHOOTING_DEPLOY_JOBS.md` | `docs/` | Troubleshooting guide for deploy jobs | 🔧 Troubleshooting / 🏗️ CI/CD |

---

## 🎯 Proposed Reorganization

| File | Proposed Location | Rationale |
|------|-------------------|-----------|
| `DEBUGGING_PIPELINE_FAILURES.md` | Merge with `LOCAL_TESTING_GUIDE.md` → `docs/guides/testing/LOCAL_TESTING.md` | Combine local testing and debugging guides |
| `DOCKER_TESTING_STATUS.md` | Merge into `docs/guides/infrastructure/DOCKER.md` | Infrastructure status tracking |
| `INTEGRATION_TESTING.md` | `docs/guides/testing/` | Testing guide - fits with other testing guides |
| `LOCAL_DEVELOPMENT.md` | `docs/guides/setup/` | Setup guide for local development |
| `LOCAL_TESTING_GUIDE.md` | Merge with `DEBUGGING_PIPELINE_FAILURES.md` → `docs/guides/testing/LOCAL_TESTING.md` | Combine local testing and debugging guides |
| `NAVIGATION.md` | `docs/` (keep in root) | Keep in root for easy discovery |
| `QUICK_START.md` | `docs/` (keep in root) | Keep in root for easy access |
| `TROUBLESHOOTING_DEPLOY_JOBS.md` | Merge into `docs/guides/troubleshooting/CI_TROUBLESHOOTING.md` | Consolidate CI/CD troubleshooting |

---

## 📁 Final Proposed Structure

```
docs/
├── README.md                              ✅ Keep in root
├── NAVIGATION.md                          ✅ Keep in root
├── QUICK_START.md                         ✅ Keep in root
│
├── guides/
│   ├── setup/
│   │   ├── LOCAL_DEVELOPMENT.md           📦 Move from root
│   │   └── ... (existing files)
│   │
│   ├── testing/
│   │   ├── INTEGRATION_TESTING.md         📦 Move from root
│   │   ├── LOCAL_TESTING.md               🔄 Merge LOCAL_TESTING_GUIDE.md + DEBUGGING_PIPELINE_FAILURES.md
│   │   └── ... (existing files)
│   │
│   ├── infrastructure/
│   │   ├── DOCKER.md                      🔄 Merge DOCKER_TESTING_STATUS.md into this
│   │   └── ... (existing files)
│   │
│   └── troubleshooting/
│       ├── CI_TROUBLESHOOTING.md          🔄 Merge TROUBLESHOOTING_DEPLOY_JOBS.md into this
│       └── ... (existing files)
```

---

## ✅ Implementation Steps

1. **Move Files**:
   - Move `INTEGRATION_TESTING.md` → `docs/guides/testing/INTEGRATION_TESTING.md`
   - Move `LOCAL_DEVELOPMENT.md` → `docs/guides/setup/LOCAL_DEVELOPMENT.md`

2. **Merge Files**:
   - Merge `LOCAL_TESTING_GUIDE.md` + `DEBUGGING_PIPELINE_FAILURES.md` → `docs/guides/testing/LOCAL_TESTING.md`
   - Merge `DOCKER_TESTING_STATUS.md` into `docs/guides/infrastructure/DOCKER.md`
   - Merge `TROUBLESHOOTING_DEPLOY_JOBS.md` into `docs/guides/troubleshooting/CI_TROUBLESHOOTING.md`

3. **Update References**:
   - Update all internal links in documentation files
   - Update `NAVIGATION.md` to reflect new structure
   - Update `README.md` to reflect new structure

---

## 🔗 Link Updates Required

After moving files, update references in:
- `docs/README.md`
- `docs/NAVIGATION.md`
- `docs/guides/testing/TEST_EXECUTION_GUIDE.md` (may reference integration testing)
- `docs/guides/infrastructure/DOCKER.md` (will include merged content)
- `docs/guides/troubleshooting/CI_TROUBLESHOOTING.md` (will include merged content)
- Any other files that link to these documents

---

## 📋 Summary

### Files to Keep in Root (3)
- `README.md` - Main documentation overview
- `NAVIGATION.md` - Documentation navigation guide
- `QUICK_START.md` - Quick start reference

### Files to Move (2)
- `INTEGRATION_TESTING.md` → `docs/guides/testing/`
- `LOCAL_DEVELOPMENT.md` → `docs/guides/setup/`

### Files to Merge (3 pairs)
- `LOCAL_TESTING_GUIDE.md` + `DEBUGGING_PIPELINE_FAILURES.md` → `docs/guides/testing/LOCAL_TESTING.md`
- `DOCKER_TESTING_STATUS.md` → Merge into `docs/guides/infrastructure/DOCKER.md`
- `TROUBLESHOOTING_DEPLOY_JOBS.md` → Merge into `docs/guides/troubleshooting/CI_TROUBLESHOOTING.md`

### Result
- **Before**: 8 files in root `docs/` folder
- **After**: 3 files in root `docs/` folder (`README.md`, `NAVIGATION.md`, `QUICK_START.md`)

---

## ⚠️ Notes

- **Merge Strategy**: When merging files, preserve all unique content and organize logically with clear sections.
- **Link Updates**: After moving/merging files, verify all internal links work correctly.

---

**Last Updated**: 2025-12-27  
**Status**: 📋 Ready for Review

