"""
Tests for database schema creation and structure.
"""
import pytest
import sqlite3


class TestSchemaCreation:
    """Test that schema is created correctly."""
    
    def test_all_tables_created(self, test_database):
        """Verify all expected tables are created."""
        cursor = test_database.execute("""
            SELECT name FROM sqlite_master 
            WHERE type='table' 
            AND name NOT LIKE 'sqlite_%'
            ORDER BY name
        """)
        
        tables = {row[0] for row in cursor.fetchall()}
        
        expected_tables = {
            'application',
            'application_sync',
            'client',
            'company',
            'contact',
            'contact_email',
            'contact_phone',
            'default_value',
            'job_search_site',
            'note'
        }
        
        assert tables == expected_tables, f"Expected {expected_tables}, got {tables}"
    
    def test_foreign_keys_enabled(self, test_database):
        """Verify foreign keys are enabled."""
        cursor = test_database.execute("PRAGMA foreign_keys")
        result = cursor.fetchone()[0]
        assert result == 1, "Foreign keys should be enabled"
    
    def test_application_table_structure(self, test_database):
        """Verify application table has all expected columns."""
        cursor = test_database.execute("PRAGMA table_info(application)")
        columns = {row[1]: row[2] for row in cursor.fetchall()}
        
        expected_columns = {
            'id', 'status', 'requirement', 'work_setting', 'compensation',
            'position', 'job_description', 'job_link', 'location',
            'resume', 'cover_letter', 'entered_iwd', 'date_close',
            'company_id', 'client_id', 'is_deleted',
            'created_on', 'modified_on', 'created_by', 'modified_by'
        }
        
        assert set(columns.keys()) == expected_columns, f"Missing columns: {expected_columns - set(columns.keys())}"
        assert columns['id'] == 'INTEGER', "id should be INTEGER"
        assert columns['status'] == 'TEXT', "status should be TEXT"
        assert columns['company_id'] == 'INTEGER', "company_id should be INTEGER (FK)"
        assert columns['client_id'] == 'INTEGER', "client_id should be INTEGER (FK)"
    
    def test_company_table_structure(self, test_database):
        """Verify company table has all expected columns."""
        cursor = test_database.execute("PRAGMA table_info(company)")
        columns = {row[1]: row[2] for row in cursor.fetchall()}
        
        expected_columns = {
            'id', 'name', 'address', 'city', 'state', 'zip',
            'country', 'job_type', 'is_deleted',
            'created_on', 'modified_on', 'created_by', 'modified_by'
        }
        
        assert set(columns.keys()) == expected_columns
        assert columns['id'] == 'INTEGER'
        assert columns['name'] == 'TEXT'
    
    def test_contact_table_structure(self, test_database):
        """Verify contact table has all expected columns."""
        cursor = test_database.execute("PRAGMA table_info(contact)")
        columns = {row[1]: row[2] for row in cursor.fetchall()}
        
        expected_columns = {
            'id', 'company_id', 'application_id', 'client_id',
            'name', 'title', 'linkedin', 'contact_type', 'is_deleted',
            'created_on', 'modified_on', 'created_by', 'modified_by'
        }
        
        assert set(columns.keys()) == expected_columns
        assert columns['company_id'] == 'INTEGER', "company_id should be INTEGER (FK)"
        assert columns['application_id'] == 'INTEGER', "application_id should be INTEGER (FK)"
        assert columns['client_id'] == 'INTEGER', "client_id should be INTEGER (FK)"
    
    def test_contact_email_table_structure(self, test_database):
        """Verify contact_email table has all expected columns."""
        cursor = test_database.execute("PRAGMA table_info(contact_email)")
        columns = {row[1]: row[2] for row in cursor.fetchall()}
        
        expected_columns = {
            'id', 'contact_id', 'email', 'email_type', 'is_primary', 'is_deleted',
            'created_on', 'modified_on', 'created_by', 'modified_by'
        }
        
        assert set(columns.keys()) == expected_columns
        assert columns['contact_id'] == 'INTEGER', "contact_id should be INTEGER (FK)"
    
    def test_contact_phone_table_structure(self, test_database):
        """Verify contact_phone table has all expected columns."""
        cursor = test_database.execute("PRAGMA table_info(contact_phone)")
        columns = {row[1]: row[2] for row in cursor.fetchall()}
        
        expected_columns = {
            'id', 'contact_id', 'phone', 'phone_type', 'is_primary', 'is_deleted',
            'created_on', 'modified_on', 'created_by', 'modified_by'
        }
        
        assert set(columns.keys()) == expected_columns
        assert columns['contact_id'] == 'INTEGER', "contact_id should be INTEGER (FK)"
    
    def test_default_value_table_structure(self, test_database):
        """Verify default_value table has all expected columns."""
        cursor = test_database.execute("PRAGMA table_info(default_value)")
        columns = {row[1]: row[2] for row in cursor.fetchall()}
        
        expected_columns = {
            'id', 'table_name', 'field_name', 'default_value', 'data_type',
            'user_id', 'description', 'is_active',
            'created_on', 'modified_on', 'created_by', 'modified_by'
        }
        
        assert set(columns.keys()) == expected_columns
        assert columns['user_id'] == 'TEXT', "user_id should be TEXT"
    
    def test_indexes_created(self, test_database):
        """Verify indexes are created."""
        cursor = test_database.execute("""
            SELECT name FROM sqlite_master 
            WHERE type='index' 
            AND name NOT LIKE 'sqlite_%'
            ORDER BY name
        """)
        
        indexes = {row[0] for row in cursor.fetchall()}
        
        # Check for key indexes
        assert 'idx_application_company_id' in indexes
        assert 'idx_application_client_id' in indexes
        assert 'idx_application_status' in indexes
        assert 'idx_contact_company_id' in indexes
        assert 'idx_contact_email_contact_id' in indexes
        assert 'idx_contact_phone_contact_id' in indexes
        assert 'idx_default_value_table_field_user' in indexes
        assert len(indexes) >= 20, f"Expected at least 20 indexes, got {len(indexes)}"
    
    def test_primary_keys(self, test_database):
        """Verify all tables have primary keys."""
        cursor = test_database.execute("""
            SELECT name FROM sqlite_master 
            WHERE type='table' 
            AND name NOT LIKE 'sqlite_%'
            ORDER BY name
        """)
        
        tables = [row[0] for row in cursor.fetchall()]
        
        for table in tables:
            cursor = test_database.execute(f'PRAGMA table_info("{table}")')
            columns = cursor.fetchall()
            pk_columns = [col for col in columns if col[5] == 1]  # Column 5 is pk flag
            assert len(pk_columns) > 0, f"Table {table} should have a primary key"
            assert pk_columns[0][1] == 'id', f"Table {table} primary key should be named 'id'"
