"""
Tests for database constraints (NOT NULL, UNIQUE, etc.).
"""
import pytest
import sqlite3


class TestConstraints:
    """Test database constraints."""
    
    def test_application_status_not_null(self, db_connection):
        """Test that application.status cannot be NULL (when no default)."""
        # Note: SQLite allows NULLs even with NOT NULL if default exists
        # This test verifies the constraint exists
        # In practice, defaults will be applied, so this may not raise an error
        # We'll test by trying to explicitly set NULL
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
                VALUES (NULL, 'Remote', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_application_work_setting_not_null(self, db_connection):
        """Test that application.work_setting cannot be NULL (when no default)."""
        # Test by explicitly setting NULL
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
                VALUES ('Pending', NULL, 'test', 'test')
            """)
            db_connection.commit()
    
    def test_application_created_by_not_null(self, db_connection):
        """Test that application.created_by cannot be NULL."""
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application" ("status", "work_setting", "modified_by")
                VALUES ('Pending', 'Remote', 'test')
            """)
            db_connection.commit()
    
    def test_application_modified_by_not_null(self, db_connection):
        """Test that application.modified_by cannot be NULL."""
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application" ("status", "work_setting", "created_by")
                VALUES ('Pending', 'Remote', 'test')
            """)
            db_connection.commit()
    
    def test_company_name_not_null(self, db_connection):
        """Test that company.name cannot be NULL."""
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "company" ("created_by", "modified_by")
                VALUES ('test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_first_name_not_null(self, db_connection):
        """Test that contact.first_name cannot be NULL."""
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact" ("last_name", "contact_type", "created_by", "modified_by")
                VALUES ('Doe', 'Recruiter', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_last_name_not_null(self, db_connection):
        """Test that contact.last_name cannot be NULL."""
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact" ("first_name", "contact_type", "created_by", "modified_by")
                VALUES ('John', 'Recruiter', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_contact_type_not_null(self, db_connection):
        """Test that contact.contact_type cannot be NULL."""
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact" ("first_name", "last_name", "created_by", "modified_by")
                VALUES ('John', 'Doe', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_email_email_not_null(self, db_connection):
        """Test that contact_email.email cannot be NULL."""
        # Create contact first
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact_email" ("contact_id", "created_by", "modified_by")
                VALUES (?, 'test', 'test')
            """, (contact_id,))
            db_connection.commit()
    
    def test_contact_phone_phone_not_null(self, db_connection):
        """Test that contact_phone.phone cannot be NULL."""
        # Create contact first
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact_phone" ("contact_id", "created_by", "modified_by")
                VALUES (?, 'test', 'test')
            """, (contact_id,))
            db_connection.commit()
    
    def test_note_note_not_null(self, db_connection):
        """Test that note.note cannot be NULL."""
        # Create application first
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "note" ("application_id", "created_by", "modified_by")
                VALUES (?, 'test', 'test')
            """, (application_id,))
            db_connection.commit()
    
    def test_job_search_site_name_unique(self, db_connection):
        """Test that job_search_site.site_name must be unique."""
        # Create first site
        db_connection.execute("""
            INSERT INTO "job_search_site" ("site_name", "created_by", "modified_by")
            VALUES ('LinkedIn', 'test', 'test')
        """)
        db_connection.commit()
        
        # Try to create duplicate (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "job_search_site" ("site_name", "created_by", "modified_by")
                VALUES ('LinkedIn', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_default_value_unique_constraint(self, db_connection):
        """Test that default_value has unique constraint on table_name, field_name, user_id."""
        # Create system default
        db_connection.execute("""
            INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "created_by", "modified_by")
            VALUES ('application', 'status', 'Pending', 'TEXT', 'system', 'test', 'test')
        """)
        db_connection.commit()
        
        # Try to create duplicate (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "created_by", "modified_by")
                VALUES ('application', 'status', 'Active', 'TEXT', 'system', 'test', 'test')
            """)
            db_connection.commit()
        
        # But should allow different user_id
        db_connection.execute("""
            INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "created_by", "modified_by")
            VALUES ('application', 'status', 'Active', 'TEXT', 'user@example.com', 'test', 'test')
        """)
        db_connection.commit()  # Should succeed
