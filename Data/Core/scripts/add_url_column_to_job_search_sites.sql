-- Migration: Add url column to job_search_site table
-- This script adds the url column to existing databases

-- Add url column (SQLite doesn't support ALTER COLUMN, so we use ALTER TABLE ADD COLUMN)
-- Note: This will fail if the column already exists, which is safe to ignore
-- SQLite doesn't support IF NOT EXISTS for ALTER TABLE ADD COLUMN, so we just try it
ALTER TABLE "job_search_site" ADD COLUMN "url" TEXT;

-- Update existing sites with their URLs
UPDATE "job_search_site" SET "url" = 'http://www.cybercoders.com' WHERE "site_name" = 'CyberCoders';
UPDATE "job_search_site" SET "url" = 'http://www.dice.com/' WHERE "site_name" = 'Dice';
UPDATE "job_search_site" SET "url" = 'https://www.indeed.com/' WHERE "site_name" = 'Indeed';
UPDATE "job_search_site" SET "url" = 'https://www.iowaworks.gov' WHERE "site_name" = 'IWD';
UPDATE "job_search_site" SET "url" = 'https://www.linkedin.com/' WHERE "site_name" = 'LinkedIn';
UPDATE "job_search_site" SET "url" = 'http://www.seek.com.au/' WHERE "site_name" = 'Seek';
UPDATE "job_search_site" SET "url" = 'https://www.ziprecruiter.com/' WHERE "site_name" = 'ZipRecruiter';
