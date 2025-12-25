-- ONE GOAL Database Delete Triggers
-- These triggers handle cascading deletes when records are deleted
-- They ensure data integrity and prevent orphaned records

-- Enable Foreign Key support
PRAGMA foreign_keys = ON;

-- ============================================================================
-- APPLICATION DELETE TRIGGERS
-- ============================================================================

-- When an application is deleted, delete related records:
-- 1. All notes for this application (already handled by FK CASCADE, but trigger provides logging)
-- 2. All application_sync records for this application
-- 3. Set contact.application_id to NULL for contacts linked to this application
--    (Note: We don't delete contacts, just remove the link)

CREATE TRIGGER IF NOT EXISTS "trg_application_delete_cascade"
AFTER DELETE ON "application"
FOR EACH ROW
BEGIN
    -- Delete application_sync records
    DELETE FROM "application_sync" WHERE "sqlite_id" = OLD.id;
    
    -- Remove application_id from contacts (set to NULL)
    -- Note: This maintains contact records but removes the link
    UPDATE "contact" SET "application_id" = NULL WHERE "application_id" = OLD.id;
END;

-- ============================================================================
-- CONTACT DELETE TRIGGERS
-- ============================================================================

-- When a contact is deleted, delete related records:
-- 1. All contact_email records (already handled by FK CASCADE)
-- 2. All contact_phone records (already handled by FK CASCADE)
-- Note: Contacts are not deleted when applications are deleted (no CASCADE on application_id FK)

CREATE TRIGGER IF NOT EXISTS "trg_contact_delete_cascade"
AFTER DELETE ON "contact"
FOR EACH ROW
BEGIN
    -- Delete contact emails (already handled by FK CASCADE, but explicit for clarity)
    DELETE FROM "contact_email" WHERE "contact_id" = OLD.id;
    
    -- Delete contact phones (already handled by FK CASCADE, but explicit for clarity)
    DELETE FROM "contact_phone" WHERE "contact_id" = OLD.id;
END;

-- ============================================================================
-- COMPANY DELETE TRIGGERS
-- ============================================================================

-- When a company is deleted, handle related records:
-- 1. Set application.company_id to NULL for applications linked to this company
--    (Note: We don't delete applications, just remove the company link)
-- 2. Set contact.company_id to NULL for contacts linked to this company
--    (Note: We don't delete contacts, just remove the company link)

CREATE TRIGGER IF NOT EXISTS "trg_company_delete_cascade"
AFTER DELETE ON "company"
FOR EACH ROW
BEGIN
    -- Remove company_id from applications (set to NULL)
    UPDATE "application" SET "company_id" = NULL WHERE "company_id" = OLD.id;
    
    -- Remove company_id from contacts (set to NULL)
    UPDATE "contact" SET "company_id" = NULL WHERE "company_id" = OLD.id;
END;

-- ============================================================================
-- CLIENT DELETE TRIGGERS
-- ============================================================================

-- When a client is deleted, handle related records:
-- 1. Set application.client_id to NULL for applications linked to this client
--    (Note: We don't delete applications, just remove the client link)
-- 2. Set contact.client_id to NULL for contacts linked to this client
--    (Note: We don't delete contacts, just remove the client link)

CREATE TRIGGER IF NOT EXISTS "trg_client_delete_cascade"
AFTER DELETE ON "client"
FOR EACH ROW
BEGIN
    -- Remove client_id from applications (set to NULL)
    UPDATE "application" SET "client_id" = NULL WHERE "client_id" = OLD.id;
    
    -- Remove client_id from contacts (set to NULL)
    UPDATE "contact" SET "client_id" = NULL WHERE "client_id" = OLD.id;
END;

-- ============================================================================
-- SOFT DELETE TRIGGERS (Optional - for is_deleted flag handling)
-- ============================================================================

-- These triggers can be used if you want to implement soft deletes
-- They automatically set is_deleted = 1 instead of actually deleting records
-- Uncomment if you want to use soft deletes instead of hard deletes

-- CREATE TRIGGER IF NOT EXISTS "trg_application_soft_delete"
-- INSTEAD OF DELETE ON "application"
-- FOR EACH ROW
-- BEGIN
--     UPDATE "application" SET "is_deleted" = 1, "modified_on" = datetime('now', 'localtime') WHERE "id" = OLD.id;
-- END;

-- ============================================================================
-- NOTES
-- ============================================================================

-- Foreign Key CASCADE already handles:
-- - contact_email deletion when contact is deleted
-- - contact_phone deletion when contact is deleted
-- - note deletion when application is deleted
--
-- Triggers handle:
-- - application_sync deletion when application is deleted
-- - Setting FK fields to NULL when parent records are deleted (company, client)
-- - Maintaining data integrity without losing child records unnecessarily
