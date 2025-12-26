"""
Configuration settings for ONE GOAL API
"""
import os
from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import List, Optional


class Settings(BaseSettings):
    """Application settings loaded from environment variables."""
    
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore"  # Ignore extra fields in .env file (like DEV_PORT, TEST_PORT, etc.)
    )
    
    # Database Configuration
    # Note: database_path is now computed dynamically based on priority:
    # 1. DATABASE_PATH env var (full path)
    # 2. DATABASE_NAME env var + DATABASE_DIR (or default dir)
    # 3. ENVIRONMENT env var → full_stack_qa_{env}.db
    # 4. Default → full_stack_qa_dev.db
    database_path: str = "../Data/Core/full_stack_qa_dev.db"  # Default (dev environment)
    database_name: Optional[str] = None  # Optional: database filename only
    database_dir: str = "../Data/Core"  # Database directory path
    environment: str = "dev"  # Environment name (dev/test/prod) - used for database selection
    
    # API Configuration
    api_host: str = "localhost"
    api_port: int = 8008
    api_reload: bool = True
    
    # CORS Configuration
    cors_origins: List[str] = [
        "http://127.0.0.1:3003",
        "http://localhost:3003",
    ]


# Get absolute path to database
def get_database_path() -> Path:
    """
    Get absolute path to database file using priority-based resolution.
    
    Priority order:
    1. DATABASE_PATH env var (full path) - highest priority
    2. DATABASE_NAME env var + DATABASE_DIR (or default dir)
    3. ENVIRONMENT env var → full_stack_qa_{env}.db
    4. Default → full_stack_qa_dev.db
    
    Returns:
        Path: Absolute path to database file
        
    Raises:
        ValueError: If attempting to use schema database (full_stack_qa.db) for runtime
    """
    settings = Settings()
    backend_dir = Path(__file__).parent.parent
    
    # Priority 1: Full path from DATABASE_PATH env var
    env_db_path = os.getenv("DATABASE_PATH")
    if env_db_path:
        db_path = Path(env_db_path).resolve()
        _validate_not_schema_database(db_path)
        return db_path
    
    # Priority 2: Database name + directory
    env_db_name = os.getenv("DATABASE_NAME")
    env_db_dir = os.getenv("DATABASE_DIR", settings.database_dir)
    
    if env_db_name:
        db_dir = Path(env_db_dir)
        if not db_dir.is_absolute():
            db_dir = (backend_dir / db_dir).resolve()
        db_path = db_dir / env_db_name
        _validate_not_schema_database(db_path)
        return db_path.resolve()
    
    # Priority 3: Environment-based selection
    env = os.getenv("ENVIRONMENT", settings.environment).lower()
    if env in ["dev", "test", "prod"]:
        db_name = f"full_stack_qa_{env}.db"
        db_dir = Path(os.getenv("DATABASE_DIR", settings.database_dir))
        if not db_dir.is_absolute():
            db_dir = (backend_dir / db_dir).resolve()
        db_path = db_dir / db_name
        _validate_not_schema_database(db_path)
        return db_path.resolve()
    
    # Priority 4: Default (dev environment)
    db_path = Path(settings.database_path)
    if not db_path.is_absolute():
        db_path = (backend_dir / db_path).resolve()
    
    _validate_not_schema_database(db_path)
    return db_path


def _validate_not_schema_database(db_path: Path) -> None:
    """
    Validate that the database path is not the schema database.
    
    The schema database (full_stack_qa.db) should NEVER be used for runtime.
    It is only a reference/template for creating environment databases.
    
    Args:
        db_path: Path to database file
        
    Raises:
        ValueError: If attempting to use schema database for runtime
    """
    if db_path.name == "full_stack_qa.db":
        raise ValueError(
            f"Cannot use schema database '{db_path.name}' for runtime. "
            f"Use an environment database instead (full_stack_qa_dev.db, "
            f"full_stack_qa_test.db, or full_stack_qa_prod.db)."
        )


# Global settings instance
settings = Settings()
