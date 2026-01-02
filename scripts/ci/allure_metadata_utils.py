#!/usr/bin/env python3
"""
Allure Metadata Utilities
Shared functions for adding verification metadata to Allure test results.

This module provides functions to generate verification metadata parameters
that can be added to Allure test results to verify they are from different
test runs and environments.
"""

import os
from datetime import datetime
from typing import List, Dict, Optional


def get_verification_metadata(
    env: Optional[str] = None,
    test_timestamp: Optional[float] = None,
    base_url_env_var: str = "BASE_URL"
) -> List[Dict[str, str]]:
    """
    Generate verification metadata parameters for Allure test results.
    
    Args:
        env: Environment name (dev, test, prod). If None or "unknown" or "combined", returns empty list.
        test_timestamp: Optional timestamp in milliseconds. If None, uses current time.
        base_url_env_var: Environment variable name for base URL (default: "BASE_URL")
    
    Returns:
        List of parameter dictionaries with keys: name, value
        Returns empty list if env is None, "unknown", or "combined"
    
    Example:
        >>> params = get_verification_metadata("dev", 1234567890000)
        >>> # Returns:
        >>> # [
        >>> #     {"name": "Base URL", "value": "http://localhost:3003"},
        >>> #     {"name": "Test Execution Time", "value": "2009-02-13T23:31:30"},
        >>> #     {"name": "CI Run ID", "value": "123456"},
        >>> #     {"name": "CI Run Number", "value": "42"}
        >>> # ]
    """
    if not env or env in ["unknown", "combined"]:
        return []
    
    params = []
    
    # Base URL
    base_url = os.environ.get(base_url_env_var, "unknown")
    params.append({"name": "Base URL", "value": base_url})
    
    # Test Execution Time
    if test_timestamp and test_timestamp > 0:
        # Convert milliseconds to datetime
        test_timestamp_iso = datetime.fromtimestamp(test_timestamp / 1000).isoformat()
    else:
        test_timestamp_iso = datetime.now().isoformat()
    params.append({"name": "Test Execution Time", "value": test_timestamp_iso})
    
    # CI Run ID
    ci_run_id = os.environ.get("GITHUB_RUN_ID", "local")
    params.append({"name": "CI Run ID", "value": ci_run_id})
    
    # CI Run Number
    ci_run_number = os.environ.get("GITHUB_RUN_NUMBER", "unknown")
    params.append({"name": "CI Run Number", "value": ci_run_number})
    
    return params


def add_verification_metadata_to_params(
    params: List[Dict[str, str]],
    env: Optional[str] = None,
    test_timestamp: Optional[float] = None,
    base_url_env_var: str = "BASE_URL"
) -> List[Dict[str, str]]:
    """
    Add verification metadata to an existing params list.
    
    This is a convenience function that extends an existing params list
    with verification metadata.
    
    Args:
        params: Existing list of parameter dictionaries
        env: Environment name (dev, test, prod)
        test_timestamp: Optional timestamp in milliseconds
        base_url_env_var: Environment variable name for base URL
    
    Returns:
        Extended params list with verification metadata appended
    """
    verification_params = get_verification_metadata(env, test_timestamp, base_url_env_var)
    params.extend(verification_params)
    return params


# For command-line usage
if __name__ == "__main__":
    import json
    import sys
    
    # Parse command line arguments
    env = sys.argv[1] if len(sys.argv) > 1 else None
    test_timestamp = float(sys.argv[2]) if len(sys.argv) > 2 and sys.argv[2] != "None" else None
    base_url_env_var = sys.argv[3] if len(sys.argv) > 3 else "BASE_URL"
    
    # Generate metadata
    metadata = get_verification_metadata(env, test_timestamp, base_url_env_var)
    
    # Output as JSON
    print(json.dumps(metadata, indent=2))

