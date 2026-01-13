"""
Configuration Helper for Robot Framework
Reads from shared config/environments.json

This module provides Robot Framework keywords to access the shared configuration.
Robot Framework can call Python functions directly, so we use the shared config/port_config.py.
"""
import sys
import os
from pathlib import Path

# Add project root to path to import shared config
PROJECT_ROOT = Path(__file__).parent.parent.parent.parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

try:
    from config.port_config import (
        get_environment_config,
        get_backend_url,
        get_frontend_url,
        get_ports_for_environment,
    )
    SHARED_CONFIG_AVAILABLE = True
except ImportError:
    SHARED_CONFIG_AVAILABLE = False
    print("⚠️  Warning: Could not import shared config. Using defaults.")


def get_backend_url_for_robot(environment: str = None) -> str:
    """
    Get backend URL for Robot Framework.
    
    Args:
        environment: Environment name (dev, test, prod). If None, reads from ENVIRONMENT env var.
    
    Returns:
        Backend URL string
    """
    if not SHARED_CONFIG_AVAILABLE:
        # Fallback to hardcoded defaults
        env = (environment or os.getenv("ENVIRONMENT", "dev")).lower()
        defaults = {
            "dev": "http://localhost:8003",
            "test": "http://localhost:8004",
            "prod": "http://localhost:8005",
        }
        return defaults.get(env, defaults["dev"])
    
    env = environment or os.getenv("ENVIRONMENT", "dev")
    return get_backend_url(env)


def get_frontend_url_for_robot(environment: str = None) -> str:
    """
    Get frontend URL for Robot Framework.
    
    Args:
        environment: Environment name (dev, test, prod). If None, reads from ENVIRONMENT env var.
    
    Returns:
        Frontend URL string
    """
    if not SHARED_CONFIG_AVAILABLE:
        # Fallback to hardcoded defaults
        env = (environment or os.getenv("ENVIRONMENT", "dev")).lower()
        defaults = {
            "dev": "http://localhost:3003",
            "test": "http://localhost:3004",
            "prod": "http://localhost:3005",
        }
        return defaults.get(env, defaults["dev"])
    
    env = environment or os.getenv("ENVIRONMENT", "dev")
    return get_frontend_url(env)


def get_base_url_for_robot(environment: str = None) -> str:
    """
    Get base URL (frontend) for Robot Framework.
    Alias for get_frontend_url_for_robot for Robot Framework compatibility.
    
    Args:
        environment: Environment name (dev, test, prod). If None, reads from ENVIRONMENT env var.
    
    Returns:
        Frontend URL string (used as BASE_URL in Robot Framework)
    """
    return get_frontend_url_for_robot(environment)
