# XML Configuration Files

## ⚠️ IMPORTANT: These files contain sensitive data and should NOT be committed to git!

---

## 📋 Setup Instructions

### First Time Setup

1. **Copy template files to create working configuration files**:
   ```bash
   cd XML/
   cp Companies.xml.template Companies.xml
   cp UserSettings.xml.template UserSettings.xml
   ```

2. **Authentication with Google Cloud** (passwords are stored in Secret Manager):
   ```bash
   gcloud auth application-default login
   gcloud config set project cscharer
   ```

3. **Verify you can access secrets**:
   ```bash
   gcloud secrets versions access latest --secret="AUTO_BTSQA_PASSWORD"
   ```

That's it! The application will automatically fetch passwords from Google Cloud Secret Manager when needed.

---

## 📁 Files in This Directory

### Working Files (NOT committed to git):
- `Companies.xml` - Contains company configurations (created from template)
- `UserSettings.xml` - Contains user settings (created from template)
- ⚠️ **DO NOT commit these files!** They are in `.gitignore`

### Template Files (Safe to commit):
- `Companies.xml.template` - Template with placeholder values
- `UserSettings.xml.template` - Template with placeholder values
- ✅ These files have `${SECRET_*}` placeholders instead of real passwords

### Documentation:
- `README.md` - This file

---

## 🔐 Password Management

### How Passwords Work Now

**OLD APPROACH** (Insecure):
```xml
<Password>ActualPasswordHere</Password>
```

**NEW APPROACH** (Secure):
```java
// Password stored in Google Cloud Secret Manager
// Code retrieves it at runtime using SecureConfig.java
String password = SecureConfig.getPassword("AUTO_COMPANY_AIC_PASSWORD");
```

### Where Passwords Are Stored

All passwords are now securely stored in **Google Cloud Secret Manager**:
- Project: `cscharer`
- Naming: `AUTO_*_PASSWORD` or `AUTO_COMPANY_*_PASSWORD`
- Total: 43 secrets
- View: https://console.cloud.google.com/security/secret-manager?project=cscharer

---

## 🚨 Security Best Practices

### DO:
- ✅ Copy template files to create your local configuration
- ✅ Keep working XML files on your local machine only
- ✅ Use Google Cloud Secret Manager for passwords
- ✅ Authenticate with `gcloud auth application-default login`
- ✅ Keep service account keys secure (never commit!)

### DON'T:
- ❌ NEVER commit `Companies.xml` or `UserSettings.xml`
- ❌ NEVER commit files with actual passwords
- ❌ NEVER share configuration files containing credentials
- ❌ NEVER disable .gitignore rules for sensitive files
- ❌ NEVER hardcode passwords in source code

---

## 🔧 Troubleshooting

### "Permission denied" when accessing secrets
```bash
# Make sure you're authenticated
gcloud auth application-default login

# Verify you have the right permissions
gcloud projects get-iam-policy cscharer
```

### "Secret not found"
```bash
# List all secrets to verify they exist
gcloud secrets list | grep AUTO

# Should show 43 secrets
```

### "Configuration file not found"
```bash
# Make sure you copied the template files
cd XML/
cp Companies.xml.template Companies.xml
cp UserSettings.xml.template UserSettings.xml
```

---

## 📞 Support

For issues or questions:
- Check the main project README.md
- Review ANALYSIS_PS_RESULTS.md for migration details
- Contact: CJS QA Team

---

**Last Updated**: November 8, 2025
**Security Level**: High - Handle with care
