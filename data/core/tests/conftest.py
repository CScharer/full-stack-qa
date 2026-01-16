"""
Pytest configuration and fixtures for database tests.
"""
import pytest
import sqlite3
import os
import tempfile
import shutil
from pathlib import Path


@pytest.fixture(scope="session")
def test_db_path():
    """
    Create a temporary database file for testing.
    Database name is environment-aware and prefixed with pytest_temp_.
    Defaults to dev environment if ENVIRONMENT not set.
    
    Examples:
        ENVIRONMENT=dev (or unset) -> pytest_temp_full_stack_qa_dev.db
        ENVIRONMENT=test -> pytest_temp_full_stack_qa_test.db
        ENVIRONMENT=prod -> pytest_temp_full_stack_qa_prod.db
    """
    # Create temporary directory for test database
    temp_dir = tempfile.mkdtemp()
    
    # Get environment from env var, default to 'dev' (consistent with other scripts)
    environment = os.getenv("ENVIRONMENT", "dev").lower()
    
    # Validate environment
    if environment not in ["dev", "test", "prod"]:
        environment = "dev"  # Fallback to dev if invalid
    
    # Create environment-aware database name with pytest_temp_ prefix
    db_name = f"pytest_temp_full_stack_qa_{environment}.db"
    db_path = os.path.join(temp_dir, db_name)
    
    yield db_path
    
    # Cleanup: Remove temporary directory and database
    if os.path.exists(db_path):
        os.remove(db_path)
    if os.path.exists(temp_dir):
        shutil.rmtree(temp_dir)


@pytest.fixture(scope="session")
def schema_file_path():
    """Get the path to the schema SQL file."""
    # Get the project root (go up from data/core/tests to project root)
    project_root = Path(__file__).parent.parent.parent.parent
    schema_path = project_root / "docs" / "new_app" / "ONE_GOAL_SCHEMA_CORRECTED.sql"
    
    if not schema_path.exists():
        raise FileNotFoundError(f"Schema file not found: {schema_path}")
    
    return str(schema_path)


@pytest.fixture(scope="session")
def default_value_schema():
    """Get the SQL for creating the default_value table."""
    return """
    -- Default values reference table with user-specific defaults
    CREATE TABLE IF NOT EXISTS "default_value" (
        "id" INTEGER PRIMARY KEY AUTOINCREMENT,
        "table_name" TEXT NOT NULL,
        "field_name" TEXT NOT NULL,
        "default_value" TEXT NOT NULL,
        "data_type" TEXT NOT NULL,
        "user_id" TEXT NOT NULL DEFAULT 'system',
        "description" TEXT,
        "is_active" INTEGER DEFAULT 1,
        "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
        "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
        "created_by" TEXT NOT NULL,
        "modified_by" TEXT NOT NULL,
        UNIQUE("table_name", "field_name", "user_id", "is_active")
    );

    CREATE INDEX IF NOT EXISTS "idx_default_value_table_field_user" ON "default_value"("table_name", "field_name", "user_id", "is_active");
    CREATE INDEX IF NOT EXISTS "idx_default_value_user" ON "default_value"("user_id", "is_active");
    """


@pytest.fixture(scope="session")
def delete_triggers_schema():
    """Get the SQL for creating delete triggers."""
    # Get the project root (go up from data/core/tests to project root)
    project_root = Path(__file__).parent.parent.parent.parent
    triggers_path = project_root / "docs" / "new_app" / "DELETE_TRIGGERS.sql"
    
    if not triggers_path.exists():
        return ""  # Return empty if triggers file doesn't exist yet
    
    with open(triggers_path, 'r') as f:
        return f.read()


@pytest.fixture(scope="session")
def test_database(test_db_path, schema_file_path, default_value_schema, delete_triggers_schema):
    """
    Create a fresh test database with schema applied.
    This runs once per test session (before all tests).
    """
    # Read and execute schema file
    with open(schema_file_path, 'r') as f:
        schema_sql = f.read()
    
    # Create database connection
    conn = sqlite3.connect(test_db_path)
    conn.row_factory = sqlite3.Row  # Return rows as dict-like objects
    
    try:
        # Enable foreign keys
        conn.execute("PRAGMA foreign_keys = ON")
        
        # Execute schema
        conn.executescript(schema_sql)
        
        # Add default_value table
        conn.executescript(default_value_schema)
        
        # Add delete triggers if available
        if delete_triggers_schema:
            conn.executescript(delete_triggers_schema)
        
        # Commit
        conn.commit()
        
        yield conn
        
    finally:
        conn.close()


@pytest.fixture(scope="function")
def db_connection(test_database, test_db_path):
    """
    Get a fresh database connection for each test.
    This ensures tests don't interfere with each other.
    """
    conn = sqlite3.connect(test_db_path)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON")
    
    yield conn
    
    conn.close()


@pytest.fixture(scope="function")
def clean_db(db_connection):
    """
    Clean database before each test (optional - use when needed).
    This deletes all data but keeps schema.
    """
    # Get all table names (except sqlite_sequence)
    cursor = db_connection.execute("""
        SELECT name FROM sqlite_master 
        WHERE type='table' 
        AND name NOT LIKE 'sqlite_%'
        ORDER BY name
    """)
    
    tables = [row[0] for row in cursor.fetchall()]
    
    # Delete data from all tables (in reverse order to respect foreign keys)
    # Start with child tables first
    for table in reversed(tables):
        db_connection.execute(f'DELETE FROM "{table}"')
    
    db_connection.commit()
    
    yield db_connection
