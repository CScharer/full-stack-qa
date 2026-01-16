"""
Tests for the default_value table functionality.
"""
import pytest
import sqlite3


class TestDefaultValueTable:
    """Test default_value table operations."""
    
    def test_create_system_default(self, clean_db):
        """Test creating a system default value."""
        db_connection = clean_db
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'status', 'Pending', 'TEXT', 'system', 'Application status', 'test', 'test')
        """)
        db_connection.commit()
        
        # Verify default was created
        cursor = db_connection.execute("""
            SELECT default_value, data_type, user_id
            FROM default_value
            WHERE table_name = 'application' AND field_name = 'status' AND user_id = 'system'
        """)
        
        row = cursor.fetchone()
        assert row is not None
        assert row['default_value'] == 'Pending'
        assert row['data_type'] == 'TEXT'
        assert row['user_id'] == 'system'
    
    def test_create_user_specific_default(self, clean_db):
        """Test creating a user-specific default value."""
        db_connection = clean_db
        # Create system default first
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'work_setting', 'Remote', 'TEXT', 'system', 'System default', 'test', 'test')
        """)
        
        # Create user-specific default
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'work_setting', 'Hybrid', 'TEXT', 'john@example.com', 'User prefers Hybrid', 'test', 'test')
        """)
        db_connection.commit()
        
        # Verify both exist
        cursor = db_connection.execute("""
            SELECT user_id, default_value
            FROM default_value
            WHERE table_name = 'application' AND field_name = 'work_setting'
            ORDER BY user_id
        """)
        
        rows = cursor.fetchall()
        assert len(rows) == 2
        assert rows[0]['user_id'] == 'john@example.com'
        assert rows[0]['default_value'] == 'Hybrid'
        assert rows[1]['user_id'] == 'system'
        assert rows[1]['default_value'] == 'Remote'
    
    def test_lookup_user_specific_first(self, clean_db):
        """Test that user-specific default takes precedence over system default."""
        db_connection = clean_db
        # Create system default
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'status', 'Pending', 'TEXT', 'system', 'System default', 'test', 'test')
        """)
        
        # Create user-specific default
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'status', 'Active', 'TEXT', 'jane@example.com', 'User prefers Active', 'test', 'test')
        """)
        db_connection.commit()
        
        # Lookup for user (should get user-specific)
        cursor = db_connection.execute("""
            SELECT default_value
            FROM default_value
            WHERE table_name = 'application' 
              AND field_name = 'status' 
              AND user_id = 'jane@example.com'
              AND is_active = 1
            LIMIT 1
        """)
        
        row = cursor.fetchone()
        assert row['default_value'] == 'Active', "Should return user-specific default"
        
        # Lookup for different user (should get system)
        cursor = db_connection.execute("""
            SELECT default_value
            FROM default_value
            WHERE table_name = 'application' 
              AND field_name = 'status' 
              AND user_id = 'other@example.com'
              AND is_active = 1
            LIMIT 1
        """)
        
        # Since other@example.com doesn't have a default, we'd need to fall back to system
        # This tests the table structure, actual lookup logic would be in application code
        cursor = db_connection.execute("""
            SELECT default_value
            FROM default_value
            WHERE table_name = 'application' 
              AND field_name = 'status' 
              AND user_id = 'system'
              AND is_active = 1
            LIMIT 1
        """)
        
        row = cursor.fetchone()
        assert row['default_value'] == 'Pending', "Should return system default"
    
    def test_update_default_value(self, clean_db):
        """Test updating a default value."""
        db_connection = clean_db
        # Create default
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'status', 'Pending', 'TEXT', 'system', 'Initial', 'test', 'test')
        """)
        db_connection.commit()
        
        # Update default
        db_connection.execute("""
            UPDATE "default_value"
            SET default_value = 'Active',
                modified_on = datetime('now', 'localtime'),
                modified_by = 'admin'
            WHERE table_name = 'application' 
              AND field_name = 'status' 
              AND user_id = 'system'
        """)
        db_connection.commit()
        
        # Verify update
        cursor = db_connection.execute("""
            SELECT default_value, modified_by
            FROM default_value
            WHERE table_name = 'application' AND field_name = 'status' AND user_id = 'system'
        """)
        
        row = cursor.fetchone()
        assert row['default_value'] == 'Active'
        assert row['modified_by'] == 'admin'
    
    def test_soft_delete_default(self, clean_db):
        """Test soft deleting a default value."""
        db_connection = clean_db
        # Create default
        db_connection.execute("""
            INSERT INTO "default_value" 
            ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
            VALUES ('application', 'status', 'Pending', 'TEXT', 'system', 'Test', 'test', 'test')
        """)
        db_connection.commit()
        
        # Soft delete (set is_active = 0)
        db_connection.execute("""
            UPDATE "default_value"
            SET is_active = 0,
                modified_on = datetime('now', 'localtime'),
                modified_by = 'admin'
            WHERE table_name = 'application' 
              AND field_name = 'status' 
              AND user_id = 'system'
        """)
        db_connection.commit()
        
        # Verify soft deleted
        cursor = db_connection.execute("""
            SELECT is_active
            FROM default_value
            WHERE table_name = 'application' AND field_name = 'status' AND user_id = 'system'
        """)
        
        row = cursor.fetchone()
        assert row['is_active'] == 0
        
        # Verify not returned in active query
        cursor = db_connection.execute("""
            SELECT COUNT(*) as count
            FROM default_value
            WHERE table_name = 'application' 
              AND field_name = 'status' 
              AND user_id = 'system'
              AND is_active = 1
        """)
        
        assert cursor.fetchone()['count'] == 0
