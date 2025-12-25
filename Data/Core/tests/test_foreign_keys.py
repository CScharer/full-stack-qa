"""
Tests for Foreign Key constraints and relationships.
"""
import pytest
import sqlite3


class TestForeignKeys:
    """Test Foreign Key constraints."""
    
    def test_application_company_fk(self, db_connection):
        """Test that application.company_id references company.id."""
        # Create a company
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Test Company', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        
        # Create application with valid company_id
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "company_id", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', ?, 'test', 'test')
        """, (company_id,))
        db_connection.commit()
        
        # Try to create application with invalid company_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application" ("status", "work_setting", "company_id", "created_by", "modified_by")
                VALUES ('Pending', 'Remote', 99999, 'test', 'test')
            """)
            db_connection.commit()
    
    def test_application_client_fk(self, db_connection):
        """Test that application.client_id references client.id."""
        # Create a client
        cursor = db_connection.execute("""
            INSERT INTO "client" ("name", "created_by", "modified_by")
            VALUES ('Test Client', 'test', 'test')
        """)
        client_id = cursor.lastrowid
        
        # Create application with valid client_id
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "client_id", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', ?, 'test', 'test')
        """, (client_id,))
        db_connection.commit()
        
        # Try to create application with invalid client_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application" ("status", "work_setting", "client_id", "created_by", "modified_by")
                VALUES ('Pending', 'Remote', 99999, 'test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_company_fk(self, db_connection):
        """Test that contact.company_id references company.id."""
        # Create a company
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Test Company', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        
        # Create contact with valid company_id
        db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "company_id", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', ?, 'test', 'test')
        """, (company_id,))
        db_connection.commit()
        
        # Try to create contact with invalid company_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact" ("first_name", "last_name", "contact_type", "company_id", "created_by", "modified_by")
                VALUES ('Jane', 'Doe', 'Recruiter', 99999, 'test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_email_contact_fk(self, db_connection):
        """Test that contact_email.contact_id references contact.id."""
        # Create a contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create contact_email with valid contact_id
        cursor = db_connection.execute("""
            INSERT INTO "contact_email" ("contact_id", "email", "created_by", "modified_by")
            VALUES (?, 'john@example.com', 'test', 'test')
        """, (contact_id,))
        db_connection.commit()
        
        # Try to create contact_email with invalid contact_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact_email" ("contact_id", "email", "created_by", "modified_by")
                VALUES (99999, 'invalid@example.com', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_contact_phone_contact_fk(self, db_connection):
        """Test that contact_phone.contact_id references contact.id."""
        # Create a contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create contact_phone with valid contact_id
        cursor = db_connection.execute("""
            INSERT INTO "contact_phone" ("contact_id", "phone", "created_by", "modified_by")
            VALUES (?, '555-1234', 'test', 'test')
        """, (contact_id,))
        db_connection.commit()
        
        # Try to create contact_phone with invalid contact_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "contact_phone" ("contact_id", "phone", "created_by", "modified_by")
                VALUES (99999, '555-9999', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_note_application_fk(self, db_connection):
        """Test that note.application_id references application.id."""
        # Create an application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        
        # Create note with valid application_id
        cursor = db_connection.execute("""
            INSERT INTO "note" ("application_id", "note", "created_by", "modified_by")
            VALUES (?, 'Test note', 'test', 'test')
        """, (application_id,))
        db_connection.commit()
        
        # Try to create note with invalid application_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "note" ("application_id", "note", "created_by", "modified_by")
                VALUES (99999, 'Invalid note', 'test', 'test')
            """)
            db_connection.commit()
    
    def test_application_sync_application_fk(self, db_connection):
        """Test that application_sync.sqlite_id references application.id."""
        # Create an application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        
        # Create application_sync with valid sqlite_id
        cursor = db_connection.execute("""
            INSERT INTO "application_sync" ("sqlite_id", "mongodb_id", "created_by", "modified_by")
            VALUES (?, 'mongodb-123', 'test', 'test')
        """, (application_id,))
        db_connection.commit()
        
        # Try to create application_sync with invalid sqlite_id (should fail)
        with pytest.raises(sqlite3.IntegrityError):
            db_connection.execute("""
                INSERT INTO "application_sync" ("sqlite_id", "mongodb_id", "created_by", "modified_by")
                VALUES (99999, 'mongodb-999', 'test', 'test')
            """)
            db_connection.commit()
