"""
Configuration settings for ONE GOAL API
"""
import os
from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import List


class Settings(BaseSettings):
    """Application settings loaded from environment variables."""
    
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore"  # Ignore extra fields in .env file (like DEV_PORT, TEST_PORT, etc.)
    )
    
    # Database Configuration
    database_path: str = "../Data/Core/full_stack_testing.db"
    
    # API Configuration
    api_host: str = "localhost"
    api_port: int = 8008
    api_reload: bool = True
    
    # CORS Configuration
    cors_origins: List[str] = [
        "http://127.0.0.1:3003",
        "http://localhost:3003",
    ]
    
    # Environment
    environment: str = "development"


# Get absolute path to database
def get_database_path() -> Path:
    """Get absolute path to database file."""
    # Check for environment variable first (for testing)
    env_db_path = os.getenv("DATABASE_PATH")
    if env_db_path:
        return Path(env_db_path).resolve()
    
    settings = Settings()
    db_path = Path(settings.database_path)
    
    # If relative path, resolve from backend directory
    if not db_path.is_absolute():
        backend_dir = Path(__file__).parent.parent
        db_path = (backend_dir / db_path).resolve()
    
    return db_path


# Global settings instance
settings = Settings()
