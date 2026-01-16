# Delete Behavior Documentation

**Created**: 2025-12-14  
**Purpose**: Document delete behavior for database records and frontend implementation  
**Status**: ✅ Complete

---

## 📋 Overview

This document describes how record deletion works in the ONE GOAL database, including:
- What gets deleted when a record is deleted
- Database triggers that handle cascading deletes
- Frontend requirements for delete operations
- User messaging requirements

---

## 🗑️ Delete Behavior by Entity

### Application Deletion

**When an application is deleted, the following happens:**

1. ✅ **Notes** - All notes for this application are **DELETED** (CASCADE)
2. ✅ **Application Sync** - All `application_sync` records are **DELETED** (Trigger)
3. ⚠️ **Contacts** - Contacts linked to this application have their `application_id` set to **NULL** (Trigger)
   - The contact records themselves are **NOT deleted**
   - They remain in the database but are no longer linked to the application

**Frontend Message:**
```
⚠️ Warning: Deleting this application will:
• Delete all notes associated with this application
• Remove the application link from any contacts
• Delete all sync records

This action cannot be undone. Are you sure you want to continue?
```

---

### Contact Deletion

**When a contact is deleted, the following happens:**

1. ✅ **Contact Emails** - All email addresses for this contact are **DELETED** (CASCADE)
2. ✅ **Contact Phones** - All phone numbers for this contact are **DELETED** (CASCADE)
3. ⚠️ **Applications** - Applications are **NOT affected** (no CASCADE)
4. ⚠️ **Company/Client Links** - Company and client links are **NOT affected** (no CASCADE)

**Frontend Message:**
```
⚠️ Warning: Deleting this contact will:
• Delete all email addresses for this contact
• Delete all phone numbers for this contact

This action cannot be undone. Are you sure you want to continue?
```

---

### Company Deletion

**When a company is deleted, the following happens:**

1. ⚠️ **Applications** - Applications linked to this company have their `company_id` set to **NULL** (Trigger)
   - Applications are **NOT deleted**
   - They remain in the database but are no longer linked to the company
2. ⚠️ **Contacts** - Contacts linked to this company have their `company_id` set to **NULL** (Trigger)
   - Contacts are **NOT deleted**
   - They remain in the database but are no longer linked to the company

**Frontend Message:**
```
⚠️ Warning: Deleting this company will:
• Remove the company link from all applications
• Remove the company link from all contacts

Applications and contacts will remain in the database but will no longer be associated with this company.

This action cannot be undone. Are you sure you want to continue?
```

---

### Client Deletion

**When a client is deleted, the following happens:**

1. ⚠️ **Applications** - Applications linked to this client have their `client_id` set to **NULL** (Trigger)
   - Applications are **NOT deleted**
   - They remain in the database but are no longer linked to the client
2. ⚠️ **Contacts** - Contacts linked to this client have their `client_id` set to **NULL** (Trigger)
   - Contacts are **NOT deleted**
   - They remain in the database but are no longer linked to the client

**Frontend Message:**
```
⚠️ Warning: Deleting this client will:
• Remove the client link from all applications
• Remove the client link from all contacts

Applications and contacts will remain in the database but will no longer be associated with this client.

This action cannot be undone. Are you sure you want to continue?
```

---

### Note Deletion

**When a note is deleted:**
- No cascading deletes
- Only the note itself is deleted

**Frontend Message:**
```
⚠️ Warning: Are you sure you want to delete this note? This action cannot be undone.
```

---

### Contact Email/Phone Deletion

**When a contact email or phone is deleted:**
- No cascading deletes
- Only the email/phone record itself is deleted

**Frontend Message:**
```
⚠️ Warning: Are you sure you want to delete this [email/phone]? This action cannot be undone.
```

---

## 🔧 Database Triggers

The following triggers are defined in `DELETE_TRIGGERS.sql`:

1. **`trg_application_delete_cascade`**
   - Deletes `application_sync` records
   - Sets `contact.application_id` to NULL

2. **`trg_contact_delete_cascade`**
   - Explicitly deletes `contact_email` records (also handled by FK CASCADE)
   - Explicitly deletes `contact_phone` records (also handled by FK CASCADE)

3. **`trg_company_delete_cascade`**
   - Sets `application.company_id` to NULL
   - Sets `contact.company_id` to NULL

4. **`trg_client_delete_cascade`**
   - Sets `application.client_id` to NULL
   - Sets `contact.client_id` to NULL

---

## 🎨 Frontend Implementation Requirements

### 1. Delete Button Behavior

**All delete buttons must:**
- Show a confirmation dialog before deleting
- Display the appropriate warning message (see above)
- Require explicit user confirmation (e.g., "Delete" button click)
- Show a success message after deletion
- Handle errors gracefully

### 2. Confirmation Dialog Component

Create a reusable confirmation dialog component:

```typescript
interface DeleteConfirmationProps {
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  isOpen: boolean;
}
```

### 3. Delete API Calls

All delete operations should:
- Use DELETE HTTP method
- Include proper error handling
- Show loading state during deletion
- Refresh data after successful deletion
- Handle 404 errors (record already deleted)
- Handle 403 errors (permission denied)

### 4. User Feedback

**After successful deletion:**
- Show success toast/notification
- Remove deleted item from UI
- Update related data if needed (e.g., remove links)

**After failed deletion:**
- Show error message
- Keep item in UI
- Log error for debugging

---

## 📊 Delete Impact Summary Table

| Entity Deleted | Records Deleted | Records Modified (FK set to NULL) | Records Unaffected |
|----------------|-----------------|-----------------------------------|---------------------|
| **Application** | • Notes<br>• Application Sync | • Contact.application_id | • Contacts<br>• Company<br>• Client |
| **Contact** | • Contact Emails<br>• Contact Phones | None | • Applications<br>• Company<br>• Client |
| **Company** | None | • Application.company_id<br>• Contact.company_id | • Applications<br>• Contacts |
| **Client** | None | • Application.client_id<br>• Contact.client_id | • Applications<br>• Contacts |
| **Note** | None | None | All other records |
| **Contact Email** | None | None | All other records |
| **Contact Phone** | None | None | All other records |

---

## 🧪 Testing Requirements

### Frontend Tests

1. **Delete Confirmation Dialog**
   - Test that dialog appears on delete button click
   - Test that cancel button closes dialog without deleting
   - Test that confirm button triggers deletion

2. **Delete Operations**
   - Test successful deletion
   - Test error handling
   - Test loading states
   - Test UI updates after deletion

3. **Warning Messages**
   - Test that correct warning message is shown for each entity type
   - Test that warning includes all affected records

### Backend Tests

See `data/Core/tests/test_delete_triggers.py` for trigger tests.

---

## 📝 API Endpoints

All delete endpoints should follow this pattern:

```
DELETE /api/v1/{entity}/{id}
```

**Response Codes:**
- `204 No Content` - Successful deletion
- `404 Not Found` - Record doesn't exist
- `403 Forbidden` - Permission denied
- `500 Internal Server Error` - Server error

**Example:**
```http
DELETE /api/v1/applications/123
```

---

## 🔒 Security Considerations

1. **Authorization**: Verify user has permission to delete before allowing operation
2. **Audit Logging**: Log all delete operations with user ID and timestamp
3. **Soft Deletes**: Consider implementing soft deletes (`is_deleted` flag) for critical records
4. **Backup**: Ensure backups are in place before allowing bulk deletions

---

## 📚 Related Documentation

- `DELETE_TRIGGERS.sql` - Database trigger definitions
- `ENTITY_RELATIONSHIPS.md` - Entity relationship documentation
- `API_CONTRACT.md` - API endpoint specifications
- `data/Core/tests/test_delete_triggers.py` - Trigger tests

---

**Last Updated**: 2025-12-14  
**Status**: ✅ Complete
