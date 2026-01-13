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
PORTS_JSON = CONFIG_DIR / 'ports.json'  # Fallback for backward compatibility

# Cache loaded config
_config_cache: Optional[Dict[str, Any]] = None
_ports_cache: Optional[Dict[str, Any]] = None


def _load_config() -> Dict[str, Any]:
    """Load environments.json with caching."""
    global _config_cache
    if _config_cache is None:
        if not ENVIRONMENTS_JSON.exists():
            raise FileNotFoundError(
                f"Configuration file not found: {ENVIRONMENTS_JSON}. "
                f"Expected at: {ENVIRONMENTS_JSON.absolute()}"
            )
        with open(ENVIRONMENTS_JSON, 'r', encoding='utf-8') as f:
            _config_cache = json.load(f)
    return _config_cache


def _load_ports() -> Dict[str, Any]:
    """Load ports.json with caching (fallback)."""
    global _ports_cache
    if _ports_cache is None:
        if PORTS_JSON.exists():
            with open(PORTS_JSON, 'r', encoding='utf-8') as f:
                _ports_cache = json.load(f)
        else:
            _ports_cache = {}
    return _ports_cache


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
    
    # Try environments.json first (comprehensive config)
    if 'environments' in config and env in config['environments']:
        env_data = config['environments'][env]
        return {
            'frontend': env_data['frontend'],
            'backend': env_data['backend'],
        }
    
    # Fallback to ports.json (backward compatibility)
    ports = _load_ports()
    if env in ports:
        return ports[env]
    
    print(f"⚠️  Unknown environment: {environment}, defaulting to {default_env}")
    return ports.get(default_env, {
        'frontend': {'port': 3003, 'url': 'http://localhost:3003'},
        'backend': {'port': 8003, 'url': 'http://localhost:8003'},
    })
