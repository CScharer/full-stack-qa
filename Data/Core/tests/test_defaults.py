"""
Tests for default values and constraints.
"""
import pytest
import sqlite3


class TestDefaults:
    """Test default values and constraints."""
    
    def test_is_deleted_defaults_to_zero(self, db_connection):
        """Test that is_deleted defaults to 0 (active)."""
        # Create application without specifying is_deleted
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        db_connection.commit()
        
        # Check is_deleted value
        cursor = db_connection.execute("SELECT is_deleted FROM application WHERE id = ?", (application_id,))
        is_deleted = cursor.fetchone()[0]
        assert is_deleted == 0, "is_deleted should default to 0 (active)"
    
    def test_created_on_modified_on_defaults(self, db_connection):
        """Test that created_on and modified_on are set automatically."""
        # Create application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        db_connection.commit()
        
        # Check timestamps
        cursor = db_connection.execute("""
            SELECT created_on, modified_on 
            FROM application 
            WHERE id = ?
        """, (application_id,))
        row = cursor.fetchone()
        
        assert row[0] is not None, "created_on should be set"
        assert row[1] is not None, "modified_on should be set"
        assert row[0] == row[1], "created_on and modified_on should be equal on insert"
    
    def test_application_status_default(self, db_connection):
        """Test that application.status defaults to 'Pending'."""
        # Create application without specifying status
        cursor = db_connection.execute("""
            INSERT INTO "application" ("work_setting", "created_by", "modified_by")
            VALUES ('Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        db_connection.commit()
        
        # Check status
        cursor = db_connection.execute("SELECT status FROM application WHERE id = ?", (application_id,))
        status = cursor.fetchone()[0]
        assert status == 'Pending', "status should default to 'Pending'"
    
    def test_application_work_setting_default(self, db_connection):
        """Test that application.work_setting defaults to 'Remote'."""
        # Create application without specifying work_setting
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "created_by", "modified_by")
            VALUES ('Pending', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        db_connection.commit()
        
        # Check work_setting
        cursor = db_connection.execute("SELECT work_setting FROM application WHERE id = ?", (application_id,))
        work_setting = cursor.fetchone()[0]
        assert work_setting == 'Remote', "work_setting should default to 'Remote'"
    
    def test_company_country_default(self, db_connection):
        """Test that company.country defaults to 'United States'."""
        # Create company without specifying country
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Test Company', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        db_connection.commit()
        
        # Check country
        cursor = db_connection.execute("SELECT country FROM company WHERE id = ?", (company_id,))
        country = cursor.fetchone()[0]
        assert country == 'United States', "country should default to 'United States'"
    
    def test_company_job_type_default(self, db_connection):
        """Test that company.job_type defaults to 'Technology'."""
        # Create company without specifying job_type
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Test Company', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        db_connection.commit()
        
        # Check job_type
        cursor = db_connection.execute("SELECT job_type FROM company WHERE id = ?", (company_id,))
        job_type = cursor.fetchone()[0]
        assert job_type == 'Technology', "job_type should default to 'Technology'"
    
    def test_contact_title_default(self, db_connection):
        """Test that contact.title defaults to 'Recruiter'."""
        # Create contact without specifying title
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        db_connection.commit()
        
        # Check title
        cursor = db_connection.execute("SELECT title FROM contact WHERE id = ?", (contact_id,))
        title = cursor.fetchone()[0]
        assert title == 'Recruiter', "title should default to 'Recruiter'"
    
    def test_contact_email_type_default(self, db_connection):
        """Test that contact_email.email_type defaults to 'Work'."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create email without specifying email_type
        cursor = db_connection.execute("""
            INSERT INTO "contact_email" ("contact_id", "email", "created_by", "modified_by")
            VALUES (?, 'john@example.com', 'test', 'test')
        """, (contact_id,))
        email_id = cursor.lastrowid
        db_connection.commit()
        
        # Check email_type
        cursor = db_connection.execute("SELECT email_type FROM contact_email WHERE id = ?", (email_id,))
        email_type = cursor.fetchone()[0]
        assert email_type == 'Work', "email_type should default to 'Work'"
    
    def test_contact_phone_type_default(self, db_connection):
        """Test that contact_phone.phone_type defaults to 'Work'."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create phone without specifying phone_type
        cursor = db_connection.execute("""
            INSERT INTO "contact_phone" ("contact_id", "phone", "created_by", "modified_by")
            VALUES (?, '555-1234', 'test', 'test')
        """, (contact_id,))
        phone_id = cursor.lastrowid
        db_connection.commit()
        
        # Check phone_type
        cursor = db_connection.execute("SELECT phone_type FROM contact_phone WHERE id = ?", (phone_id,))
        phone_type = cursor.fetchone()[0]
        assert phone_type == 'Work', "phone_type should default to 'Work'"
    
    def test_entered_iwd_default(self, db_connection):
        """Test that application.entered_iwd defaults to 0."""
        # Create application without specifying entered_iwd
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        db_connection.commit()
        
        # Check entered_iwd
        cursor = db_connection.execute("SELECT entered_iwd FROM application WHERE id = ?", (application_id,))
        entered_iwd = cursor.fetchone()[0]
        assert entered_iwd == 0, "entered_iwd should default to 0"
    
    def test_is_primary_defaults_to_zero(self, db_connection):
        """Test that is_primary defaults to 0 (not primary)."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create email without specifying is_primary
        cursor = db_connection.execute("""
            INSERT INTO "contact_email" ("contact_id", "email", "created_by", "modified_by")
            VALUES (?, 'john@example.com', 'test', 'test')
        """, (contact_id,))
        email_id = cursor.lastrowid
        db_connection.commit()
        
        # Check is_primary
        cursor = db_connection.execute("SELECT is_primary FROM contact_email WHERE id = ?", (email_id,))
        is_primary = cursor.fetchone()[0]
        assert is_primary == 0, "is_primary should default to 0 (not primary)"
