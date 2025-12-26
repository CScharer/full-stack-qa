"""
Database connection module for ONE GOAL API
"""
import logging
import sqlite3
from pathlib import Path
from contextlib import contextmanager
from typing import Generator
from app.config import get_database_path

# Set up logger
logger = logging.getLogger(__name__)


@contextmanager
def get_db_connection() -> Generator[sqlite3.Connection, None, None]:
    """
    Get database connection with proper configuration.
    
    Uses environment-based database selection from config.
    Logs which database is being used for debugging.
    
    Yields:
        sqlite3.Connection: Database connection with foreign keys enabled
        
    Example:
        with get_db_connection() as conn:
            cursor = conn.execute("SELECT * FROM application")
            results = cursor.fetchall()
            
    Raises:
        FileNotFoundError: If database file does not exist
        ValueError: If attempting to use schema database (full_stack_qa.db) for runtime
    """
    db_path = get_database_path()
    
    # Log which database is being used (helpful for debugging)
    logger.info(f"Connecting to database: {db_path}")
    logger.debug(f"Database path (absolute): {db_path.resolve()}")
    
    # Ensure database file exists
    if not db_path.exists():
        error_msg = f"Database file not found: {db_path}"
        logger.error(error_msg)
        raise FileNotFoundError(error_msg)
    
    conn = sqlite3.connect(str(db_path))
    conn.row_factory = sqlite3.Row  # Return rows as dict-like objects
    
    try:
        # Enable foreign keys
        conn.execute("PRAGMA foreign_keys = ON")
        
        logger.debug(f"Database connection established: {db_path.name}")
        
        yield conn
        
        conn.commit()
    except Exception as e:
        logger.error(f"Database error on {db_path.name}: {e}")
        conn.rollback()
        raise
    finally:
        conn.close()
        logger.debug(f"Database connection closed: {db_path.name}")


def check_database_connection() -> bool:
    """
    Check if database connection works.
    
    Returns:
        bool: True if connection successful, False otherwise
    """
    try:
        with get_db_connection() as conn:
            conn.execute("SELECT 1")
        logger.info("Database connection check: SUCCESS")
        return True
    except Exception as e:
        logger.error(f"Database connection check: FAILED - {e}")
        return False
