-- ONE GOAL Database Schema (Corrected)
-- This schema fixes all issues identified in the AI review:
-- 1. Proper Foreign Key constraints
-- 2. Correct data types (INTEGER for Foreign Keys)
-- 3. Consistent naming (no t_ prefixes)
-- 4. Normalized structure
-- 5. All fields from t_JobSearch table mapped to appropriate tables

-- Enable Foreign Key support in SQLite
PRAGMA foreign_keys = ON;

-- Synchronization table (SQLite ↔ MongoDB mapping)
-- Maps to: t_JobSearch.Mongodb_ID
CREATE TABLE "application_sync" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "sqlite_id" INTEGER NOT NULL,
    "mongodb_id" TEXT,
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    FOREIGN KEY("sqlite_id") REFERENCES "application"("id")
);

-- Core application table
-- Maps fields from: t_JobSearch (Status, Work_Setting, Compensation, Position, Job_Description, 
--                                Resume, Cover_Letter, Entered_IWD, Date_Close, Requirement, Job_Link, Location)
CREATE TABLE "application" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "status" TEXT NOT NULL DEFAULT 'Pending',  -- Maps from: t_JobSearch.Status
    "requirement" TEXT,  -- Maps from: t_JobSearch.Requirement
    "work_setting" TEXT NOT NULL DEFAULT 'Remote',  -- Maps from: t_JobSearch.Work_Setting
    "compensation" TEXT,  -- Maps from: t_JobSearch.Compensation
    "position" TEXT,  -- Maps from: t_JobSearch.Position
    "job_description" TEXT,  -- Maps from: t_JobSearch.Job_Description
    "job_link" TEXT,  -- Maps from: t_JobSearch.Job_Link
    "location" TEXT,  -- Maps from: t_JobSearch.Location
    "resume" TEXT,  -- Maps from: t_JobSearch.Resume
    "cover_letter" TEXT,  -- Maps from: t_JobSearch.Cover_Letter
    "entered_iwd" INTEGER DEFAULT 0,  -- Maps from: t_JobSearch.Entered_IWD
    "date_close" TEXT,  -- Maps from: t_JobSearch.Date_Close
    "company_id" INTEGER,  -- Foreign Key to company table
    "client_id" INTEGER,  -- Foreign Key to client table (for Client field)
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),  -- Maps from: t_JobSearch.created_at
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),  -- Maps from: t_JobSearch.modified_at
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    FOREIGN KEY("company_id") REFERENCES "company"("id"),
    FOREIGN KEY("client_id") REFERENCES "client"("id")
);

-- Company/Firm information
-- Maps fields from: t_JobSearch (Firm, Address, City, State, Zip, Country, Job_Type)
CREATE TABLE "company" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL,  -- Maps from: t_JobSearch.Firm
    "address" TEXT,  -- Maps from: t_JobSearch.Address
    "city" TEXT,  -- Maps from: t_JobSearch.City
    "state" TEXT,  -- Maps from: t_JobSearch.State
    "zip" TEXT,  -- Maps from: t_JobSearch.Zip
    "country" TEXT NOT NULL DEFAULT 'United States',  -- Maps from: t_JobSearch.Country
    "job_type" TEXT NOT NULL DEFAULT 'Technology',  -- Maps from: t_JobSearch.Job_Type (Industry)
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL
);

-- Client information (where the job is located)
-- Maps fields from: t_JobSearch.Client
CREATE TABLE "client" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "name" TEXT,  -- Maps from: t_JobSearch.Client
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL
);

-- Contact information (recruiters, managers, leads, account managers)
-- Maps fields from: t_JobSearch (Recruiter, Title, LinkedIn, Account_Managers, Manager, Leads)
CREATE TABLE "contact" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "company_id" INTEGER,  -- Optional: link to company
    "application_id" INTEGER,  -- Optional: link to specific application
    "client_id" INTEGER,  -- Optional: link to client (for Manager, Leads)
    "first_name" TEXT NOT NULL,  -- Maps from: t_JobSearch.Recruiter, Manager, Leads, Account_Managers (first name)
    "last_name" TEXT NOT NULL,  -- Maps from: t_JobSearch.Recruiter, Manager, Leads, Account_Managers (last name)
    "title" TEXT NOT NULL DEFAULT 'Recruiter',  -- Maps from: t_JobSearch.Title
    "linkedin" TEXT,  -- Maps from: t_JobSearch.LinkedIn
    "contact_type" TEXT NOT NULL,  -- 'Recruiter', 'Manager', 'Lead', 'Account Manager'
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    FOREIGN KEY("company_id") REFERENCES "company"("id"),
    FOREIGN KEY("application_id") REFERENCES "application"("id"),
    FOREIGN KEY("client_id") REFERENCES "client"("id")
);

-- Contact emails (supports multiple emails per contact: Personal, Work, etc.)
-- Maps fields from: t_JobSearch.EMail
CREATE TABLE "contact_email" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "contact_id" INTEGER NOT NULL,
    "email" TEXT NOT NULL,
    "email_type" TEXT NOT NULL DEFAULT 'Work',  -- 'Personal', 'Work', 'Other'
    "is_primary" INTEGER DEFAULT 0,  -- Boolean: 1 for primary email, 0 for others
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    FOREIGN KEY("contact_id") REFERENCES "contact"("id") ON DELETE CASCADE
);

-- Contact phone numbers (supports multiple phones per contact: Home, Cell, Work, etc.)
-- Maps fields from: t_JobSearch.Phone
CREATE TABLE "contact_phone" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "contact_id" INTEGER NOT NULL,
    "phone" TEXT NOT NULL,
    "phone_type" TEXT NOT NULL DEFAULT 'Work',  -- 'Home', 'Cell', 'Work', 'Other'
    "is_primary" INTEGER DEFAULT 0,  -- Boolean: 1 for primary phone, 0 for others
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    FOREIGN KEY("contact_id") REFERENCES "contact"("id") ON DELETE CASCADE
);

-- Application notes
-- Maps fields from: t_JobSearch.Notes and t_JobSearchNotes table
CREATE TABLE "note" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "application_id" INTEGER NOT NULL,
    "note" TEXT NOT NULL,
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    FOREIGN KEY("application_id") REFERENCES "application"("id") ON DELETE CASCADE
);

-- Job search sites (reference data)
-- Maps from: t_JobSearchSites table
CREATE TABLE "job_search_site" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "site_name" TEXT NOT NULL UNIQUE,
    "url" TEXT,  -- URL of the job search site
    "is_deleted" INTEGER DEFAULT 0,  -- Soft delete flag (0 = active, 1 = deleted)
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL
);

-- Create indexes on Foreign Keys for better performance
CREATE INDEX "idx_application_company_id" ON "application"("company_id");
CREATE INDEX "idx_application_client_id" ON "application"("client_id");
CREATE INDEX "idx_application_status" ON "application"("status");
CREATE INDEX "idx_application_deleted" ON "application"("is_deleted");
CREATE INDEX "idx_contact_company_id" ON "contact"("company_id");
CREATE INDEX "idx_contact_application_id" ON "contact"("application_id");
CREATE INDEX "idx_contact_client_id" ON "contact"("client_id");
CREATE INDEX "idx_contact_type" ON "contact"("contact_type");
CREATE INDEX "idx_contact_deleted" ON "contact"("is_deleted");
CREATE INDEX "idx_note_application_id" ON "note"("application_id");
CREATE INDEX "idx_note_deleted" ON "note"("is_deleted");
CREATE INDEX "idx_application_sync_sqlite_id" ON "application_sync"("sqlite_id");
CREATE INDEX "idx_application_sync_deleted" ON "application_sync"("is_deleted");
CREATE INDEX "idx_contact_email_contact_id" ON "contact_email"("contact_id");
CREATE INDEX "idx_contact_email_primary" ON "contact_email"("contact_id", "is_primary");
CREATE INDEX "idx_contact_email_deleted" ON "contact_email"("is_deleted");
CREATE INDEX "idx_contact_phone_contact_id" ON "contact_phone"("contact_id");
CREATE INDEX "idx_contact_phone_primary" ON "contact_phone"("contact_id", "is_primary");
CREATE INDEX "idx_contact_phone_deleted" ON "contact_phone"("is_deleted");
CREATE INDEX "idx_company_deleted" ON "company"("is_deleted");
CREATE INDEX "idx_client_deleted" ON "client"("is_deleted");
CREATE INDEX "idx_job_search_site_deleted" ON "job_search_site"("is_deleted");

-- Seed data: Pre-populate job search sites
-- These are reference data that should always be available
INSERT INTO "job_search_site" ("site_name", "url", "created_by", "modified_by")
VALUES
    ('CyberCoders', 'http://www.cybercoders.com', 'system', 'system'),
    ('Dice', 'http://www.dice.com/', 'system', 'system'),
    ('Indeed', 'https://www.indeed.com/', 'system', 'system'),
    ('IWD', 'https://www.iowaworks.gov', 'system', 'system'),
    ('LinkedIn', 'https://www.linkedin.com/', 'system', 'system'),
    ('Seek', 'http://www.seek.com.au/', 'system', 'system'),
    ('ZipRecruiter', 'https://www.ziprecruiter.com/', 'system', 'system');
