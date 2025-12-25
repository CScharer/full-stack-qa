"""
Database connection module for ONE GOAL API
"""
import sqlite3
from pathlib import Path
from contextlib import contextmanager
from typing import Generator
from app.config import get_database_path


@contextmanager
def get_db_connection() -> Generator[sqlite3.Connection, None, None]:
    """
    Get database connection with proper configuration.
    
    Yields:
        sqlite3.Connection: Database connection with foreign keys enabled
        
    Example:
        with get_db_connection() as conn:
            cursor = conn.execute("SELECT * FROM application")
            results = cursor.fetchall()
    """
    db_path = get_database_path()
    
    # Ensure database file exists
    if not db_path.exists():
        raise FileNotFoundError(f"Database file not found: {db_path}")
    
    conn = sqlite3.connect(str(db_path))
    conn.row_factory = sqlite3.Row  # Return rows as dict-like objects
    
    try:
        # Enable foreign keys
        conn.execute("PRAGMA foreign_keys = ON")
        
        yield conn
        
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()


def check_database_connection() -> bool:
    """
    Check if database connection works.
    
    Returns:
        bool: True if connection successful, False otherwise
    """
    try:
        with get_db_connection() as conn:
            conn.execute("SELECT 1")
        return True
    except Exception:
        return False
