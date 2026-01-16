"""
Tests for database delete triggers.
"""
import pytest
import sqlite3


class TestDeleteTriggers:
    """Test that delete triggers work correctly."""
    
    def test_application_delete_triggers_sync_deletion(self, db_connection):
        """Test that deleting an application deletes related application_sync records."""
        # Create application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        
        # Create application_sync records
        cursor = db_connection.execute("""
            INSERT INTO "application_sync" ("sqlite_id", "mongodb_id", "created_by", "modified_by")
            VALUES (?, 'mongodb-123', 'test', 'test')
        """, (application_id,))
        sync_id_1 = cursor.lastrowid
        
        cursor = db_connection.execute("""
            INSERT INTO "application_sync" ("sqlite_id", "mongodb_id", "created_by", "modified_by")
            VALUES (?, 'mongodb-456', 'test', 'test')
        """, (application_id,))
        sync_id_2 = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify sync records exist
        cursor = db_connection.execute("SELECT COUNT(*) FROM application_sync WHERE sqlite_id = ?", (application_id,))
        assert cursor.fetchone()[0] == 2
        
        # Delete application (trigger should delete sync records)
        db_connection.execute("DELETE FROM application WHERE id = ?", (application_id,))
        db_connection.commit()
        
        # Verify sync records are deleted (trigger)
        cursor = db_connection.execute("SELECT COUNT(*) FROM application_sync WHERE id IN (?, ?)", (sync_id_1, sync_id_2))
        assert cursor.fetchone()[0] == 0, "Sync records should be deleted when application is deleted"
    
    def test_application_delete_triggers_contact_application_id_null(self, db_connection):
        """Test that deleting an application sets contact.application_id to NULL."""
        # Create application
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', 'test', 'test')
        """)
        application_id = cursor.lastrowid
        
        # Create contact linked to application
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "application_id", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', ?, 'test', 'test')
        """, (application_id,))
        contact_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify contact is linked
        cursor = db_connection.execute("SELECT application_id FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] == application_id
        
        # Delete application (trigger should set contact.application_id to NULL)
        db_connection.execute("DELETE FROM application WHERE id = ?", (application_id,))
        db_connection.commit()
        
        # Verify contact.application_id is NULL (trigger)
        cursor = db_connection.execute("SELECT application_id FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] is None, "contact.application_id should be NULL when application is deleted"
        
        # Verify contact still exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] == 1, "Contact should still exist after application deletion"
    
    def test_company_delete_triggers_application_company_id_null(self, db_connection):
        """Test that deleting a company sets application.company_id to NULL."""
        # Create company
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Tech Recruiters', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        
        # Create application linked to company
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "company_id", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', ?, 'test', 'test')
        """, (company_id,))
        application_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify application is linked
        cursor = db_connection.execute("SELECT company_id FROM application WHERE id = ?", (application_id,))
        assert cursor.fetchone()[0] == company_id
        
        # Delete company (trigger should set application.company_id to NULL)
        db_connection.execute("DELETE FROM company WHERE id = ?", (company_id,))
        db_connection.commit()
        
        # Verify application.company_id is NULL (trigger)
        cursor = db_connection.execute("SELECT company_id FROM application WHERE id = ?", (application_id,))
        assert cursor.fetchone()[0] is None, "application.company_id should be NULL when company is deleted"
        
        # Verify application still exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM application WHERE id = ?", (application_id,))
        assert cursor.fetchone()[0] == 1, "Application should still exist after company deletion"
    
    def test_company_delete_triggers_contact_company_id_null(self, db_connection):
        """Test that deleting a company sets contact.company_id to NULL."""
        # Create company
        cursor = db_connection.execute("""
            INSERT INTO "company" ("name", "created_by", "modified_by")
            VALUES ('Tech Recruiters', 'test', 'test')
        """)
        company_id = cursor.lastrowid
        
        # Create contact linked to company
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "company_id", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', ?, 'test', 'test')
        """, (company_id,))
        contact_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify contact is linked
        cursor = db_connection.execute("SELECT company_id FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] == company_id
        
        # Delete company (trigger should set contact.company_id to NULL)
        db_connection.execute("DELETE FROM company WHERE id = ?", (company_id,))
        db_connection.commit()
        
        # Verify contact.company_id is NULL (trigger)
        cursor = db_connection.execute("SELECT company_id FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] is None, "contact.company_id should be NULL when company is deleted"
        
        # Verify contact still exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] == 1, "Contact should still exist after company deletion"
    
    def test_client_delete_triggers_application_client_id_null(self, db_connection):
        """Test that deleting a client sets application.client_id to NULL."""
        # Create client
        cursor = db_connection.execute("""
            INSERT INTO "client" ("name", "created_by", "modified_by")
            VALUES ('Google', 'test', 'test')
        """)
        client_id = cursor.lastrowid
        
        # Create application linked to client
        cursor = db_connection.execute("""
            INSERT INTO "application" ("status", "work_setting", "client_id", "created_by", "modified_by")
            VALUES ('Pending', 'Remote', ?, 'test', 'test')
        """, (client_id,))
        application_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify application is linked
        cursor = db_connection.execute("SELECT client_id FROM application WHERE id = ?", (application_id,))
        assert cursor.fetchone()[0] == client_id
        
        # Delete client (trigger should set application.client_id to NULL)
        db_connection.execute("DELETE FROM client WHERE id = ?", (client_id,))
        db_connection.commit()
        
        # Verify application.client_id is NULL (trigger)
        cursor = db_connection.execute("SELECT client_id FROM application WHERE id = ?", (application_id,))
        assert cursor.fetchone()[0] is None, "application.client_id should be NULL when client is deleted"
        
        # Verify application still exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM application WHERE id = ?", (application_id,))
        assert cursor.fetchone()[0] == 1, "Application should still exist after client deletion"
    
    def test_client_delete_triggers_contact_client_id_null(self, db_connection):
        """Test that deleting a client sets contact.client_id to NULL."""
        # Create client
        cursor = db_connection.execute("""
            INSERT INTO "client" ("name", "created_by", "modified_by")
            VALUES ('Google', 'test', 'test')
        """)
        client_id = cursor.lastrowid
        
        # Create contact linked to client
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "client_id", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', ?, 'test', 'test')
        """, (client_id,))
        contact_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify contact is linked
        cursor = db_connection.execute("SELECT client_id FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] == client_id
        
        # Delete client (trigger should set contact.client_id to NULL)
        db_connection.execute("DELETE FROM client WHERE id = ?", (client_id,))
        db_connection.commit()
        
        # Verify contact.client_id is NULL (trigger)
        cursor = db_connection.execute("SELECT client_id FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] is None, "contact.client_id should be NULL when client is deleted"
        
        # Verify contact still exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact WHERE id = ?", (contact_id,))
        assert cursor.fetchone()[0] == 1, "Contact should still exist after client deletion"
    
    def test_contact_delete_triggers_email_deletion(self, db_connection):
        """Test that deleting a contact deletes related emails (FK CASCADE + trigger)."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create emails
        cursor = db_connection.execute("""
            INSERT INTO "contact_email" ("contact_id", "email", "created_by", "modified_by")
            VALUES (?, 'john@example.com', 'test', 'test')
        """, (contact_id,))
        email_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify email exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact_email WHERE contact_id = ?", (contact_id,))
        assert cursor.fetchone()[0] == 1
        
        # Delete contact (FK CASCADE + trigger should delete emails)
        db_connection.execute("DELETE FROM contact WHERE id = ?", (contact_id,))
        db_connection.commit()
        
        # Verify email is deleted
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact_email WHERE id = ?", (email_id,))
        assert cursor.fetchone()[0] == 0, "Email should be deleted when contact is deleted"
    
    def test_contact_delete_triggers_phone_deletion(self, db_connection):
        """Test that deleting a contact deletes related phones (FK CASCADE + trigger)."""
        # Create contact
        cursor = db_connection.execute("""
            INSERT INTO "contact" ("first_name", "last_name", "contact_type", "created_by", "modified_by")
            VALUES ('John', 'Doe', 'Recruiter', 'test', 'test')
        """)
        contact_id = cursor.lastrowid
        
        # Create phone
        cursor = db_connection.execute("""
            INSERT INTO "contact_phone" ("contact_id", "phone", "created_by", "modified_by")
            VALUES (?, '555-1234', 'test', 'test')
        """, (contact_id,))
        phone_id = cursor.lastrowid
        
        db_connection.commit()
        
        # Verify phone exists
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact_phone WHERE contact_id = ?", (contact_id,))
        assert cursor.fetchone()[0] == 1
        
        # Delete contact (FK CASCADE + trigger should delete phones)
        db_connection.execute("DELETE FROM contact WHERE id = ?", (contact_id,))
        db_connection.commit()
        
        # Verify phone is deleted
        cursor = db_connection.execute("SELECT COUNT(*) FROM contact_phone WHERE id = ?", (phone_id,))
        assert cursor.fetchone()[0] == 0, "Phone should be deleted when contact is deleted"
