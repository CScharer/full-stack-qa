-- Migration: Change contact.name to contact.first_name and contact.last_name
-- Run this migration on existing databases

-- Step 1: Add new columns
ALTER TABLE contact ADD COLUMN first_name TEXT;
ALTER TABLE contact ADD COLUMN last_name TEXT;

-- Step 2: Migrate existing data (split name into first_name and last_name)
-- For existing records, put the entire name in first_name
UPDATE contact SET first_name = name, last_name = '' WHERE first_name IS NULL;

-- Step 3: Make first_name and last_name NOT NULL (after data migration)
-- Note: SQLite doesn't support ALTER COLUMN, so we'll need to recreate the table
-- For now, we'll leave them nullable and handle in application code

-- Step 4: Update the schema for new databases
-- The new schema should use first_name and last_name instead of name
