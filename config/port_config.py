"""
Shared Environment Configuration for Python Projects
Single source of truth for all environment configuration

This module reads from config/environments.json to ensure consistency
between Python projects (Backend, tests) and other frameworks.

Usage:
    from config.port_config import get_environment_config, get_backend_url
    
    config = get_environment_config('dev')
    backend_url = get_backend_url('test')
    print(config['database']['name'])  # "full_stack_qa_dev.db"
"""
import json
import os
from pathlib import Path
from typing import Dict, Any, Optional

# Get the project root directory (parent of config/)
PROJECT_ROOT = Path(__file__).parent.parent
CONFIG_DIR = PROJECT_ROOT / 'config'
ENVIRONMENTS_JSON = CONFIG_DIR / 'environments.json'

# Cache loaded config
_config_cache: Optional[Dict[str, Any]] = None


def _get_hardcoded_config() -> Dict[str, Any]:
    """
    Get hardcoded configuration as fallback when environments.json is unavailable.
    
    Returns:
        Hardcoded configuration dictionary matching environments.json structure.
        These values match the previous ports.json values for backward compatibility.
    """
    return {
        "api": {
            "basePath": "/api/v1",
            "healthEndpoint": "/health",
            "docsEndpoint": "/docs",
            "redocEndpoint": "/redoc"
        },
        "database": {
            "directory": "data/core",
            "schemaDatabase": "full_stack_qa.db",
            "namingPattern": "full_stack_qa_{env}.db"
        },
        "timeouts": {
            "serviceStartup": 120,
            "serviceVerification": 30,
            "apiClient": 10000,
            "webServer": 120000,
            "checkInterval": 2
        },
        "environments": {
            "dev": {
                "frontend": {"port": 3003, "url": "http://localhost:3003"},
                "backend": {"port": 8003, "url": "http://localhost:8003"},
                "database": {
                    "name": "full_stack_qa_dev.db",
                    "path": "data/core/full_stack_qa_dev.db"
                },
                "corsOrigins": [
                    "http://127.0.0.1:3003",
                    "http://localhost:3003",
                    "http://0.0.0.0:3003"
                ]
            },
            "test": {
                "frontend": {"port": 3004, "url": "http://localhost:3004"},
                "backend": {"port": 8004, "url": "http://localhost:8004"},
                "database": {
                    "name": "full_stack_qa_test.db",
                    "path": "data/core/full_stack_qa_test.db"
                },
                "corsOrigins": [
                    "http://127.0.0.1:3004",
                    "http://localhost:3004",
                    "http://0.0.0.0:3004"
                ]
            },
            "prod": {
                "frontend": {"port": 3005, "url": "http://localhost:3005"},
                "backend": {"port": 8005, "url": "http://localhost:8005"},
                "database": {
                    "name": "full_stack_qa_prod.db",
                    "path": "data/core/full_stack_qa_prod.db"
                },
                "corsOrigins": [
                    "http://127.0.0.1:3005",
                    "http://localhost:3005",
                    "http://0.0.0.0:3005"
                ]
            }
        }
    }


def _load_config() -> Dict[str, Any]:
    """
    Load environments.json with caching, fallback to hardcoded values.
    
    Returns:
        Configuration dictionary from environments.json or hardcoded fallback
    """
    global _config_cache
    if _config_cache is None:
        if ENVIRONMENTS_JSON.exists():
            try:
                with open(ENVIRONMENTS_JSON, 'r', encoding='utf-8') as f:
                    _config_cache = json.load(f)
            except (json.JSONDecodeError, IOError) as e:
                print(f"⚠️  Warning: Failed to load {ENVIRONMENTS_JSON}: {e}")
                print("⚠️  Falling back to hardcoded configuration values")
                _config_cache = _get_hardcoded_config()
        else:
            print(f"⚠️  Warning: Configuration file not found: {ENVIRONMENTS_JSON}")
            print("⚠️  Falling back to hardcoded configuration values")
            _config_cache = _get_hardcoded_config()
    return _config_cache


def get_environment_config(environment: str = 'dev', default_env: str = 'dev') -> Dict[str, Any]:
    """
    Get full environment configuration (ports, database, CORS, etc.)
    
    Args:
        environment: Environment name (dev, test, prod)
        default_env: Default environment if invalid (defaults to 'dev')
    
    Returns:
        Full environment configuration dictionary
    """
    env = (environment or default_env).lower()
    config = _load_config()
    
    if 'environments' in config and env in config['environments']:
        return config['environments'][env]
    
    print(f"⚠️  Unknown environment: {environment}, defaulting to {default_env}")
    return config['environments'][default_env]


def get_backend_url(environment: str = 'dev', default_env: str = 'dev') -> str:
    """
    Get backend URL for a specific environment
    
    Args:
        environment: Environment name (dev, test, prod)
        default_env: Default environment if invalid (defaults to 'dev')
    
    Returns:
        Backend URL (e.g., 'http://localhost:8003')
    """
    config = get_environment_config(environment, default_env)
    return config['backend']['url']


def get_frontend_url(environment: str = 'dev', default_env: str = 'dev') -> str:
    """
    Get frontend URL for a specific environment
    
    Args:
        environment: Environment name (dev, test, prod)
        default_env: Default environment if invalid (defaults to 'dev')
    
    Returns:
        Frontend URL (e.g., 'http://localhost:3003')
    """
    config = get_environment_config(environment, default_env)
    return config['frontend']['url']


def get_api_config() -> Dict[str, Any]:
    """
    Get API configuration
    
    Returns:
        API configuration dictionary
    """
    config = _load_config()
    return config.get('api', {})


def get_api_base_path() -> str:
    """
    Get API base path from config (e.g., "/api/v1")
    
    Returns:
        API base path string
    """
    api_config = get_api_config()
    return api_config.get('basePath', '/api/v1')


def get_timeout_config() -> Dict[str, Any]:
    """
    Get timeout configuration
    
    Returns:
        Timeout configuration dictionary
    """
    config = _load_config()
    return config.get('timeouts', {})


def get_database_config() -> Dict[str, Any]:
    """
    Get database configuration
    
    Returns:
        Database configuration dictionary
    """
    config = _load_config()
    return config.get('database', {})


def get_ports_for_environment(environment: str = 'dev', default_env: str = 'dev') -> Dict[str, Any]:
    """
    Get port configuration for a specific environment
    
    Args:
        environment: Environment name (dev, test, prod)
        default_env: Default environment if invalid (defaults to 'dev')
    
    Returns:
        Port configuration dictionary with 'frontend' and 'backend' keys
    """
    env = (environment or default_env).lower()
    config = _load_config()
    
    # Get environment configuration (from environments.json or hardcoded fallback)
    if 'environments' in config and env in config['environments']:
        env_data = config['environments'][env]
        return {
            'frontend': env_data['frontend'],
            'backend': env_data['backend'],
        }
    
    # Unknown environment - default to specified default_env
    print(f"⚠️  Unknown environment: {environment}, defaulting to {default_env}")
    if 'environments' in config and default_env in config['environments']:
        env_data = config['environments'][default_env]
        return {
            'frontend': env_data['frontend'],
            'backend': env_data['backend'],
        }
    
    # Final fallback to hardcoded dev values
    return {
        'frontend': {'port': 3003, 'url': 'http://localhost:3003'},
        'backend': {'port': 8003, 'url': 'http://localhost:8003'},
    }
