#!/usr/bin/env python3
"""
Script to seed job search sites into the database.
This script can be run multiple times safely (uses INSERT OR IGNORE).
"""

import sqlite3
import sys
from pathlib import Path

# Get the project root (go up from Data/Core/scripts to project root)
PROJECT_ROOT = Path(__file__).parent.parent.parent.parent
DB_PATH = PROJECT_ROOT / "Data" / "Core" / "full_stack_qa.db"

JOB_SEARCH_SITES = [
    {'name': 'CyberCoders', 'url': 'http://www.cybercoders.com'},
    {'name': 'Dice', 'url': 'http://www.dice.com/'},
    {'name': 'Indeed', 'url': 'https://www.indeed.com/'},
    {'name': 'IWD', 'url': 'https://www.iowaworks.gov'},
    {'name': 'LinkedIn', 'url': 'https://www.linkedin.com/'},
    {'name': 'Seek', 'url': 'http://www.seek.com.au/'},
    {'name': 'ZipRecruiter', 'url': 'https://www.ziprecruiter.com/'},
]


def seed_job_search_sites():
    """Seed job search sites into the database."""
    if not DB_PATH.exists():
        print(f"❌ Database not found at: {DB_PATH}")
        print("   Please create the database first using the schema file.")
        sys.exit(1)
    
    print(f"📊 Seeding job search sites into: {DB_PATH}")
    
    conn = sqlite3.connect(str(DB_PATH))
    conn.row_factory = sqlite3.Row
    
    try:
        # Enable foreign keys
        conn.execute("PRAGMA foreign_keys = ON")
        
        # Check if table exists
        cursor = conn.execute("""
            SELECT name FROM sqlite_master 
            WHERE type='table' AND name='job_search_site'
        """)
        if not cursor.fetchone():
            print("❌ Table 'job_search_site' does not exist.")
            print("   Please create the database schema first.")
            sys.exit(1)
        
        # Check if url column exists
        cursor = conn.execute("PRAGMA table_info(job_search_site)")
        columns = [row[1] for row in cursor.fetchall()]
        has_url_column = 'url' in columns
        
        # Insert sites (using INSERT OR IGNORE to avoid duplicates)
        inserted_count = 0
        skipped_count = 0
        updated_count = 0
        
        for site in JOB_SEARCH_SITES:
            site_name = site['name']
            site_url = site['url']
            
            try:
                # Check if site already exists
                cursor = conn.execute("""
                    SELECT id, url FROM job_search_site 
                    WHERE site_name = ?
                """, (site_name,))
                existing = cursor.fetchone()
                
                if existing:
                    # Site exists - update URL if column exists and URL is different
                    if has_url_column and existing['url'] != site_url:
                        conn.execute("""
                            UPDATE job_search_site 
                            SET url = ?, modified_by = 'system', 
                                modified_on = datetime('now', 'localtime')
                            WHERE id = ?
                        """, (site_url, existing['id']))
                        updated_count += 1
                        print(f"  🔄 Updated URL for: {site_name}")
                    else:
                        skipped_count += 1
                        print(f"  ⏭️  Skipped (already exists): {site_name}")
                else:
                    # Site doesn't exist - insert it
                    if has_url_column:
                        cursor = conn.execute("""
                            INSERT INTO job_search_site 
                            (site_name, url, created_by, modified_by)
                            VALUES (?, ?, 'system', 'system')
                        """, (site_name, site_url))
                    else:
                        cursor = conn.execute("""
                            INSERT INTO job_search_site 
                            (site_name, created_by, modified_by)
                            VALUES (?, 'system', 'system')
                        """, (site_name,))
                    
                    if cursor.rowcount > 0:
                        inserted_count += 1
                        print(f"  ✅ Inserted: {site_name}")
            except sqlite3.Error as e:
                print(f"  ❌ Error processing {site_name}: {e}")
        
        conn.commit()
        
        print(f"\n✅ Seeding complete!")
        print(f"   Inserted: {inserted_count} sites")
        print(f"   Updated: {updated_count} sites")
        print(f"   Skipped: {skipped_count} sites (already existed)")
        
        # Verify by listing all sites
        if has_url_column:
            cursor = conn.execute("""
                SELECT site_name, url FROM job_search_site 
                WHERE is_deleted = 0 
                ORDER BY site_name
            """)
            sites = [(row[0], row[1]) for row in cursor.fetchall()]
            
            print(f"\n📋 Current job search sites in database ({len(sites)}):")
            for site_name, url in sites:
                if url:
                    print(f"   - {site_name}: {url}")
                else:
                    print(f"   - {site_name}: (no URL)")
        else:
            cursor = conn.execute("""
                SELECT site_name FROM job_search_site 
                WHERE is_deleted = 0 
                ORDER BY site_name
            """)
            sites = [row[0] for row in cursor.fetchall()]
            
            print(f"\n📋 Current job search sites in database ({len(sites)}):")
            for site in sites:
                print(f"   - {site}")
        
    except sqlite3.Error as e:
        print(f"❌ Database error: {e}")
        conn.rollback()
        sys.exit(1)
    finally:
        conn.close()


if __name__ == "__main__":
    seed_job_search_sites()
