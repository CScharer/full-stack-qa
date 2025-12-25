# Configurations Directory

## ⚠️ IMPORTANT: This directory may contain sensitive data!

---

## 📋 Setup Instructions

### First Time Setup

1. **Copy template file**:
   ```bash
   cp Environments.xml.template Environments.xml
   ```

2. **Configure Google Cloud authentication**:
   ```bash
   gcloud auth application-default login
   gcloud config set project cscharer
   ```

3. **Run your tests** - Passwords are automatically fetched from Google Cloud Secret Manager!

---

## 📁 Files in This Directory

### Working Files (NOT in git):
- `Environments.xml` - Environment configurations
- ⚠️ **DO NOT commit** - File is in `.gitignore`

### Template Files (Safe to commit):
- `Environments.xml.template` - Template file
- ✅ Safe to commit - No sensitive data

### Documentation:
- `README.md` - This file

---

## 🔐 Security Notice

All sensitive credentials are stored in **Google Cloud Secret Manager**, not in these configuration files. The application automatically retrieves passwords at runtime using the `SecureConfig.java` utility class.

---

**Last Updated**: November 8, 2025
**Security Level**: High
