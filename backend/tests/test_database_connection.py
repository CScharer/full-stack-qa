"""
Tests for database connection module.
"""
import pytest
import sqlite3
from app.database.connection import get_db_connection, check_database_connection
from pathlib import Path
import tempfile
import os


def test_get_db_connection_creates_connection(test_db_path, test_database):
    """Test that get_db_connection creates a valid connection."""
    # Monkey patch the database path
    import app.database.connection
    original_get_database_path = app.database.connection.get_database_path
    
    def mock_get_database_path():
        return Path(test_db_path)
    
    app.database.connection.get_database_path = mock_get_database_path
    
    try:
        with get_db_connection() as conn:
            assert conn is not None
            assert isinstance(conn, sqlite3.Connection)
            
            # Test that foreign keys are enabled
            cursor = conn.execute("PRAGMA foreign_keys")
            result = cursor.fetchone()[0]
            assert result == 1, "Foreign keys should be enabled"
            
            # Test that we can execute a query
            cursor = conn.execute("SELECT 1")
            assert cursor.fetchone()[0] == 1
    finally:
        app.database.connection.get_database_path = original_get_database_path


def test_get_db_connection_rollback_on_error(test_db_path, test_database):
    """Test that get_db_connection rolls back on error."""
    import app.database.connection
    original_get_database_path = app.database.connection.get_database_path
    
    def mock_get_database_path():
        return Path(test_db_path)
    
    app.database.connection.get_database_path = mock_get_database_path
    
    try:
        with get_db_connection() as conn:
            # Create a table for testing
            conn.execute("CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, value TEXT)")
            conn.commit()
            
            # Insert a value
            conn.execute("INSERT INTO test_table (value) VALUES ('test')")
            conn.commit()
            
            # Verify it exists
            cursor = conn.execute("SELECT COUNT(*) FROM test_table")
            assert cursor.fetchone()[0] == 1
            
            # Now cause an error (this should trigger rollback)
            try:
                with get_db_connection() as conn2:
                    conn2.execute("INSERT INTO test_table (value) VALUES ('test2')")
                    # Intentionally cause an error
                    raise ValueError("Test error")
            except ValueError:
                pass
            
            # Verify the second insert was rolled back
            cursor = conn.execute("SELECT COUNT(*) FROM test_table")
            assert cursor.fetchone()[0] == 1, "Rollback should have prevented the second insert"
    finally:
        app.database.connection.get_database_path = original_get_database_path


def test_check_database_connection_success(test_db_path, test_database):
    """Test check_database_connection returns True for valid database."""
    import app.database.connection
    original_get_database_path = app.database.connection.get_database_path
    
    def mock_get_database_path():
        return Path(test_db_path)
    
    app.database.connection.get_database_path = mock_get_database_path
    
    try:
        result = check_database_connection()
        assert result is True
    finally:
        app.database.connection.get_database_path = original_get_database_path


def test_check_database_connection_failure():
    """Test check_database_connection returns False for invalid database."""
    import app.database.connection
    original_get_database_path = app.database.connection.get_database_path
    
    def mock_get_database_path():
        return Path("/nonexistent/path/database.db")
    
    app.database.connection.get_database_path = mock_get_database_path
    
    try:
        result = check_database_connection()
        assert result is False
    finally:
        app.database.connection.get_database_path = original_get_database_path
