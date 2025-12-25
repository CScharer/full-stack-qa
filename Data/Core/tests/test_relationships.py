"""
Tests for entity relationships and data integrity.
"""
import pytest
import sqlite3


class TestRelationships:
    """Test entity relationships and data integrity."""
    
    def test_application_with_company_and_client(self, db_connection):
        """Test creating application with company and client relationships."""
        # Create company
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Tech Recruiters Inc', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        
        # Create client
        cursor = db_connection.execute("""
            INSERT INTO "client" ("name", "created_by", "modified_by")
            VALUES ('Google', 'test', 'test')
        """)
        client_id = cursor.lastrowid
        
        # Create application with both relationships
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "position", "company_id", "client_id", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'Software Engineer', ?, ?, 'test', 'test')
        """, (company_id, client_id))
        application_id = cursor.lastrowid
        db_connection.commit()
        
        # Verify relationships
        cursor = db_connection.execute("""
            SELECT a.id, a.position, c.name as company_name, cl.name as client_name
            FROM application a
            LEFT JOIN company c ON a.company_id = c.id
            LEFT JOIN client cl ON a.client_id = cl.id
            WHERE a.id = ?
        """, (application_id,))
        
        row = cursor.fetchone()
        assert row['company_name'] == 'Tech Recruiters Inc'
        assert row['client_name'] == 'Google'
        assert row['position'] == 'Software Engineer'
    
    def test_contact_with_multiple_emails(self, db_connection):
        """Test creating contact with multiple emails."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create multiple emails
        cursor = db_connection.execute("""
            INSERT INTO "contact_email" ("contact_id", "email", "email_type", "is_primary", "created_by", "modified_by")
            VALUES (?, 'john.work@example.com', 'Work', 1, 'test', 'test')
        """, (contact_id,))
        
        cursor = db_connection.execute("""
            INSERT INTO "contact_email" ("contact_id", "email", "email_type", "is_primary", "created_by", "modified_by")
            VALUES (?, 'john.personal@example.com', 'Personal', 0, 'test', 'test')
        """, (contact_id,))
        
        db_connection.commit()
        
        # Verify emails
        cursor = db_connection.execute("""
            SELECT email, email_type, is_primary
            FROM contact_email
            WHERE contact_id = ?
            ORDER BY is_primary DESC
        """, (contact_id,))
        
        emails = cursor.fetchall()
        assert len(emails) == 2
        assert emails[0]['is_primary'] == 1
        assert emails[0]['email'] == 'john.work@example.com'
        assert emails[1]['email'] == 'john.personal@example.com'
    
    def test_contact_with_multiple_phones(self, db_connection):
        """Test creating contact with multiple phone numbers."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('Jane', 'Doe', 'Manager', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create multiple phones
        cursor = db_connection.execute("""
            INSERT INTO "contact_phone" ("contact_id", "phone", "phone_type", "is_primary", "created_by", "modified_by")
            VALUES (?, '555-1234', 'Work', 1, 'test', 'test')
        """, (contact_id,))
        
        cursor = db_connection.execute("""
            INSERT INTO "contact_phone" ("contact_id", "phone", "phone_type", "is_primary", "created_by", "modified_by")
            VALUES (?, '555-5678', 'Cell', 0, 'test', 'test')
        """, (contact_id,))
        
        db_connection.commit()
        
        # Verify phones
        cursor = db_connection.execute("""
            SELECT phone, phone_type, is_primary
            FROM contact_phone
            WHERE contact_id = ?
            ORDER BY is_primary DESC
        """, (contact_id,))
        
        phones = cursor.fetchall()
        assert len(phones) == 2
        assert phones[0]['is_primary'] == 1
        assert phones[0]['phone'] == '555-1234'
        assert phones[1]['phone'] == '555-5678'
    
    def test_application_with_multiple_notes(self, db_connection):
        """Test creating application with multiple notes."""
        # Create application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        
        # Create multiple notes
        cursor = db_connection.execute("""
            INSERT INTO "note" ("application_id", "note", "created_by", "modified_by")
            VALUES (?, 'Initial phone screen completed', 'test', 'test')
        """, (application_id,))
        
        cursor = db_connection.execute("""
            INSERT INTO "note" ("application_id", "note", "created_by", "modified_by")
            VALUES (?, 'Follow up scheduled for next week', 'test', 'test')
        """, (application_id,))
        
        db_connection.commit()
        
        # Verify notes
        cursor = db_connection.execute("""
            SELECT note
            FROM note
            WHERE application_id = ?
            ORDER BY created_on
        """, (application_id,))
        
        notes = cursor.fetchall()
        assert len(notes) == 2
        assert 'phone screen' in notes[0]['note']
        assert 'Follow up' in notes[1]['note']
    
    def test_contact_linked_to_company_application_and_client(self, db_connection):
        """Test contact can be linked to company, application, and client simultaneously."""
        # Create company
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Tech Recruiters', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        
        # Create client
        cursor = db_connection.execute("""
            INSERT INTO "client" ("name", "created_by", "modified_by")
            VALUES ('Microsoft', 'test', 'test')
        """)
        client_id = cursor.lastrowid
        
        # Create application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "company_id", "client_id", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', ?, ?, 'test', 'test')
        """, (company_id, client_id))
        application_id = cursor.lastrowid
        
        # Create contact linked to all three
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "company_id", "application_id", "client_id", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', ?, ?, ?, 'test', 'test')
        """, (company_id, application_id, client_id))
        contact_id = cursor.lastrowid
        db_connection.commit()
        
        # Verify all relationships
        cursor = db_connection.execute("""
            SELECT c.first_name, c.last_name, c.company_id, c.application_id, c.client_id,
                   co.name as company_name, cl.name as client_name, a.id as app_id
            FROM contact c
            LEFT JOIN company co ON c.company_id = co.id
            LEFT JOIN client cl ON c.client_id = cl.id
            LEFT JOIN application a ON c.application_id = a.id
            WHERE c.id = ?
        """, (contact_id,))
        
        row = cursor.fetchone()
        assert row['first_name'] == 'John'
        assert row['last_name'] == 'Doe'
        assert row['company_name'] == 'Tech Recruiters'
        assert row['client_name'] == 'Microsoft'
        assert row['app_id'] == application_id
